package com.pdm.basead.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.pdm.basead.databinding.ActivityHomeBinding
import com.pdm.basead.manager.FileManager
import com.pdm.basead.utils.ads.AdKeyPosition
import com.pdm.basead.utils.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private val tag = "DebugHomeActivity"

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var homeViewModel: HomeViewModel

    override fun onClosedAdInterstitial(keyPosition: String) {
        super.onClosedAdInterstitial(keyPosition)
        if (keyPosition == AdKeyPosition.INTERSTITIAL_AD_SC_HOME.name) {
            cleanToFinish()
        }
        if (keyPosition == AdKeyPosition.INTERSTITIAL_AD_SC_EVENT.name) {
            cleanToFinish()
        }
    }

    override fun onClosedAdReward(keyPosition: String) {
        super.onClosedAdReward(keyPosition)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        showAdBanner(Pair(AdKeyPosition.BANNER_AD_SC_HOME, binding.bannerView))

        binding.btnShowAd.setOnClickListener {
            showAdInterstitial(AdKeyPosition.INTERSTITIAL_AD_SC_HOME)
        }

        showAdNative(Pair(AdKeyPosition.NATIVE_AD_SC_DASHBOARD, binding.containerNativeAd))

        binding.btnGetFile.setOnClickListener {
            val intent = FileManager.createFilePickerIntent()
            filePickerLauncher.launch(intent)
        }

        val bottomSheet = EventBottomSheet.newInstance()
        preloadNativeAd(Pair(AdKeyPosition.NATIVE_AD_SC_HOME_1, bottomSheet))
        binding.btnSendFile.setOnClickListener {
            homeViewModel.postFile(contentResolver)
            val fragmentManager = supportFragmentManager
            val existingFragment = fragmentManager.findFragmentByTag("EventBottomSheet")
            if (existingFragment == null) {
                bottomSheet.show(fragmentManager, "EventBottomSheet")
            }
        }

        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                Log.d("DocumentTest", "file: $result")
                if (result.resultCode == RESULT_OK) {
                    val fileUri = result.data?.data
                    Log.d("DocumentTest", "file: $fileUri")
                    if (fileUri != null) {
                        Log.d("DocumentTest", "file: $fileUri")
                        homeViewModel.setFilePath(fileUri)
                    } else {
                        Toast.makeText(this, "Không tìm thấy tệp", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("DocumentTest", "file: run")
                } else {
                    Log.d("DocumentTest", "file: e")
                }
            }
    }

    override fun onFilePickedResult(uri: Uri) {
        super.onFilePickedResult(uri)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
