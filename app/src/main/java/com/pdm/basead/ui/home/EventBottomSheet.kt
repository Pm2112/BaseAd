package com.pdm.basead.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pdm.basead.ui.ads.AdKeyPosition
import com.pdm.basead.databinding.FragmentEventBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import gs.ad.utils.ads.TYPE_ADS

@AndroidEntryPoint
class EventBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentEventBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var activity: HomeActivity

    companion object {
        fun newInstance(): EventBottomSheet {
            return EventBottomSheet()
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as HomeActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBottomSheetBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity.applyNativeAd(Pair(AdKeyPosition.NATIVE_AD_SC_HOME_1, binding.containerNativeAd))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.showAd.setOnClickListener {
            activity.showAdInterstitial(AdKeyPosition.INTERSTITIAL_AD_SC_EVENT)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
