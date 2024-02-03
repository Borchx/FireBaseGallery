package com.borja.android.firestorage.ui.xml.list

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.borja.android.firestorage.R
import com.borja.android.firestorage.databinding.ActivityListXmlBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListXmlActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context) =
            Intent(context, ListXmlActivity::class.java)

    }

    private val listXmlViewModel: ListXmlViewModel by viewModels()
    private lateinit var binding: ActivityListXmlBinding
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_xml)
        binding = ActivityListXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        listXmlViewModel.getAllImages()
    }

    private fun initUI() {
        initUIState()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        galleryAdapter = GalleryAdapter()
        binding.rvGallery.apply {
            layoutManager = GridLayoutManager(this@ListXmlActivity, 2)
            this.adapter = galleryAdapter
        }
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                listXmlViewModel.uiState.collect { uiState ->
                    galleryAdapter.updateList(uiState.images)
                    binding.pbGallery.isVisible = uiState.isLoading
                }
            }
        }
    }
}