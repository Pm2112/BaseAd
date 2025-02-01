package com.pdm.basead

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pdm.basead.ui.ads.AdKeyPosition
import com.pdm.basead.databinding.ActivityMainBinding
import com.pdm.basead.ui.home.HomeActivity
import com.pdm.basead.ui.test.TestActivity
import com.pdm.basead.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val tag = "DebugMainActivity"

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivityLauncher(intent)
        }

        binding.btnTest.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            startActivityLauncher(intent)
        }

        showAdBanner(Pair(AdKeyPosition.BANNER_AD_SC_MAIN, binding.bannerView))
        preloadNativeAd(AdKeyPosition.NATIVE_AD_SC_HOME)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
