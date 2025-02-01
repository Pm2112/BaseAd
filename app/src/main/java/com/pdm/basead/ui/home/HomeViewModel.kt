package com.pdm.basead.ui.home

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.basead.audd.AudD
import com.pdm.basead.network.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {
    private val tag = "DebugHomeViewModel"

    private var _filePath = MutableLiveData<Uri>()
    val filePath: LiveData<Uri> get() = _filePath

    fun setFilePath(filePath: Uri) {
        _filePath.value = filePath
    }

    fun postFile(contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                val (url, partMap, file) = AudD.configSendFile(filePath.value!!, contentResolver)
                val response = networkRepository.postMultipartData<String>(
                    url,
                    partMap,
                    file
                )
                Log.d(tag, "Response: ${response.body()}")
            } catch (e: Exception) {
                Log.e(tag, "Error fetching data: ${e.message}")
            }
        }
    }
}