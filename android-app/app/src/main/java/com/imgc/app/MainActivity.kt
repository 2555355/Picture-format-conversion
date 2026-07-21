package com.imgc.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var permText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var openBtn: Button
    private lateinit var permBtn: Button

    private var serverReady = false

    private val readyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == HttpServerService.ACTION_SERVER_READY) {
                serverReady = true
                refreshUi()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statusText = findViewById(R.id.statusText)
        permText = findViewById(R.id.permText)
        progressBar = findViewById(R.id.progressBar)
        openBtn = findViewById(R.id.openBtn)
        permBtn = findViewById(R.id.permBtn)

        openBtn.setOnClickListener { openInBrowser() }
        permBtn.setOnClickListener { requestAllPermissions() }

        // Step 1: request permissions (notification + battery whitelist)
        requestAllPermissions()

        // Step 2: start foreground service
        startHttpService()
    }

    private fun requestAllPermissions() {
        PermissionHelper.requestNotificationPermission(this)
        PermissionHelper.requestIgnoreBatteryOptimizations(this)
        refreshUi()
    }

    private fun startHttpService() {
        val svc = Intent(this, HttpServerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(svc)
        } else {
            startService(svc)
        }
    }

    override fun onResume() {
        super.onResume()
        // User may have just come back from system permission screens
        refreshUi()
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(HttpServerService.ACTION_SERVER_READY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(readyReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(readyReceiver, filter)
        }
    }

    override fun onStop() {
        unregisterReceiver(readyReceiver)
        super.onStop()
    }

    // Called when returning from permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        refreshUi()
    }

    private fun refreshUi() {
        val hasNotif = PermissionHelper.hasNotificationPermission(this)
        val hasBattery = PermissionHelper.isIgnoringBatteryOptimizations(this)
        val allOk = hasNotif && hasBattery

        if (!allOk) {
            // Show permission panel
            permText.visibility = View.VISIBLE
            permBtn.visibility = View.VISIBLE
            val sb = StringBuilder()
            if (!hasNotif) sb.append("• 通知权限（前台服务必需）\n")
            if (!hasBattery) sb.append("• 电池优化白名单（后台不被杀）\n")
            permText.text = sb.toString().trimEnd()
            permBtn.text = "授予后台运行权限"
        } else {
            permText.visibility = View.GONE
            permBtn.visibility = View.GONE
        }

        if (serverReady) {
            statusText.text = "服务运行中 · http://localhost:8080" +
                if (allOk) "\n✓ 已获得后台运行权限" else ""
            progressBar.visibility = View.GONE
            openBtn.visibility = View.VISIBLE
            if (allOk) {
                openBtn.postDelayed({ openInBrowser() }, 400)
            }
        } else {
            statusText.text = "正在启动本地服务…"
            progressBar.visibility = View.VISIBLE
            openBtn.visibility = View.GONE
        }
    }

    private fun openInBrowser() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://localhost:8080/"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            statusText.text = "没有可用的浏览器"
        }
    }
}
