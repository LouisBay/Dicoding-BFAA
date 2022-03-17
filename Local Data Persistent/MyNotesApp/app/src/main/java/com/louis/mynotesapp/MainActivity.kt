package com.louis.mynotesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.louis.mynotesapp.adapter.NoteAdapter
import com.louis.mynotesapp.databinding.ActivityMainBinding
import com.louis.mynotesapp.db.NoteHelper
import com.louis.mynotesapp.entity.Note
import com.louis.mynotesapp.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter

    var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data != null) {
            // Akan dipanggil jika request codenya ADD
            when (result.resultCode) {
                NoteAddUpdateActivity.RESULT_ADD -> {
                    val note = result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                    adapter.addItem(note)
                    binding.rvNotes.smoothScrollToPosition(adapter.itemCount - 1)
                    showSnackBarMessage("Satu item berhasil ditambahkan")
                }
                NoteAddUpdateActivity.RESULT_UPDATE -> {
                    val note = result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                    val position = result?.data?.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION,0) as Int
                    adapter.updateItem(position, note)
                    binding.rvNotes.smoothScrollToPosition(position)
                    showSnackBarMessage("Satu item berhasil diubah")
                }
                NoteAddUpdateActivity.RESULT_DELETE -> {
                    val position = result?.data?.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION,0) as Int
                    adapter.removeItem(position)
                    showSnackBarMessage("Satu item berhasil dihapus")
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Notes"

        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        adapter = NoteAdapter(object : NoteAdapter.OnItemClickCallback {
            override fun onItemClicked(selectedNote: Note, position: Int?) {
                Intent(this@MainActivity, NoteAddUpdateActivity::class.java).apply {
                    putExtra(NoteAddUpdateActivity.EXTRA_NOTE, selectedNote)
                    putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position)
                }.also {
                    resultLauncher.launch(it)
                }
            }
        })

        with(binding){
            rvNotes.adapter = adapter
            fabAdd.setOnClickListener {
                Intent(this@MainActivity, NoteAddUpdateActivity::class.java).let {
                    resultLauncher.launch(it)
                }
            }
        }

        if (savedInstanceState == null) {
            // proses ambil data
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }


    }

    private fun loadNotesAsync() {
        lifecycleScope.launch {
            binding.progressbar.visibility = View.VISIBLE
            val noteHelper = NoteHelper.getInstance(applicationContext)
            noteHelper.open()
            val deferredNotes = async (Dispatchers.IO) {
                val cursor = noteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }

            binding.progressbar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0){
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackBarMessage("Tidak ada data saat ini")
            }
            noteHelper.close()
        }
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(binding.rvNotes, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }
}