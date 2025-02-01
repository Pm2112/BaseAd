package com.pdm.basead.ui.onboard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.pdm.basead.AppOwner
import com.pdm.basead.ui.ads.AdKeyPosition
import com.pdm.basead.databinding.ActivityOnboardBinding
import gs.ad.utils.ads.AdmManager
import gs.ad.utils.ads.OnAdmListener
import gs.ad.utils.ads.TYPE_ADS

class OnBoardActivity : AppCompatActivity() {
    private var _binding: ActivityOnboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var onBoardViewModel: OnBoardViewModel
    private val mAdmManager: AdmManager get() { return (application as AppOwner).mAdmBuilder.getActivity(this) }

    private lateinit var onboardViewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        _binding = ActivityOnboardBinding.inflate(layoutInflater)
        onBoardViewModel = ViewModelProvider(this)[OnBoardViewModel::class.java]
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBarsInsets.top,
                view.paddingRight,
                systemBarsInsets.bottom
            )
            insets
        }
        onboardViewPager = binding.onboardViewPager
        val onboardAdapter = OnboardAdapter(this)
        onboardViewPager.adapter = onboardAdapter

        onBoardViewModel.pagePosition.observe(this) {
            onboardViewPager.setCurrentItem(it, true)
        }

        mAdmManager.setListener(object : OnAdmListener {
            override fun onAdClicked(typeAds: TYPE_ADS, keyPosition: String) {
                super.onAdClicked(typeAds, keyPosition)
            }

            override fun onAdShowed(typeAds: TYPE_ADS, keyPosition: String) {
                super.onAdShowed(typeAds, keyPosition)
            }
        })
    }

    fun getAdmManager(): AdmManager {
        return mAdmManager
    }

    fun removeListener() {
        mAdmManager
            .destroyAdByKeyPosition(TYPE_ADS.NativeAd, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_1.name)
            .destroyAdByKeyPosition(TYPE_ADS.NativeAd, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_2.name)
            .destroyAdByKeyPosition(TYPE_ADS.NativeAd, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_3.name)
            .destroyAdByKeyPosition(TYPE_ADS.NativeAd, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_4.name)
            .removeListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListener()
    }
}
