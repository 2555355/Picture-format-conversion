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

    @PostMapping("/watermark/text")
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

    @PostMapping("/watermark/image")
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
