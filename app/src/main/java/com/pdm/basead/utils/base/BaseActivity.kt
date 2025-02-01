package com.pdm.basead.utils.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.pdm.basead.AppOwner
import com.pdm.basead.MainActivity
import com.pdm.basead.R
import com.pdm.basead.SubscriptionActivity
import com.pdm.basead.utils.ads.AdKeyPosition
import com.pdm.basead.utils.manager.ad.AdBannerManager
import com.pdm.basead.utils.manager.ad.AdInterstitialManager
import com.pdm.basead.utils.manager.ad.AdNativeManager
import com.pdm.basead.utils.manager.ad.AdRewardManager
import com.pdm.basead.utils.manager.file.FileManager
import gs.ad.utils.ads.AdmManager
import gs.ad.utils.ads.OnAdmListener
import gs.ad.utils.ads.TYPE_ADS
import gs.ad.utils.google_iab.BillingClientLifecycle
import gs.ad.utils.google_iab.OnBillingListener
import gs.ad.utils.google_iab.models.PurchaseInfo
import gs.ad.utils.utils.PreferencesManager

/**
 * Sử dụng cho activity
 *
 * @property showAdBanner Function show BannerAd.
 * @property showAdInterstitial Function show InterstitialAd.
 * @property onClosedAdInterstitial Override function onClosedAdInterstitial.
 * @property showAdReward Function show RewardAd.
 * @property showRewardToRemoveAds Function show RewardAd to remove ads.
 * @property onClosedAdReward Override function onClosedAdReward.
 * @property showAdNative Function show NativeAd.
 * @property preloadNativeAd Function preload NativeAd.
 * @property applyNativeAd Function apply NativeAd.
 * @property cleanToFinish Function clean to finish.
 * @property startActivityLauncher Function start activity for result.
 * @property pickImageLauncher Function pick image from gallery.
 * @property pickVideoLauncher Function pick video from gallery.
 * @property pickFileLauncher Function pick file from gallery.
 * @property pickAudioLauncher Function pick audio from gallery.
 */
abstract class BaseActivity : AppCompatActivity() {
    private val tag = "DebugBaseActivity"

    private val mAdmManager: AdmManager
        get() {
            val application = application as AppOwner
            return if (this is MainActivity) {
                (application).mAdmBuilder.isMainActivity(this)
            } else {
                (application).mAdmBuilder.getActivity(this)
            }
        }
    private lateinit var adBannerManager: AdBannerManager
    private lateinit var adInterstitialManager: AdInterstitialManager
    private lateinit var adRewardManager: AdRewardManager
    private lateinit var adNativeManager: AdNativeManager

    private val mBillingClientLifecycle: BillingClientLifecycle
        get() {
            return (application as AppOwner).mBillingClientLifecycle
        }
    private lateinit var subscriptionLauncher: ActivityResultLauncher<Intent>

    private lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileManager: FileManager

    //region Activity Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adBannerManager = AdBannerManager(mAdmManager)
        adInterstitialManager = AdInterstitialManager(mAdmManager)
        adRewardManager = AdRewardManager(mAdmManager, this)
        adNativeManager = AdNativeManager(mAdmManager)

        mAdmManager.setListener(object : OnAdmListener {
            override fun onAdLoaded(typeAds: TYPE_ADS, keyPosition: String) {
                super.onAdLoaded(typeAds, keyPosition)
                if (typeAds == TYPE_ADS.NativeAd) {
                    adNativeManager.mNativeAdLoaded(keyPosition)
                }
            }

            override fun onAdClosed(typeAds: TYPE_ADS, keyPosition: String) {
                super.onAdClosed(typeAds, keyPosition)
                if (typeAds == TYPE_ADS.InterstitialAd) {
                    onClosedAdInterstitial(keyPosition)
                }
                if (typeAds == TYPE_ADS.RewardAd) {
                    adRewardManager.mRewardAdClose(keyPosition) { adKeyPosition ->
                        onClosedAdReward(adKeyPosition)
                    }
                }
            }
        })

        adNativeManager.mNativeAdFragmentInit(supportFragmentManager)

        activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                adNativeManager.mNativeAdResult(result)

