package com.sarfrazqureshi.clipvault.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.method.LinkMovementMethod
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.sarfrazqureshi.clipvault.R
import com.sarfrazqureshi.clipvault.databinding.ActivityMainBinding
import com.sarfrazqureshi.clipvault.databinding.DialogDeveloperInfoBinding
import com.sarfrazqureshi.clipvault.db.ClipType

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tabTypes = listOf(ClipType.NUMBER, ClipType.LINK, ClipType.TEXT, ClipType.ADULT)
    private val tabTitles = listOf(
        R.string.tab_numbers, R.string.tab_links, R.string.tab_text, R.string.tab_adult
    )

    private val pinLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.viewPager.setCurrentItem(3, true)
        } else {
            binding.viewPager.setCurrentItem(0, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = tabTypes.size
            override fun createFragment(position: Int) = ClipListFragment.newInstance(tabTypes[position])
        }
        binding.viewPager.isUserInputEnabled = false // force tab-bar navigation so we can guard the locked tab

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.setText(tabTitles[position])
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                if (tab.position == 3) {
                    pinLauncher.launch(Intent(this@MainActivity, PinActivity::class.java))
                } else {
                    binding.viewPager.setCurrentItem(tab.position, true)
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
        })

        binding.developerCredit.setOnClickListener { showDeveloperDialog() }

        promptAccessibilityIfNeeded()
    }

    private fun showDeveloperDialog() {
        val dialogBinding = DialogDeveloperInfoBinding.inflate(layoutInflater)
        dialogBinding.devName.text = getString(R.string.dev_name)
        dialogBinding.devWhatsapp.text = getString(R.string.dev_whatsapp)
        dialogBinding.devEmail.text = getString(R.string.dev_email)
        dialogBinding.devSkills.text = getString(R.string.dev_skills)
        dialogBinding.devPassion.text = getString(R.string.dev_passion)
        // Plain informational text only — not clickable/interactive.
        dialogBinding.devWhatsapp.movementMethod = null
        dialogBinding.devEmail.movementMethod = null

        AlertDialog.Builder(this, R.style.Theme_ClipVault_Dialog)
            .setView(dialogBinding.root)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun promptAccessibilityIfNeeded() {
        // Clipboard capture only works once the user turns this on manually —
        // Android does not allow apps to enable it themselves.
        AlertDialog.Builder(this)
            .setTitle("Permission zaroori hai")
            .setMessage("Copy ki hui cheezein automatically save karne ke liye, ClipVault ko Accessibility Settings mein ON karen.")
            .setPositiveButton("Settings kholen") { _, _ ->
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            .setNegativeButton("Baad mein", null)
            .show()
    }
}
