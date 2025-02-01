package com.pdm.basead.utils.ads

enum class AdKeyPosition {
    //region APP
    APP_OPEN_AD_APP_FROM_BACKGROUND,
    //endregion

    //region Main
    INTERSTITIAL_AD_SC_MAIN,
    BANNER_AD_SC_MAIN,
    NATIVE_AD_SC_HOME_1,
    NATIVE_AD_SC_DASHBOARD,
    NATIVE_AD_SC_NOTIFICATIONS,
    //endregion

    //region Home
    BANNER_AD_SC_HOME,
    INTERSTITIAL_AD_SC_HOME,
    NATIVE_AD_SC_HOME,
    INTERSTITIAL_AD_SC_EVENT,
    //endregion

    //region OnBoard
    NATIVE_AD_SC_ON_BOARD_1,
    NATIVE_AD_SC_ON_BOARD_2,
    NATIVE_AD_SC_ON_BOARD_3,
    NATIVE_AD_SC_ON_BOARD_4,
    //endregion

    //region RemoveAds Reward
    REWARD_AD_REMOVE_ADS,
    //endregion

    //region Sample Ads
    NATIVE_AD_SC_SAMPLE_1,
    INTERSTITIAL_AD_SC_SAMPLE,
    REWARD_AD_SC_SAMPLE,
    BANNER_AD_SC_SAMPLE
    //endregion
}
