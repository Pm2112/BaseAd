package com.pdm.basead

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.pdm.basead.utils.ads.AdKeyPosition
import com.pdm.basead.databinding.ActivitySplashBinding
import com.pdm.basead.ui.onboard.OnBoardActivity
import gs.ad.utils.ads.AdmManager
import gs.ad.utils.ads.OnAdmListener
import gs.ad.utils.ads.TYPE_ADS
import gs.ad.utils.google_iab.BillingClientLifecycle
import gs.ad.utils.google_iab.OnBillingListener
import gs.ad.utils.google_iab.enums.ErrorType
import gs.ad.utils.google_iab.models.ProductInfo
import gs.ad.utils.google_iab.models.PurchaseInfo
import gs.ad.utils.utils.GlobalVariables
import gs.ad.utils.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity(), OnAdmListener {
    private val TAG = "DebugSplashActivity"

    //region Variables
    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!
    //endregion

    //region Setup ads
    private val mAdmManager: AdmManager
        get() {
            return (application as AppOwner).mAdmBuilder.getActivity(this)
        }
    private val mBillingClientLifecycle: BillingClientLifecycle?
        get() {
            return (application as AppOwner).mBillingClientLifecycle ?: null
        }
    //endregion

    private var countLoadAd = 0
    private var totalLoadAd = 0
        set(value) {
            maxProgress = value
            field = value
        }
    private var maxProgress: Int = 0
        set(value) {
            field = value * 100 + 1000
        }
    private var isInitUMP: Boolean = false

    private data class AdPreload(
        val typeAds: TYPE_ADS,
        val id: Int,
        val position: String,
        val isFullScreen: Boolean = false
    )

    private var adPosition: MutableList<AdPreload> = mutableListOf(
        AdPreload(TYPE_ADS.OpenAd, -1, ""),
        AdPreload(TYPE_ADS.InterstitialAd, -1, ""),
        AdPreload(TYPE_ADS.NativeAd, -1, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_1.name),
        AdPreload(TYPE_ADS.NativeAd, -1, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_2.name),
        AdPreload(TYPE_ADS.NativeAd, -1, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_3.name, true),
        AdPreload(TYPE_ADS.NativeAd, -1, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_4.name),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        GlobalVariables.canShowOpenAd = false
        binding.splashProcessBar.visibility = View.GONE
        binding.splashProcessBar.progress = 0

        mAdmManager.setListener(this@SplashActivity)

        if (mBillingClientLifecycle == null) {
            mAdmManager.initUMP(gatherConsentFinished = {
                loadProgress()
            })
        } else {
            isInitUMP = false
            mBillingClientLifecycle?.setListener(
                this,
                eventListener = object : OnBillingListener {
                    override fun onProductDetailsFetched(productInfos: HashMap<String, ProductInfo>) {
                        super.onProductDetailsFetched(productInfos)
                        mBillingClientLifecycle?.fetchSubPurchasedProducts()
                    }

                    override fun onPurchasedProductsFetched(purchaseInfos: List<PurchaseInfo>) {
                        super.onPurchasedProductsFetched(purchaseInfos)
                        if (isInitUMP) return
                        isInitUMP = true
                        mAdmManager.initUMP(gatherConsentFinished = {
                            loadProgress()
                        })
                    }

                    override fun onBillingError(errorType: ErrorType) {
                        super.onBillingError(errorType)
                    }
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        mBillingClientLifecycle?.fetchSubPurchasedProducts()
    }

    private fun loadProgress() {
        if (!PreferencesManager.getInstance().isSUB()) {
            val progressBar = binding.splashProcessBar
            progressBar.visibility = View.VISIBLE
            if (PreferencesManager.getInstance().isShowOnBoard()) {
                adPosition.removeAll { it.typeAds == TYPE_ADS.NativeAd }
            }

            var countLoadedAd = 0
            var countProgress = 0
            totalLoadAd = adPosition.count()
            binding.splashProcessBar.max = maxProgress

            adPosition.forEach {
                when (it.typeAds) {
                    TYPE_ADS.NativeAd -> mAdmManager.preloadNativeAd(
                        it.id,
                        it.position,
                        it.isFullScreen
                    )

                    TYPE_ADS.BannerAd -> TODO()
                    TYPE_ADS.OpenAd -> mAdmManager.preloadOpenAd()
                    TYPE_ADS.InterstitialAd -> mAdmManager.preloadInterstitialAd()
                    TYPE_ADS.RewardAd -> TODO()
                }
            }

            lifecycleScope.launch(Dispatchers.Main) {
                val progressJob = async {
                    while (countProgress < maxProgress) {
                        countProgress += 1
                        progressBar.progress = countProgress
                        delay(1)
                    }
                }

                val loadAdsJob = async(Dispatchers.IO) {
                    while (getCountLoadAd() < totalLoadAd) {
                        if (countLoadedAd != getCountLoadAd()) {
                            countLoadedAd = getCountLoadAd()
                            countProgress += 100
                            progressBar.progress = countProgress
                        }
                    }
                }

                progressJob.await()
                loadAdsJob.await()

                startMainActivity()
            }
        } else {
            startMainActivity()
        }
    }

    private suspend fun getCountLoadAd(): Int = withContext(Dispatchers.IO) {
        return@withContext countLoadAd
    }

    override fun onAdLoaded(typeAds: TYPE_ADS, keyPosition: String) {
        super.onAdLoaded(typeAds, keyPosition)
        countLoadAd += 1
    }

    override fun onAdFailToLoaded(typeAds: TYPE_ADS, keyPosition: String) {
        super.onAdFailToLoaded(typeAds, keyPosition)
        countLoadAd += 1
    }

    private fun startMainActivity() {
        val intent: Intent?
        if (PreferencesManager.getInstance().isShowOnBoard()) {
            intent = Intent(this, MainActivity::class.java)
            mAdmManager
                .destroyAdByKeyPosition(TYPE_ADS.NativeAd, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_1.name)
                .destroyAdByKeyPosition(TYPE_ADS.NativeAd, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_2.name)
                .destroyAdByKeyPosition(TYPE_ADS.NativeAd, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_3.name)
                .destroyAdByKeyPosition(TYPE_ADS.NativeAd, AdKeyPosition.NATIVE_AD_SC_ON_BOARD_4.name)
        } else {
            intent = Intent(this, OnBoardActivity::class.java)
        }
//        val intent = Intent(this, MainActivity::class.java)
        MainScope().launch {
            mAdmManager.removeListener()
            mBillingClientLifecycle?.removeListener(this@SplashActivity)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}