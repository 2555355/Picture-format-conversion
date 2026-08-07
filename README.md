# 图片工坊

一个功能丰富的在线图片处理 Web 应用，基于 Spring Boot 构建，提供 **132+ 种** 图片处理能力。

## 功能特性（132+）

### 基础操作（19）
- 格式转换（PNG/JPG/GIF/BMP/TIFF）
- 调整尺寸 / 等比缩放 / 适配缩放 / 覆盖裁剪
- 任意角度旋转 / 水平垂直翻转 / 转置 / 反对角转置
- 裁切 / 智能裁边 / 圆形裁切
- 内边距 / 画布尺寸 / 平铺拼接
- 横向分块 / 纵向分块 / 网格分块
- 去除透明 / 背景合并

### 滤镜（21）
- 黑白 / 反色 / 复古 / 亮度对比度 / 色相饱和度
- 色彩平衡 / 色调分离 / 二值化 / 伽马校正
- 通道提取 / 双色调 / 渐变映射
- 直方图均衡 / 自动对比度
- 通道互换 / 通道混合
- 染色 / 浅染色 / 抖动 / 半色调 / 日晒

### 电影感滤镜（16）
- 冷色调 / 暖色调 / 老照片 / Lomo / 电影感
- 褪色 / 马卡龙 / 霓虹 / 热成像 / 夜视 / 红外
- 宝丽来 / 漂白旁路 / 交叉冲洗 / 蓝晒 / 银盐

### 特效（20）
- 高斯模糊 / 盒状模糊 / 动感模糊 / 径向模糊 / 缩放模糊
- 锐化 / USM 锐化 / 柔光 / 泛光 / 梦幻
- 像素化 / 暗角 / 边缘检测 / 浮雕 / 素描 / 噪点
- 暗角

### 扭曲特效（11）
- 鱼眼 / 挤压 / 漩涡 / 波浪 / 玻璃 / 水晶
- 故障艺术 / RGB 分离 / 扫描线
- 德罗斯特画中画 / 同心隧道

### 艺术风格（5）
- 油画 / 卡通 / 炭笔 / 万花筒 / 四向镜像

### 装饰叠加（17）
- 加边框 / 圆角 / 缩略图
- 文字水印 / 平铺水印 / 斜向水印
- 网格线 / 渐变叠层
- 渐变边框 / 宝丽来框 / 胶片框
- 字幕条 / 时间戳
- 投影 / 外发光 / 描边

### 自然叠加（4）
- 散景光斑 / 雪花 / 雨丝 / 星光

### 工具（8）
- 图片信息 / 字符画 / 彩色字符画 / Base64 编码
- 格式识别 / 十六进制查看
- MD5 哈希 / SHA256 哈希

### 图像分析（6）
- 直方图 / 主色调 / 调色板 / 平均色 / 亮度计 / 对比度计

## 技术栈

- Spring Boot 3.3.5 (Java 17)
- TwelveMonkeys ImageIO（WEBP/TIFF/PSD/PCX/TGA/PNM 等扩展格式支持）
- ONNX Runtime（本地 AI 超分辨率推理）
- Java AWT / BufferedImage / Graphics2D
- 现代简约 UI 设计（多 Tab 分类）

## 本地构建运行

```bash
mvn clean package -DskipTests
java -jar target/image-converter.jar
```

服务运行在 http://localhost:8080

## API 调用示例

所有接口都是 `POST /api/{action}`，使用 `multipart/form-data` 上传 `file` 字段，附加各操作所需参数：

```bash
# 格式转换
curl -F "file=@input.png" -F "format=jpeg" -F "quality=0.9" http://localhost:8080/api/convert -o out.jpg

# 调整尺寸
curl -F "file=@input.png" -F "width=800" -F "height=600" http://localhost:8080/api/resize -o out.png

# 电影感滤镜
curl -F "file=@input.png" -F "amount=0.8" http://localhost:8080/api/cinematic -o out.png

# 故障艺术
curl -F "file=@input.png" -F "amount=20" http://localhost:8080/api/glitch -o out.png

# 图像分析（返回 JSON）
curl -F "file=@input.png" http://localhost:8080/api/histogram
```

## 支持的图像格式

| 类型 | 支持读取 | 支持写出 |
| --- | --- | --- |
| PNG | ✓ | ✓ |
| JPEG/JPG | ✓ | ✓ |
| GIF | ✓ | ✓ |
| BMP | ✓ | ✓ |
| TIFF | ✓ | ✓ |
| WEBP | ✓ | ✗ |
| PSD | ✓ | ✗ |
| PCX/TGA/PNM/IFF | ✓ | ✗ |

## GitHub Actions

每次推送到 `main` 分支会自动构建 JAR，构件保留 30 天可下载。

## AI 声明

本项目仅在 **超清增强（AI 超分）** 功能中使用人工智能模型，其他 130+ 项功能均为传统图像处理算法（Java AWT / BufferedImage / 自实现算法），不涉及 AI 推理。

### AI 模型

| 项目 | 内容 |
| --- | --- |
| 模型 | Real-ESRGAN General x4 v3 |
| 格式 | ONNX |
| 用途 | 图像超分辨率（2x/3x/4x 上采样） |
| 推理框架 | ONNX Runtime（本地 CPU 推理） |
| 模型来源 | https://github.com/xinntao/Real-ESRGAN （Apache-2.0 / MIT） |
| 模型大小 | 约 4.65 MB |
| 存放位置 | `{jar 同级目录}/models/realesr-general-x4v3.onnx` |

### 数据隐私

- **完全本地推理**：所有 AI 推理在用户机器上完成，**图片数据不会上传到任何远程服务器**
- **无网络依赖**：模型下载完成后即可离线使用
- **无数据收集**：不收集任何用户数据、图像内容或使用统计
- **开源透明**：所有算法（除模型权重外）均开源可审计

### 局限与免责

- AI 推理仅作为辅助工具，结果仅供参考
- 处理时间取决于图片大小与硬件性能（CPU 推理约 1–10 秒/张）
- 4x 超分对极小图（< 64px）可能产生伪影
- 模型遵循上游许可协议，使用本项目即代表您同意 Real-ESRGAN 的相关条款
