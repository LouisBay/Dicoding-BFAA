package com.louis.mynoteapps.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.louis.mynoteapps.R
import com.louis.mynoteapps.databinding.ActivityMainBinding
import com.louis.mynoteapps.helper.ViewModelFactory
import com.louis.mynoteapps.ui.insert.NoteAddUpdateActivity

class MainActivity : AppCompatActivity() {

    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding

    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setViewModel()
        setRecyclerView()
        setFabOnClick()
    }

    private fun setFabOnClick() {
        binding?.fabAdd?.setOnClickListener { view ->
            if (view.id == R.id.fab_add) {
                Intent(this@MainActivity, NoteAddUpdateActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun setViewModel() {
        val mainViewModel = obtainViewModel(this@MainActivity)
        mainViewModel.getAllNotes().observe(this) { noteList ->
            if (noteList != null) {
                adapter.setListNotes(noteList)
                Log.d("MainActivity", noteList.toString())
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

    private fun setRecyclerView() {
        adapter = NoteAdapter()

        binding?.rvNotes?.also {
            it.layoutManager = LinearLayoutManager(this@MainActivity)
            it.setHasFixedSize(true)
            it.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityMainBinding = null
    }
}