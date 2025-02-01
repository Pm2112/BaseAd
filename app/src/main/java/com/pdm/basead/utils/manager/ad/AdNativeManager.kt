package com.pdm.basead.utils.manager.ad

import androidx.activity.result.ActivityResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.pdm.basead.R
import com.pdm.basead.utils.ads.AdKeyPosition
import gs.ad.utils.ads.AdmManager
import gs.ad.utils.ads.TYPE_ADS

class AdNativeManager(
    private val mAdmManager: AdmManager
) {
    private val listNativeAdPreloadFragment: MutableList<Pair<AdKeyPosition, Fragment>> =
        mutableListOf()
    private val listNativeAdPreload: MutableList<AdKeyPosition> = mutableListOf()
    val listNativeAdApply: MutableList<AdKeyPosition> = mutableListOf()

    fun mNativeAdLoaded(keyPosition: String, onLoaded: (keyPosition: String) -> Unit = {}) {
        listNativeAdPreload.find { it.name == keyPosition }
            ?: listNativeAdPreload.add(
                AdKeyPosition.valueOf(keyPosition)
            )
        onLoaded(keyPosition)
    }

    fun mNativeAdFragmentInit(supportFragmentManager: FragmentManager) {
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) {
                    super.onFragmentDetached(fm, fragment)
                    val removeNativeCurrent: MutableList<AdKeyPosition> = mutableListOf()
                    listNativeAdPreloadFragment.forEach { native ->
                        if (native.second == fragment) {
                            mDestroyAdNative(native)
                            removeNativeCurrent.add(native.first)
                        }
                    }
                    removeNativeCurrent.forEach { adKeyPosition ->
                        mAdmManager.preloadNativeAd(
                            -1,
                            adKeyPosition.name,
                            false
                        )
                    }
                }
            },
            true
        )
    }

    fun mLoadAdNative(
        native: Pair<AdKeyPosition, ConstraintLayout>,
        layoutNativeAd: Int = R.layout.layout_native_ad,
        isNativeFullScreen: Boolean = false
    ) {
        mAdmManager.loadNativeAd(
            -1,
            native.first.name,
            native.second,
            layoutNativeAd,
            isNativeFullScreen
        )
    }

    //region NativeAd Preload
    /**
     * Chỉ sử dụng cho 1 NativeAd
     *
     * Sử dụng để Preload NativeAd cho BottomSheet, Dialog, ... (Liên quan đến fragment).
     *
     * Sử dụng trong Activity chứa các Fragment, BottomSheet, Dialog.
     * (Các thành phần hiển thị sau khi UI chính đã được hiển thị)
     *
     * Không dùng để preload NativeAd cho Activity tiếp theo.
     *
     * @param adKeyAndFragment Pair<AdKeyPosition, Fragment> truyền key và Fragment.
     * @param AdKeyPosition Key của NativeAd cần preload.
     * @param Fragment Instance của fragment cần hiển thị NativeAd.
     * @param isFullScreen Mặc định là false.
     *
     * @sample SampleActivity.preloadNativeAd
     *
     * @return Hiển thị NativeAd.
     */
    fun mPreloadNativeAd(
        adKeyPosition: Pair<AdKeyPosition, Fragment>,
        isFullScreen: Boolean = false
    ) {
        listNativeAdPreloadFragment.find { it == adKeyPosition } ?: listNativeAdPreloadFragment.add(
            adKeyPosition
        )
        mAdmManager.preloadNativeAd(
            -1,
            adKeyPosition.first.name,
            isFullScreen
        )
    }

    fun mPreloadNativeAd(
        listAdKeyPosition: List<Pair<AdKeyPosition, Fragment>>,
        isFullScreen: Boolean = false
    ) {
        listAdKeyPosition.forEach { adKeyPosition ->
            listNativeAdPreloadFragment.find { it == adKeyPosition }
                ?: listNativeAdPreloadFragment.add(adKeyPosition)
            mAdmManager.preloadNativeAd(
                -1,
                adKeyPosition.first.name,
                isFullScreen
            )
        }
    }

    fun mPreloadNativeAd(listAdKeyPosition: List<AdKeyPosition>) {
        listAdKeyPosition.forEach { adKeyPosition ->
            listNativeAdPreload.find { it == adKeyPosition } ?: listNativeAdPreload.add(
                adKeyPosition
            )
            mAdmManager.preloadNativeAd(
                -1,
                adKeyPosition.name,
                isFullScreen = false
            )
        }
    }

    fun mPreloadNativeAd(adKeyPosition: AdKeyPosition) {
        listNativeAdPreload.find { it == adKeyPosition } ?: listNativeAdPreload.add(adKeyPosition)
        mAdmManager.preloadNativeAd(
            -1,
            adKeyPosition.name,
            isFullScreen = false
        )
    }

    /**
     * Hiển thị NativeAd đã được preload.
     * Không thể hiển thị NativeAd chưa được preload.
     * @param native Pair<AdKeyPosition, ConstraintLayout> truyền key và view.
     * @param layoutNativeAd Id layout của NativeAd trong res/layout.
     * Nếu không truyền tham số thì mặc định là R.layout.layout_native_ad.
     * Nếu trong res/layout không có file xml layout_native_ad sẽ bị lỗi.
     * @param isApplyNow Nếu không truyền tham số thì mặc định là false.
     *
     * Đặt isApplyNow = true: Nếu hiển thị NativeAd ngay sau khi vào Activity hoặc Fragment.
     * Đặt isApplyNow = false: Nếu hiển thị NativeAd trong BottomSheet hoặc Dialog.
     *
     * @return Hiển thị NativeAd.
     */
    fun mApplyNativeAd(
        native: Pair<AdKeyPosition, ConstraintLayout>,
        layoutNativeAd: Int = R.layout.layout_native_ad,
        isApplyNow: Boolean = false
    ) {
        if (isApplyNow) {
            listNativeAdApply.find { it == native.first } ?: listNativeAdApply.add(native.first)
            mAdmManager.applyNativeAdView(
                native.first.name,
                native.second,
                layoutNativeAd,
            )
        } else {
            mAdmManager.applyNativeAdView(
                native.first.name,
                native.second,
                layoutNativeAd,
            )
        }
    }
    //endregion


    fun mNativeAdResult(result: ActivityResult) {
        val adKeyPosition = result.data?.getStringExtra("adKeyPosition")
        adKeyPosition?.let {
            val adKeyList = it.split(",")
            val currentList = adKeyList.mapNotNull { key ->
                try {
                    AdKeyPosition.valueOf(key.trim())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            if (currentList.isNotEmpty()) {
                mPreloadNativeAd(currentList)
            }
        }
    }

    fun mDestroyAdNative(native: Pair<AdKeyPosition, Fragment>) {
        mAdmManager.destroyAdByKeyPosition(TYPE_ADS.NativeAd, native.first.name)
        listNativeAdPreloadFragment.minus(native)
    }

    private fun mDestroyAdNative(listAdKeyPosition: List<AdKeyPosition>) {
        listAdKeyPosition.forEach { adKeyPosition ->
            mAdmManager.destroyAdByKeyPosition(TYPE_ADS.NativeAd, adKeyPosition.name)
        }
    }

    fun mDestroyAllAdNative() {
        mDestroyAdNative(listNativeAdPreloadFragment.map { it.first })
        mDestroyAdNative(listNativeAdApply)
        mDestroyAdNative(listNativeAdPreload)
        listNativeAdPreload.clear()
        listNativeAdApply.clear()
        listNativeAdPreloadFragment.clear()
    }
}