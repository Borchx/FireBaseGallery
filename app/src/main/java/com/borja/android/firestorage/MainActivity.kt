package com.borja.android.firestorage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.borja.android.firestorage.databinding.ActivityMainBinding
import com.borja.android.firestorage.ui.compose.upload.UploadComposeActivity
import com.borja.android.firestorage.ui.xml.upload.UploadXmlActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNavigateToXml.setOnClickListener {
            startActivity(UploadXmlActivity.create(this))
        }
        binding.btnNavigateToCompose.setOnClickListener {
            startActivity(UploadComposeActivity.create(this))
        }
    }
}