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

    override fun onServiceConnected() {
        super.onServiceConnected()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Explicitly listen for window/content change events — this keeps the
        // service "active" so the OS allows clipboard reads from here.
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
        serviceInfo = info

        showToast("ClipVault service active")
    }

    // Checked on every relevant accessibility event, not just via a passive listener.
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
        showToast("Saved: ${text.take(20)}")
        val type = ClipClassifier.classify(text)
        scope.launch {
            val dao = ClipDatabase.getInstance(applicationContext).clipDao()
            dao.insert(ClipItem(content = text, type = type))
        }
    }

    private fun showToast(msg: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInterrupt() {}
}