                onActivityResult(result)
            }
        }

        mBillingClientLifecycle.setListener(
            this,
            object : OnBillingListener {
                override fun onPurchasedProductsFetched(purchaseInfos: List<PurchaseInfo>) {
                    super.onPurchasedProductsFetched(purchaseInfos)
                    Log.d(tag, "onPurchasedProductsFetched: ")
                    runOnUiThread {
                        checkSubToUpdateUI()
                    }
                }
            }
        )

        subscriptionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    checkSubToUpdateUI()
                }
            }

        fileManager = FileManager(this)

        onBackPressedDispatcher.addCallback(this) {
            cleanToFinish()
        }
    }

    override fun onStart() {
        super.onStart()
        adRewardManager.checkRemoveNoAds()
        mBillingClientLifecycle.fetchSubPurchasedProducts()
    }

    override fun onResume() {
        Log.d(tag, "onResume: $this")
        super.onResume()
        adRewardManager.checkRemoveNoAds()
        adBannerManager.mResumeAdBanner()
        mBillingClientLifecycle.fetchSubPurchasedProducts()
    }

    override fun onPause() {
        Log.d(tag, "onPause: $this")
        super.onPause()
        adBannerManager.mPauseAdBanner()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this is MainActivity) {
            adBannerManager.mDestroyAdBanner()
            adNativeManager.mDestroyAllAdNative()
            mAdmManager
                .removeListener()
                .removeMainActivity()
            mBillingClientLifecycle.removeListener(this)
        }
    }
    //endregion

    //region BannerAd
    /**
     * Function show BannerAd.
     *
     * Hiển thị quảng cáo BannerAd với key và trong View đã chỉ định.
     *
     * Sử dụng ở các Activity có banner.
     *
     * @param banner (Pair<AdKeyPosition, ConstraintLayout>?): Key và container View để hiển thị BannerAd.
     *
     */
    protected fun showAdBanner(
        banner: Pair<AdKeyPosition, ConstraintLayout>? = null
    ) {
        if (banner != null) {
            adBannerManager.mLoadAdBanner(banner)
        }
    }
    //endregion

    //region InterstitialAd
    /**
     * Function show InterstitialAd.
     *
     * Hiển thị quảng cáo InterstitialAd.
     * Nếu người dùng không đăng ký subscription, quảng cáo sẽ hiển thị..
     *
     * @param adKeyPosition (AdKeyPosition): Key của quảng cáo Interstitial.
     *
     */
    fun showAdInterstitial(adKeyPosition: AdKeyPosition) {
        adInterstitialManager.mLoadAdInterstitial(adKeyPosition)
    }

    /**
     * Override function onClosedAdInterstitial.
     *
     * Phương thức được gọi sau khi quảng cáo InterstitialAd được đóng.
     * Phương thức này có thể được override để thực hiện logic sau khi quảng cáo bị đóng.
     *
     * @param keyPosition Key của quảng cáo Interstitial đã được đóng.
     *
     */
    protected open fun onClosedAdInterstitial(keyPosition: String) {
        Log.d(tag, "onClosedAdInterstitial: $keyPosition")
    }
    //endregion

    //region RewardAd
    protected open fun onClosedAdReward(keyPosition: String) {
        Log.d(tag, "onClosedAdReward: $keyPosition")
    }

    protected fun showAdReward(adKeyPosition: AdKeyPosition) {
        adRewardManager.mLoadAdReward(adKeyPosition)
    }

    protected fun showRewardToRemoveAds(days: Int = 7) {
        adRewardManager.mLoadAdRewardToRemoveAds(days)
    }
    //endregion

    //region NativeAd
    /**
     * Function show NativeAd.
     *
     * Hiển thị quảng cáo NativeAd với key và trong View được chỉ định.
     *
     * Function này sử dụng để hiển thị NativeAd ngay sau khi vào Screen.
     *
     * Khi vào Screen, function sẽ load NativeAd nên sẽ mất một khoảng thời gian NativeAd mới hiển thị.
     *
     * @param native (Pair<AdKeyPosition, ConstraintLayout>): Key và container View của NativeAd.
     * @param layoutNativeAd (Int): Id của layout NativeAd (mặc định: R.layout.layout_native_ad).
     * @param isNativeFullScreen (Boolean): Có phải NativeAd toàn màn hình không (mặc định: false).
     *
     */
    protected fun showAdNative(
        native: Pair<AdKeyPosition, ConstraintLayout>,
        layoutNativeAd: Int = R.layout.layout_native_ad,
        isNativeFullScreen: Boolean = false
    ) {
        adNativeManager.mLoadAdNative(native, layoutNativeAd, isNativeFullScreen)
    }

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
     * @param native Pair<AdKeyPosition, Fragment> truyền key và Fragment.
     * @param isFullScreen Mặc định là false.
     *
     * @return Hiển thị NativeAd.
     */
    protected fun preloadNativeAd(
        native: Pair<AdKeyPosition, Fragment>,
        isFullScreen: Boolean = false
    ) {
        adNativeManager.mPreloadNativeAd(native, isFullScreen)
    }

    protected fun preloadNativeAd(
        listNative: List<Pair<AdKeyPosition, Fragment>>,
        isFullScreen: Boolean = false
    ) {
        adNativeManager.mPreloadNativeAd(listNative, isFullScreen)
    }

    protected fun preloadNativeAd(
        adKeyPosition: AdKeyPosition
    ) {
        adNativeManager.mPreloadNativeAd(adKeyPosition)
    }

    protected fun preloadNativeAd(
        listAdKeyPosition: List<AdKeyPosition>,
    ) {
        adNativeManager.mPreloadNativeAd(listAdKeyPosition)
    }

    fun applyNativeAd(
        native: Pair<AdKeyPosition, ConstraintLayout>,
        layoutNativeAd: Int = R.layout.layout_native_ad,
        isApplyNow: Boolean = false,

    ) {
        adNativeManager.mApplyNativeAd(native, layoutNativeAd, isApplyNow)
    }
    //endregion

    //region Subscription
    protected fun openSubscriptionActivity() {
        val intent = Intent(this, SubscriptionActivity::class.java)
        subscriptionLauncher.launch(intent)
    }

    protected fun openPlayStoreSubscriptionPage() {
        try {
            val uri =
                Uri.parse("https://play.google.com/store/account/subscriptions?package=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open subscription page", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkSubToUpdateUI() {
        runOnUiThread {
            if (PreferencesManager.getInstance().isSUB()) {
                adBannerManager.mDestroyAdBanner()
                adNativeManager.mDestroyAllAdNative()
                onSubUI()
            }
        }
    }

    open fun onSubUI() {
        Log.d(tag, "onSubUI")
    }
    //endregion

    //region FilePicker
    /**
     * Mở thư viện ảnh để chọn một hình ảnh từ thiết bị.
     *
     * Khi chọn xong, kết quả sẽ được trả về thông qua onImagePickedResult().
     */
    fun pickImageLauncher() {
        fileManager.mPickImageLauncher { uri ->
            onImagePickedResult(uri)
        }
    }

    /**
     * Mở thư viện video để chọn một video từ thiết bị.
     *
     * Khi chọn xong, kết quả sẽ được trả về thông qua onVideoPickedResult().
     */
    fun pickVideoLauncher() {
        fileManager.mPickVideoLauncher { uri ->
            onVideoPickedResult(uri)
        }
    }

    /**
     * Mở trình chọn tệp để chọn bất kỳ tệp nào từ thiết bị.
     *
     * Khi chọn xong, kết quả sẽ được trả về thông qua onFilePickedResult().
     */
    fun pickFileLauncher() {
        fileManager.mPickFileLauncher { uri ->
            onFilePickedResult(uri)
        }
    }

    /**
     * Mở trình chọn tệp để chọn một tệp âm thanh từ thiết bị.
     *
     * Khi chọn xong, kết quả sẽ được trả về thông qua onAudioPickedResult().
     */
    fun pickAudioLauncher() {
        fileManager.mPickAudioLauncher { uri ->
            onAudioPickedResult(uri)
        }
    }

    /**
     * Hàm này được gọi khi một tệp bất kỳ được chọn thành công.
     * @param uri URI của tệp đã chọn.
     */
    open fun onFilePickedResult(uri: Uri) {
        Log.d(tag, "onFilePicked: $uri")
    }

    /**
     * Hàm này được gọi khi một hình ảnh được chọn thành công.
     * @param uri URI của hình ảnh đã chọn.
     */
    open fun onImagePickedResult(uri: Uri) {
        Log.d(tag, "onImagePicked: $uri")
    }

    /**
     * Hàm này được gọi khi một video được chọn thành công.
     * @param uri URI của video đã chọn.
     */
    open fun onVideoPickedResult(uri: Uri) {
        Log.d(tag, "onVideoPicked: $uri")
    }

    /**
     * Hàm này được gọi khi một audio được chọn thành công.
     * @param uri URI của video đã chọn.
     */
    open fun onAudioPickedResult(uri: Uri) {
        Log.d(tag, "onVideoPicked: $uri")
    }
    //endregion

    fun startActivityLauncher(intent: Intent) {
        activityLauncher.launch(intent)
    }

    fun cleanToFinish(onResult: (intent: Intent) -> Unit = {}) {
        if (this is MainActivity) {
            return
        } else {
            val adKeyPosition = adNativeManager.listNativeAdApply.joinToString(",") { it.name }
            intent.putExtra("adKeyPosition", adKeyPosition)
            onResult(intent)
            setResult(RESULT_OK, intent)

            adBannerManager.mDestroyAdBanner()
            adNativeManager.mDestroyAllAdNative()
            mAdmManager
                .removeListener()
            mBillingClientLifecycle.removeListener(this)

            finish()
        }
    }

    open fun onActivityResult(result: ActivityResult) {
        Log.d(tag, "onActivityResult: $result")
    }
}
