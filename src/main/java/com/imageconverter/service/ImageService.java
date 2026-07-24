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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

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

    // ===== Extended operations =====

    /** Threshold / binarize: pixels above threshold become white, below become black. */
    public byte[] threshold(byte[] inputBytes, int threshold, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                int avg = (r + g + b) / 3;
                out.setRGB(x, y, avg >= threshold ? 0xFFFFFF : 0x000000);
            }
        }
        return writeImage(out, format, quality);
    }

    /** Gamma correction. */
    public byte[] gamma(byte[] inputBytes, double gamma, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] table = new int[256];
        for (int i = 0; i < 256; i++) {
            table[i] = (int) Math.min(255, Math.max(0, Math.pow(i / 255.0, 1.0 / gamma) * 255));
        }
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = table[(rgb >> 16) & 0xFF];
                int g = table[(rgb >> 8) & 0xFF];
                int b = table[rgb & 0xFF];
                out.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return writeImage(out, format, quality);
    }

    /** Color balance: shift R/G/B channels. */
    public byte[] colorBalance(byte[] inputBytes, int red, int green, int blue, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = Math.min(255, Math.max(0, ((rgb >> 16) & 0xFF) + red));
                int g = Math.min(255, Math.max(0, ((rgb >> 8) & 0xFF) + green));
                int b = Math.min(255, Math.max(0, (rgb & 0xFF) + blue));
                out.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return writeImage(out, format, quality);
    }

    /** Extract a single R/G/B channel. */
    public byte[] channelExtract(byte[] inputBytes, String channel, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                int v;
                switch (channel.toLowerCase()) {
                    case "red": v = r; out.setRGB(x, y, (v << 16)); break;
                    case "green": v = g; out.setRGB(x, y, (v << 8)); break;
                    case "blue": v = b; out.setRGB(x, y, v); break;
                    default: v = (r + g + b) / 3; out.setRGB(x, y, (v << 16) | (v << 8) | v);
                }
            }
        }
        return writeImage(out, format, quality);
    }

    /** Duotone: map image to two-color gradient. */
    public byte[] duotone(byte[] inputBytes, String shadowHex, String highlightHex, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Color shadow = Color.decode("#" + shadowHex);
        Color highlight = Color.decode("#" + highlightHex);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int avg = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
                float t = avg / 255f;
                int r = (int) (shadow.getRed() + (highlight.getRed() - shadow.getRed()) * t);
                int g = (int) (shadow.getGreen() + (highlight.getGreen() - shadow.getGreen()) * t);
                int b = (int) (shadow.getBlue() + (highlight.getBlue() - shadow.getBlue()) * t);
                out.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return writeImage(out, format, quality);
    }

    /** Edge detection using Sobel operator. */
    public byte[] edgeDetect(byte[] inputBytes, float intensity, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int w = src.getWidth(), h = src.getHeight();
        int[][] gray = new int[w][h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = src.getRGB(x, y);
                gray[x][y] = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
            }
        }
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int[][] gx = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][] gy = {{-1,-2,-1},{0,0,0},{1,2,1}};
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                int sx = 0, sy = 0;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        sx += gray[x + i][y + j] * gx[i + 1][j + 1];
                        sy += gray[x + i][y + j] * gy[i + 1][j + 1];
                    }
                }
                int mag = (int) Math.min(255, Math.sqrt(sx * sx + sy * sy) * intensity);
                out.setRGB(x, y, (mag << 16) | (mag << 8) | mag);
            }
        }
        return writeImage(out, format, quality);
    }

    /** Emboss effect. */
    public byte[] emboss(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int w = src.getWidth(), h = src.getHeight();
        int[][] gray = new int[w][h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = src.getRGB(x, y);
                gray[x][y] = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
            }
        }
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int[][] kernel = {{-2,-1,0},{-1,1,1},{0,1,2}};
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                int val = 0;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        val += gray[x + i][y + j] * kernel[i + 1][j + 1];
                    }
                }
                val = (int) (val * amount + 128);
                val = Math.min(255, Math.max(0, val));
                out.setRGB(x, y, (val << 16) | (val << 8) | val);
            }
        }
        return writeImage(out, format, quality);
    }

    /** Sketch effect: grayscale + inverted blur blend. */
    public byte[] sketch(byte[] inputBytes, float intensity, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage gray = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = src.getRGB(x, y);
                int avg = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
                gray.setRGB(x, y, (avg << 16) | (avg << 8) | avg);
            }
        }
        BufferedImage inv = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = gray.getRGB(x, y);
                int avg = 255 - (rgb & 0xFF);
                inv.setRGB(x, y, (avg << 16) | (avg << 8) | avg);
            }
        }
        BufferedImage blurred = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = blurred.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(inv, 0, 0, w / 2, h / 2, null);
        g.drawImage(inv, 0, 0, w, h, null);
        g.dispose();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int base = gray.getRGB(x, y) & 0xFF;
                int blur = blurred.getRGB(x, y) & 0xFF;
                int result = blur == 0 ? 255 : Math.min(255, (base * 255) / blur);
                result = (int) (result * intensity + (255 - result) * (1 - intensity) * 0.3);
                result = Math.min(255, Math.max(0, result));
                out.setRGB(x, y, (result << 16) | (result << 8) | result);
            }
        }
        return writeImage(out, format, quality);
    }

    /** Add random noise. */
    public byte[] noise(byte[] inputBytes, int amount, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        java.util.Random rnd = new java.util.Random();
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                int n = (int) (rnd.nextGaussian() * amount);
                r = Math.min(255, Math.max(0, r + n));
                g = Math.min(255, Math.max(0, g + n));
                b = Math.min(255, Math.max(0, b + n));
                out.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return writeImage(out, format, quality);
    }

    /** Crop to circle (with transparent background for PNG). */
    public byte[] cropCircle(byte[] inputBytes, int padding, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int size = Math.min(src.getWidth(), src.getHeight()) - padding * 2;
        int sx = (src.getWidth() - size) / 2;
        int sy = (src.getHeight() - size) / 2;
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(new java.awt.geom.Ellipse2D.Double(0, 0, size, size));
        g.drawImage(src, 0, 0, size, size, sx, sy, sx + size, sy + size, null);
        g.dispose();
        if (!formatSupportsAlpha(format)) out = flatten(out);
        return writeImage(out, format, quality);
    }

    // ===== Helper methods =====

    /** Functional helper for per-pixel RGB transforms. */
    private BufferedImage pixelTransform(BufferedImage src, java.util.function.IntUnaryOperator fn) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int a = (rgb >> 24) & 0xFF;
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int nv = fn.applyAsInt((r << 16) | (g << 8) | b);
                int nr = clamp255((nv >> 16) & 0xFF);
                int ng = clamp255((nv >> 8) & 0xFF);
                int nb = clamp255(nv & 0xFF);
                out.setRGB(x, y, (a << 24) | (nr << 16) | (ng << 8) | nb);
            }
        }
        return out;
    }

    private static int clamp255(int v) { return v < 0 ? 0 : (v > 255 ? 255 : v); }
    private static int clamp(int v, int lo, int hi) { return v < lo ? lo : (v > hi ? hi : v); }
    private static float clampf(float v, float lo, float hi) { return v < lo ? lo : (v > hi ? hi : v); }

    private byte[] writeOut(BufferedImage img, String format, float quality) throws IOException {
        return writeImage(formatSupportsAlpha(format) ? img : flatten(img), format, quality);
    }

    private BufferedImage toRgb(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_RGB) return src;
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }

    /** Generic 3x3 convolution on grayscale-style RGB. */
    private BufferedImage convolve3x3(BufferedImage src, float[] kernel, float bias) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float r = 0, g = 0, b = 0;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        int sx = clamp(x + i, 0, w - 1);
                        int sy = clamp(y + j, 0, h - 1);
                        int rgb = src.getRGB(sx, sy);
                        float k = kernel[(j + 1) * 3 + (i + 1)];
                        r += ((rgb >> 16) & 0xFF) * k;
                        g += ((rgb >> 8) & 0xFF) * k;
                        b += (rgb & 0xFF) * k;
                    }
                }
                int a = (src.getRGB(x, y) >> 24) & 0xFF;
                int nr = clamp255((int) (r + bias));
                int ng = clamp255((int) (g + bias));
                int nb = clamp255((int) (b + bias));
                out.setRGB(x, y, (a << 24) | (nr << 16) | (ng << 8) | nb);
            }
        }
        return out;
    }

    // ==================== NEW FEATURES (70+) ====================

    /** Fit (contain) inside a box keeping aspect ratio, padding transparent. */
    public byte[] fit(byte[] inputBytes, int w, int h, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int sw = src.getWidth(), sh = src.getHeight();
        double r = Math.min((double) w / sw, (double) h / sh);
        int nw = (int) (sw * r), nh = (int) (sh * r);
        BufferedImage scaled = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.drawImage(scaled, (w - nw) / 2, (h - nh) / 2, null);
        g2.dispose();
        return writeOut(out, format, quality);
    }

    /** Cover a box (fill, crop overflow) keeping aspect ratio. */
    public byte[] cover(byte[] inputBytes, int w, int h, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int sw = src.getWidth(), sh = src.getHeight();
        double r = Math.max((double) w / sw, (double) h / sh);
        int nw = (int) Math.round(sw * r), nh = (int) Math.round(sh * r);
        BufferedImage scaled = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();
        int x = (nw - w) / 2, y = (nh - h) / 2;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.drawImage(scaled, 0, 0, w, h, x, y, x + w, y + h, null);
        g2.dispose();
        return writeOut(out, format, quality);
    }

    /** Pad (add uniform padding with a color). */
    public byte[] pad(byte[] inputBytes, int padding, String colorHex, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        Color color = Color.decode("#" + colorHex);
        int w = src.getWidth() + padding * 2, h = src.getHeight() + padding * 2;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.drawImage(src, padding, padding, null);
        g.dispose();
        return writeOut(out, format, quality);
    }

    /** Trim uniform-colored borders. */
    public byte[] trim(byte[] inputBytes, int tolerance, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int w = src.getWidth(), h = src.getHeight();
        int bg = src.getRGB(0, 0);
        int br = (bg >> 16) & 0xFF, bgc = (bg >> 8) & 0xFF, bb = bg & 0xFF;
        int x0 = 0, y0 = 0, x1 = w, y1 = h;
        boolean done;
        done = false;
        for (int x = 0; x < w && !done; x++) {
            for (int y = 0; y < h; y++) {
                int rgb = src.getRGB(x, y);
                if (Math.abs(((rgb >> 16) & 0xFF) - br) > tolerance ||
                    Math.abs(((rgb >> 8) & 0xFF) - bgc) > tolerance ||
                    Math.abs((rgb & 0xFF) - bb) > tolerance) { x0 = x; done = true; break; }
            }
        }
        done = false;
        for (int x = w - 1; x > x0 && !done; x--) {
            for (int y = 0; y < h; y++) {
                int rgb = src.getRGB(x, y);
                if (Math.abs(((rgb >> 16) & 0xFF) - br) > tolerance ||
                    Math.abs(((rgb >> 8) & 0xFF) - bgc) > tolerance ||
                    Math.abs((rgb & 0xFF) - bb) > tolerance) { x1 = x + 1; done = true; break; }
            }
        }
        done = false;
        for (int y = 0; y < h && !done; y++) {
            for (int x = x0; x < x1; x++) {
                int rgb = src.getRGB(x, y);
                if (Math.abs(((rgb >> 16) & 0xFF) - br) > tolerance ||
                    Math.abs(((rgb >> 8) & 0xFF) - bgc) > tolerance ||
                    Math.abs((rgb & 0xFF) - bb) > tolerance) { y0 = y; done = true; break; }
            }
        }
        done = false;
        for (int y = h - 1; y > y0 && !done; y--) {
            for (int x = x0; x < x1; x++) {
                int rgb = src.getRGB(x, y);
                if (Math.abs(((rgb >> 16) & 0xFF) - br) > tolerance ||
                    Math.abs(((rgb >> 8) & 0xFF) - bgc) > tolerance ||
                    Math.abs((rgb & 0xFF) - bb) > tolerance) { y1 = y + 1; done = true; break; }
            }
        }
        int nw = Math.max(1, x1 - x0), nh = Math.max(1, y1 - y0);
        return crop(inputBytes, x0, y0, nw, nh, format, quality);
    }

    /** Transpose (rotate 90 CW). */
    public byte[] transpose(byte[] inputBytes, String format, float quality) throws IOException {
        return rotate(inputBytes, 90, format, quality);
    }

    /** Transverse (rotate 90 CCW). */
    public byte[] transverse(byte[] inputBytes, String format, float quality) throws IOException {
        return rotate(inputBytes, -90, format, quality);
    }

    /** Canvas resize: enlarge canvas keeping image centered, fill with color. */
    public byte[] canvasResize(byte[] inputBytes, int w, int h, String colorHex, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        Color color = Color.decode("#" + colorHex);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.drawImage(src, (w - src.getWidth()) / 2, (h - src.getHeight()) / 2, null);
        g.dispose();
        return writeOut(out, format, quality);
    }

    /** Split horizontally into 2 halves; output index (0=left,1=right). */
    public byte[] splitH(byte[] inputBytes, int index, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int w = src.getWidth() / 2;
        int x = index == 0 ? 0 : w;
        return crop(inputBytes, x, 0, w, src.getHeight(), format, quality);
    }

    /** Split vertically into 2 halves; output index (0=top,1=bottom). */
    public byte[] splitV(byte[] inputBytes, int index, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int h = src.getHeight() / 2;
        int y = index == 0 ? 0 : h;
        return crop(inputBytes, 0, y, src.getWidth(), h, format, quality);
    }

    /** Split into a grid (rows x cols); output the cell at (row,col). */
    public byte[] splitGrid(byte[] inputBytes, int rows, int cols, int row, int col, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int cw = src.getWidth() / cols, ch = src.getHeight() / rows;
        return crop(inputBytes, col * cw, row * ch, cw, ch, format, quality);
    }

    /** Tile the image to fill a target canvas. */
    public byte[] tile(byte[] inputBytes, int w, int h, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        for (int y = 0; y < h; y += src.getHeight()) {
            for (int x = 0; x < w; x += src.getWidth()) {
                g.drawImage(src, x, y, null);
            }
        }
        g.dispose();
        return writeOut(out, format, quality);
    }

    /** Strip alpha (compose onto white). */
    public byte[] stripAlpha(byte[] inputBytes, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        return writeImage(flatten(src), format, quality);
    }

    /** Flatten onto a custom background color. */
    public byte[] flattenBg(byte[] inputBytes, String colorHex, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        Color color = Color.decode("#" + colorHex);
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, src.getWidth(), src.getHeight());
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return writeOut(out, format, quality);
    }

    // ---- Filters ----

    public byte[] cool(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            r = clamp255((int) (r - 30 * amount));
            b = clamp255((int) (b + 30 * amount));
            return (r << 16) | (g << 8) | b;
        });
        return writeOut(out, format, quality);
    }

    public byte[] warm(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            r = clamp255((int) (r + 30 * amount));
            b = clamp255((int) (b - 30 * amount));
            g = clamp255((int) (g + 5 * amount));
            return (r << 16) | (g << 8) | b;
        });
        return writeOut(out, format, quality);
    }

    public byte[] vintage(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int nr = clamp255((int) (r * 0.9 + 40 + g * 0.1));
            int ng = clamp255((int) (g * 0.85 + 25));
            int nb = clamp255((int) (b * 0.65 + 10));
            nr = (int) (nr * amount + r * (1 - amount));
            ng = (int) (ng * amount + g * (1 - amount));
            nb = (int) (nb * amount + b * (1 - amount));
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] lomo(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage v = toArgb(readImage(inputBytes));
        BufferedImage sat = pixelTransform(v, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            float[] hsb = Color.RGBtoHSB(r, g, b, null);
            hsb[1] = clampf(hsb[1] * (1 + 0.4f * amount), 0, 1);
            int c = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
            return c & 0xFFFFFF;
        });
        return vignette(toBytesForVignette(sat), amount * 0.8f, format, quality);
    }

    private byte[] toBytesForVignette(BufferedImage img) {
        try { return writeImage(img, "png", 0.9f); } catch (IOException e) { return new byte[0]; }
    }

    public byte[] cinematic(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int nr = clamp255((int) (r * 1.05 + 10));
            int ng = clamp255((int) (g * 0.92));
            int nb = clamp255((int) (b * 0.85 + 15));
            nr = (int) (nr * amount + r * (1 - amount));
            ng = (int) (ng * amount + g * (1 - amount));
            nb = (int) (nb * amount + b * (1 - amount));
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] fade(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            r = clamp255((int) (r + (255 - r) * 0.3 * amount));
            g = clamp255((int) (g + (255 - g) * 0.3 * amount));
            b = clamp255((int) (b + (255 - b) * 0.3 * amount));
            return (r << 16) | (g << 8) | b;
        });
        return writeOut(out, format, quality);
    }

    public byte[] pastel(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            float[] hsb = Color.RGBtoHSB(r, g, b, null);
            hsb[1] = clampf(hsb[1] * (1 - 0.4f * amount), 0, 1);
            hsb[2] = clampf(hsb[2] + 0.1f * amount, 0, 1);
            int c = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
            return c & 0xFFFFFF;
        });
        return writeOut(out, format, quality);
    }

    public byte[] neon(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage edges = toArgb(readImage(edgeDetect(writeImage(src, "png", 0.9f), 1.2f, "png", 0.9f)));
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int e = edges.getRGB(x, y) & 0xFF;
                int a = clamp255((int) (e * amount * 2));
                int r = clamp255((int) (e * 1.0));
                int g = clamp255((int) (e * 0.2));
                int b = clamp255((int) (e * 1.0));
                out.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return writeOut(out, format, quality);
    }

    public byte[] heat(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int v = (r + g + b) / 3;
            int nr = clamp255((int) (v * 2.0 - 100));
            int ng = clamp255((int) (v * 2.0 - 200));
            int nb = clamp255((int) (255 - v * 2.0));
            nr = (int) (nr * amount + r * (1 - amount));
            ng = (int) (ng * amount + g * (1 - amount));
            nb = (int) (nb * amount + b * (1 - amount));
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] nightVision(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int v = (int) (((r + g + b) / 3) * 1.6);
            int ng = clamp255(v);
            int nr = (int) (ng * 0.2 * amount);
            int nb = (int) (ng * 0.2 * amount);
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] infrared(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int v = (int) (r * 0.5 + g * 1.4 + b * 0.1);
            v = clamp255(v);
            int nr = clamp255(v);
            int ng = clamp255((int) (v * 0.3));
            int nb = clamp255((int) (v * 0.6));
            nr = (int) (nr * amount + r * (1 - amount));
            ng = (int) (ng * amount + g * (1 - amount));
            nb = (int) (nb * amount + b * (1 - amount));
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] polaroid(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int nr = clamp255((int) (r * 1.05 + 15));
            int ng = clamp255((int) (g * 0.95 + 5));
            int nb = clamp255((int) (b * 0.8 - 5));
            nr = (int) (nr * amount + r * (1 - amount));
            ng = (int) (ng * amount + g * (1 - amount));
            nb = (int) (nb * amount + b * (1 - amount));
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] bleachBypass(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage gray = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++)
            for (int x = 0; x < src.getWidth(); x++) {
                int rgb = src.getRGB(x, y);
                int a = (rgb >> 24) & 0xFF;
                int v = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
                gray.setRGB(x, y, (a << 24) | (v << 16) | (v << 8) | v);
            }
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++)
            for (int x = 0; x < src.getWidth(); x++) {
                int c1 = src.getRGB(x, y), c2 = gray.getRGB(x, y);
                int a = (c1 >> 24) & 0xFF;
                int r = clamp255((int) ((((c1 >> 16) & 0xFF) * (1 - amount * 0.5) + ((c2 >> 16) & 0xFF) * amount * 0.5)));
                int g = clamp255((int) ((((c1 >> 8) & 0xFF) * (1 - amount * 0.5) + ((c2 >> 8) & 0xFF) * amount * 0.5)));
                int b = clamp255((int) (((c1 & 0xFF) * (1 - amount * 0.5) + (c2 & 0xFF) * amount * 0.5)));
                out.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        return writeOut(out, format, quality);
    }

    public byte[] crossProcess(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int nr = clamp255((int) (r * 1.1 + 5));
            int ng = clamp255((int) (g * 0.95));
            int nb = clamp255((int) (b * 1.15 + 10));
            nr = (int) (nr * amount + r * (1 - amount));
            ng = (int) (ng * amount + g * (1 - amount));
            nb = (int) (nb * amount + b * (1 - amount));
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] cyanotype(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int v = (r + g + b) / 3;
            int nr = clamp255((int) (v * 0.1));
            int ng = clamp255((int) (v * 0.4));
            int nb = clamp255((int) (v * 0.9 + 30));
            nr = (int) (nr * amount + r * (1 - amount));
            ng = (int) (ng * amount + g * (1 - amount));
            nb = (int) (nb * amount + b * (1 - amount));
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] silver(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int v = (int) (r * 0.3 + g * 0.59 + b * 0.11);
            v = clamp255((int) (v * 1.1 - 10));
            int nr = (int) (v * amount + r * (1 - amount));
            int ng = (int) (v * amount + g * (1 - amount));
            int nb = (int) (v * amount + b * (1 - amount));
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] solarize(byte[] inputBytes, int threshold, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            r = r < threshold ? 255 - r : r;
            g = g < threshold ? 255 - g : g;
            b = b < threshold ? 255 - b : b;
            return (r << 16) | (g << 8) | b;
        });
        return writeOut(out, format, quality);
    }

    public byte[] equalize(byte[] inputBytes, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int[] histR = new int[256], histG = new int[256], histB = new int[256];
        int w = src.getWidth(), h = src.getHeight(), n = w * h;
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int rgb = src.getRGB(x, y);
            histR[(rgb >> 16) & 0xFF]++; histG[(rgb >> 8) & 0xFF]++; histB[rgb & 0xFF]++;
        }
        int[] cdfR = new int[256], cdfG = new int[256], cdfB = new int[256];
        int sR = 0, sG = 0, sB = 0;
        int minR = 0, minG = 0, minB = 0;
        for (int i = 0; i < 256; i++) {
            sR += histR[i]; cdfR[i] = sR; if (minR == 0 && sR > 0) minR = sR;
            sG += histG[i]; cdfG[i] = sG; if (minG == 0 && sG > 0) minG = sG;
            sB += histB[i]; cdfB[i] = sB; if (minB == 0 && sB > 0) minB = sB;
        }
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int rgb = src.getRGB(x, y);
            int a = (rgb >> 24) & 0xFF;
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int nr = (int) (((double) (cdfR[r] - minR) / (n - minR)) * 255);
            int ng = (int) (((double) (cdfG[g] - minG) / (n - minG)) * 255);
            int nb = (int) (((double) (cdfB[b] - minB) / (n - minB)) * 255);
            out.setRGB(x, y, (a << 24) | (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb));
        }
        return writeOut(out, format, quality);
    }

    public byte[] autoContrast(byte[] inputBytes, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int minV = 255, maxV = 0;
        int w = src.getWidth(), h = src.getHeight();
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int rgb = src.getRGB(x, y);
            int v = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
            if (v < minV) minV = v; if (v > maxV) maxV = v;
        }
        if (maxV <= minV) maxV = minV + 1;
        final int lo = minV;
        final double scale = 255.0 / (maxV - minV);
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            r = clamp255((int) ((r - lo) * scale));
            g = clamp255((int) ((g - lo) * scale));
            b = clamp255((int) ((b - lo) * scale));
            return (r << 16) | (g << 8) | b;
        });
        return writeOut(out, format, quality);
    }

    public byte[] swapChannels(byte[] inputBytes, String mode, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            switch (mode.toLowerCase()) {
                case "rg": return (g << 16) | (r << 8) | b;
                case "rb": return (b << 16) | (g << 8) | r;
                case "gb": return (r << 16) | (b << 8) | g;
                default: return rgb;
            }
        });
        return writeOut(out, format, quality);
    }

    public byte[] channelMix(byte[] inputBytes, int rr, int gg, int bb, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int nr = clamp255((r * rr + g * gg + b * bb) / 100);
            int ng = clamp255((r * (100 - rr - bb) + g * rr + b * bb) / 100);
            int nb = clamp255((r * bb + g * gg + b * (100 - rr - gg)) / 100);
            return (nr << 16) | (ng << 8) | nb;
        });
        return writeOut(out, format, quality);
    }

    public byte[] colorize(byte[] inputBytes, String colorHex, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        Color tint = Color.decode("#" + colorHex);
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int v = (r + g + b) / 3;
            int nr = (int) (tint.getRed() * (v / 255.0) * amount + r * (1 - amount));
            int ng = (int) (tint.getGreen() * (v / 255.0) * amount + g * (1 - amount));
            int nb = (int) (tint.getBlue() * (v / 255.0) * amount + b * (1 - amount));
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] tint(byte[] inputBytes, String colorHex, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        Color tint = Color.decode("#" + colorHex);
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int nr = (int) (r * (1 - amount) + tint.getRed() * amount);
            int ng = (int) (g * (1 - amount) + tint.getGreen() * amount);
            int nb = (int) (b * (1 - amount) + tint.getBlue() * amount);
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] gradientMap(byte[] inputBytes, String c1Hex, String c2Hex, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        Color c1 = Color.decode("#" + c1Hex), c2 = Color.decode("#" + c2Hex);
        BufferedImage out = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            float t = (r + g + b) / (3f * 255f);
            int nr = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t);
            int ng = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
            int nb = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
            return (clamp255(nr) << 16) | (clamp255(ng) << 8) | clamp255(nb);
        });
        return writeOut(out, format, quality);
    }

    public byte[] dither(byte[] inputBytes, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int[][] buf = new int[w][h];
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int rgb = src.getRGB(x, y);
            buf[x][y] = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
        }
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int oldp = buf[x][y];
            int newp = oldp < 128 ? 0 : 255;
            buf[x][y] = newp;
            int err = oldp - newp;
            if (x + 1 < w) buf[x + 1][y] += err * 7 / 16;
            if (x - 1 >= 0 && y + 1 < h) buf[x - 1][y + 1] += err * 3 / 16;
            if (y + 1 < h) buf[x][y + 1] += err * 5 / 16;
            if (x + 1 < w && y + 1 < h) buf[x + 1][y + 1] += err * 1 / 16;
        }
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int v = clamp255(buf[x][y]);
            out.setRGB(x, y, (v << 16) | (v << 8) | v);
        }
        return writeImage(out, format, quality);
    }

    public byte[] halftone(byte[] inputBytes, int dotSize, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        for (int y = 0; y < h; y += dotSize) {
            for (int x = 0; x < w; x += dotSize) {
                long sum = 0, cnt = 0;
                for (int dy = 0; dy < dotSize && y + dy < h; dy++)
                    for (int dx = 0; dx < dotSize && x + dx < w; dx++) {
                        int rgb = src.getRGB(x + dx, y + dy);
                        sum += (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
                        cnt++;
                    }
                int avg = cnt > 0 ? (int) (sum / cnt) : 0;
                int r = (int) ((1 - avg / 255.0) * dotSize / 2.0);
                if (r > 0) g.fillOval(x + dotSize / 2 - r, y + dotSize / 2 - r, r * 2, r * 2);
            }
        }
        g.dispose();
        return writeImage(out, format, quality);
    }

    // ---- Effects ----

    public byte[] motionBlur(byte[] inputBytes, int radius, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            long r = 0, g = 0, b = 0, a = 0, cnt = 0;
            for (int i = -radius; i <= radius; i++) {
                int sx = clamp(x + i, 0, w - 1);
                int rgb = src.getRGB(sx, y);
                a += (rgb >> 24) & 0xFF; r += (rgb >> 16) & 0xFF; g += (rgb >> 8) & 0xFF; b += rgb & 0xFF; cnt++;
            }
            out.setRGB(x, y, ((int) (a / cnt) << 24) | ((int) (r / cnt) << 16) | ((int) (g / cnt) << 8) | (int) (b / cnt));
        }
        return writeOut(out, format, quality);
    }

    public byte[] boxBlur(byte[] inputBytes, int radius, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            long r = 0, g = 0, b = 0, a = 0, cnt = 0;
            for (int j = -radius; j <= radius; j++) for (int i = -radius; i <= radius; i++) {
                int sx = clamp(x + i, 0, w - 1), sy = clamp(y + j, 0, h - 1);
                int rgb = src.getRGB(sx, sy);
                a += (rgb >> 24) & 0xFF; r += (rgb >> 16) & 0xFF; g += (rgb >> 8) & 0xFF; b += rgb & 0xFF; cnt++;
            }
            out.setRGB(x, y, ((int) (a / cnt) << 24) | ((int) (r / cnt) << 16) | ((int) (g / cnt) << 8) | (int) (b / cnt));
        }
        return writeOut(out, format, quality);
    }

    public byte[] radialBlur(byte[] inputBytes, int radius, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int cx = w / 2, cy = h / 2;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            long r = 0, g = 0, b = 0, a = 0, cnt = 0;
            double dx = x - cx, dy = y - cy;
            double dist = Math.sqrt(dx * dx + dy * dy);
            double ang = Math.atan2(dy, dx);
            for (int i = 1; i <= radius; i += 2) {
                double a2 = ang + (i / (double) radius) * 0.3;
                int sx = clamp((int) (cx + Math.cos(a2) * dist), 0, w - 1);
                int sy = clamp((int) (cy + Math.sin(a2) * dist), 0, h - 1);
                int rgb = src.getRGB(sx, sy);
                a += (rgb >> 24) & 0xFF; r += (rgb >> 16) & 0xFF; g += (rgb >> 8) & 0xFF; b += rgb & 0xFF; cnt++;
            }
            if (cnt == 0) { out.setRGB(x, y, src.getRGB(x, y)); continue; }
            out.setRGB(x, y, ((int) (a / cnt) << 24) | ((int) (r / cnt) << 16) | ((int) (g / cnt) << 8) | (int) (b / cnt));
        }
        return writeOut(out, format, quality);
    }

    public byte[] zoomBlur(byte[] inputBytes, int radius, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int cx = w / 2, cy = h / 2;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            long r = 0, g = 0, b = 0, a = 0, cnt = 0;
            double dx = x - cx, dy = y - cy;
            for (int i = 0; i <= radius; i++) {
                double t = i / (double) Math.max(1, radius);
                int sx = clamp((int) (cx + dx * t), 0, w - 1);
                int sy = clamp((int) (cy + dy * t), 0, h - 1);
                int rgb = src.getRGB(sx, sy);
                a += (rgb >> 24) & 0xFF; r += (rgb >> 16) & 0xFF; g += (rgb >> 8) & 0xFF; b += rgb & 0xFF; cnt++;
            }
            out.setRGB(x, y, ((int) (a / cnt) << 24) | ((int) (r / cnt) << 16) | ((int) (g / cnt) << 8) | (int) (b / cnt));
        }
        return writeOut(out, format, quality);
    }

    public byte[] unsharpMask(byte[] inputBytes, int radius, float amount, String format, float quality) throws IOException {
        byte[] blurredBytes = blur(inputBytes, radius, "png", 0.9f);
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage blurred = toArgb(readImage(blurredBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int s = src.getRGB(x, y), b = blurred.getRGB(x, y);
            int a = (s >> 24) & 0xFF;
            int r = clamp255((int) (((s >> 16) & 0xFF) + (((s >> 16) & 0xFF) - ((b >> 16) & 0xFF)) * amount));
            int g = clamp255((int) (((s >> 8) & 0xFF) + (((s >> 8) & 0xFF) - ((b >> 8) & 0xFF)) * amount));
            int bb = clamp255((int) ((s & 0xFF) + ((s & 0xFF) - (b & 0xFF)) * amount));
            out.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | bb);
        }
        return writeOut(out, format, quality);
    }

    public byte[] softGlow(byte[] inputBytes, int radius, float intensity, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        byte[] blurredBytes = blur(inputBytes, radius, "png", 0.9f);
        BufferedImage blurred = toArgb(readImage(blurredBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int s = src.getRGB(x, y), b = blurred.getRGB(x, y);
            int a = (s >> 24) & 0xFF;
            int r = clamp255((int) (((s >> 16) & 0xFF) * (1 - intensity) + Math.max(((s >> 16) & 0xFF), ((b >> 16) & 0xFF)) * intensity));
            int g = clamp255((int) (((s >> 8) & 0xFF) * (1 - intensity) + Math.max(((s >> 8) & 0xFF), ((b >> 8) & 0xFF)) * intensity));
            int bb = clamp255((int) ((s & 0xFF) * (1 - intensity) + Math.max(s & 0xFF, b & 0xFF) * intensity));
            out.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | bb);
        }
        return writeOut(out, format, quality);
    }

    public byte[] bloom(byte[] inputBytes, int threshold, float intensity, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage bright = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int rgb = src.getRGB(x, y);
            int v = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
            if (v > threshold) bright.setRGB(x, y, rgb); else bright.setRGB(x, y, 0);
        }
        byte[] bloomBytes = writeImage(bright, "png", 0.9f);
        BufferedImage blurred = toArgb(readImage(blur(bloomBytes, 8, "png", 0.9f)));
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int s = src.getRGB(x, y), b = blurred.getRGB(x, y);
            int a = (s >> 24) & 0xFF;
            int r = clamp255(((s >> 16) & 0xFF) + (int) (((b >> 16) & 0xFF) * intensity));
            int g = clamp255(((s >> 8) & 0xFF) + (int) (((b >> 8) & 0xFF) * intensity));
            int bb = clamp255((s & 0xFF) + (int) ((b & 0xFF) * intensity));
            out.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | bb);
        }
        return writeOut(out, format, quality);
    }

    public byte[] dreamy(byte[] inputBytes, float intensity, String format, float quality) throws IOException {
        return softGlow(inputBytes, 6, intensity, format, quality);
    }

    public byte[] glitch(byte[] inputBytes, int amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < amount; i++) {
            int y = rnd.nextInt(h);
            int sh = 1 + rnd.nextInt(Math.max(1, h / 10));
            int off = rnd.nextInt(40) - 20;
            g.drawImage(src, off, y, w, y + sh, 0, y, w, y + sh, null);
        }
        g.dispose();
        // RGB split
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int rgb = out.getRGB(x, y);
            int r = (rgb >> 16) & 0xFF, gg = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int sx = clamp(x + amount / 2, 0, w - 1);
            int rgbR = out.getRGB(sx, y);
            r = (rgbR >> 16) & 0xFF;
            out.setRGB(x, y, (rgb & 0xFF000000) | (r << 16) | (gg << 8) | b);
        }
        return writeOut(out, format, quality);
    }

    public byte[] rgbSplit(byte[] inputBytes, int offset, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int rgb = src.getRGB(x, y);
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            int sxR = clamp(x + offset, 0, w - 1);
            int sxG = clamp(x - offset, 0, w - 1);
            r = (src.getRGB(sxR, y) >> 16) & 0xFF;
            g = (src.getRGB(sxG, y) >> 8) & 0xFF;
            out.setRGB(x, y, (rgb & 0xFF000000) | (r << 16) | (g << 8) | b);
        }
        return writeOut(out, format, quality);
    }

    public byte[] scanlines(byte[] inputBytes, int lineSpacing, float strength, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.setColor(new Color(0, 0, 0, (int) (strength * 255)));
        for (int y = 0; y < h; y += lineSpacing) g.fillRect(0, y, w, 1);
        g.dispose();
        return writeOut(out, format, quality);
    }

    public byte[] fisheye(byte[] inputBytes, float strength, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int cx = w / 2, cy = h / 2;
        double maxR = Math.min(w, h) / 2.0;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            double dx = (x - cx) / maxR, dy = (y - cy) / maxR;
            double r = Math.sqrt(dx * dx + dy * dy);
            double theta = Math.atan2(dy, dx);
            double nr = Math.pow(r, 1 + strength) ;
            int sx = clamp((int) (cx + Math.cos(theta) * nr * maxR), 0, w - 1);
            int sy = clamp((int) (cy + Math.sin(theta) * nr * maxR), 0, h - 1);
            out.setRGB(x, y, src.getRGB(sx, sy));
        }
        return writeOut(out, format, quality);
    }

    public byte[] pinch(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int cx = w / 2, cy = h / 2;
        double maxR = Math.min(w, h) / 2.0;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            double dx = x - cx, dy = y - cy;
            double r = Math.sqrt(dx * dx + dy * dy) / maxR;
            double t = 1 - amount * Math.pow(r, 2);
            int sx = clamp((int) (cx + dx * t), 0, w - 1);
            int sy = clamp((int) (cy + dy * t), 0, h - 1);
            out.setRGB(x, y, src.getRGB(sx, sy));
        }
        return writeOut(out, format, quality);
    }

    public byte[] swirl(byte[] inputBytes, float degrees, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int cx = w / 2, cy = h / 2;
        double maxR = Math.min(w, h) / 2.0;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        double rad = Math.toRadians(degrees);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            double dx = x - cx, dy = y - cy;
            double r = Math.sqrt(dx * dx + dy * dy);
            double theta = Math.atan2(dy, dx) + (r / maxR) * rad;
            int sx = clamp((int) (cx + Math.cos(theta) * r), 0, w - 1);
            int sy = clamp((int) (cy + Math.sin(theta) * r), 0, h - 1);
            out.setRGB(x, y, src.getRGB(sx, sy));
        }
        return writeOut(out, format, quality);
    }

    public byte[] waveEffect(byte[] inputBytes, float amplitude, float wavelength, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int sy = clamp((int) (y + amplitude * Math.sin(x / wavelength)), 0, h - 1);
            out.setRGB(x, y, src.getRGB(x, sy));
        }
        return writeOut(out, format, quality);
    }

    public byte[] glass(byte[] inputBytes, int amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        java.util.Random rnd = new java.util.Random();
        int[][] off = new int[w][h];
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) off[x][y] = rnd.nextInt(amount * 2 + 1) - amount;
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int sx = clamp(x + off[x][y], 0, w - 1);
            int sy = clamp(y + off[(x + 7) % w][y], 0, h - 1);
            out.setRGB(x, y, src.getRGB(sx, sy));
        }
        return writeOut(out, format, quality);
    }

    public byte[] crystalize(byte[] inputBytes, int cellSize, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        java.util.Random rnd = new java.util.Random();
        int cols = (w + cellSize - 1) / cellSize, rows = (h + cellSize - 1) / cellSize;
        int[][] centersX = new int[cols][rows], centersY = new int[cols][rows];
        for (int cy = 0; cy < rows; cy++) for (int cx = 0; cx < cols; cx++) {
            centersX[cx][cy] = cx * cellSize + rnd.nextInt(cellSize);
            centersY[cx][cy] = cy * cellSize + rnd.nextInt(cellSize);
        }
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int ccx = Math.min(cols - 1, x / cellSize), ccy = Math.min(rows - 1, y / cellSize);
            int bestCx = centersX[ccx][ccy], bestCy = centersY[ccx][ccy];
            double bestD = Double.MAX_VALUE;
            for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
                int nx = ccx + dx, ny = ccy + dy;
                if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue;
                double d = (x - centersX[nx][ny]) * (x - centersX[nx][ny]) + (y - centersY[nx][ny]) * (y - centersY[nx][ny]);
                if (d < bestD) { bestD = d; bestCx = centersX[nx][ny]; bestCy = centersY[nx][ny]; }
            }
            out.setRGB(x, y, src.getRGB(clamp(bestCx, 0, w - 1), clamp(bestCy, 0, h - 1)));
        }
        return writeOut(out, format, quality);
    }

    public byte[] painting(byte[] inputBytes, int radius, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        BufferedImage blurred = toArgb(readImage(blur(writeImage(src, "png", 0.9f), radius, "png", 0.9f)));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int b = blurred.getRGB(x, y);
            int r = clamp255((((b >> 16) & 0xFF) / 32) * 32 + 16);
            int g = clamp255((((b >> 8) & 0xFF) / 32) * 32 + 16);
            int bb = clamp255(((b & 0xFF) / 32) * 32 + 16);
            out.setRGB(x, y, (b & 0xFF000000) | (r << 16) | (g << 8) | bb);
        }
        return writeOut(out, format, quality);
    }

    public byte[] cartoon(byte[] inputBytes, int levels, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        byte[] edgesBytes = edgeDetect(writeImage(src, "png", 0.9f), 1.5f, "png", 0.9f);
        BufferedImage edges = toArgb(readImage(edgesBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int s = src.getRGB(x, y);
            int r = clamp255((((s >> 16) & 0xFF) / (256 / levels)) * (255 / (levels - 1)));
            int g = clamp255((((s >> 8) & 0xFF) / (256 / levels)) * (255 / (levels - 1)));
            int b = clamp255(((s & 0xFF) / (256 / levels)) * (255 / (levels - 1)));
            int e = edges.getRGB(x, y) & 0xFF;
            if (e < 60) { r = g = b = 0; }
            out.setRGB(x, y, (s & 0xFF000000) | (r << 16) | (g << 8) | b);
        }
        return writeOut(out, format, quality);
    }

    public byte[] charcoal(byte[] inputBytes, float intensity, String format, float quality) throws IOException {
        byte[] edgesBytes = edgeDetect(inputBytes, 1.2f, "png", 0.9f);
        BufferedImage edges = toArgb(readImage(edgesBytes));
        int w = edges.getWidth(), h = edges.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int e = edges.getRGB(x, y) & 0xFF;
            int v = clamp255((int) (255 - e * intensity * 2));
            out.setRGB(x, y, (v << 16) | (v << 8) | v);
        }
        return writeImage(out, format, quality);
    }

    public byte[] kaleidoscope(byte[] inputBytes, int segments, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int cx = w / 2, cy = h / 2;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        double segAngle = 2 * Math.PI / segments;
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            double dx = x - cx, dy = y - cy;
            double r = Math.sqrt(dx * dx + dy * dy);
            double theta = Math.atan2(dy, dx);
            theta = ((theta % segAngle) + segAngle) % segAngle;
            if (theta > segAngle / 2) theta = segAngle - theta;
            int sx = clamp((int) (cx + Math.cos(theta) * r), 0, w - 1);
            int sy = clamp((int) (cy + Math.sin(theta) * r), 0, h - 1);
            out.setRGB(x, y, src.getRGB(sx, sy));
        }
        return writeOut(out, format, quality);
    }

    public byte[] mirrorQuad(byte[] inputBytes, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int hw = w / 2, hh = h / 2;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src.getSubimage(0, 0, hw, hh), 0, 0, null);
        g.drawImage(src.getSubimage(0, 0, hw, hh), hw, 0, w, hh, hw, 0, 0, hh, null);
        g.drawImage(src.getSubimage(0, 0, hw, hh), 0, hh, hw, h, 0, hh, hw, 0, null);
        g.drawImage(src.getSubimage(0, 0, hw, hh), hw, hh, w, h, hw, hh, 0, 0, null);
        g.dispose();
        return writeOut(out, format, quality);
    }

    // ---- Decorate ----

    public byte[] dropShadow(byte[] inputBytes, int offset, int blur, String colorHex, float alpha, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        Color color = Color.decode("#" + colorHex);
        int w = src.getWidth(), h = src.getHeight();
        int pad = blur + offset + 4;
        BufferedImage shadow = new BufferedImage(w + pad * 2, h + pad * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = shadow.createGraphics();
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
        g.fillRect(pad, pad, w, h);
        g.dispose();
        byte[] blurredShadow = blur(writeImage(shadow, "png", 0.9f), blur, "png", 0.9f);
        BufferedImage bs = toArgb(readImage(blurredShadow));
        BufferedImage out = new BufferedImage(w + pad * 2, h + pad * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.drawImage(bs, 0, 0, null);
        g2.drawImage(src, pad - offset, pad - offset, null);
        g2.dispose();
        return writeOut(out, format, quality);
    }

    public byte[] outerGlow(byte[] inputBytes, int radius, String colorHex, float alpha, String format, float quality) throws IOException {
        return dropShadow(inputBytes, 0, radius, colorHex, alpha, format, quality);
    }

    public byte[] strokeOutline(byte[] inputBytes, int width, String colorHex, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        Color color = Color.decode("#" + colorHex);
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.setColor(color);
        g.setStroke(new BasicStroke(width));
        g.drawRect(width / 2, width / 2, w - width, h - width);
        g.dispose();
        return writeOut(out, format, quality);
    }

    public byte[] gradientBorder(byte[] inputBytes, int borderWidth, String c1Hex, String c2Hex, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int w = src.getWidth(), h = src.getHeight();
        Color c1 = Color.decode("#" + c1Hex), c2 = Color.decode("#" + c2Hex);
        BufferedImage out = new BufferedImage(w + borderWidth * 2, h + borderWidth * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        GradientPaint gp = new GradientPaint(0, 0, c1, out.getWidth(), out.getHeight(), c2);
        g.setPaint(gp);
        g.fillRect(0, 0, out.getWidth(), out.getHeight());
        g.drawImage(src, borderWidth, borderWidth, null);
        g.dispose();
        return writeOut(out, format, quality);
    }

    public byte[] polaroidFrame(byte[] inputBytes, int border, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int w = src.getWidth(), h = src.getHeight();
        int fw = w + border * 2, fh = h + border * 3 + border;
        BufferedImage out = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, fw, fh);
        g.drawImage(src, border, border, null);
        g.setColor(new Color(0, 0, 0, 20));
        g.fillRect(border, h + border + 4, w, 4);
        g.dispose();
        return writeOut(out, format, quality);
    }

    public byte[] filmFrame(byte[] inputBytes, int holeSize, String format, float quality) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int w = src.getWidth(), h = src.getHeight();
        int top = holeSize * 2;
        int fw = w + top * 2, fh = h + top * 2;
        BufferedImage out = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, fw, fh);
        g.setColor(Color.WHITE);
        for (int x = top / 2; x < fw - top; x += holeSize * 2) {
            g.fillRect(x, top / 3, holeSize, holeSize / 2);
            g.fillRect(x, fh - top / 3 - holeSize / 2, holeSize, holeSize / 2);
        }
        g.drawImage(src, top, top, null);
        g.dispose();
        return writeOut(out, format, quality);
    }

    public byte[] caption(byte[] inputBytes, String text, int fontSize, String colorHex, String bgHex, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        Color fg = Color.decode("#" + colorHex), bg = Color.decode("#" + bgHex);
        int w = src.getWidth(), h = src.getHeight();
        int barH = fontSize + 16;
        BufferedImage out = new BufferedImage(w, h + barH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.setColor(bg);
        g.fillRect(0, h, w, barH);
        g.setColor(fg);
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, 10, h + (barH + fm.getAscent() - fm.getDescent()) / 2);
        g.dispose();
        return writeOut(out, format, quality);
    }

    public byte[] timestamp(byte[] inputBytes, String format, float quality) throws IOException {
        String ts = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        return caption(inputBytes, ts, 18, "ffffff", "000000", format, quality);
    }

    public byte[] gridOverlay(byte[] inputBytes, int cellSize, String colorHex, float alpha, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        Color color = Color.decode("#" + colorHex);
        int w = src.getWidth(), h = src.getHeight();
        Graphics2D g = src.createGraphics();
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
        g.setStroke(new BasicStroke(1));
        for (int x = 0; x <= w; x += cellSize) g.drawLine(x, 0, x, h);
        for (int y = 0; y <= h; y += cellSize) g.drawLine(0, y, w, y);
        g.dispose();
        return writeOut(src, format, quality);
    }

    public byte[] watermarkTile(byte[] inputBytes, String text, int fontSize, String colorHex, float alpha, int spacing, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        Color color = Color.decode("#" + colorHex);
        int w = src.getWidth(), h = src.getHeight();
        Graphics2D g = src.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text);
        for (int y = -tw; y < h + tw; y += spacing) {
            for (int x = -tw; x < w + tw; x += spacing) {
                g.drawString(text, x, y);
            }
        }
        g.dispose();
        return writeOut(src, format, quality);
    }

    public byte[] watermarkDiagonal(byte[] inputBytes, String text, int fontSize, String colorHex, float alpha, int spacing, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        Color color = Color.decode("#" + colorHex);
        int w = src.getWidth(), h = src.getHeight();
        Graphics2D g = src.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
        g.rotate(Math.toRadians(-30), w / 2.0, h / 2.0);
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text);
        for (int y = -h; y < h * 2; y += spacing) {
            for (int x = -w; x < w * 2; x += tw + spacing) {
                g.drawString(text, x, y);
            }
        }
        g.dispose();
        return writeOut(src, format, quality);
    }

    public byte[] gradientOverlay(byte[] inputBytes, String c1Hex, String c2Hex, float alpha, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        Color c1 = Color.decode("#" + c1Hex), c2 = Color.decode("#" + c2Hex);
        Graphics2D g = src.createGraphics();
        GradientPaint gp = new GradientPaint(0, 0, new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), (int) (alpha * 255)),
                0, h, new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), (int) (alpha * 255)));
        g.setPaint(gp);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return writeOut(src, format, quality);
    }

    public byte[] bokehOverlay(byte[] inputBytes, int count, int maxRadius, String colorHex, float alpha, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        Color color = Color.decode("#" + colorHex);
        int w = src.getWidth(), h = src.getHeight();
        Graphics2D g = src.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < count; i++) {
            int r = 5 + rnd.nextInt(maxRadius);
            int x = rnd.nextInt(w), y = rnd.nextInt(h);
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
            g.fillOval(x, y, r * 2, r * 2);
        }
        g.dispose();
        return writeOut(src, format, quality);
    }

    public byte[] snowOverlay(byte[] inputBytes, int count, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        Graphics2D g = src.createGraphics();
        java.util.Random rnd = new java.util.Random();
        g.setColor(Color.WHITE);
        for (int i = 0; i < count; i++) {
            int x = rnd.nextInt(w), y = rnd.nextInt(h), r = 1 + rnd.nextInt(3);
            g.fillOval(x, y, r, r);
        }
        g.dispose();
        return writeOut(src, format, quality);
    }

    public byte[] rainOverlay(byte[] inputBytes, int count, int length, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        Graphics2D g = src.createGraphics();
        java.util.Random rnd = new java.util.Random();
        g.setColor(new Color(180, 200, 220, 120));
        g.setStroke(new BasicStroke(1));
        for (int i = 0; i < count; i++) {
            int x = rnd.nextInt(w), y = rnd.nextInt(h);
            g.drawLine(x, y, x - 2, y + length);
        }
        g.dispose();
        return writeOut(src, format, quality);
    }

    public byte[] sparklesOverlay(byte[] inputBytes, int count, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        Graphics2D g = src.createGraphics();
        java.util.Random rnd = new java.util.Random();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < count; i++) {
            int x = rnd.nextInt(w), y = rnd.nextInt(h), r = 2 + rnd.nextInt(5);
            g.setColor(new Color(255, 255, 200, 200));
            g.fillOval(x, y, r, r);
            g.setColor(new Color(255, 255, 255, 80));
            g.drawLine(x - r * 2, y + r / 2, x + r * 2 + r, y + r / 2);
            g.drawLine(x + r / 2, y - r * 2, x + r / 2, y + r * 2 + r);
        }
        g.dispose();
        return writeOut(src, format, quality);
    }

    public byte[] droste(byte[] inputBytes, int count, float scale, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        for (int i = 1; i <= count; i++) {
            int nw = (int) (w * Math.pow(scale, i));
            int nh = (int) (h * Math.pow(scale, i));
            if (nw < 4 || nh < 4) break;
            g.drawImage(src, (w - nw) / 2, (h - nh) / 2, nw, nh, null);
        }
        g.dispose();
        return writeOut(out, format, quality);
    }

    public byte[] tunnel(byte[] inputBytes, int count, float scale, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        for (int i = count; i >= 1; i--) {
            int nw = (int) (w * Math.pow(scale, i));
            int nh = (int) (h * Math.pow(scale, i));
            if (nw < 4 || nh < 4) break;
            g.drawImage(src, (w - nw) / 2, (h - nh) / 2, nw, nh, null);
        }
        g.dispose();
        return writeOut(out, format, quality);
    }

    // ---- Tools / Analysis ----

    public Map<String, Object> histogram(byte[] inputBytes) throws IOException {
        BufferedImage src = readImage(inputBytes);
        int[] r = new int[256], g = new int[256], b = new int[256];
        for (int y = 0; y < src.getHeight(); y++) for (int x = 0; x < src.getWidth(); x++) {
            int rgb = src.getRGB(x, y);
            r[(rgb >> 16) & 0xFF]++; g[(rgb >> 8) & 0xFF]++; b[rgb & 0xFF]++;
        }
        Map<String, Object> m = new HashMap<>();
        m.put("red", r); m.put("green", g); m.put("blue", b);
        return m;
    }

    public Map<String, Object> dominantColor(byte[] inputBytes, int count) throws IOException {
        BufferedImage src = readImage(inputBytes);
        java.util.Map<Integer, Integer> freq = new java.util.HashMap<>();
        for (int y = 0; y < src.getHeight(); y += 2) for (int x = 0; x < src.getWidth(); x += 2) {
            int rgb = src.getRGB(x, y) & 0xFFFFFF;
            int q = ((rgb >> 16) & 0xE0) | ((rgb >> 3) & 0xE0) | ((rgb >> 6) & 0xC0);
            freq.merge(q, 1, Integer::sum);
        }
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        freq.entrySet().stream()
                .sorted((a, c) -> c.getValue() - a.getValue())
                .limit(count)
                .forEach(e -> result.put(String.format("#%06X", e.getKey()), e.getValue()));
        return result;
    }

    public Map<String, Object> palette(byte[] inputBytes, int count) throws IOException {
        return dominantColor(inputBytes, count);
    }

    public Map<String, Object> averageColor(byte[] inputBytes) throws IOException {
        BufferedImage src = readImage(inputBytes);
        long r = 0, g = 0, b = 0, n = 0;
        for (int y = 0; y < src.getHeight(); y++) for (int x = 0; x < src.getWidth(); x++) {
            int rgb = src.getRGB(x, y);
            r += (rgb >> 16) & 0xFF; g += (rgb >> 8) & 0xFF; b += rgb & 0xFF; n++;
        }
        if (n == 0) n = 1;
        int ar = (int) (r / n), ag = (int) (g / n), ab = (int) (b / n);
        Map<String, Object> m = new HashMap<>();
        m.put("red", ar); m.put("green", ag); m.put("blue", ab);
        m.put("hex", String.format("#%02X%02X%02X", ar, ag, ab));
        return m;
    }

    public Map<String, Object> brightnessMeter(byte[] inputBytes) throws IOException {
        BufferedImage src = readImage(inputBytes);
        long sum = 0, n = 0;
        for (int y = 0; y < src.getHeight(); y++) for (int x = 0; x < src.getWidth(); x++) {
            int rgb = src.getRGB(x, y);
            sum += (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3; n++;
        }
        double avg = n > 0 ? (double) sum / n : 0;
        Map<String, Object> m = new HashMap<>();
        m.put("brightness", (int) avg);
        m.put("normalized", avg / 255.0);
        m.put("level", avg < 64 ? "dark" : (avg < 128 ? "dim" : (avg < 192 ? "bright" : "very bright")));
        return m;
    }

    public Map<String, Object> contrastMeter(byte[] inputBytes) throws IOException {
        BufferedImage src = readImage(inputBytes);
        long sum = 0, sumSq = 0, n = 0;
        for (int y = 0; y < src.getHeight(); y++) for (int x = 0; x < src.getWidth(); x++) {
            int rgb = src.getRGB(x, y);
            int v = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
            sum += v; sumSq += (long) v * v; n++;
        }
        double mean = n > 0 ? (double) sum / n : 0;
        double variance = n > 0 ? (double) sumSq / n - mean * mean : 0;
        double std = Math.sqrt(Math.max(0, variance));
        Map<String, Object> m = new HashMap<>();
        m.put("stddev", std);
        m.put("contrast", std / 128.0);
        m.put("level", std < 30 ? "low" : (std < 70 ? "medium" : "high"));
        return m;
    }

    public Map<String, Object> hashMd5(byte[] inputBytes) {
        Map<String, Object> m = new HashMap<>();
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] d = md.digest(inputBytes);
            m.put("md5", toHex(d));
        } catch (Exception e) { m.put("md5", "error"); }
        m.put("size", inputBytes.length);
        return m;
    }

    public Map<String, Object> hashSha256(byte[] inputBytes) {
        Map<String, Object> m = new HashMap<>();
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(inputBytes);
            m.put("sha256", toHex(d));
        } catch (Exception e) { m.put("sha256", "error"); }
        m.put("size", inputBytes.length);
        return m;
    }

    private String toHex(byte[] d) {
        StringBuilder sb = new StringBuilder();
        for (byte b : d) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public Map<String, Object> hexDump(byte[] inputBytes, int length) throws IOException {
        int len = Math.min(length, inputBytes.length);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(String.format("%02X ", inputBytes[i] & 0xFF));
            if ((i + 1) % 16 == 0) sb.append("\n");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("hex", sb.toString());
        m.put("bytes", len);
        m.put("total", inputBytes.length);
        return m;
    }

    public Map<String, Object> detectFormat(byte[] inputBytes) throws IOException {
        String fmt = "unknown";
        if (inputBytes.length >= 4) {
            int b0 = inputBytes[0] & 0xFF, b1 = inputBytes[1] & 0xFF, b2 = inputBytes[2] & 0xFF, b3 = inputBytes[3] & 0xFF;
            if (b0 == 0xFF && b1 == 0xD8) fmt = "JPEG";
            else if (b0 == 0x89 && b1 == 0x50 && b2 == 0x4E && b3 == 0x47) fmt = "PNG";
            else if (b0 == 0x47 && b1 == 0x49 && b2 == 0x46) fmt = "GIF";
            else if (b0 == 0x42 && b1 == 0x4D) fmt = "BMP";
            else if (b0 == 0x49 && b1 == 0x49 && b2 == 0x2A) fmt = "TIFF (LE)";
            else if (b0 == 0x4D && b1 == 0x4D && b3 == 0x2A) fmt = "TIFF (BE)";
            else if (b0 == 0x38 && b1 == 0x42 && b2 == 0x50 && b3 == 0x53) fmt = "PSD";
            else if (b0 == 'R' && b1 == 'I' && inputBytes.length > 12 && inputBytes[8] == 'W' && inputBytes[9] == 'E' && inputBytes[10] == 'B' && inputBytes[11] == 'P') fmt = "WEBP";
        }
        Map<String, Object> m = new HashMap<>();
        m.put("format", fmt);
        m.put("size", inputBytes.length);
        try {
            BufferedImage img = readImage(inputBytes);
            m.put("width", img.getWidth());
            m.put("height", img.getHeight());
        } catch (Exception e) { m.put("width", 0); m.put("height", 0); }
        return m;
    }

    public String asciiColor(byte[] inputBytes, int width) throws IOException {
        String chars = " .:-=+*#%@";
        BufferedImage input = readImage(inputBytes);
        int imgW = input.getWidth(), imgH = input.getHeight();
        int newH = (int) (imgH * (width * 0.5) / imgW);
        BufferedImage small = new BufferedImage(width, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = small.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(input, 0, 0, width, newH, null);
        g.dispose();
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < newH; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = small.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, gc = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                int avg = (r + gc + b) / 3;
                int idx = (avg * (chars.length() - 1)) / 255;
                sb.append("\u001B[38;2;").append(r).append(";").append(gc).append(";").append(b).append("m").append(chars.charAt(idx));
            }
            sb.append("\u001B[0m\n");
        }
        return sb.toString();
    }

    public byte[] originalEcho(byte[] inputBytes, String format, float quality) throws IOException {
        return convertFormat(inputBytes, format, quality);
    }

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

    // ==================== AI-Like Enhancement Features ====================

    /** 去除背景: 基于四角颜色相似度的 flood-fill 式背景移除 */
    public byte[] removeBg(byte[] inputBytes, float tolerance, int feather, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        // 采样四角颜色作为背景色
        int[] corners = {
            src.getRGB(0, 0), src.getRGB(w - 1, 0),
            src.getRGB(0, h - 1), src.getRGB(w - 1, h - 1)
        };
        int tol = (int) (tolerance * 442); // 0-1 → 0-442 (max RGB distance)
        boolean[][] bg = new boolean[w][h];
        java.util.ArrayDeque<int[]> queue = new java.util.ArrayDeque<>();
        // 从四角开始 BFS
        for (int[] pt : new int[][]{{0,0},{w-1,0},{0,h-1},{w-1,h-1}}) {
            if (!bg[pt[0]][pt[1]] && colorMatch(src.getRGB(pt[0], pt[1]), corners, tol)) {
                bg[pt[0]][pt[1]] = true;
                queue.add(pt);
            }
        }
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int[] d : dirs) {
                int nx = cur[0] + d[0], ny = cur[1] + d[1];
                if (nx >= 0 && nx < w && ny >= 0 && ny < h && !bg[nx][ny]) {
                    if (colorMatch(src.getRGB(nx, ny), corners, tol)) {
                        bg[nx][ny] = true;
                        queue.add(new int[]{nx, ny});
                    }
                }
            }
        }
        // 羽化边缘
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = src.getRGB(x, y);
                if (!bg[x][y]) {
                    out.setRGB(x, y, rgb);
                } else if (feather > 0) {
                    // 检查是否靠近前景边缘
                    int minDist = feather + 1;
                    for (int fy = Math.max(0, y - feather); fy <= Math.min(h-1, y + feather); fy++) {
                        for (int fx = Math.max(0, x - feather); fx <= Math.min(w-1, x + feather); fx++) {
                            if (!bg[fx][fy]) {
                                int d = Math.abs(fx - x) + Math.abs(fy - y);
                                if (d < minDist) minDist = d;
                            }
                        }
                    }
                    if (minDist <= feather) {
                        int a = (int) (255 * (1.0 - (double) minDist / feather));
                        out.setRGB(x, y, (a << 24) | (rgb & 0x00FFFFFF));
                    } else {
                        out.setRGB(x, y, 0);
                    }
                } else {
                    out.setRGB(x, y, 0);
                }
            }
        }
        if (!formatSupportsAlpha(format)) out = flatten(out);
        return writeImage(out, format, quality);
    }

    private boolean colorMatch(int rgb, int[] samples, int tol) {
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        for (int s : samples) {
            int sr = (s >> 16) & 0xFF, sg = (s >> 8) & 0xFF, sb = s & 0xFF;
            int dist = (int) Math.sqrt((r-sr)*(r-sr) + (g-sg)*(g-sg) + (b-sb)*(b-sb));
            if (dist <= tol) return true;
        }
        return false;
    }

    /** 无损放大: 双三次插值放大 + 自适应锐化 */
    public byte[] upscaleHq(byte[] inputBytes, int scale, float sharpness, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int nw = w * scale, nh = h * scale;
        // 双三次插值放大
        java.awt.Image scaled = src.getScaledInstance(nw, nh, java.awt.Image.SCALE_AREA_AVERAGING);
        BufferedImage out = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
        // 自适应锐化 (Unsharp Mask)
        if (sharpness > 0) {
            out = unsharpMaskOp(out, sharpness, 1.5f);
        }
        return writeOut(out, format, quality);
    }

    private BufferedImage unsharpMaskOp(BufferedImage src, float amount, float radius) {
        int w = src.getWidth(), h = src.getHeight();
        int r = Math.max(1, (int) radius);
        float[] kernel = createGaussianKernel(r);
        // 高斯模糊
        int[] blurred = gaussianBlurHorizontal(src, kernel, r);
        blurred = gaussianBlurVertical(src, blurred, kernel, r);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int orig = src.getRGB(x, y);
                int blur = blurred[y * w + x];
                int a = (orig >> 24) & 0xFF;
                int or = (orig >> 16) & 0xFF, og = (orig >> 8) & 0xFF, ob = orig & 0xFF;
                int br = (blur >> 16) & 0xFF, bg = (blur >> 8) & 0xFF, bb = blur & 0xFF;
                int nr = clamp255((int) (or + (or - br) * amount));
                int ng = clamp255((int) (og + (og - bg) * amount));
                int nb = clamp255((int) (ob + (ob - bb) * amount));
                out.setRGB(x, y, (a << 24) | (nr << 16) | (ng << 8) | nb);
            }
        }
        return out;
    }

    private int[] gaussianBlurHorizontal(BufferedImage src, float[] kernel, int r) {
        int w = src.getWidth(), h = src.getHeight();
        int[] result = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float rr = 0, gg = 0, bb = 0;
                for (int i = -r; i <= r; i++) {
                    int sx = clamp(x + i, 0, w - 1);
                    int rgb = src.getRGB(sx, y);
                    float k = kernel[i + r];
                    rr += ((rgb >> 16) & 0xFF) * k;
                    gg += ((rgb >> 8) & 0xFF) * k;
                    bb += (rgb & 0xFF) * k;
                }
                result[y * w + x] = (clamp255((int) rr) << 16) | (clamp255((int) gg) << 8) | clamp255((int) bb);
            }
        }
        return result;
    }

    private int[] gaussianBlurVertical(BufferedImage src, int[] horizontal, float[] kernel, int r) {
        int w = src.getWidth(), h = src.getHeight();
        int[] result = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float rr = 0, gg = 0, bb = 0;
                for (int j = -r; j <= r; j++) {
                    int sy = clamp(y + j, 0, h - 1);
                    int rgb = horizontal[sy * w + x];
                    float k = kernel[j + r];
                    rr += ((rgb >> 16) & 0xFF) * k;
                    gg += ((rgb >> 8) & 0xFF) * k;
                    bb += (rgb & 0xFF) * k;
                }
                result[y * w + x] = (clamp255((int) rr) << 16) | (clamp255((int) gg) << 8) | clamp255((int) bb);
            }
        }
        return result;
    }

    /** 智能降噪: 中值滤波 */
    public byte[] denoise(byte[] inputBytes, int radius, float threshold, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int r = Math.max(1, radius);
        int thr = (int) (threshold * 100);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int orig = src.getRGB(x, y);
                int a = (orig >> 24) & 0xFF;
                int or = (orig >> 16) & 0xFF, og = (orig >> 8) & 0xFF, ob = orig & 0xFF;
                java.util.List<Integer> rs = new java.util.ArrayList<>();
                java.util.List<Integer> gs = new java.util.ArrayList<>();
                java.util.List<Integer> bs = new java.util.ArrayList<>();
                for (int j = -r; j <= r; j++) {
                    for (int i = -r; i <= r; i++) {
                        int sx = clamp(x + i, 0, w - 1);
                        int sy = clamp(y + j, 0, h - 1);
                        int rgb = src.getRGB(sx, sy);
                        rs.add((rgb >> 16) & 0xFF);
                        gs.add((rgb >> 8) & 0xFF);
                        bs.add(rgb & 0xFF);
                    }
                }
                Collections.sort(rs);
                Collections.sort(gs);
                Collections.sort(bs);
                int medR = rs.get(rs.size() / 2);
                int medG = gs.get(gs.size() / 2);
                int medB = bs.get(bs.size() / 2);
                // 只在差异超过阈值时替换 (保留细节)
                if (Math.abs(or - medR) > thr || Math.abs(og - medG) > thr || Math.abs(ob - medB) > thr) {
                    out.setRGB(x, y, (a << 24) | (medR << 16) | (medG << 8) | medB);
                } else {
                    out.setRGB(x, y, orig);
                }
            }
        }
        return writeOut(out, format, quality);
    }

    /** 去雾: 暗通道先验 (Dark Channel Prior) */
    public byte[] dehaze(byte[] inputBytes, float omega, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int patch = Math.max(3, Math.min(w, h) / 50);
        if (patch % 2 == 0) patch++;
        int pr = patch / 2;
        // Step 1: 计算暗通道
        int[] darkChannel = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int minVal = 255;
                for (int j = -pr; j <= pr; j++) {
                    for (int i = -pr; i <= pr; i++) {
                        int sx = clamp(x + i, 0, w - 1);
                        int sy = clamp(y + j, 0, h - 1);
                        int rgb = src.getRGB(sx, sy);
                        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                        int minc = Math.min(r, Math.min(g, b));
                        if (minc < minVal) minVal = minc;
                    }
                }
                darkChannel[y * w + x] = minVal;
            }
        }
        // Step 2: 估计大气光 A (取暗通道最亮的前 0.1% 像素的平均)
        int[] sorted = darkChannel.clone();
        Arrays.sort(sorted);
        int topCount = Math.max(1, (int) (w * h * 0.001));
        int threshold = sorted[w * h - topCount];
        long aR = 0, aG = 0, aB = 0, aCount = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (darkChannel[y * w + x] >= threshold) {
                    int rgb = src.getRGB(x, y);
                    aR += (rgb >> 16) & 0xFF;
                    aG += (rgb >> 8) & 0xFF;
                    aB += rgb & 0xFF;
                    aCount++;
                }
            }
        }
        if (aCount == 0) aCount = 1;
        int AR = clamp255((int) (aR / aCount));
        int AG = clamp255((int) (aG / aCount));
        int AB = clamp255((int) (aB / aCount));
        // Step 3: 估算透射率 t = 1 - omega * min(I/A)
        float[] t = new float[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = src.getRGB(x, y);
                float r = ((rgb >> 16) & 0xFF) / (float) Math.max(AR, 1);
                float g = ((rgb >> 8) & 0xFF) / (float) Math.max(AG, 1);
                float b = (rgb & 0xFF) / (float) Math.max(AB, 1);
                float minC = Math.min(r, Math.min(g, b));
                // 暗通道取邻域最小
                float minDark = 1.0f;
                for (int j = -pr; j <= pr; j++) {
                    for (int i = -pr; i <= pr; i++) {
                        int sx = clamp(x + i, 0, w - 1);
                        int sy = clamp(y + j, 0, h - 1);
                        if (darkChannel[sy * w + sx] / 255.0f < minDark) {
                            minDark = darkChannel[sy * w + sx] / 255.0f;
                        }
                    }
                }
                t[y * w + x] = Math.max(0.1f, 1.0f - omega * minDark);
            }
        }
        // Step 4: 恢复 J = (I - A) / t + A
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = src.getRGB(x, y);
                int a = (rgb >> 24) & 0xFF;
                float r = (rgb >> 16) & 0xFF;
                float g = (rgb >> 8) & 0xFF;
                float b = rgb & 0xFF;
                float tv = t[y * w + x];
                int nr = clamp255((int) ((r - AR) / tv + AR));
                int ng = clamp255((int) ((g - AG) / tv + AG));
                int nb = clamp255((int) ((b - AB) / tv + AB));
                out.setRGB(x, y, (a << 24) | (nr << 16) | (ng << 8) | nb);
            }
        }
        return writeOut(out, format, quality);
    }

    /** 画质增强: 自动对比度 + 轻度锐化 + 饱和度提升 */
    public byte[] smartEnhance(byte[] inputBytes, float amount, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        // Step 1: 自动对比度
        int minV = 255, maxV = 0;
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int rgb = src.getRGB(x, y);
            int v = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
            if (v < minV) minV = v; if (v > maxV) maxV = v;
        }
        if (maxV <= minV) maxV = minV + 1;
        final int lo = minV;
        final double scale = 255.0 / (maxV - minV);
        BufferedImage contrasted = pixelTransform(src, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            r = clamp255((int) ((r - lo) * scale));
            g = clamp255((int) ((g - lo) * scale));
            b = clamp255((int) ((b - lo) * scale));
            return (r << 16) | (g << 8) | b;
        });
        // Step 2: 饱和度提升
        final float sat = 1.0f + 0.3f * amount;
        BufferedImage saturated = pixelTransform(contrasted, rgb -> {
            int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
            float gray = 0.299f * r + 0.587f * g + 0.114f * b;
            int nr = clamp255((int) (gray + (r - gray) * sat));
            int ng = clamp255((int) (gray + (g - gray) * sat));
            int nb = clamp255((int) (gray + (b - gray) * sat));
            return (nr << 16) | (ng << 8) | nb;
        });
        // Step 3: 轻度锐化
        if (amount > 0) {
            saturated = unsharpMaskOp(saturated, 0.5f * amount, 1.5f);
        }
        return writeOut(saturated, format, quality);
    }

    /** 美颜磨皮: 双边滤波 (保边平滑) */
    public byte[] beautify(byte[] inputBytes, int radius, float strength, String format, float quality) throws IOException {
        BufferedImage src = toArgb(readImage(inputBytes));
        int w = src.getWidth(), h = src.getHeight();
        int r = Math.max(1, radius);
        float sigmaR = 30.0f; // 色彩空间 sigma
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int center = src.getRGB(x, y);
                int ca = (center >> 24) & 0xFF;
                int cr = (center >> 16) & 0xFF, cg = (center >> 8) & 0xFF, cb = center & 0xFF;
                float wSum = 0, rSum = 0, gSum = 0, bSum = 0;
                for (int j = -r; j <= r; j++) {
                    for (int i = -r; i <= r; i++) {
                        int sx = clamp(x + i, 0, w - 1);
                        int sy = clamp(y + j, 0, h - 1);
                        int rgb = src.getRGB(sx, sy);
                        int rr = (rgb >> 16) & 0xFF, gg = (rgb >> 8) & 0xFF, bb = rgb & 0xFF;
                        // 空间权重 (高斯)
                        float dist2 = i * i + j * j;
                        float ws = (float) Math.exp(-dist2 / (2.0f * r * r));
                        // 色彩权重 (高斯)
                        int cd = (cr - rr) * (cr - rr) + (cg - gg) * (cg - gg) + (cb - bb) * (cb - bb);
                        float wc = (float) Math.exp(-cd / (2.0f * sigmaR * sigmaR));
                        float weight = ws * wc;
                        wSum += weight;
                        rSum += rr * weight;
                        gSum += gg * weight;
                        bSum += bb * weight;
                    }
                }
                if (wSum == 0) wSum = 1;
                // 混合原图和滤波结果 (strength 控制磨皮强度)
                int br = clamp255((int) (rSum / wSum));
                int bg = clamp255((int) (gSum / wSum));
                int bb = clamp255((int) (bSum / wSum));
                int nr = clamp255((int) (cr * (1 - strength) + br * strength));
                int ng = clamp255((int) (cg * (1 - strength) + bg * strength));
                int nb = clamp255((int) (cb * (1 - strength) + bb * strength));
                out.setRGB(x, y, (ca << 24) | (nr << 16) | (ng << 8) | nb);
            }
        }
        return writeOut(out, format, quality);
    }
}
