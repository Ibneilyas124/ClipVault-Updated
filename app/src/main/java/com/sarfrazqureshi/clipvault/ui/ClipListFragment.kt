package com.sarfrazqureshi.clipvault.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarfrazqureshi.clipvault.databinding.FragmentClipListBinding
import com.sarfrazqureshi.clipvault.db.ClipDatabase
import com.sarfrazqureshi.clipvault.db.ClipType
import kotlinx.coroutines.launch

class ClipListFragment : Fragment() {

    private var _binding: FragmentClipListBinding? = null
    private val binding get() = _binding!!

    private lateinit var type: ClipType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val typeName = arguments?.getString(ARG_TYPE) ?: ClipType.TEXT.name
        type = ClipType.valueOf(typeName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClipListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ClipAdapter(
            onClick = { item ->
                val cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("clipvault", item.content))
                Toast.makeText(requireContext(), "Copied", Toast.LENGTH_SHORT).show()
            },
            onLongClick = { item ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete karen?")
                    .setMessage(item.content.take(80))
                    .setPositiveButton("Delete") { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            ClipDatabase.getInstance(requireContext()).clipDao().delete(item.id)
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val viewModel = ViewModelProvider(
            this,
            ClipListViewModel.Factory(requireActivity().application, type)
        )[ClipListViewModel::class.java]

        viewModel.items.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TYPE = "arg_type"
        fun newInstance(type: ClipType): ClipListFragment {
            val f = ClipListFragment()
            f.arguments = Bundle().apply { putString(ARG_TYPE, type.name) }
            return f
        }
    }
}
