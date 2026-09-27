package com.sarfrazqureshi.clipvault.ui

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sarfrazqureshi.clipvault.db.ClipDatabase
import com.sarfrazqureshi.clipvault.db.ClipItem
import com.sarfrazqureshi.clipvault.util.ClipClassifier
import kotlinx.coroutines.launch

class CaptureActivity : AppCompatActivity() {

    private var handled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus || handled) return
        handled = true

        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = cm.primaryClip
        val text = if (clip != null && clip.itemCount > 0)
            clip.getItemAt(0).coerceToText(this)?.toString()?.trim() else null

        if (!text.isNullOrEmpty()) {
            val type = ClipClassifier.classify(text)
            lifecycleScope.launch {
                ClipDatabase.getInstance(applicationContext).clipDao()
                    .insert(ClipItem(content = text, type = type))
            }
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Clipboard khali hai", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}
