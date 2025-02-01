package com.pdm.basead.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ApiRepository(
    private val apiService: ApiService
) {
    suspend fun <T> getData(url: String, param: String? = null): Response<T> {
        return apiService.getData(url, param)
    }

    suspend fun <T> postData(url: String, body: Any): Response<T> {
        return apiService.postData(url, body)
    }

    suspend fun <T> postMultipartData(
        url: String,
        partMap: Map<String, RequestBody>,
        file: MultipartBody.Part? = null
    ): Response<T> {
        return apiService.postMultipartData(url, partMap, file)
    }
}