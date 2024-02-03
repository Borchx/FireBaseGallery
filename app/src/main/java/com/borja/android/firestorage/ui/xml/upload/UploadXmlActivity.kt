package com.borja.android.firestorage.ui.xml.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.borja.android.firestorage.databinding.ActivityUploadXmlBinding
import com.borja.android.firestorage.databinding.DialogImageSelectorBinding
import com.borja.android.firestorage.ui.xml.list.ListXmlActivity
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@AndroidEntryPoint
class UploadXmlActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent = Intent(context, UploadXmlActivity::class.java)
    }

    private lateinit var binding: ActivityUploadXmlBinding

    private val uploadXmlViewModel: UploadXmlViewModel by viewModels()

    private lateinit var uri: Uri

    private val intentCameraLauncher =
        registerForActivityResult(TakePicture()) {
            if (it && uri.path?.isNotEmpty() == true) { // comprobar la URI
                uploadXmlViewModel.uploadAndGetImage(uri) { downloadUri ->
                    clearText()
                    showNewImage(downloadUri)
                }
            }
        }

    private val intentGalleryLauncher = registerForActivityResult(GetContent()) { uri ->
        uri?.let {
            uploadXmlViewModel.uploadAndGetImage(it) { downloadUri ->
                showNewImage(downloadUri)
            }
        }
    }

    private fun showNewImage(downloadUri: Uri) {
        Glide.with(this).load(downloadUri).into(binding.ivImage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        initListeners()
        initUIState()
    }

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                uploadXmlViewModel.isLoading.collect {
                    binding.pbImage.isVisible = it
                    if (it) {
                        binding.ivPlaceHolder.isVisible = false
                        binding.ivImage.setImageDrawable(null)
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.fabImage.setOnClickListener {
            showImageDialog()
        }
        binding.btnNavigateToList.setOnClickListener {
            startActivity(ListXmlActivity.create(this))
        }
    }

    private fun showImageDialog() {
        val dialogBinding = DialogImageSelectorBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        dialogBinding.btnTakePhoto.setOnClickListener {
            takePhoto()
            dialog.dismiss()
        }
        dialogBinding.btnGallery.setOnClickListener {
            getImageFromGallery()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()
    }

    private fun getImageFromGallery() {
        intentGalleryLauncher.launch("image/*")
    }

    private fun takePhoto() {
        generateUri()
        intentCameraLauncher.launch(uri)
    }

    private fun generateUri() {
        uri = FileProvider.getUriForFile(
            Objects.requireNonNull(this),
            "com.borja.android.firestorage.provider",
            createFile()
        )
    }

    private fun clearText(){
        binding.etTitle.setText("")
        binding.etTitle.clearFocus()
    }

    private fun createFile(): File {
        val userTitle = binding.etTitle.text.toString()
        val name: String =
            userTitle.ifEmpty { SimpleDateFormat("yyyyMMdd_hhmmss").format(Date()) + "image" }

        return File.createTempFile(name, ".jpg", externalCacheDir)
    }

}