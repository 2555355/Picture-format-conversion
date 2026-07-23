package com.imageconverter.controller;

import com.imageconverter.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/formats")
    public ResponseEntity<Map<String, Object>> supportedFormats() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("read", java.util.Arrays.asList("png", "jpeg", "jpg", "gif", "bmp", "webp", "tiff", "psd", "pnm", "pcx", "iff", "tga"));
        // WEBP is read-only in TwelveMonkeys (no pure-Java writer available)
        resp.put("write", java.util.Arrays.asList("png", "jpeg", "jpg", "gif", "bmp", "tiff"));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/info")
    public ResponseEntity<Map<String, Object>> info(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] data = file.getBytes();
        return ResponseEntity.ok(imageService.getImageInfo(data));
    }

    @PostMapping("/convert")
    public ResponseEntity<byte[]> convert(
            @RequestParam("file") MultipartFile file,
            @RequestParam("format") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.convertFormat(file.getBytes(), format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/resize")
    public ResponseEntity<byte[]> resize(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.resize(file.getBytes(), width, height, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/scale")
    public ResponseEntity<byte[]> scale(
            @RequestParam("file") MultipartFile file,
            @RequestParam("percent") double percent,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.scaleByPercent(file.getBytes(), percent, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/crop")
    public ResponseEntity<byte[]> crop(
            @RequestParam("file") MultipartFile file,
            @RequestParam("x") int x,
            @RequestParam("y") int y,
            @RequestParam("width") int width,
            @RequestParam("height") int height,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.crop(file.getBytes(), x, y, width, height, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/rotate")
    public ResponseEntity<byte[]> rotate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("degrees") double degrees,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.rotate(file.getBytes(), degrees, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/flip")
    public ResponseEntity<byte[]> flip(
            @RequestParam("file") MultipartFile file,
            @RequestParam("direction") String direction,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.flip(file.getBytes(), direction, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/grayscale")
    public ResponseEntity<byte[]> grayscale(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.grayscale(file.getBytes(), format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/invert")
    public ResponseEntity<byte[]> invert(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.invert(file.getBytes(), format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/brightness-contrast")
    public ResponseEntity<byte[]> brightnessContrast(
            @RequestParam("file") MultipartFile file,
            @RequestParam("brightness") float brightness,
            @RequestParam("contrast") float contrast,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.adjustBrightnessContrast(file.getBytes(), brightness, contrast, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/sepia")
    public ResponseEntity<byte[]> sepia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.sepia(file.getBytes(), format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/watermark-text")
    public ResponseEntity<byte[]> textWatermark(
            @RequestParam("file") MultipartFile file,
            @RequestParam("text") String text,
            @RequestParam(value = "position", defaultValue = "center") String position,
            @RequestParam(value = "fontSize", defaultValue = "32") int fontSize,
            @RequestParam(value = "color", defaultValue = "ffffff") String colorHex,
            @RequestParam(value = "alpha", defaultValue = "0.8") float alpha,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        Color color = Color.decode("#" + colorHex);
        byte[] result = imageService.addTextWatermark(file.getBytes(), text, position, fontSize, color, alpha, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/watermark-image")
    public ResponseEntity<byte[]> imageWatermark(
            @RequestParam("file") MultipartFile file,
            @RequestParam("watermark") MultipartFile watermark,
            @RequestParam(value = "position", defaultValue = "center") String position,
            @RequestParam(value = "scale", defaultValue = "30") float scale,
            @RequestParam(value = "alpha", defaultValue = "0.7") float alpha,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.addImageWatermark(file.getBytes(), watermark.getBytes(), position, scale, alpha, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/border")
    public ResponseEntity<byte[]> border(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int borderWidth,
            @RequestParam(value = "color", defaultValue = "000000") String colorHex,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        Color color = Color.decode("#" + colorHex);
        byte[] result = imageService.addBorder(file.getBytes(), borderWidth, color, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/round-corners")
    public ResponseEntity<byte[]> roundCorners(
            @RequestParam("file") MultipartFile file,
            @RequestParam("radius") int radius,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.roundCorners(file.getBytes(), radius, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/thumbnail")
    public ResponseEntity<byte[]> thumbnail(
            @RequestParam("file") MultipartFile file,
            @RequestParam("maxSize") int maxSize,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.createThumbnail(file.getBytes(), maxSize, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/blur")
    public ResponseEntity<byte[]> blur(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "radius", defaultValue = "5") int radius,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.blur(file.getBytes(), radius, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/sharpen")
    public ResponseEntity<byte[]> sharpen(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.sharpen(file.getBytes(), format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/posterize")
    public ResponseEntity<byte[]> posterize(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "levels", defaultValue = "4") int levels,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.posterize(file.getBytes(), levels, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/pixelate")
    public ResponseEntity<byte[]> pixelate(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "pixelSize", defaultValue = "10") int pixelSize,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.pixelate(file.getBytes(), pixelSize, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/vignette")
    public ResponseEntity<byte[]> vignette(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "strength", defaultValue = "0.5") float strength,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.vignette(file.getBytes(), strength, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/hsb")
    public ResponseEntity<byte[]> adjustHsb(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "hue", defaultValue = "0") float hue,
            @RequestParam(value = "saturation", defaultValue = "0") float saturation,
            @RequestParam(value = "brightness", defaultValue = "0") float brightness,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.adjustHsb(file.getBytes(), hue, saturation, brightness, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping(value = "/ascii", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> ascii(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "width", defaultValue = "100") int width) throws IOException {
        String ascii = imageService.toAsciiArt(file.getBytes(), width);
        return ResponseEntity.ok(ascii);
    }

    @PostMapping("/base64")
    public ResponseEntity<Map<String, String>> base64(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        String b64 = imageService.toBase64(file.getBytes(), format, quality);
        Map<String, String> resp = new HashMap<>();
        resp.put("base64", b64);
        resp.put("dataUri", "data:image/" + format + ";base64," + b64);
        resp.put("format", format);
        return ResponseEntity.ok(resp);
    }

    // ===== Extended endpoints =====

    @PostMapping("/threshold")
    public ResponseEntity<byte[]> threshold(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "threshold", defaultValue = "128") int threshold,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.threshold(file.getBytes(), threshold, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/gamma")
    public ResponseEntity<byte[]> gamma(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "gamma", defaultValue = "1.0") double gamma,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.gamma(file.getBytes(), gamma, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/color-balance")
    public ResponseEntity<byte[]> colorBalance(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "red", defaultValue = "0") int red,
            @RequestParam(value = "green", defaultValue = "0") int green,
            @RequestParam(value = "blue", defaultValue = "0") int blue,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.colorBalance(file.getBytes(), red, green, blue, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/channel-extract")
    public ResponseEntity<byte[]> channelExtract(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "channel", defaultValue = "red") String channel,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.channelExtract(file.getBytes(), channel, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/duotone")
    public ResponseEntity<byte[]> duotone(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "shadow", defaultValue = "1e3a8a") String shadowHex,
            @RequestParam(value = "highlight", defaultValue = "fbbf24") String highlightHex,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.duotone(file.getBytes(), shadowHex, highlightHex, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/edge-detect")
    public ResponseEntity<byte[]> edgeDetect(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "intensity", defaultValue = "1.0") float intensity,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.edgeDetect(file.getBytes(), intensity, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/emboss")
    public ResponseEntity<byte[]> emboss(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "1.0") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.emboss(file.getBytes(), amount, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/sketch")
    public ResponseEntity<byte[]> sketch(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "intensity", defaultValue = "0.8") float intensity,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.sketch(file.getBytes(), intensity, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/noise")
    public ResponseEntity<byte[]> noise(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "25") int amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.noise(file.getBytes(), amount, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    @PostMapping("/crop-circle")
    public ResponseEntity<byte[]> cropCircle(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "padding", defaultValue = "0") int padding,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        byte[] result = imageService.cropCircle(file.getBytes(), padding, format, quality);
        return buildResponse(result, format, file.getOriginalFilename());
    }

    // ===== Extended endpoints (Phase 2 - 70+ features) =====

    @PostMapping("/fit")
    public ResponseEntity<byte[]> fit(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.fit(file.getBytes(), width, height, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/cover")
    public ResponseEntity<byte[]> cover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.cover(file.getBytes(), width, height, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/pad")
    public ResponseEntity<byte[]> pad(
            @RequestParam("file") MultipartFile file,
            @RequestParam("padding") int padding,
            @RequestParam(value = "color", defaultValue = "ffffff") String colorHex,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.pad(file.getBytes(), padding, colorHex, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/trim")
    public ResponseEntity<byte[]> trim(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "tolerance", defaultValue = "10") int tolerance,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.trim(file.getBytes(), tolerance, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/transpose")
    public ResponseEntity<byte[]> transpose(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.transpose(file.getBytes(), format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/transverse")
    public ResponseEntity<byte[]> transverse(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.transverse(file.getBytes(), format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/canvas-resize")
    public ResponseEntity<byte[]> canvasResize(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height,
            @RequestParam(value = "color", defaultValue = "ffffff") String colorHex,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.canvasResize(file.getBytes(), width, height, colorHex, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/split-h")
    public ResponseEntity<byte[]> splitH(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "index", defaultValue = "0") int index,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.splitH(file.getBytes(), index, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/split-v")
    public ResponseEntity<byte[]> splitV(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "index", defaultValue = "0") int index,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.splitV(file.getBytes(), index, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/split-grid")
    public ResponseEntity<byte[]> splitGrid(
            @RequestParam("file") MultipartFile file,
            @RequestParam("rows") int rows,
            @RequestParam("cols") int cols,
            @RequestParam("row") int row,
            @RequestParam("col") int col,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.splitGrid(file.getBytes(), rows, cols, row, col, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/tile")
    public ResponseEntity<byte[]> tile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.tile(file.getBytes(), width, height, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/strip-alpha")
    public ResponseEntity<byte[]> stripAlpha(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.stripAlpha(file.getBytes(), format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/flatten-bg")
    public ResponseEntity<byte[]> flattenBg(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "color", defaultValue = "ffffff") String colorHex,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.flattenBg(file.getBytes(), colorHex, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/cool")
    public ResponseEntity<byte[]> cool(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.5") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.cool(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/warm")
    public ResponseEntity<byte[]> warm(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.5") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.warm(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/vintage")
    public ResponseEntity<byte[]> vintage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.8") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.vintage(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/lomo")
    public ResponseEntity<byte[]> lomo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.7") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.lomo(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/cinematic")
    public ResponseEntity<byte[]> cinematic(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.8") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.cinematic(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/fade")
    public ResponseEntity<byte[]> fade(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.5") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.fade(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/pastel")
    public ResponseEntity<byte[]> pastel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.6") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.pastel(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/neon")
    public ResponseEntity<byte[]> neon(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.8") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.neon(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/heat")
    public ResponseEntity<byte[]> heat(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.8") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.heat(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/night-vision")
    public ResponseEntity<byte[]> nightVision(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.8") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.nightVision(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/infrared")
    public ResponseEntity<byte[]> infrared(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.8") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.infrared(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/polaroid-filter")
    public ResponseEntity<byte[]> polaroidFilter(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.7") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.polaroid(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/bleach-bypass")
    public ResponseEntity<byte[]> bleachBypass(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.7") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.bleachBypass(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/cross-process")
    public ResponseEntity<byte[]> crossProcess(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.7") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.crossProcess(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/cyanotype")
    public ResponseEntity<byte[]> cyanotype(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.9") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.cyanotype(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/silver")
    public ResponseEntity<byte[]> silver(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.8") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.silver(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/solarize")
    public ResponseEntity<byte[]> solarize(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "threshold", defaultValue = "128") int threshold,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.solarize(file.getBytes(), threshold, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/equalize")
    public ResponseEntity<byte[]> equalize(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.equalize(file.getBytes(), format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/auto-contrast")
    public ResponseEntity<byte[]> autoContrast(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.autoContrast(file.getBytes(), format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/swap-channels")
    public ResponseEntity<byte[]> swapChannels(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "mode", defaultValue = "rg") String mode,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.swapChannels(file.getBytes(), mode, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/channel-mix")
    public ResponseEntity<byte[]> channelMix(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "rr", defaultValue = "60") int rr,
            @RequestParam(value = "gg", defaultValue = "30") int gg,
            @RequestParam(value = "bb", defaultValue = "10") int bb,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.channelMix(file.getBytes(), rr, gg, bb, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/colorize")
    public ResponseEntity<byte[]> colorize(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "color", defaultValue = "ff6b6b") String colorHex,
            @RequestParam(value = "amount", defaultValue = "0.6") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.colorize(file.getBytes(), colorHex, amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/tint")
    public ResponseEntity<byte[]> tint(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "color", defaultValue = "ff6b6b") String colorHex,
            @RequestParam(value = "amount", defaultValue = "0.4") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.tint(file.getBytes(), colorHex, amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/gradient-map")
    public ResponseEntity<byte[]> gradientMap(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "c1", defaultValue = "1e3a8a") String c1Hex,
            @RequestParam(value = "c2", defaultValue = "fbbf24") String c2Hex,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.gradientMap(file.getBytes(), c1Hex, c2Hex, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/dither")
    public ResponseEntity<byte[]> dither(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.dither(file.getBytes(), format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/halftone")
    public ResponseEntity<byte[]> halftone(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dotSize", defaultValue = "6") int dotSize,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.halftone(file.getBytes(), dotSize, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/motion-blur")
    public ResponseEntity<byte[]> motionBlur(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "radius", defaultValue = "8") int radius,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.motionBlur(file.getBytes(), radius, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/box-blur")
    public ResponseEntity<byte[]> boxBlur(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "radius", defaultValue = "4") int radius,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.boxBlur(file.getBytes(), radius, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/radial-blur")
    public ResponseEntity<byte[]> radialBlur(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "radius", defaultValue = "8") int radius,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.radialBlur(file.getBytes(), radius, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/zoom-blur")
    public ResponseEntity<byte[]> zoomBlur(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "radius", defaultValue = "10") int radius,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.zoomBlur(file.getBytes(), radius, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/unsharp-mask")
    public ResponseEntity<byte[]> unsharpMask(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "radius", defaultValue = "3") int radius,
            @RequestParam(value = "amount", defaultValue = "1.5") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.unsharpMask(file.getBytes(), radius, amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/soft-glow")
    public ResponseEntity<byte[]> softGlow(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "radius", defaultValue = "6") int radius,
            @RequestParam(value = "intensity", defaultValue = "0.6") float intensity,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.softGlow(file.getBytes(), radius, intensity, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/bloom")
    public ResponseEntity<byte[]> bloom(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "threshold", defaultValue = "180") int threshold,
            @RequestParam(value = "intensity", defaultValue = "0.6") float intensity,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.bloom(file.getBytes(), threshold, intensity, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/dreamy")
    public ResponseEntity<byte[]> dreamy(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "intensity", defaultValue = "0.5") float intensity,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.dreamy(file.getBytes(), intensity, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/glitch")
    public ResponseEntity<byte[]> glitch(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "20") int amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.glitch(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/rgb-split")
    public ResponseEntity<byte[]> rgbSplit(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "offset", defaultValue = "8") int offset,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.rgbSplit(file.getBytes(), offset, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/scanlines")
    public ResponseEntity<byte[]> scanlines(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "lineSpacing", defaultValue = "3") int lineSpacing,
            @RequestParam(value = "strength", defaultValue = "0.4") float strength,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.scanlines(file.getBytes(), lineSpacing, strength, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/fisheye")
    public ResponseEntity<byte[]> fisheye(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "strength", defaultValue = "0.5") float strength,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.fisheye(file.getBytes(), strength, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/pinch")
    public ResponseEntity<byte[]> pinch(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "0.5") float amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.pinch(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/swirl")
    public ResponseEntity<byte[]> swirl(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "degrees", defaultValue = "120") float degrees,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.swirl(file.getBytes(), degrees, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/wave")
    public ResponseEntity<byte[]> wave(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amplitude", defaultValue = "10") float amplitude,
            @RequestParam(value = "wavelength", defaultValue = "30") float wavelength,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.waveEffect(file.getBytes(), amplitude, wavelength, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/glass")
    public ResponseEntity<byte[]> glass(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "amount", defaultValue = "5") int amount,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.glass(file.getBytes(), amount, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/crystalize")
    public ResponseEntity<byte[]> crystalize(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "cellSize", defaultValue = "16") int cellSize,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.crystalize(file.getBytes(), cellSize, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/painting")
    public ResponseEntity<byte[]> painting(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "radius", defaultValue = "4") int radius,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.painting(file.getBytes(), radius, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/cartoon")
    public ResponseEntity<byte[]> cartoon(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "levels", defaultValue = "6") int levels,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.cartoon(file.getBytes(), levels, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/charcoal")
    public ResponseEntity<byte[]> charcoal(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "intensity", defaultValue = "0.8") float intensity,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.charcoal(file.getBytes(), intensity, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/kaleidoscope")
    public ResponseEntity<byte[]> kaleidoscope(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "segments", defaultValue = "8") int segments,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.kaleidoscope(file.getBytes(), segments, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/mirror-quad")
    public ResponseEntity<byte[]> mirrorQuad(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.mirrorQuad(file.getBytes(), format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/drop-shadow")
    public ResponseEntity<byte[]> dropShadow(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "offset", defaultValue = "6") int offset,
            @RequestParam(value = "blur", defaultValue = "8") int blur,
            @RequestParam(value = "color", defaultValue = "000000") String colorHex,
            @RequestParam(value = "alpha", defaultValue = "0.5") float alpha,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.dropShadow(file.getBytes(), offset, blur, colorHex, alpha, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/outer-glow")
    public ResponseEntity<byte[]> outerGlow(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "radius", defaultValue = "12") int radius,
            @RequestParam(value = "color", defaultValue = "6366f1") String colorHex,
            @RequestParam(value = "alpha", defaultValue = "0.7") float alpha,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.outerGlow(file.getBytes(), radius, colorHex, alpha, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/stroke")
    public ResponseEntity<byte[]> stroke(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam(value = "color", defaultValue = "000000") String colorHex,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.strokeOutline(file.getBytes(), width, colorHex, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/gradient-border")
    public ResponseEntity<byte[]> gradientBorder(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int borderWidth,
            @RequestParam(value = "c1", defaultValue = "6366f1") String c1Hex,
            @RequestParam(value = "c2", defaultValue = "ec4899") String c2Hex,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.gradientBorder(file.getBytes(), borderWidth, c1Hex, c2Hex, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/frame-polaroid")
    public ResponseEntity<byte[]> framePolaroid(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "border", defaultValue = "20") int border,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.polaroidFrame(file.getBytes(), border, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/frame-film")
    public ResponseEntity<byte[]> frameFilm(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "holeSize", defaultValue = "12") int holeSize,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.filmFrame(file.getBytes(), holeSize, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/caption")
    public ResponseEntity<byte[]> caption(
            @RequestParam("file") MultipartFile file,
            @RequestParam("text") String text,
            @RequestParam(value = "fontSize", defaultValue = "24") int fontSize,
            @RequestParam(value = "color", defaultValue = "ffffff") String colorHex,
            @RequestParam(value = "bg", defaultValue = "000000") String bgHex,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.caption(file.getBytes(), text, fontSize, colorHex, bgHex, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/timestamp")
    public ResponseEntity<byte[]> timestamp(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.timestamp(file.getBytes(), format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/grid-overlay")
    public ResponseEntity<byte[]> gridOverlay(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "cellSize", defaultValue = "32") int cellSize,
            @RequestParam(value = "color", defaultValue = "000000") String colorHex,
            @RequestParam(value = "alpha", defaultValue = "0.3") float alpha,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.gridOverlay(file.getBytes(), cellSize, colorHex, alpha, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/watermark-tile")
    public ResponseEntity<byte[]> watermarkTile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("text") String text,
            @RequestParam(value = "fontSize", defaultValue = "20") int fontSize,
            @RequestParam(value = "color", defaultValue = "ffffff") String colorHex,
            @RequestParam(value = "alpha", defaultValue = "0.2") float alpha,
            @RequestParam(value = "spacing", defaultValue = "120") int spacing,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.watermarkTile(file.getBytes(), text, fontSize, colorHex, alpha, spacing, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/watermark-diagonal")
    public ResponseEntity<byte[]> watermarkDiagonal(
            @RequestParam("file") MultipartFile file,
            @RequestParam("text") String text,
            @RequestParam(value = "fontSize", defaultValue = "28") int fontSize,
            @RequestParam(value = "color", defaultValue = "ffffff") String colorHex,
            @RequestParam(value = "alpha", defaultValue = "0.2") float alpha,
            @RequestParam(value = "spacing", defaultValue = "150") int spacing,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.watermarkDiagonal(file.getBytes(), text, fontSize, colorHex, alpha, spacing, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/gradient-overlay")
    public ResponseEntity<byte[]> gradientOverlay(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "c1", defaultValue = "6366f1") String c1Hex,
            @RequestParam(value = "c2", defaultValue = "ec4899") String c2Hex,
            @RequestParam(value = "alpha", defaultValue = "0.3") float alpha,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.gradientOverlay(file.getBytes(), c1Hex, c2Hex, alpha, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/bokeh")
    public ResponseEntity<byte[]> bokeh(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "count", defaultValue = "30") int count,
            @RequestParam(value = "maxRadius", defaultValue = "40") int maxRadius,
            @RequestParam(value = "color", defaultValue = "ffffff") String colorHex,
            @RequestParam(value = "alpha", defaultValue = "0.6") float alpha,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.bokehOverlay(file.getBytes(), count, maxRadius, colorHex, alpha, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/snow")
    public ResponseEntity<byte[]> snow(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "count", defaultValue = "300") int count,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.snowOverlay(file.getBytes(), count, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/rain")
    public ResponseEntity<byte[]> rain(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "count", defaultValue = "400") int count,
            @RequestParam(value = "length", defaultValue = "15") int length,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.rainOverlay(file.getBytes(), count, length, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/sparkles")
    public ResponseEntity<byte[]> sparkles(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "count", defaultValue = "40") int count,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.sparklesOverlay(file.getBytes(), count, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/droste")
    public ResponseEntity<byte[]> droste(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "count", defaultValue = "8") int count,
            @RequestParam(value = "scale", defaultValue = "0.7") float scale,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.droste(file.getBytes(), count, scale, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/tunnel")
    public ResponseEntity<byte[]> tunnel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "count", defaultValue = "12") int count,
            @RequestParam(value = "scale", defaultValue = "0.8") float scale,
            @RequestParam(value = "format", defaultValue = "png") String format,
            @RequestParam(value = "quality", defaultValue = "0.9") float quality) throws IOException {
        return buildResponse(imageService.tunnel(file.getBytes(), count, scale, format, quality), format, file.getOriginalFilename());
    }

    @PostMapping("/histogram")
    public ResponseEntity<Map<String, Object>> histogram(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.histogram(file.getBytes()));
    }

    @PostMapping("/dominant-color")
    public ResponseEntity<Map<String, Object>> dominantColor(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "count", defaultValue = "6") int count) throws IOException {
        return ResponseEntity.ok(imageService.dominantColor(file.getBytes(), count));
    }

    @PostMapping("/palette")
    public ResponseEntity<Map<String, Object>> palette(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "count", defaultValue = "8") int count) throws IOException {
        return ResponseEntity.ok(imageService.palette(file.getBytes(), count));
    }

    @PostMapping("/average-color")
    public ResponseEntity<Map<String, Object>> averageColor(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.averageColor(file.getBytes()));
    }

    @PostMapping("/brightness-meter")
    public ResponseEntity<Map<String, Object>> brightnessMeter(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.brightnessMeter(file.getBytes()));
    }

    @PostMapping("/contrast-meter")
    public ResponseEntity<Map<String, Object>> contrastMeter(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.contrastMeter(file.getBytes()));
    }

    @PostMapping("/hash-md5")
    public ResponseEntity<Map<String, Object>> hashMd5(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.hashMd5(file.getBytes()));
    }

    @PostMapping("/hash-sha256")
    public ResponseEntity<Map<String, Object>> hashSha256(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.hashSha256(file.getBytes()));
    }

    @PostMapping("/hex-dump")
    public ResponseEntity<Map<String, Object>> hexDump(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "length", defaultValue = "256") int length) throws IOException {
        return ResponseEntity.ok(imageService.hexDump(file.getBytes(), length));
    }

    @PostMapping("/detect-format")
    public ResponseEntity<Map<String, Object>> detectFormat(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.detectFormat(file.getBytes()));
    }

    @PostMapping(value = "/ascii-color", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> asciiColor(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "width", defaultValue = "100") int width) throws IOException {
        return ResponseEntity.ok(imageService.asciiColor(file.getBytes(), width));
    }

    private ResponseEntity<byte[]> buildResponse(byte[] data, String format, String originalName) {
        MediaType mediaType = mediaTypeFor(format);
        String safeName = (originalName == null ? "image" : originalName.replaceAll("\\.[^.]+$", ""));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(data.length);
        headers.setContentDispositionFormData("attachment", safeName + "-converted." + format.toLowerCase());
        return new ResponseEntity<>(data, headers, org.springframework.http.HttpStatus.OK);
    }

    private MediaType mediaTypeFor(String format) {
        if (format == null) return MediaType.APPLICATION_OCTET_STREAM;
        switch (format.toLowerCase()) {
            case "png": return MediaType.IMAGE_PNG;
            case "jpeg":
            case "jpg": return MediaType.IMAGE_JPEG;
            case "gif": return MediaType.IMAGE_GIF;
            case "bmp": return MediaType.parseMediaType("image/bmp");
            case "webp": return MediaType.parseMediaType("image/webp");
            case "tiff":
            case "tif": return MediaType.parseMediaType("image/tiff");
            default: return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
