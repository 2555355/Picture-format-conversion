# 图片工坊

一个功能丰富的在线图片处理 Web 应用，基于 Spring Boot 构建，提供 19+ 种图片处理能力。

## 功能特性

- **格式转换**：PNG / JPG / GIF / BMP / TIFF / PSD / WEBP / PCX / TGA / PNM 等
- **尺寸调整**：按像素调整、按百分比缩放
- **方向操作**：任意角度旋转、水平/垂直翻转
- **滤镜效果**：黑白、反色、复古、亮度对比度
- **特殊效果**：高斯模糊、锐化、像素化、暗角
- **装饰美化**：边框、圆角、缩略图、文字水印
- **实用工具**：字符画、Base64 编码

## 技术栈

- Spring Boot 3.3.5 (Java 17)
- TwelveMonkeys ImageIO（扩展格式支持）
- Java AWT / BufferedImage / Graphics2D
- 现代简约 UI 设计

## 本地构建运行

```bash
mvn clean package -DskipTests
java -jar target/image-converter.jar
```

服务运行在 http://localhost:8080

## GitHub Actions

每次推送到 `main` 分支会自动构建 JAR，构件保留 30 天可下载。
