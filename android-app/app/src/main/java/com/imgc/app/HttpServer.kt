package com.imgc.app

import android.content.Context
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * 嵌入式 HTTP 服务器（基于 NanoHTTPD）
 *
 * 端点：
 *   GET  /            -> 返回 assets/index.html
 *   GET  /static/*    -> 返回 assets 中的静态资源
 *   POST /api/{action} -> 图片处理
 *
 * 端口：8080（与本机 Spring Boot 一致，方便复用前端代码）
 */
class HttpServer(
    private val ctx: Context,
    port: Int = 8080
) : NanoHTTPD(port) {

    private val tag = "HttpServer"

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        val method = session.method
        return try {
            when {
                method == Method.GET && (uri == "/" || uri == "/index.html") ->
                    serveAsset("index.html", "text/html; charset=utf-8")
                method == Method.GET && uri.startsWith("/static/") ->
                    serveAsset(uri.removePrefix("/static/"), mimeFor(uri))
                method == Method.POST && uri.startsWith("/api/") -> {
                    val action = uri.removePrefix("/api/")
                    handleImageAction(session, action)
                }
                else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found")
            }
        } catch (t: Throwable) {
            Log.e(tag, "serve error: ${t.message}", t)
            newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "500 ${t.message}")
        }
    }

    /** 读取 assets 资源 */
    private fun serveAsset(path: String, mime: String): Response {
        return try {
            val input: InputStream = ctx.assets.open(path)
            val bytes = input.readBytes()
            input.close()
            newFixedLengthResponse(Response.Status.OK, mime, ByteArrayInputStream(bytes), bytes.size.toLong())
        } catch (e: Exception) {
            Log.w(tag, "asset not found: $path")
            newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 asset: $path")
        }
    }

    /** 处理 /api/{action} POST 请求 */
    private fun handleImageAction(session: IHTTPSession, action: String): Response {
        // NanoHTTPD 需要先解析 body；files 里存的是上传文件
        val params = mutableMapOf<String, String>()
        val files = mutableMapOf<String, String>()
        session.parseBody(files)

        // 收集表单参数
        session.parameters.forEach { (k, v) -> if (v.isNotEmpty()) params[k] = v[0] }

        // 找上传文件（"file" 字段）
        val uploadedPath = files["file"]
        if (uploadedPath == null) {
            return newFixedLengthResponse(
                Response.Status.BAD_REQUEST, "text/plain",
                "缺少 file 字段"
            )
        }
        val fileBytes = java.io.File(uploadedPath).readBytes()
        val fileName = session.parameters["file"]?.firstOrNull()
            ?: params["file"]
            ?: "image"

        val result = ImageProcessor.process(action, params, fileBytes, fileName)
        return when {
            result.error != null -> newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR, "text/plain", result.error
            )
            result.isText && result.dataUri != null -> {
                // base64 -> 返回 JSON { base64, dataUri }
                val json = JSONObject().apply {
                    put("base64", result.text ?: "")
                    put("dataUri", result.dataUri)
                }.toString()
                newFixedLengthResponse(Response.Status.OK, "application/json", json)
            }
            result.isText -> {
                // ASCII -> 纯文本
                newFixedLengthResponse(Response.Status.OK, "text/plain; charset=utf-8", result.text ?: "")
            }
            else -> {
                val bytes = result.bytes ?: return newFixedLengthResponse(
                    Response.Status.INTERNAL_ERROR, "text/plain", "no output"
                )
                val resp = newFixedLengthResponse(
                    Response.Status.OK, result.contentType,
                    ByteArrayInputStream(bytes), bytes.size.toLong()
                )
                resp.addHeader("Content-Disposition", "attachment; filename=\"${result.filename}\"")
                resp
            }
        }
    }

    private fun mimeFor(uri: String): String = when {
        uri.endsWith(".html") -> "text/html; charset=utf-8"
        uri.endsWith(".css") -> "text/css; charset=utf-8"
        uri.endsWith(".js") -> "application/javascript; charset=utf-8"
        uri.endsWith(".png") -> "image/png"
        uri.endsWith(".jpg") || uri.endsWith(".jpeg") -> "image/jpeg"
        uri.endsWith(".svg") -> "image/svg+xml"
        uri.endsWith(".ico") -> "image/x-icon"
        else -> "application/octet-stream"
    }
}
