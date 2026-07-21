package com.imgc.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

// Foreground service: keeps HttpServer running in background
// so the service survives when user switches to browser
class HttpServerService : Service() {

    private val tag = "HttpServerService"
    private var server: HttpServer? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIF_ID, buildNotification("服务启动中…"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if (server == null) {
                server = HttpServer(applicationContext, 8080).also { it.start() }
                Log.i(tag, "HttpServer started on :8080")
                updateNotification("服务已就绪 · http://localhost:8080")
                // 通知 Activity
                sendBroadcast(Intent(ACTION_SERVER_READY).setPackage(packageName))
            }
        } catch (t: Throwable) {
            Log.e(tag, "start failed: ${t.message}", t)
            updateNotification("启动失败: ${t.message}")
        }
        return START_STICKY
    }

    override fun onDestroy() {
        try { server?.stop() } catch (_: Throwable) {}
        server = null
        Log.i(tag, "HttpServer stopped")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_desc)
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID, buildNotification(text))
    }

    companion object {
        const val CHANNEL_ID = "imgc_server"
        const val NOTIF_ID = 1001
        const val ACTION_SERVER_READY = "com.imgc.app.SERVER_READY"
    }
}
