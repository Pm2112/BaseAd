package com.pdm.basead.ui

import android.os.Bundle
import android.view.View
import com.pdm.basead.databinding.ActivitySampleBinding
import com.pdm.basead.utils.base.BaseActivity

class SampleActivity : BaseActivity() {
    private var _binding: ActivitySampleBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnSampleBottomSheet: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySampleBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

//        showAdBanner(Pair(AdKeyPosition.BANNER_AD_SC_SAMPLE, binding.bannerView))
    }

//    override fun onClosedAdInterstitial(keyPosition: String) {
//        super.onClosedAdInterstitial(keyPosition)
//        if (keyPosition == AdKeyPosition.INTERSTITIAL_AD_SC_SAMPLE.name) {
//            Log.d("DebugSample", "Interstitial ad closed with key: $keyPosition")
//        }
//    }
//
//    override fun onClosedAdReward(keyPosition: String) {
//        super.onClosedAdReward(keyPosition)
//        if (keyPosition == AdKeyPosition.REWARD_AD_SC_SAMPLE.name) {
//            Log.d("DebugSample", "Reward ad closed with key: $keyPosition")
//        }
//    }
//
//    // Preload 1 native ad cho bottom sheet, dialog,...
//    private fun preloadNativeAd() {
//        val sampleBottomSheet = SampleBottomSheet.newInstance()
//        preloadNativeAd(Pair(AdKeyPosition.NATIVE_AD_SC_SAMPLE_1, sampleBottomSheet))
//
//        btnSampleBottomSheet.setOnClickListener {
//            // Kiểm tra bottom sheet có tồn tại không
//            val fragmentManager = supportFragmentManager
//            val existingFragment = fragmentManager.findFragmentByTag("SampleBottomSheet")
//            if (existingFragment == null) {
//                sampleBottomSheet.show(fragmentManager, "SampleBottomSheet")
//            }
//        }
//    }
//
//    // Hiển thị banner ad
//    private fun setupBanner() {
//        showAdBanner(Pair(AdKeyPosition.BANNER_AD_SC_SAMPLE, binding.bannerView))
//    }
//
//    // Hiển thị interstitial ad
//    private fun showInterstitialAd() {
//        showAdInterstitial(AdKeyPosition.INTERSTITIAL_AD_SC_SAMPLE)
//    }
//
//    // Hiển thị reward ad
//    private fun showRewardAdCustomKey() {
//        showAdReward(AdKeyPosition.REWARD_AD_SC_SAMPLE)
//    }
//    private fun showRewardAdToNoAds() {
//        // Hiển thị reward ad để xóa quảng cáo với số ngày mặc định.
//        showRewardToRemoveAds()
//        // Hiển thị reward ad để xóa quảng cáo với số ngày custom.
//        showRewardToRemoveAds(14)
//    }
//
//    private fun setupStartActivity() {
//        // Start activity
//        val intent = Intent(this, SampleActivity::class.java)
//        startActivityLauncher(intent)
//    }
//
//    private fun setupCleanToFinish() {
//        // Clean to finish
//        cleanToFinish()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
}