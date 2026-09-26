package com.sarfrazqureshi.clipvault.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sarfrazqureshi.clipvault.databinding.ActivityPinBinding
import com.sarfrazqureshi.clipvault.util.PinManager

class PinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinBinding
    private var isSettingUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isSettingUp = !PinManager.isPinSet(this)
        binding.title.text = if (isSettingUp)
            getString(com.sarfrazqureshi.clipvault.R.string.set_pin_title)
        else
            getString(com.sarfrazqureshi.clipvault.R.string.enter_pin_title)

        binding.confirmButton.setOnClickListener {
            val pin = binding.pinInput.text.toString().trim()
            if (pin.length < 4) {
                Toast.makeText(this, "Kam se kam 4 digit ka PIN dakhil karen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isSettingUp) {
                PinManager.setPin(this, pin)
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                if (PinManager.verifyPin(this, pin)) {
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, getString(com.sarfrazqureshi.clipvault.R.string.pin_mismatch), Toast.LENGTH_SHORT).show()
                    binding.pinInput.text?.clear()
                }
            }
        }
    }
}
