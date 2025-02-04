package com.pdm.basead.ui.home

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.basead.audd.AudD
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
) : ViewModel() {
    private val tag = "DebugHomeViewModel"

    private var _filePath = MutableLiveData<Uri>()
    val filePath: LiveData<Uri> get() = _filePath

    fun setFilePath(filePath: Uri) {
        _filePath.value = filePath
    }


}