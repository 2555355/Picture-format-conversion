# Picture Format Conversion

A feature-rich image conversion website built with Spring Boot, supporting 20+ image processing operations.

## Features

- **Format conversion**: PNG / JPG / GIF / BMP / TIFF / PSD / WEBP (read) / PCX / TGA / PNM / etc.
- **Resizing & scaling**: Resize by pixels or scale by percentage
- **Cropping & rotation**: Crop to region, rotate by angle, flip H/V
- **Filters**: Grayscale, invert, sepia, posterize, pixelate, vignette
- **Adjustments**: Brightness/contrast, HSB (hue/saturation/brightness)
- **Effects**: Blur (Gaussian), sharpen
- **Decoration**: Text/image watermark, border, rounded corners
- **Utilities**: Image info, ASCII art, Base64 encoding, thumbnail

## Tech Stack

- Spring Boot 3.3.5 (Java 17)
- TwelveMonkeys ImageIO (extended format support)
- Java AWT / BufferedImage / Graphics2D
- Glassmorphism UI with custom SVG illustrations

## Build

```bash
mvn clean package -DskipTests
java -jar target/image-converter.jar
```

The service listens on http://localhost:8080.

## GitHub Actions

CI builds the JAR on every push to `main`. Artifacts are uploaded for 30 days.
