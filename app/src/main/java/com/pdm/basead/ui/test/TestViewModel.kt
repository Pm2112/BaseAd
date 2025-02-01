package com.pdm.basead.ui.test

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.basead.api.ApiRepository
import com.pdm.basead.audd.AudD
import kotlinx.coroutines.launch

class TestViewModel(
    private val apiRepository: ApiRepository
) : ViewModel() {
    private val tag = "DebugTestViewModel"

    private var _filePath = MutableLiveData<Uri>()
    val filePath: LiveData<Uri> get() = _filePath

    fun setFilePath(filePath: Uri) {
        _filePath.value = filePath
    }

    fun postFile(contentResolver: ContentResolver) {
        viewModelScope.launch {
            try {
                val (url, partMap, file) = AudD.configSendFile(filePath.value!!, contentResolver)
                val response = apiRepository.postMultipartData<String>(
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