package com.pdm.basead.utils.manager.ad

import com.pdm.basead.utils.ads.AdKeyPosition
import gs.ad.utils.ads.AdmManager

class AdInterstitialManager(
    private val mAdmManager: AdmManager
) {
    fun mLoadAdInterstitial(adKeyPosition: AdKeyPosition) {
        mAdmManager.showInterstitialAd(adKeyPosition.name)
    }
}
