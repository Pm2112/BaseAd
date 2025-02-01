package com.pdm.basead.ui.test

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pdm.basead.api.ApiRepository
import com.pdm.basead.api.ApiService
import com.pdm.basead.api.RetrofitClient
import com.pdm.basead.databinding.ActivityTestBinding
import com.pdm.basead.base.BaseActivity

class TestActivity : BaseActivity() {
    private lateinit var _binding: ActivityTestBinding
    private val binding get() = _binding

    private val apiService: ApiService = RetrofitClient.apiService
    private val apiRepository: ApiRepository = ApiRepository(apiService)
    private val testViewModel: TestViewModel = TestViewModel(apiRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        _binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvTest.setOnClickListener {
            pickFileLauncher()
        }

        binding.tvPost.setOnClickListener {
            testViewModel.postFile(contentResolver)
        }
    }

    override fun onFilePickedResult(uri: Uri) {
        super.onFilePickedResult(uri)
        Log.d("DebugTestActivity", "Uri: $uri")
        testViewModel.setFilePath(uri)
    }

    override fun onImagePickedResult(uri: Uri) {
        super.onImagePickedResult(uri)
        Log.d("DebugTestActivity", "Uri: $uri")
    }
}
