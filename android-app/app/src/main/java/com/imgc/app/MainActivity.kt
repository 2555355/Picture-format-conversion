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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var openBtn: Button

    private val readyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == HttpServerService.ACTION_SERVER_READY) {
                onServerReady()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statusText = findViewById(R.id.statusText)
        progressBar = findViewById(R.id.progressBar)
        openBtn = findViewById(R.id.openBtn)

        openBtn.setOnClickListener { openInBrowser() }

        // 启动前台服务（Android 9+ 需要前台服务）
        val svc = Intent(this, HttpServerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(svc)
        } else {
            startService(svc)
        }
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

    private fun onServerReady() {
        statusText.text = "服务已启动 · http://localhost:8080"
        progressBar.visibility = View.GONE
        openBtn.visibility = View.VISIBLE
        // 自动跳转浏览器
        openBtn.postDelayed({ openInBrowser() }, 400)
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
