package com.imageconverter.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageService {

    /**
     * Convert image bytes to the target format.
     */
    public byte[] convertFormat(byte[] inputBytes, String targetFormat, float quality) throws IOException {
        BufferedImage input = readImage(inputBytes);
        BufferedImage processed = input;
        // For formats without alpha support (JPEG, BMP), flatten against white background
        if ("jpg".equalsIgnoreCase(targetFormat) || "jpeg".equalsIgnoreCase(targetFormat)
                || "bmp".equalsIgnoreCase(targetFormat)) {
            processed = flatten(input);
        }
        return writeImage(processed, targetFormat, quality);
    }

    /**
     * Resize image to specific width/height. If one dimension is -1, it keeps aspect ratio.
     */
    public byte[] resize(byte[] inputBytes, int targetWidth, int targetHeight, String format, float quality) throws IOException {
        BufferedImage input = readImage(inputBytes);
        int w = targetWidth, h = targetHeight;
        if (targetWidth <= 0 && targetHeight <= 0) {
            w = input.getWidth();
            h = input.getHeight();
        } else if (targetWidth <= 0) {
            double ratio = (double) targetHeight / input.getHeight();
            w = (int) (input.getWidth() * ratio);
            h = targetHeight;
        } else if (targetHeight <= 0) {
            double ratio = (double) targetWidth / input.getWidth();
            h = (int) (input.getHeight() * ratio);
            w = targetWidth;
        }
        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(input, 0, 0, w, h, null);
        g.dispose();
        return writeImage(formatSupportsAlpha(format) ? toArgb(resized) : resized, format, quality);
    }

    /**
     * Scale image by a percentage factor (e.g., 50 = 50%).
     */
    public byte[] scaleByPercent(byte[] inputBytes, double percent, String format, float quality) throws IOException {
        BufferedImage input = readImage(inputBytes);
        int w = (int) (input.getWidth() * percent / 100.0);
        int h = (int) (input.getHeight() * percent / 100.0);
        return resize(inputBytes, w, h, format, quality);
    }

    /**
     * Crop image to a specific region.
     */
    public byte[] crop(byte[] inputBytes, int x, int y, int width, int height, String format, float quality) throws IOException {
        BufferedImage input = readImage(inputBytes);
        x = Math.max(0, Math.min(x, input.getWidth() - 1));
        y = Math.max(0, Math.min(y, input.getHeight() - 1));
        width = Math.min(width, input.getWidth() - x);
        height = Math.min(height, input.getHeight() - y);
        BufferedImage cropped = input.getSubimage(x, y, width, height);
        return writeImage(formatSupportsAlpha(format) ? toArgb(cropped) : cropped, format, quality);
    }

    /**
     * Rotate image by degrees (90, 180, 270, or arbitrary).
     */
    public byte[] rotate(byte[] inputBytes, double degrees, String format, float quality) throws IOException {
        BufferedImage input = readImage(inputBytes);
        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newW = (int) Math.floor(input.getWidth() * cos + input.getHeight() * sin);
        int newH = (int) Math.floor(input.getWidth() * sin + input.getHeight() * cos);

        BufferedImage result = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform at = new AffineTransform();
        at.translate((newW - input.getWidth()) / 2.0, (newH - input.getHeight()) / 2.0);
        at.rotate(radians, input.getWidth() / 2.0, input.getHeight() / 2.0);
        g.drawRenderedImage(input, at);
        g.dispose();
        return writeImage(formatSupportsAlpha(format) ? result : flatten(result), format, quality);
    }

    /**
     * Flip image horizontally or vertically.
     */
    public byte[] flip(byte[] inputBytes, String direction, String format, float quality) throws IOException {
        BufferedImage input = readImage(inputBytes);
        BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
        AffineTransform tx;
        if ("vertical".equalsIgnoreCase(direction)) {
            tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -input.getHeight());
        } else {
            tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-input.getWidth(), 0);
        }
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        op.filter(input, output);
        return writeImage(formatSupportsAlpha(format) ? output : flatten(output), format, quality);
    }

    /**
     * Convert to grayscale.
     */
    public byte[] grayscale(byte[] inputBytes, String format, float quality) throws IOException {
        BufferedImage input = readImage(inputBytes);
        BufferedImage gray = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = gray.createGraphics();
        g.drawImage(input, 0, 0, null);
        g.dispose();
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        op.filter(gray, gray);
        return writeImage(gray, format, quality);
    }

    /**
     * Invert colors (negative effect).
     */
    public byte[] invert(byte[] inputBytes, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        RescaleOp op = new RescaleOp(-1.0f, 255f, null);
        op.filter(input, input);
        return writeImage(formatSupportsAlpha(format) ? input : flatten(input), format, quality);
    }

    /**
     * Adjust brightness and contrast.
     */
    public byte[] adjustBrightnessContrast(byte[] inputBytes, float brightness, float contrast, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        RescaleOp op = new RescaleOp(contrast, brightness, null);
        op.filter(input, input);
        return writeImage(formatSupportsAlpha(format) ? input : flatten(input), format, quality);
    }

    /**
     * Apply sepia tone effect.
     */
    public byte[] sepia(byte[] inputBytes, String format, float quality) throws IOException {
        BufferedImage input = readImage(inputBytes);
        BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                int rgb = input.getRGB(x, y);
                int a = (rgb >> 24) & 0xFF;
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int newR = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                int newG = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                int newB = (int) (0.272 * r + 0.534 * g + 0.131 * b);
                newR = Math.min(255, newR);
                newG = Math.min(255, newG);
                newB = Math.min(255, newB);
                output.setRGB(x, y, (a << 24) | (newR << 16) | (newG << 8) | newB);
            }
        }
        return writeImage(output, format, quality);
    }

    /**
     * Add a text watermark to the image.
     */
    public byte[] addTextWatermark(byte[] inputBytes, String text, String position, int fontSize, Color color, float alpha, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        Graphics2D g = input.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        g.setColor(color);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        Point p = computePosition(position, input.getWidth(), input.getHeight(), textWidth, textHeight, fm.getAscent());
        g.drawString(text, p.x, p.y);
        g.dispose();
        return writeImage(formatSupportsAlpha(format) ? input : flatten(input), format, quality);
    }

    /**
     * Add an image watermark.
     */
    public byte[] addImageWatermark(byte[] inputBytes, byte[] watermarkBytes, String position, float scale, float alpha, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        BufferedImage watermark = toArgb(readImage(watermarkBytes));
        int wmW = (int) (watermark.getWidth() * scale / 100.0);
        int wmH = (int) (watermark.getHeight() * scale / 100.0);
        BufferedImage scaledWm = new BufferedImage(wmW, wmH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D wg = scaledWm.createGraphics();
        wg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        wg.drawImage(watermark, 0, 0, wmW, wmH, null);
        wg.dispose();

        Graphics2D g = input.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        FontMetrics fm = g.getFontMetrics();
        Point p = computePosition(position, input.getWidth(), input.getHeight(), wmW, wmH, 0);
        g.drawImage(scaledWm, p.x, p.y, null);
        g.dispose();
        return writeImage(formatSupportsAlpha(format) ? input : flatten(input), format, quality);
    }

    /**
     * Add a solid border.
     */
    public byte[] addBorder(byte[] inputBytes, int borderWidth, Color color, String format, float quality) throws IOException {
        BufferedImage input = readImage(inputBytes);
        int w = input.getWidth() + borderWidth * 2;
        int h = input.getHeight() + borderWidth * 2;
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.drawImage(input, borderWidth, borderWidth, null);
        g.dispose();
        return writeImage(formatSupportsAlpha(format) ? output : flatten(output), format, quality);
    }

    /**
     * Round the corners of the image.
     */
    public byte[] roundCorners(byte[] inputBytes, int radius, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D round = new RoundRectangle2D.Float(0, 0, input.getWidth(), input.getHeight(), radius, radius);
        g.setClip(round);
        g.drawImage(input, 0, 0, null);
        g.dispose();
        return writeImage(formatSupportsAlpha(format) ? output : flatten(output), format, quality);
    }

    /**
     * Generate a thumbnail with max dimension constraint.
     */
    public byte[] createThumbnail(byte[] inputBytes, int maxSize, String format, float quality) throws IOException {
        BufferedImage input = readImage(inputBytes);
        int w = input.getWidth();
        int h = input.getHeight();
        if (w > h) {
            h = (int) (h * ((double) maxSize / w));
            w = maxSize;
        } else {
            w = (int) (w * ((double) maxSize / h));
            h = maxSize;
        }
        return resize(inputBytes, w, h, format, quality);
    }

    /**
     * Apply a Gaussian blur.
     */
    public byte[] blur(byte[] inputBytes, int radius, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        float[] kernel = createGaussianKernel(radius);
        ConvolveOp op = new ConvolveOp(new Kernel(kernel.length, 1, normalizeKernel(kernel)), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage blurred = op.filter(input, null);
        ConvolveOp op2 = new ConvolveOp(new Kernel(1, kernel.length, normalizeKernel(kernel)), ConvolveOp.EDGE_NO_OP, null);
        blurred = op2.filter(blurred, null);
        return writeImage(formatSupportsAlpha(format) ? blurred : flatten(blurred), format, quality);
    }

    /**
     * Sharpen the image.
     */
    public byte[] sharpen(byte[] inputBytes, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        float[] sharpenKernel = {
                0.0f, -1.0f, 0.0f,
                -1.0f, 5.0f, -1.0f,
                0.0f, -1.0f, 0.0f
        };
        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, sharpenKernel), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage result = op.filter(input, null);
        return writeImage(formatSupportsAlpha(format) ? result : flatten(result), format, quality);
    }

    /**
     * Convert to a specific solid-color background tone (posterize effect).
     */
    public byte[] posterize(byte[] inputBytes, int levels, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int step = 255 / (levels - 1);
        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                int rgb = input.getRGB(x, y);
                int a = (rgb >> 24) & 0xFF;
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                r = Math.min(255, (r / step) * step);
                g = Math.min(255, (g / step) * step);
                b = Math.min(255, (b / step) * step);
                output.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return writeImage(formatSupportsAlpha(format) ? output : flatten(output), format, quality);
    }

    /**
     * Apply a pixelation effect.
     */
    public byte[] pixelate(byte[] inputBytes, int pixelSize, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        int w = input.getWidth();
        int h = input.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y += pixelSize) {
            for (int x = 0; x < w; x += pixelSize) {
                int avgColor = getAverageColor(input, x, y, pixelSize, pixelSize);
                for (int dy = 0; dy < pixelSize && y + dy < h; dy++) {
                    for (int dx = 0; dx < pixelSize && x + dx < w; dx++) {
                        output.setRGB(x + dx, y + dy, avgColor);
                    }
                }
            }
        }
        return writeImage(formatSupportsAlpha(format) ? output : flatten(output), format, quality);
    }

    /**
     * Add vignette (dark corners).
     */
    public byte[] vignette(byte[] inputBytes, float strength, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        int w = input.getWidth();
        int h = input.getHeight();
        Graphics2D g = input.createGraphics();
        Point2D center = new Point2D.Double(w / 2.0, h / 2.0);
        float radius = (float) Math.max(w, h) / 2.0f;
        float[] dist = {0.0f, 0.7f, 1.0f};
        Color[] colors = {new Color(0, 0, 0, 0), new Color(0, 0, 0, (int) (strength * 100)), new Color(0, 0, 0, (int) (strength * 255))};
        RadialGradientPaint paint = new RadialGradientPaint(center, radius, dist, colors);
        g.setPaint(paint);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, strength));
        g.fillRect(0, 0, w, h);
        g.dispose();
        return writeImage(formatSupportsAlpha(format) ? input : flatten(input), format, quality);
    }

    /**
     * Adjust hue/saturation/brightness (HSB shift).
     */
    public byte[] adjustHsb(byte[] inputBytes, float hueShift, float satShift, float brightShift, String format, float quality) throws IOException {
        BufferedImage input = toArgb(readImage(inputBytes));
        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                int rgb = input.getRGB(x, y);
                int a = (rgb >> 24) & 0xFF;
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                float[] hsb = Color.RGBtoHSB(r, g, b, null);
                hsb[0] = (hsb[0] + hueShift) % 1.0f;
                if (hsb[0] < 0) hsb[0] += 1.0f;
                hsb[1] = Math.max(0, Math.min(1, hsb[1] + satShift));
                hsb[2] = Math.max(0, Math.min(1, hsb[2] + brightShift));
                int newRgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
                input.setRGB(x, y, (a << 24) | (newRgb & 0x00FFFFFF));
            }
        }
        return writeImage(formatSupportsAlpha(format) ? input : flatten(input), format, quality);
    }

    /**
     * Combine multiple operations: input passes through a pipeline.
     * Operations map keys -> params (caller-defined).
     */
    public Map<String, Object> getImageInfo(byte[] inputBytes) throws IOException {
        BufferedImage input = readImage(inputBytes);
        Map<String, Object> info = new HashMap<>();
        info.put("width", input.getWidth());
        info.put("height", input.getHeight());
        info.put("colorComponents", input.getColorModel().getNumComponents());
        info.put("hasAlpha", input.getColorModel().hasAlpha());
        info.put("fileSize", inputBytes.length);
        return info;
    }

    /**
     * Generate a QR-code-like ascii art preview (text output).
     * Not strictly image conversion but useful; returning ASCII preview as text.
     */
    public String toAsciiArt(byte[] inputBytes, int width) throws IOException {
        BufferedImage input = readImage(inputBytes);
        int imgW = input.getWidth();
        int imgH = input.getHeight();
        int newH = (int) (imgH * (width * 0.5) / imgW);
        BufferedImage small = new BufferedImage(width, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = small.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(input, 0, 0, width, newH, null);
        g.dispose();
        String chars = "@%#*+=-:. ";
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < newH; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = small.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int gC = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int avg = (r + gC + b) / 3;
                int idx = (avg * (chars.length() - 1)) / 255;
                sb.append(chars.charAt(idx));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Convert image to Base64 string.
     */
    public String toBase64(byte[] inputBytes, String format, float quality) throws IOException {
        byte[] out = convertFormat(inputBytes, format, quality);
        return java.util.Base64.getEncoder().encodeToString(out);
    }

    // ===== Helper methods =====

    public BufferedImage readImage(byte[] bytes) throws IOException {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                throw new IOException("Unsupported image format or corrupted data");
            }
            return img;
        }
    }

    private byte[] writeImage(BufferedImage image, String format, float quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String fmt = format.toLowerCase();
        boolean useWriter = false;
        if (fmt.equals("jpg")) fmt = "jpeg";
        // JPEG/BMP quality setting requires ImageWriter
        if ((fmt.equals("jpeg") || fmt.equals("webp")) && quality >= 0 && quality <= 1.0f) {
            useWriter = true;
        }
        if (useWriter) {
            javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName(fmt).next();
            javax.imageio.ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
            }
            try (javax.imageio.stream.ImageOutputStream output = ImageIO.createImageOutputStream(baos)) {
                writer.setOutput(output);
                writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
                writer.dispose();
            }
        } else {
            if (!ImageIO.write(image, fmt, baos)) {
                throw new IOException("No writer available for format: " + fmt);
            }
        }
        return baos.toByteArray();
    }

    private BufferedImage toArgb(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_ARGB) return src;
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }

    private BufferedImage flatten(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, src.getWidth(), src.getHeight());
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }

    private boolean formatSupportsAlpha(String format) {
        if (format == null) return false;
        String f = format.toLowerCase();
        return f.equals("png") || f.equals("gif") || f.equals("tiff") || f.equals("webp");
    }

    private Point computePosition(String position, int imgW, int imgH, int objW, int objH, int yOffset) {
        int margin = 10;
        Point p = new Point();
        if (position == null) position = "center";
        switch (position.toLowerCase()) {
            case "top-left":
                p.x = margin;
                p.y = margin + yOffset;
                break;
            case "top-right":
                p.x = imgW - objW - margin;
                p.y = margin + yOffset;
                break;
            case "bottom-left":
                p.x = margin;
                p.y = imgH - objH - margin;
                break;
            case "bottom-right":
                p.x = imgW - objW - margin;
                p.y = imgH - objH - margin;
                break;
            case "center":
            default:
                p.x = (imgW - objW) / 2;
                p.y = (imgH - objH) / 2 + yOffset;
                break;
        }
        return p;
    }

    private int getAverageColor(BufferedImage img, int x, int y, int w, int h) {
        long rSum = 0, gSum = 0, bSum = 0, aSum = 0, count = 0;
        for (int dy = y; dy < y + h && dy < img.getHeight(); dy++) {
            for (int dx = x; dx < x + w && dx < img.getWidth(); dx++) {
                int rgb = img.getRGB(dx, dy);
                aSum += (rgb >> 24) & 0xFF;
                rSum += (rgb >> 16) & 0xFF;
                gSum += (rgb >> 8) & 0xFF;
                bSum += rgb & 0xFF;
                count++;
            }
        }
        if (count == 0) count = 1;
        int a = (int) (aSum / count);
        int r = (int) (rSum / count);
        int g = (int) (gSum / count);
        int b = (int) (bSum / count);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private float[] createGaussianKernel(int radius) {
        int size = radius * 2 + 1;
        float[] kernel = new float[size];
        float sigma = radius / 3.0f;
        float sum = 0;
        for (int i = 0; i < size; i++) {
            int x = i - radius;
            kernel[i] = (float) Math.exp(-(x * x) / (2 * sigma * sigma));
            sum += kernel[i];
        }
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }
        return kernel;
    }

    private float[] normalizeKernel(float[] kernel) {
        float sum = 0;
        for (float v : kernel) sum += v;
        if (sum == 0) return kernel;
        for (int i = 0; i < kernel.length; i++) kernel[i] /= sum;
        return kernel;
    }
}
