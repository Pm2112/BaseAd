package com.pdm.basead.utils.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pdm.basead.AppOwner
import com.pdm.basead.utils.sub.MyProductId
import gs.ad.utils.google_iab.BillingClientLifecycle
import gs.ad.utils.google_iab.OnBillingListener
import gs.ad.utils.google_iab.enums.ErrorType
import gs.ad.utils.google_iab.models.PurchaseInfo
import gs.ad.utils.utils.GlobalVariables
import gs.ad.utils.utils.PreferencesManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

abstract class BaseSubscriptionActivity : AppCompatActivity() {
    private val tag = "DebugBaseSubscriptionActivity"

    private lateinit var myProductId: MyProductId
    var isRestoreSub: Boolean = false
    private val mBillingClientLifecycle: BillingClientLifecycle
        get() {
            return (application as AppOwner).mBillingClientLifecycle
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBillingClientLifecycle.setListener(
            this,
            object : OnBillingListener {
                override fun onProductsPurchased(purchaseInfos: List<PurchaseInfo>) {
                    super.onProductsPurchased(purchaseInfos)
                    showAlertDialogPurchasedSuccess(Date())
                }

                override fun onPurchasedProductsFetched(purchaseInfos: List<PurchaseInfo>) {
                    super.onPurchasedProductsFetched(purchaseInfos)
                    checkSubSuccessfully()
                }

                override fun onPurchaseAcknowledged(purchaseInfo: PurchaseInfo) {
                    super.onPurchaseAcknowledged(purchaseInfo)
                }

                override fun onPurchaseConsumed(purchaseInfo: PurchaseInfo) {
                    super.onPurchaseConsumed(purchaseInfo)
                }

                override fun onBillingError(errorType: ErrorType) {
                    super.onBillingError(errorType)
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        GlobalVariables.isShowSub = true
    }

    override fun onDestroy() {
        mBillingClientLifecycle.removeListener(this)
        super.onDestroy()
    }

    protected fun showBtnBack(btnBack: View, delayTime: Long = 5000) {
        MainScope().launch {
            delay(delayTime)
            btnBack.visibility = View.VISIBLE
        }
    }

    protected open fun chooseOptionWeekly() {
        myProductId = MyProductId.Weekly
    }

    protected open fun chooseOptionYearly() {
        myProductId = MyProductId.Yearly
    }

//    protected open fun chooseOptionLifetime() {
//        myProductId = MyProductId.Lifetime
//    }

    protected fun chooseRestoreSub() {
        isRestoreSub = true
        mBillingClientLifecycle.fetchSubPurchasedProducts()
    }

    protected fun chooseBuySubAndIAP() {
        val myProductIdValue = myProductId.id
        when (myProductId) {
//            MyProductId.Lifetime -> {
//                mBillingClientLifecycle.purchaseLifetime(this@SubscriptionActivity, myProductIdValue)
//            }

            MyProductId.Weekly -> {
                mBillingClientLifecycle.subscribe(this, myProductIdValue)
            }

            MyProductId.Yearly -> {
                mBillingClientLifecycle.subscribe(this, myProductIdValue)
            }
        }
    }

    fun checkSubSuccessfully() {
        if (!isRestoreSub) return
        isRestoreSub = false

        runOnUiThread {
            val alertTitle = if (!PreferencesManager.getInstance().isSUB()) {
                "Nothing to restore"
            } else {
                "All purchased is restored"
            }

            val alertMessage = if (!PreferencesManager.getInstance().isSUB()) {
                "You have never made a payment before, nothing will be restored"
            } else {
                null
            }

            AlertDialog.Builder(this)
                .setTitle(alertTitle)
                .setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    if (PreferencesManager.getInstance().isSUB()) {
                        finish()
                    }
                }
                .create()
                .show()
        }
    }

    private fun showAlertDialogPurchasedSuccess(purchaseTime: Date) {
        AlertDialog.Builder(this)
            .setTitle("Product is purchased")
            .setMessage("Product is valid until $purchaseTime")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .create()
            .show()
    }
}
