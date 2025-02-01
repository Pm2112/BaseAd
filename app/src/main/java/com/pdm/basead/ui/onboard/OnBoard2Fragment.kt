package com.pdm.basead.ui.onboard

import com.pdm.basead.R

class OnBoard2Fragment : BaseOnBoardFragment() {
    override val layoutResId: Int = R.layout.fragment_on_board2
    override val position: Int = 1
    override var isNativeAdFull = false

    override fun onNextClicked() {
        onBoardViewModel.setPagePosition(2)
    }
}
