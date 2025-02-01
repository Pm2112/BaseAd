package com.pdm.basead.ui.onboard

import android.os.Bundle
import com.pdm.basead.R

class OnBoard1Fragment : BaseOnBoardFragment() {
    override val layoutResId: Int = R.layout.fragment_on_board1
    override val position: Int = 0
    override var isNativeAdFull = false

    override fun onNextClicked() {
        onBoardViewModel.setPagePosition(1)
    }

}
