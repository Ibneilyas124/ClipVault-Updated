package com.sarfrazqureshi.clipvault.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.sarfrazqureshi.clipvault.db.ClipDatabase
import com.sarfrazqureshi.clipvault.db.ClipItem
import com.sarfrazqureshi.clipvault.util.ClipClassifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClipCaptureService : AccessibilityService() {

    private lateinit var clipboardManager: ClipboardManager
    private var lastSaved: String? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val listener = ClipboardManager.OnPrimaryClipChangedListener {
        checkClipboard()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.addPrimaryClipChangedListener(listener)

        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
        info.packageNames = null // listen across every app, not just ClipVault
        serviceInfo = info
        android.widget.Toast.makeText(applicationContext, "ClipVault service started", android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        checkClipboard()
    }

    private fun checkClipboard() {
        if (!::clipboardManager.isInitialized) return
        val clip = clipboardManager.primaryClip ?: return
        if (clip.itemCount == 0) return
        val text = clip.getItemAt(0).coerceToText(this)?.toString()?.trim() ?: return
        if (text.isEmpty() || text == lastSaved) return

        lastSaved = text
        saveClip(text)
    }

    private fun saveClip(text: String) {
        android.widget.Toast.makeText(applicationContext, "Captured: ${text.take(15)}", android.widget.Toast.LENGTH_SHORT).show()
        val type = ClipClassifier.classify(text)
        scope.launch {
            val dao = ClipDatabase.getInstance(applicationContext).clipDao()
            dao.insert(ClipItem(content = text, type = type))
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        if (::clipboardManager.isInitialized) {
            clipboardManager.removePrimaryClipChangedListener(listener)
        }
    }
}
