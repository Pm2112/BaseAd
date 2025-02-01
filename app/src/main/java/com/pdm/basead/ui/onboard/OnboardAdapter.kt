package com.pdm.basead.ui.onboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pdm.basead.ui.onboard.OnBoard1Fragment
import com.pdm.basead.ui.onboard.OnBoard2Fragment
import com.pdm.basead.ui.onboard.OnBoard3Fragment
import com.pdm.basead.ui.onboard.OnBoard4Fragment
import gs.ad.utils.utils.PreferencesManager

class OnboardAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnBoard1Fragment()
            1 -> OnBoard2Fragment()
            2 -> OnBoard3Fragment()
            3 -> OnBoard4Fragment()
            else -> OnBoard1Fragment()
        }
    }
}
