package com.sarfrazqureshi.clipvault.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sarfrazqureshi.clipvault.databinding.ActivityPinBinding
import com.sarfrazqureshi.clipvault.util.PinManager

class PinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinBinding
    private var mode = "verify"
    private var step = "verify"
    private var pendingNewPin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mode = intent.getStringExtra("mode") ?: "verify"
        step = when {
            mode == "change" -> "old"
            !PinManager.isPinSet(this) -> "new"
            else -> "verify"
        }
        updateUi()

        binding.confirmButton.setOnClickListener { handleStep() }
    }

    private fun updateUi() {
        binding.title.text = when (step) {
            "old" -> "Purana PIN dakhil karen"
            "new" -> "Naya PIN set karen"
            "confirm" -> "Naya PIN dobara dakhil karen (confirm)"
            else -> "PIN dakhil karen"
        }
        binding.pinInput.text?.clear()
    }

    private fun handleStep() {
        val pin = binding.pinInput.text.toString().trim()
        if (pin.length < 4) {
            Toast.makeText(this, "Kam se kam 4 digit ka PIN dakhil karen", Toast.LENGTH_SHORT).show()
            return
        }

        when (step) {
            "old" -> {
                if (PinManager.verifyPin(this, pin)) {
                    step = "new"
                    updateUi()
                } else {
                    Toast.makeText(this, "Galat PIN", Toast.LENGTH_SHORT).show()
                    binding.pinInput.text?.clear()
                }
            }
            "new" -> {
                pendingNewPin = pin
                step = "confirm"
                updateUi()
            }
            "confirm" -> {
                if (pin == pendingNewPin) {
                    PinManager.setPin(this, pin)
                    Toast.makeText(this, "PIN save ho gaya", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "PIN match nahi hua, dobara try karen", Toast.LENGTH_SHORT).show()
                    pendingNewPin = null
                    step = "new"
                    updateUi()
                }
            }
            "verify" -> {
                if (PinManager.verifyPin(this, pin)) {
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "PIN match nahi hua, dobara koshish karen", Toast.LENGTH_SHORT).show()
                    binding.pinInput.text?.clear()
                }
            }
        }
    }
}
