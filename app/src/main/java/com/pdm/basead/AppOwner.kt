package com.pdm.basead

import androidx.multidex.MultiDexApplication
import com.pdm.basead.ui.ads.AdKeyPosition
import com.pdm.basead.ui.sub.ConsumableProductId
import com.pdm.basead.ui.sub.SubscriptionProductId
import dagger.hilt.android.HiltAndroidApp
import gs.ad.utils.ads.AdmBuilder
import gs.ad.utils.ads.AdmConfig
import gs.ad.utils.google_iab.BillingClientLifecycle
import gs.ad.utils.utils.GlobalVariables

@HiltAndroidApp
class AppOwner : MultiDexApplication() {
    lateinit var mAdmBuilder: AdmBuilder
    lateinit var mBillingClientLifecycle: BillingClientLifecycle

    override fun onCreate() {
        super.onCreate()

        mAdmBuilder = AdmBuilder.build(applicationContext) {
            keyShowOpen = AdKeyPosition.APP_OPEN_AD_APP_FROM_BACKGROUND.name
            config = AdmConfig(
                listBannerAdUnitID = resources.getStringArray(R.array.banner_ad_unit_id).toList(),
                listInterstitialAdUnitID = resources.getStringArray(R.array.interstitial_ad_unit_id).toList(),
                listNativeAdUnitID = resources.getStringArray(R.array.native_ad_unit_id).toList(),
                listRewardAdUnitID = resources.getStringArray(R.array.reward_ad_unit_id).toList(),
                listOpenAdUnitID = resources.getStringArray(R.array.open_ad_unit_id).toList(),
            )
        }

        mBillingClientLifecycle = BillingClientLifecycle.build(applicationContext) {
            licenseKey = applicationContext.getString(R.string.license_key)
            consumableIds = enumValues<ConsumableProductId>().map { it.id }
            subscriptionIds = enumValues<SubscriptionProductId>().map { it.id }
        }

        enumValues<AdKeyPosition>().forEach {
            GlobalVariables.AdsKeyPositionAllow[it.name] = true
        }
    }

    override fun onTerminate() {
        mAdmBuilder.resetCounterAds(AdKeyPosition.INTERSTITIAL_AD_SC_MAIN.name)
        super.onTerminate()
    }

    companion object {
        const val TAG = "AppOwner"
    }
}
