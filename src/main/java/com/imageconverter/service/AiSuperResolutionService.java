package com.imageconverter.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * AI 超分辨率服务: 基于 ONNX Runtime 的本地 AI 图片增强
 * 手动安装模型 → 分块推理 → 无缝拼接
 */
@Service
public class AiSuperResolutionService {

    private static final Logger log = LoggerFactory.getLogger(AiSuperResolutionService.class);

    // 模型缓存目录 (jar 所在目录/models)
    private static final String MODEL_DIR = getJarDir() + "/models";
    private static final String MODEL_NAME = "realesr-general-x4v3.onnx";

    /** 获取 jar 所在目录，获取失败则降级到当前工作目录 */
    private static String getJarDir() {
        try {
            String path = AiSuperResolutionService.class
                .getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            java.io.File jarFile = new java.io.File(path);
            // 如果是 jar 文件，返回其父目录；如果是目录(开发模式)，返回该目录
            return jarFile.isFile() ? jarFile.getParent() : jarFile.getPath();
        } catch (Exception e) {
            return System.getProperty("user.dir");
        }
    }

    // 模型下载地址 (分层降级: 优先 SOTA 强力模型, 降级到轻量可靠模型)
    // 所有源已实测验证可下载真实 ONNX 文件 (非 LFS pointer)
    private static final String[] MODEL_URLS = {
        // === 第一梯队: SOTA 强力模型 (大文件, 效果最好) ===
        // DAT-2 架构 (2024 SOTA), 专为真实网络照片设计 (JPEG/WEBP 压缩修复+去模糊+降噪)
        // 46.5MB, GitHub Release 托管 (最可靠的下载源), fp32 opset17
        "https://github.com/Phhofm/models/releases/download/4xRealWebPhoto_v4_dat2/4xRealWebPhoto_v4_dat2_fp32_opset17.onnx",
        // === 第二梯队: 轻量 SOTA 模型 (小文件, 快速下载, 效果仍很强) ===
        // SeemoRe x4v2 (ICML 2024), 通用场景 4x, 动态输入尺寸, 1.8MB
        "https://cdn.jsdelivr.net/gh/nareshmlx/opencv-superres-models@main/seemore_x4v2_dynamic.onnx",
        "https://raw.githubusercontent.com/nareshmlx/opencv-superres-models/main/seemore_x4v2_dynamic.onnx",
        // === 第三梯队: 备用模型 ===
        // SeemoRe 静态版本 (512x512 固定输入)
        "https://cdn.jsdelivr.net/gh/nareshmlx/opencv-superres-models@main/seemore_x4.onnx",
        // RealESR-AnimeVideo-v3 (动漫场景 4x)
        "https://cdn.jsdelivr.net/gh/the-NTSC-RS-guy/RealESRGAN-AnimeVideo-v3-x4-ONNX@main/RealESR-AnimeVideo-v3_x4.onnx",
        "https://raw.githubusercontent.com/the-NTSC-RS-guy/RealESRGAN-AnimeVideo-v3-x4-ONNX/main/RealESR-AnimeVideo-v3_x4.onnx",
        // HuggingFace 镜像 (LFS，需正确跟随跨主机重定向)
        "https://hf-mirror.com/qualcomm/Real-ESRGAN-General-x4v3/resolve/main/Real-ESRGAN-General-x4v3.onnx",
        // HuggingFace 原始 (LFS)
        "https://huggingface.co/qualcomm/Real-ESRGAN-General-x4v3/resolve/main/Real-ESRGAN-General-x4v3.onnx",
        // Qualcomm S3 zip (含完整 ONNX float 模型)
        "https://qaihub-public-assets.s3.us-west-2.amazonaws.com/qai-hub-models/models/real_esrgan_general_x4v3/releases/v0.58.0/real_esrgan_general_x4v3-onnx-float.zip"
    };

    // 分块大小 (模型输入尺寸)
    private static final int TILE_SIZE = 128;
    // 分块重叠 (避免拼接缝隙)
    private static final int TILE_OVERLAP = 16;
    // 超分辨率倍数
    private static final int SCALE = 4;

    private OrtEnvironment env;
    private OrtSession session;
    private String inputName;
    private boolean initialized = false;
    private boolean initFailed = false;

