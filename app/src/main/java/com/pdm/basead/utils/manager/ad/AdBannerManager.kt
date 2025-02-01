package com.pdm.basead.utils.manager.ad

import androidx.constraintlayout.widget.ConstraintLayout
import com.pdm.basead.utils.ads.AdKeyPosition
import gs.ad.utils.ads.AdmManager
import gs.ad.utils.ads.TYPE_ADS

class AdBannerManager(
    private val mAdmManager: AdmManager
) {
    private var bannerAdKeyPosition: AdKeyPosition? = null


    fun mLoadAdBanner(banner: Pair<AdKeyPosition, ConstraintLayout>) {
        mAdmManager.loadBannerAd(-1, banner.first.name, banner.second)
        bannerAdKeyPosition = banner.first
    }

    fun mResumeAdBanner() {
        mAdmManager.resumeBannerAdView()
    }

    fun mPauseAdBanner() {
        mAdmManager.pauseBannerAdView()
    }

    fun mDestroyAdBanner() {
        if (bannerAdKeyPosition != null) {
            mAdmManager.destroyAdByKeyPosition(TYPE_ADS.BannerAd, bannerAdKeyPosition!!.name)
        }
    }
}