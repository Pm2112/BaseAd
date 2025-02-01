package com.pdm.basead.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun <T> getData(
        @Url url: String,
        @Query("param") param: String? = null
    ): Response<T>

    @POST
    suspend fun <T> postData(
        @Url url: String,
        @Body body: Any
    ): Response<T>

    @Multipart
    @POST
    suspend fun <T> postMultipartData(
        @Url url: String,
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part? = null
    ): Response<T>
}