package com.imgc.app

import android.content.Context
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.InputStream

// Embedded HTTP server based on NanoHTTPD
// GET  /            -> assets/index.html
// GET  /static/*    -> assets resources
// POST /api/{action} -> image processing
// Port: 8080
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
            Log.e(tag, "serve error: " + t.message, t)
            newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "500 " + t.message)
        }
    }

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

    private fun handleImageAction(session: IHTTPSession, action: String): Response {
        val params = mutableMapOf<String, String>()
        val files = mutableMapOf<String, String>()
        session.parseBody(files)

        session.parameters.forEach { (k, v) -> if (v.isNotEmpty()) params[k] = v[0] }

        val uploadedPath = files["file"]
        if (uploadedPath == null) {
            return newFixedLengthResponse(
                Response.Status.BAD_REQUEST, "text/plain",
                "missing file field"
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
                val json = JSONObject().apply {
                    put("base64", result.text ?: "")
                    put("dataUri", result.dataUri)
                }.toString()
                newFixedLengthResponse(Response.Status.OK, "application/json", json)
            }
            result.isText -> {
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
                resp.addHeader("Content-Disposition", "attachment; filename=\"" + result.filename + "\"")
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