    /**
     * 初始化 ONNX 模型 (懒加载，首次调用时触发)
     */
    private synchronized void init() {
        if (initialized || initFailed) return;
        try {
            // 确保目录存在
            Files.createDirectories(Paths.get(MODEL_DIR));

            // 1. 先检查本地 models 目录
            Path modelPath = findModel();

            // 2. 本地没有，从 jar 内部资源提取
            if (modelPath == null) {
                modelPath = extractBundledModel();
            }

            // 3. 仍然没有，尝试在线下载
            if (modelPath == null) {
                log.warn("本地未找到模型，尝试在线下载...");
                modelPath = downloadModel();
            }

            if (modelPath == null) {
                log.warn("AI 模型不可用，将使用 Java 算法降级处理");
                initFailed = true;
                return;
            }

            env = OrtEnvironment.getEnvironment();
            OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
            opts.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);
            opts.setInterOpNumThreads(1);
            opts.setIntraOpNumThreads(Math.min(4, Runtime.getRuntime().availableProcessors()));

            session = env.createSession(modelPath.toString(), opts);
            inputName = session.getInputNames().iterator().next();
            initialized = true;
            log.info("AI 超分辨率模型加载成功: {}", modelPath);
        } catch (Exception e) {
            log.warn("AI 模型初始化失败: {}，将使用 Java 算法降级处理", e.getMessage());
            initFailed = true;
        }
    }

    /**
     * 从 jar 内部资源提取模型 (CI 构建时打包的)
     */
    private Path extractBundledModel() {
        String resourcePath = "/models/" + MODEL_NAME;
        try (InputStream is = AiSuperResolutionService.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                log.info("jar 内未打包 AI 模型");
                return null;
            }
            Path targetPath = Paths.get(MODEL_DIR, MODEL_NAME);
            log.info("从 jar 内提取 AI 模型到: {}", targetPath);
            Files.copy(is, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            long size = Files.size(targetPath);
            if (size > 1_000_000) {
                log.info("模型提取成功: {} KB", size / 1024);
                return targetPath;
            } else {
                log.warn("jar 内模型太小 ({} bytes)，可能不完整", size);
                Files.deleteIfExists(targetPath);
            }
        } catch (Exception e) {
            log.warn("从 jar 提取模型失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 查找本地模型文件
     * 会检测并清理 LFS pointer 文件 (用户之前下载失败遗留的 43KB pointer)
     */
    private Path findModel() {
        Path modelPath = Paths.get(MODEL_DIR, MODEL_NAME);
        try {
            if (Files.exists(modelPath)) {
                long size = Files.size(modelPath);
                if (size > 1_000_000) {
                    // 进一步检测: 大文件也可能是 LFS pointer + 填充, 检查开头
                    if (isLfsPointer(modelPath)) {
                        log.warn("本地文件是 LFS pointer (非真实模型)，删除后重新下载");
                        Files.deleteIfExists(modelPath);
                        return null;
                    }
                    log.info("找到本地模型: {} ({} KB)", modelPath, size / 1024);
                    return modelPath;
                } else if (size > 100_000) {
                    if (isLfsPointer(modelPath)) {
                        log.warn("本地文件是 LFS pointer ({} bytes)，删除后重新下载", size);
                        Files.deleteIfExists(modelPath);
                        return null;
                    }
                    log.warn("本地模型较小 ({} bytes)，可能是量化版，尝试加载", size);
                    return modelPath;
                } else {
                    log.warn("本地模型太小 ({} bytes)，删除后重新获取", size);
                    Files.deleteIfExists(modelPath);
                }
            }
        } catch (IOException ignored) {}
        return null;
    }

    /**
     * 检测文件是否是 Git LFS pointer (而非真实模型)
     * LFS pointer 文件以 "version https://git-lfs.github.com/spec/" 开头
     */
    private boolean isLfsPointer(Path path) {
        try (InputStream is = Files.newInputStream(path)) {
            byte[] header = new byte[40];
            int read = is.read(header);
            if (read > 0) {
                String preview = new String(header, 0, read);
                return preview.contains("git-lfs") || preview.startsWith("version https://git-lfs");
            }
        } catch (IOException ignored) {}
        return false;
    }

    /**
     * 自动下载模型 (尝试多个源，含国内镜像)
     * 使用 HttpClient 自动跟随跨主机重定向 (修复 HuggingFace LFS 只拿到 pointer 的问题)
     */
    private Path downloadModel() {
        Path targetPath = Paths.get(MODEL_DIR, MODEL_NAME);
        // 关键: Redirect.ALWAYS 会跟随跨主机/跨协议重定向 (HttpURLConnection 默认不跟随)
        HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

        for (String urlStr : MODEL_URLS) {
            Path tempPath = Paths.get(MODEL_DIR, MODEL_NAME + ".part");
            try {
                // 大文件 (DAT-2 46MB) 需要更长超时, 小文件 (SeemoRe 1.8MB) 快速完成
                // 根据是否为第一梯队 SOTA 模型调整超时
                boolean isLargeModel = urlStr.contains("dat2") || urlStr.contains("HAT");
                Duration timeout = isLargeModel ? Duration.ofMinutes(30) : Duration.ofMinutes(5);
                log.info("正在从 {} 下载 AI 模型... (超时: {})", urlStr, isLargeModel ? "30分钟" : "5分钟");
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .timeout(timeout)
                    .header("User-Agent", "ImageConverter/1.0")
                    .header("Accept", "application/octet-stream, */*")
                    .GET()
                    .build();

                HttpResponse<Path> resp = client.send(req,
                    HttpResponse.BodyHandlers.ofFile(tempPath,
                        StandardCopyOption.REPLACE_EXISTING));

                int code = resp.statusCode();
                if (code != 200) {
                    log.warn("HTTP {} 从 {}", code, urlStr);
                    Files.deleteIfExists(tempPath);
                    continue;
                }

                String contentType = resp.headers().firstValue("Content-Type").orElse("");
                long downloadedSize = Files.exists(tempPath) ? Files.size(tempPath) : 0;

                // 如果是 zip 或文件特别小可能是 LFS pointer, 进一步判断
                boolean isZip = contentType.contains("zip") || urlStr.endsWith(".zip");

                if (isZip) {
                    // zip: 解压提取 onnx
                    try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(tempPath))) {
                        ZipEntry entry;
                        boolean found = false;
                        while ((entry = zis.getNextEntry()) != null) {
                            if (entry.getName().endsWith(".onnx")) {
                                Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
                                zis.closeEntry();
                                found = true;
                                break;
                            }
                            zis.closeEntry();
                        }
                        Files.deleteIfExists(tempPath);
                        if (!found) {
                            log.warn("zip 中未找到 .onnx 文件，尝试下一个源");
                            continue;
                        }
                    }
                } else {
                    // 直接 onnx
                    Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }

                // 验证下载文件大小
                long size = Files.size(targetPath);
                if (size > 1_000_000) {
                    log.info("模型下载成功: {} ({} KB)", targetPath, size / 1024);
                    return targetPath;
                } else {
                    log.warn("下载的文件太小 ({} bytes)，可能是 LFS pointer 或错误页面，尝试下一个源", size);
                    // 打印前 200 字节帮助诊断 (LFS pointer 通常以 'version https://git-lfs' 开头)
                    if (size > 0 && size < 1024) {
                        String preview = new String(Files.readAllBytes(targetPath)).trim();
                        log.warn("文件内容预览: {}", preview.substring(0, Math.min(preview.length(), 200)));
                    }
                    Files.deleteIfExists(targetPath);
                }
            } catch (Exception e) {
                log.warn("从 {} 下载失败: {}", urlStr, e.getMessage());
                try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
            }
        }
        return null;
    }

    /**
     * 是否可用 (AI 模型已加载)
     */
    public boolean isAvailable() {
        if (!initialized && !initFailed) {
            init();
        }
        return initialized;
    }

    /**
     * AI 超分辨率增强
     * @param inputBytes 图片字节
     * @param scale 放大倍数 (2 或 4，实际模型固定 4x，2x 会先 4x 再缩放)
     * @return 增强后的 BufferedImage
     */
    public BufferedImage enhance(byte[] inputBytes, int scale) throws IOException {
        if (!isAvailable()) {
            throw new IOException("AI 模型不可用");
        }

        BufferedImage src = readImage(inputBytes);
        int w = src.getWidth(), h = src.getHeight();

        // 如果图片很小，直接整图推理
        if (w <= TILE_SIZE && h <= TILE_SIZE) {
            return runInference(src);
        }

        // 大图: 分块推理
        BufferedImage result = tiledInference(src);

        // 如果请求 2x 但模型是 4x，缩放到 2x
        if (scale == 2 && SCALE == 4) {
            int nw = result.getWidth() / 2, nh = result.getHeight() / 2;
            BufferedImage scaled = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(result, 0, 0, nw, nh, null);
            g.dispose();
            return scaled;
        }

        return result;
    }

    /**
     * 分块推理: 将大图切分为重叠的小块，分别推理后无缝拼接
     */
    private BufferedImage tiledInference(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        int outW = w * SCALE, outH = h * SCALE;
        BufferedImage result = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_ARGB);

        int step = TILE_SIZE - TILE_OVERLAP;
        int tilesX = (w + step - 1) / step;
        int tilesY = (h + step - 1) / step;

        log.info("分块推理: {}x{} → {}x{}, {} 块", w, h, outW, outH, tilesX * tilesY);

        for (int ty = 0; ty < tilesY; ty++) {
            for (int tx = 0; tx < tilesX; tx++) {
                int x0 = tx * step;
                int y0 = ty * step;
                int x1 = Math.min(x0 + TILE_SIZE, w);
                int y1 = Math.min(y0 + TILE_SIZE, h);
                int tw = x1 - x0;
                int th = y1 - y0;

                // 裁剪小块
                BufferedImage tile = src.getSubimage(x0, y0, tw, th);
                // 如果小块不足 TILE_SIZE，pad 到 TILE_SIZE
                boolean padded = false;
                if (tw < TILE_SIZE || th < TILE_SIZE) {
                    tile = padToSize(tile, TILE_SIZE, TILE_SIZE);
                    padded = true;
                }

                // 推理
                BufferedImage srTile = runInference(tile);

                // 如果 pad 过，裁回实际尺寸
                int srTw = tw * SCALE, srTh = th * SCALE;
                if (padded) {
                    srTile = srTile.getSubimage(0, 0, srTw, srTh);
                }

                // 计算重叠区域权重 (线性渐变)
                pasteTile(result, srTile, x0 * SCALE, y0 * SCALE, tx, ty, tilesX, tilesY);
            }
        }

        return result;
    }

    /**
     * 将推理后的小块粘贴到结果图上，重叠区域用线性权重混合
     */
    private void pasteTile(BufferedImage result, BufferedImage tile, int dx, int dy,
                           int tx, int ty, int tilesX, int tilesY) {
        int tw = tile.getWidth(), th = tile.getHeight();
        int overlapPx = TILE_OVERLAP * SCALE;

        for (int y = 0; y < th; y++) {
            for (int x = 0; x < tw; x++) {
                // 计算权重: 在重叠区域用线性渐变
                float wx = 1.0f, wy = 1.0f;
                // 左边重叠
                if (tx > 0 && x < overlapPx) {
                    wx = (float) x / overlapPx;
                }
                // 右边重叠
                if (tx < tilesX - 1 && x > tw - overlapPx) {
                    wx = (float) (tw - x) / overlapPx;
                }
                // 上边重叠
                if (ty > 0 && y < overlapPx) {
                    wy = (float) y / overlapPx;
                }
                // 下边重叠
                if (ty < tilesY - 1 && y > th - overlapPx) {
                    wy = (float) (th - y) / overlapPx;
                }
                float weight = Math.min(wx, 1.0f) * Math.min(wy, 1.0f);

                int rx = dx + x, ry = dy + y;
                if (rx >= result.getWidth() || ry >= result.getHeight()) continue;

                int newRGB = tile.getRGB(x, y);
                if (weight >= 1.0f) {
                    result.setRGB(rx, ry, newRGB);
                } else {
                    int oldRGB = result.getRGB(rx, ry);
                    int na = (newRGB >> 24) & 0xFF, oa = (oldRGB >> 24) & 0xFF;
                    int nr = (newRGB >> 16) & 0xFF, or_ = (oldRGB >> 16) & 0xFF;
                    int ng = (newRGB >> 8) & 0xFF, og = (oldRGB >> 8) & 0xFF;
                    int nb = newRGB & 0xFF, ob = oldRGB & 0xFF;
                    int r = (int) (nr * weight + or_ * (1 - weight));
                    int g = (int) (ng * weight + og * (1 - weight));
                    int b = (int) (nb * weight + ob * (1 - weight));
                    int a = (int) (na * weight + oa * (1 - weight));
                    result.setRGB(rx, ry, (a << 24) | (r << 16) | (g << 8) | b);
                }
            }
        }
    }

    /**
     * pad 图片到指定大小 (边缘复制)
     */
    private BufferedImage padToSize(BufferedImage src, int targetW, int targetH) {
        BufferedImage padded = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = padded.createGraphics();
        g.drawImage(src, 0, 0, null);
        // 右侧 pad: 复制最后一列
        for (int x = src.getWidth(); x < targetW; x++) {
            g.drawImage(src, x, 0, x + 1, src.getHeight(), src.getWidth() - 1, 0, src.getWidth(), src.getHeight(), null);
        }
        // 下方 pad: 复制最后一行
        for (int y = src.getHeight(); y < targetH; y++) {
            g.drawImage(padded, 0, y, targetW, y + 1, 0, src.getHeight() - 1, targetW, src.getHeight(), null);
        }
        g.dispose();
        return padded;
    }

    /**
     * 运行 ONNX 推理
     * 输入: [1, 3, H, W] float32, RGB, 归一化到 [0, 1]
     * 输出: [1, 3, 4H, 4W] float32, RGB, [0, 1]
     */
    private BufferedImage runInference(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();

        // 准备输入张量 [1, 3, H, W], RGB, [0, 1]
        float[] inputData = new float[3 * w * h];
        int[] pixels = src.getRGB(0, 0, w, h, null, 0, w);
        for (int i = 0; i < pixels.length; i++) {
            int rgb = pixels[i];
            inputData[i] = ((rgb >> 16) & 0xFF) / 255.0f;                   // R
            inputData[pixels.length + i] = ((rgb >> 8) & 0xFF) / 255.0f;     // G
            inputData[pixels.length * 2 + i] = (rgb & 0xFF) / 255.0f;        // B
        }

        try (OnnxTensor inputTensor = OnnxTensor.createTensor(env, java.nio.FloatBuffer.wrap(inputData), new long[]{1, 3, h, w})) {
            Map<String, OnnxTensor> inputs = Collections.singletonMap(inputName, inputTensor);

            try (OrtSession.Result output = session.run(inputs)) {
                // 输出 [1, 3, 4H, 4W]
                float[][][][] outputData = (float[][][][]) output.get(0).getValue();
                float[][][] out = outputData[0]; // [3][4H][4W]

                int outW = out[0][0].length;
                int outH = out[0].length;

                BufferedImage result = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_ARGB);
                int[] outPixels = new int[outW * outH];
                for (int y = 0; y < outH; y++) {
                    for (int x = 0; x < outW; x++) {
                        int r = clamp255((int) (out[0][y][x] * 255));
                        int g = clamp255((int) (out[1][y][x] * 255));
                        int b = clamp255((int) (out[2][y][x] * 255));
                        outPixels[y * outW + x] = (255 << 24) | (r << 16) | (g << 8) | b;
                    }
                }
                result.setRGB(0, 0, outW, outH, outPixels, 0, outW);
                return result;
            }
        } catch (Exception e) {
            log.error("ONNX 推理失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI 推理失败", e);
        }
    }

    private BufferedImage readImage(byte[] bytes) throws IOException {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            BufferedImage img = ImageIO.read(is);
            if (img == null) throw new IOException("无法读取图片");
            // 转为 ARGB
            if (img.getType() != BufferedImage.TYPE_INT_ARGB) {
                BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = out.createGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();
                return out;
            }
            return img;
        }
    }

    private static int clamp255(int v) { return v < 0 ? 0 : (v > 255 ? 255 : v); }
}
