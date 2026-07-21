package com.imgc.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64

// Image processor using Android native Bitmap/Canvas
// Supports all 19 actions from the Spring Boot version
object ImageProcessor {

    // Result: success = bytes + contentType + filename; failure = error message
    data class Result(
        val bytes: ByteArray? = null,
        val contentType: String = "image/png",
        val filename: String = "converted.png",
        val text: String? = null,
        val isText: Boolean = false,
        val dataUri: String? = null,
        val error: String? = null
    )

    // Entry: route to handler by action name
    fun process(action: String, params: Map<String, String>, fileBytes: ByteArray, fileName: String): Result {
        return try {
            val opts = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
            val src = BitmapFactory.decodeStream(ByteArrayInputStream(fileBytes), null, opts)
                ?: return Result(error = "无法解码图片")
            val fmt = (params["format"] ?: "png").lowercase()
            val baseName = fileName.substringBeforeLast('.')

            val (bitmap, ext) = when (action) {
                "convert" -> Pair(src, fmt)
                "resize" -> {
                    val w = (params["width"]?.toIntOrNull() ?: 800).coerceIn(1, 10000)
                    val h = (params["height"]?.toIntOrNull() ?: 600).coerceIn(1, 10000)
                    Pair(Bitmap.createScaledBitmap(src, w, h, true), fmt)
                }
                "scale" -> {
                    val p = (params["percent"]?.toFloatOrNull() ?: 50f) / 100f
                    val w = (src.width * p).toInt().coerceAtLeast(1)
                    val h = (src.height * p).toInt().coerceAtLeast(1)
                    Pair(Bitmap.createScaledBitmap(src, w, h, true), fmt)
                }
                "rotate" -> {
                    val deg = params["degrees"]?.toFloatOrNull() ?: 90f
                    val m = Matrix().apply { postRotate(deg) }
                    Pair(Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true), fmt)
                }
                "flip" -> {
                    val sx = if (params["direction"] == "vertical") 1f else -1f
                    val sy = if (params["direction"] == "vertical") -1f else 1f
                    val m = Matrix().apply { preScale(sx, sy) }
                    Pair(Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true), fmt)
                }
                "grayscale" -> Pair(applyColorMatrix(src, ColorMatrix().apply { setSaturation(0f) }), fmt)
                "invert" -> Pair(applyColorMatrix(src, ColorMatrix(floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                ))), fmt)
                "sepia" -> Pair(applyColorMatrix(src, ColorMatrix(floatArrayOf(
                    0.393f, 0.769f, 0.189f, 0f, 0f,
                    0.349f, 0.686f, 0.168f, 0f, 0f,
                    0.272f, 0.534f, 0.131f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))), fmt)
                "brightness-contrast" -> {
                    val b = params["brightness"]?.toFloatOrNull() ?: 0.1f
                    val c = params["contrast"]?.toFloatOrNull() ?: 0.1f
                    val cm = ColorMatrix().apply {
                        setSaturation(1f)
                        postConcat(ColorMatrix(floatArrayOf(
                            c + 1f, 0f, 0f, 0f, b * 255f,
                            0f, c + 1f, 0f, 0f, b * 255f,
                            0f, 0f, c + 1f, 0f, b * 255f,
                            0f, 0f, 0f, 1f, 0f
                        )))
                    }
                    Pair(applyColorMatrix(src, cm), fmt)
                }
                "blur" -> {
                    val r = (params["radius"]?.toFloatOrNull() ?: 5f).coerceIn(1f, 30f)
                    Pair(blurBitmap(src, r), fmt)
                }
                "sharpen" -> Pair(convolve(src, floatArrayOf(
                    0f, -1f, 0f,
                    -1f, 5f, -1f,
                    0f, -1f, 0f
                )), fmt)
                "pixelate" -> {
                    val ps = (params["pixelSize"]?.toIntOrNull() ?: 10).coerceIn(2, 40)
                    Pair(pixelate(src, ps), fmt)
                }
                "vignette" -> {
                    val s = (params["strength"]?.toFloatOrNull() ?: 0.5f).coerceIn(0.1f, 1f)
                    Pair(vignette(src, s), fmt)
                }
                "border" -> {
                    val w = (params["width"]?.toIntOrNull() ?: 10).coerceIn(1, 50)
                    val color = parseColor(params["color"] ?: "000000")
                    Pair(addBorder(src, w, color), fmt)
                }
                "round-corners" -> {
                    val r = (params["radius"]?.toIntOrNull() ?: 30).coerceIn(1, 200)
                    Pair(roundCorners(src, r), fmt)
                }
                "thumbnail" -> {
                    val max = (params["maxSize"]?.toIntOrNull() ?: 128).coerceIn(32, 500)
                    val scale = max.toFloat() / maxOf(src.width, src.height)
                    val w = (src.width * scale).toInt().coerceAtLeast(1)
                    val h = (src.height * scale).toInt().coerceAtLeast(1)
                    Pair(Bitmap.createScaledBitmap(src, w, h, true), fmt)
                }
                "watermark-text" -> {
                    val text = params["text"] ?: "水印"
                    val pos = params["position"] ?: "center"
                    val fs = (params["fontSize"]?.toIntOrNull() ?: 32).coerceIn(12, 120)
                    val color = parseColor(params["color"] ?: "ffffff")
                    val alpha = (params["alpha"]?.toFloatOrNull() ?: 0.8f).coerceIn(0.1f, 1f)
                    Pair(watermark(src, text, pos, fs, color, alpha), fmt)
                }
                "ascii" -> {
                    val width = (params["width"]?.toIntOrNull() ?: 100).coerceIn(40, 200)
                    return Result(text = toAscii(src, width), filename = "$baseName-ascii.txt", contentType = "text/plain", isText = true)
                }
                "base64" -> {
                    val outBytes = encode(src, fmt, params["quality"]?.toFloatOrNull() ?: 0.9f)
                    val b64 = Base64.getEncoder().encodeToString(outBytes)
                    val mime = mimeFor(fmt)
                    return Result(
                        text = b64,
                        dataUri = "data:$mime;base64,$b64",
                        filename = "$baseName.json",
                        contentType = "application/json",
                        isText = true
                    )
                }
                else -> return Result(error = "未知操作: $action")
            }

            val bytes = encode(bitmap, ext, params["quality"]?.toFloatOrNull() ?: 0.9f)
            Result(bytes = bytes, contentType = mimeFor(ext), filename = "$baseName-converted.$ext")
        } catch (t: Throwable) {
            Result(error = t.message ?: "处理失败")
        }
    }

    /* ============ 内部工具 ============ */

    private fun applyColorMatrix(src: Bitmap, cm: ColorMatrix): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(cm)
            isAntiAlias = true
        }
        Canvas(out).drawBitmap(src, 0f, 0f, paint)
        return out
    }

    private fun blurBitmap(src: Bitmap, radius: Float): Bitmap {
        val scale = if (src.width > 800) 800f / src.width else 1f
        val w = (src.width * scale).toInt().coerceAtLeast(1)
        val h = (src.height * scale).toInt().coerceAtLeast(1)
        val small = Bitmap.createScaledBitmap(src, w, h, true)
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            isAntiAlias = true
            maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
        }
        Canvas(out).drawBitmap(small, 0f, 0f, paint)
        return if (scale < 1f) Bitmap.createScaledBitmap(out, src.width, src.height, true) else out
    }

    private fun convolve(src: Bitmap, kernel: FloatArray): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val w = src.width
        val h = src.height
        val pixels = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)
        val outPixels = IntArray(w * h)
        val kSize = Math.sqrt(kernel.size.toDouble()).toInt()
        val kHalf = kSize / 2
        for (y in 0 until h) {
            for (x in 0 until w) {
                var r = 0f; var g = 0f; var b = 0f
                for (ky in 0 until kSize) {
                    for (kx in 0 until kSize) {
                        val px = (x + kx - kHalf).coerceIn(0, w - 1)
                        val py = (y + ky - kHalf).coerceIn(0, h - 1)
                        val c = pixels[py * w + px]
                        val kv = kernel[ky * kSize + kx]
                        r += Color.red(c) * kv
                        g += Color.green(c) * kv
                        b += Color.blue(c) * kv
                    }
                }
                val a = Color.alpha(pixels[y * w + x])
                outPixels[y * w + x] = Color.argb(a, r.toInt().coerceIn(0, 255), g.toInt().coerceIn(0, 255), b.toInt().coerceIn(0, 255))
            }
        }
        out.setPixels(outPixels, 0, w, 0, 0, w, h)
        return out
    }

    private fun pixelate(src: Bitmap, size: Int): Bitmap {
        val w = src.width
        val h = src.height
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val paint = Paint().apply { isAntiAlias = false }
        var y = 0
        while (y < h) {
            var x = 0
            while (x < w) {
                val c = src.getPixel(x.coerceIn(0, w - 1), y.coerceIn(0, h - 1))
                paint.color = c
                canvas.drawRect(
                    x.toFloat(), y.toFloat(),
                    (x + size).toFloat().coerceAtMost(w.toFloat()),
                    (y + size).toFloat().coerceAtMost(h.toFloat()),
                    paint
                )
                x += size
            }
            y += size
        }
        return out
    }

    private fun vignette(src: Bitmap, strength: Float): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawBitmap(src, 0f, 0f, null)
        val w = src.width.toFloat()
        val h = src.height.toFloat()
        val cx = w / 2
        val cy = h / 2
        val r = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val paint = Paint().apply {
            isAntiAlias = true
            shader = android.graphics.RadialGradient(
                cx, cy, r,
                intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.argb((255 * strength).toInt(), 0, 0, 0)),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, paint)
        return out
    }

    private fun addBorder(src: Bitmap, borderWidth: Int, color: Int): Bitmap {
        val w = src.width + borderWidth * 2
        val h = src.height + borderWidth * 2
        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(color)
        canvas.drawBitmap(src, borderWidth.toFloat(), borderWidth.toFloat(), null)
        return out
    }

    private fun roundCorners(src: Bitmap, radius: Int): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
        }
        val rect = RectF(0f, 0f, src.width.toFloat(), src.height.toFloat())
        canvas.drawRoundRect(rect, radius.toFloat(), radius.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, 0f, 0f, paint)
        return out
    }

    private fun watermark(src: Bitmap, text: String, position: String, fontSize: Int, color: Int, alpha: Float): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawBitmap(src, 0f, 0f, null)
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = fontSize.toFloat()
            typeface = Typeface.DEFAULT_BOLD
            this.color = applyAlpha(color, alpha)
            textAlign = Paint.Align.LEFT
        }
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val tw = bounds.width().toFloat()
        val th = bounds.height().toFloat()
        val pad = 16f
        val (x, y) = when (position) {
            "top-left" -> Pair(pad, pad + th)
            "top-right" -> Pair(src.width - tw - pad, pad + th)
            "bottom-left" -> Pair(pad, src.height - pad)
            "bottom-right" -> Pair(src.width - tw - pad, src.height - pad)
            else -> Pair((src.width - tw) / 2f, (src.height + th) / 2f)
        }
        canvas.drawText(text, x, y, paint)
        return out
    }

    private fun toAscii(src: Bitmap, width: Int): String {
        val scale = width.toFloat() / src.width
        val h = (src.height * scale * 0.5f).toInt().coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(src, width, h, true)
        val chars = "@%#*+=-:. "
        val sb = StringBuilder()
        for (y in 0 until h) {
            for (x in 0 until width) {
                val c = scaled.getPixel(x, y)
                val lum = (Color.red(c) * 0.299 + Color.green(c) * 0.587 + Color.blue(c) * 0.114).toInt()
                sb.append(chars[(lum * (chars.length - 1) / 255)])
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    private fun encode(bitmap: Bitmap, format: String, quality: Float): ByteArray {
        val baos = ByteArrayOutputStream()
        val fmt = when (format) {
            "jpeg", "jpg" -> Bitmap.CompressFormat.JPEG
            "webp" -> Bitmap.CompressFormat.WEBP
            "png" -> Bitmap.CompressFormat.PNG
            else -> Bitmap.CompressFormat.PNG
        }
        bitmap.compress(fmt, (quality * 100).toInt().coerceIn(1, 100), baos)
        return baos.toByteArray()
    }

    private fun mimeFor(fmt: String): String = when (fmt.lowercase()) {
        "jpeg", "jpg" -> "image/jpeg"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        "bmp" -> "image/bmp"
        else -> "image/png"
    }

    private fun parseColor(hex: String): Int {
        val h = hex.removePrefix("#")
        return try {
            if (h.length == 6) Color.parseColor("#$h") else Color.BLACK
        } catch (e: Exception) { Color.BLACK }
    }

    private fun applyAlpha(color: Int, alpha: Float): Int {
        val a = (255 * alpha).toInt().coerceIn(0, 255)
        return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color))
    }
}
