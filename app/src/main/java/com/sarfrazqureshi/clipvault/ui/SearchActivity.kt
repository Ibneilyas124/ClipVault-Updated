package com.sarfrazqureshi.clipvault.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarfrazqureshi.clipvault.databinding.ActivitySearchBinding
import com.sarfrazqureshi.clipvault.db.ClipDatabase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: ClipAdapter
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ClipAdapter(
            onClick = { item ->
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("clipvault", item.content))
                Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
            },
            onLongClick = { }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.backButton.setOnClickListener { finish() }

        binding.searchInput.addTextChangedListener {
            val query = it?.toString()?.trim() ?: ""
            searchJob?.cancel()
            if (query.isEmpty()) {
                adapter.submitList(emptyList())
                binding.emptyText.visibility = android.view.View.GONE
                return@addTextChangedListener
            }
            searchJob = lifecycleScope.launch {
                val results = ClipDatabase.getInstance(applicationContext).clipDao().searchOnce(query)
                adapter.submitList(results)
                binding.emptyText.visibility = if (results.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }
}
