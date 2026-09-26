package com.sarfrazqureshi.clipvault.service

import android.accessibilityservice.AccessibilityService
import android.content.ClipboardManager
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import com.sarfrazqureshi.clipvault.db.ClipDatabase
import com.sarfrazqureshi.clipvault.db.ClipItem
import com.sarfrazqureshi.clipvault.util.ClipClassifier

/**
 * Uses Android's Accessibility Service permission to read the clipboard
 * even while ClipVault is not the app currently open on screen.
 * (Regular apps are blocked from doing this since Android 10 — accessibility
 * services are exempt, which is how real clipboard-manager apps work.)
 */
class ClipCaptureService : AccessibilityService() {

    private lateinit var clipboardManager: ClipboardManager
    private var lastSaved: String? = null

    private val listener = ClipboardManager.OnPrimaryClipChangedListener {
        val clip = clipboardManager.primaryClip ?: return@OnPrimaryClipChangedListener
        if (clip.itemCount == 0) return@OnPrimaryClipChangedListener
        val text = clip.getItemAt(0).coerceToText(this)?.toString()?.trim() ?: return@OnPrimaryClipChangedListener
        if (text.isEmpty() || text == lastSaved) return@OnPrimaryClipChangedListener

        lastSaved = text
        saveClip(text)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.addPrimaryClipChangedListener(listener)
    }

    private fun saveClip(text: String) {
        val type = ClipClassifier.classify(text)
        val dao = ClipDatabase.getInstance(applicationContext).clipDao()
        // AccessibilityService has no built-in coroutine scope, so use a simple thread.
        Thread {
            kotlinx.coroutines.runBlocking {
                dao.insert(ClipItem(content = text, type = type))
            }
        }.start()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not needed — we only care about clipboard changes, handled above.
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        if (::clipboardManager.isInitialized) {
            clipboardManager.removePrimaryClipChangedListener(listener)
        }
    }
}
