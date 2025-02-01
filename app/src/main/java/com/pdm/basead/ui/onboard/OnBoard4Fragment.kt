package com.pdm.basead.ui.onboard

import android.content.Intent
import com.pdm.basead.MainActivity
import com.pdm.basead.R
import gs.ad.utils.utils.PreferencesManager

class OnBoard4Fragment : BaseOnBoardFragment() {
    override val layoutResId: Int = R.layout.fragment_on_board4
    override val position: Int = 3
    override var isNativeAdFull = false

    override fun onNextClicked() {
        PreferencesManager.getInstance().saveShowOnBoard(true)
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        (activity as OnBoardActivity).finish()
    }
}
