package com.sarfrazqureshi.clipvault.util

import android.content.Context
import java.security.MessageDigest

object PinManager {
    private const val PREFS = "clipvault_secure_prefs"
    private const val KEY_PIN_HASH = "pin_hash"

    private fun hash(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun isPinSet(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.contains(KEY_PIN_HASH)
    }

    fun setPin(context: Context, pin: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PIN_HASH, hash(pin)).apply()
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val stored = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return stored == hash(pin)
    }
}
