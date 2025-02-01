package com.pdm.basead.ui.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pdm.basead.R
import gs.ad.utils.ads.AdmManager
import gs.ad.utils.utils.PreferencesManager

abstract class BaseOnBoardFragment : Fragment() {
    private var _binding: View? = null
    protected val binding get() = _binding!!

    protected lateinit var btnNext: TextView
    protected lateinit var containerNativeAd: ConstraintLayout
    protected lateinit var onBoardViewModel: OnBoardViewModel
    protected lateinit var mAdmManager: AdmManager

    protected abstract val layoutResId: Int
    protected abstract val position: Int
    protected abstract var isNativeAdFull: Boolean
    protected abstract fun onNextClicked()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflater.inflate(layoutResId, container, false)
        onBoardViewModel = ViewModelProvider(requireActivity())[OnBoardViewModel::class.java]
        mAdmManager = (requireActivity() as OnBoardActivity).getAdmManager()
        bindViews()
        return binding
    }

    private fun bindViews() {
        if (isNativeAdFull) {
            containerNativeAd = binding.findViewById(R.id.container_native_ad)
        } else {
            containerNativeAd = binding.findViewById(R.id.container_native_ad)
            btnNext = binding.findViewById(R.id.onboard_navigate_text)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAd()
        setupButton()
    }

    private fun setupAd() {
        if (!PreferencesManager.getInstance().isSUB()) {
            if (isNativeAdFull) {
                mAdmManager.applyNativeAdView(
                    "NativeAd_ScOnBoard_${position + 1}",
                    containerNativeAd,
                    R.layout.layout_native_ad_full
                )
            } else {
                mAdmManager.applyNativeAdView(
                    "NativeAd_ScOnBoard_${position + 1}",
                    containerNativeAd,
                    R.layout.layout_native_ad
                )
            }
        }
    }

    private fun setupButton() {
        if (!isNativeAdFull) {
            btnNext.setOnClickListener {
                onNextClicked()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
