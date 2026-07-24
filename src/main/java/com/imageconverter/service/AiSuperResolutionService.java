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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

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

    // 模型下载地址 (仅供手动下载参考，不再自动下载)
    private static final String[] MODEL_URLS = {
        "https://huggingface.co/qualcomm/Real-ESRGAN-General-x4v3/resolve/main/Real-ESRGAN-General-x4v3.onnx",
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
            Path modelPath = findModel();
            if (modelPath == null) {
                log.warn("未找到 AI 模型文件，将使用 Java 算法降级处理");
                log.warn("=== 手动安装 AI 模型步骤 ===");
                log.warn("1. 下载模型 (浏览器打开以下任一地址):");
                log.warn("   地址1: {}", MODEL_URLS[0]);
                log.warn("   地址2 (zip解压后取.onnx): {}", MODEL_URLS[1]);
                log.warn("2. 将模型文件重命名为: {}", MODEL_NAME);
                log.warn("3. 放到目录: {}", MODEL_DIR);
                log.warn("   完整路径: {}/{}", MODEL_DIR, MODEL_NAME);
                log.warn("4. 重启应用");
                // 列出 models 目录下的实际文件，帮助排查
                java.io.File dir = new java.io.File(MODEL_DIR);
                java.io.File[] files = dir.listFiles();
                if (files != null && files.length > 0) {
                    log.warn("当前 {} 目录下的文件:", MODEL_DIR);
                    for (java.io.File f : files) {
                        log.warn("  {} ({} bytes)", f.getName(), f.length());
                    }
                } else {
                    log.warn("目录 {} 为空或不存在", MODEL_DIR);
                }
                initFailed = true;
                return;
            }

            env = OrtEnvironment.getEnvironment();
            OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
            opts.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);
            // CPU 线程数
            opts.setInterOpNumThreads(1);
            opts.setIntraOpNumThreads(Math.min(4, Runtime.getRuntime().availableProcessors()));

            session = env.createSession(modelPath.toString(), opts);

            // 自动获取输入/输出名称
            inputName = session.getInputNames().iterator().next();
            initialized = true;
            log.info("AI 超分辨率模型加载成功: {}", modelPath);
        } catch (Exception e) {
            log.warn("AI 模型初始化失败: {}，将使用 Java 算法降级处理", e.getMessage());
            initFailed = true;
        }
    }

    /**
     * 查找本地模型文件 (仅手动安装，不自动下载)
     */
    private Path findModel() {
        Path modelPath = Paths.get(MODEL_DIR, MODEL_NAME);
        try {
            if (Files.exists(modelPath)) {
                long size = Files.size(modelPath);
                log.info("找到模型文件: {} ({} bytes / {} KB)", modelPath, size, size / 1024);
                if (size > 1_000_000) {
                    // 正常大小 (约 4-5MB)
                    return modelPath;
                } else if (size > 100_000) {
                    // 可能是量化版 (1.25MB)，也接受
                    log.warn("模型文件较小 ({} bytes)，可能是量化版本，尝试加载", size);
                    return modelPath;
                } else {
                    log.warn("模型文件太小 ({} bytes)，下载不完整，请重新下载", size);
                }
            } else {
                log.warn("模型文件不存在: {}", modelPath);
            }
        } catch (IOException ignored) {}
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
