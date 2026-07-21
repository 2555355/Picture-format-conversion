# 图画工坊 · Android APK

把网页版图片处理工具打包成 APK：应用启动后在后台跑一个嵌入式 HTTP 服务（NanoHTTPD），然后自动跳转到系统浏览器打开 `http://localhost:8080/`。

## 架构

- **前端**：复用 Spring Boot 项目的 `index.html`（位于 `app/src/main/assets/`）
- **后端**：`HttpServer.kt`（NanoHTTPD） + `ImageProcessor.kt`（Android Bitmap/Canvas 实现图片处理）
- **服务**：`HttpServerService.kt` 前台服务，保证切到浏览器后服务不被回收
- **入口**：`MainActivity.kt` 启动服务 → 收到 READY 广播 → 用 `Intent.ACTION_VIEW` 跳浏览器

## 端点

| 路径 | 方法 | 说明 |
|---|---|---|
| `/` | GET | 返回 `assets/index.html` |
| `/static/*` | GET | 返回 assets 中的静态资源 |
| `/api/{action}` | POST | 图片处理（action = convert / resize / rotate / grayscale / blur / ...） |

支持 19 项功能，与 Spring Boot 版完全一致。

## 编译 APK

沙箱里没有 Android SDK，所以需要你在本地用 Android Studio 编译：

### 方法 1：Android Studio（推荐）

1. 下载本仓库：`git clone https://github.com/2555355/Picture-format-conversion.git`
2. Android Studio → `Open` → 选择 `android-app/` 目录
3. 等待 Gradle sync 完成（首次会下载 Gradle 8.7 + Android SDK 34）
4. `Build → Build APK(s)` → `Build APK(s)`
5. 输出在 `android-app/app/build/outputs/apk/debug/app-debug.apk`

### 方法 2：命令行

```bash
cd android-app
# 设置 Android SDK 路径
echo "sdk.dir=/path/to/Android/Sdk" > local.properties
# Gradle 构建
./gradlew assembleDebug
# 输出
ls app/build/outputs/apk/debug/app-debug.apk
```

### 在手机上安装

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

或把 APK 文件传到手机上点击安装（需开启"未知来源应用"）。

## 使用

1. 打开应用 → 自动启动服务 → 自动跳转浏览器
2. 在浏览器中上传图片、选择功能、下载结果
3. 关闭应用 = 停止服务（也可以在通知栏点"图画工坊"通知停止）

## 系统要求

- Android 7.0 (API 24) 及以上
- 需要授予"通知"权限（前台服务通知）
- 部分浏览器可能拦截 localhost，建议使用 Chrome / Edge

## 与 Spring Boot 版的差异

| 项目 | Spring Boot JAR | Android APK |
|---|---|---|
| 后端 | Java + TwelveMonkeys ImageIO | Kotlin + Android Bitmap/Canvas |
| HTTP | Spring MVC | NanoHTTPD |
| 部署 | 任意 OS 跑 `java -jar` | 安装到 Android |
| 端口 | 8080 | 8080 |
| 前端 | 同一份 index.html | 同一份 index.html |

## 文件结构

```
android-app/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/imgc/app/
│       │   ├── MainActivity.kt          # 入口 Activity
│       │   ├── HttpServerService.kt     # 前台服务
│       │   ├── HttpServer.kt            # NanoHTTPD 服务器
│       │   └── ImageProcessor.kt        # 图片处理（19 项功能）
│       └── assets/index.html            # 前端页面
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradle/wrapper/gradle-wrapper.properties
```
