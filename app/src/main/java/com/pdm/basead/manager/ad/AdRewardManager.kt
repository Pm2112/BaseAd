package com.pdm.basead.manager.ad

import android.content.Context
import com.pdm.basead.ui.ads.AdKeyPosition
import com.pdm.basead.base.SharedPreferencesOwner
import gs.ad.utils.ads.AdmManager
import gs.ad.utils.utils.PreferencesManager

class AdRewardManager(
    private val mAdmManager: AdmManager,
    private val context: Context
) {
    private val keyRemove = "startTimeRemoveAds"
    private val daysRemove = "daysRemove"
    private val sharedPreferencesOwner = SharedPreferencesOwner.getInstance(context)

    fun mRewardAdClose(keyPosition: String, onClose: (keyPosition: String) -> Unit) {
        if (keyPosition == "REWARD_AD_REMOVE_ADS") {
            PreferencesManager.getInstance().removeAds(true)
            sharedPreferencesOwner.putLong(keyRemove, System.currentTimeMillis())
        } else {
            onClose(keyPosition)
        }
    }

    fun mLoadAdReward(adKeyPosition: AdKeyPosition) {
        mAdmManager.showRewardAd(adKeyPosition.name)
    }

    fun mLoadAdRewardToRemoveAds(days: Int) {
        sharedPreferencesOwner.putInt(daysRemove, days)
        mAdmManager.showRewardAd("REWARD_AD_REMOVE_ADS")
    }

    fun checkRemoveNoAds() {
        val dayRemove = sharedPreferencesOwner.getInt(daysRemove)
        val startTime = sharedPreferencesOwner.getLong(keyRemove)
        if (startTime != 0L) {
            val currentTimeMillis = System.currentTimeMillis()
            val timeRemove = dayRemove * 24 * 60 * 60 * 1000
            if (currentTimeMillis - startTime >= timeRemove) {
                PreferencesManager.getInstance().removeAds(false)
                sharedPreferencesOwner.removeKey(keyRemove)
                sharedPreferencesOwner.removeKey(daysRemove)
            }
        }
    }
}
