package com.pdm.basead.ui.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnBoardViewModel : ViewModel() {
    private val _pagePosition = MutableLiveData<Int>()
    val pagePosition: LiveData<Int> get() = _pagePosition

    fun setPagePosition(position: Int) {
        _pagePosition.value = position
    }
}
