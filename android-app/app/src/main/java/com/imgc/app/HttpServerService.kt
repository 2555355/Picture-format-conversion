package com.imgc.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat

// Foreground service: keeps HttpServer running in background
// Uses WakeLock to prevent CPU sleep + sticky restart
class HttpServerService : Service() {

    private val tag = "HttpServerService"
    private var server: HttpServer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIF_ID, buildNotification("服务启动中…"))
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // User pressed "停止服务" in notification
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        return try {
            if (server == null) {
                server = HttpServer(applicationContext, 8080).also { it.start() }
                Log.i(tag, "HttpServer started on :8080")
                updateNotification("服务运行中 · 点击停止")
                sendBroadcast(Intent(ACTION_SERVER_READY).setPackage(packageName))
            }
            // START_STICKY: system will restart service if killed
            START_STICKY
        } catch (t: Throwable) {
            Log.e(tag, "start failed: " + t.message, t)
            updateNotification("启动失败: " + t.message)
            START_STICKY
        }
    }

    private fun acquireWakeLock() {
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "imgc:server")
            wakeLock?.setReferenceCounted(false)
            wakeLock?.acquire(10 * 60 * 60 * 1000L) // 10 hours max
            Log.i(tag, "WakeLock acquired")
        } catch (t: Throwable) {
            Log.w(tag, "WakeLock failed: " + t.message)
        }
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) wakeLock?.release()
        } catch (_: Throwable) {}
        wakeLock = null
    }

    override fun onDestroy() {
        try { server?.stop() } catch (_: Throwable) {}
        server = null
        releaseWakeLock()
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
        // Click notification -> open MainActivity
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Stop action
        val stopIntent = Intent(this, HttpServerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPi = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pi)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "停止服务", stopPi)
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
        const val ACTION_STOP = "com.imgc.app.STOP"
    }
}
