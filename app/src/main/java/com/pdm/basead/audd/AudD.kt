package com.pdm.basead.audd

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream

object AudD {
    private const val URL_SEND_FILE = "https://enterprise.audd.io/"
    private const val API_TOKEN = "e2e00f755505503ae9c753ef742e5771"

    fun configSendFile(
        path: Uri,
        contentResolver: ContentResolver
    ): Triple<String, Map<String, RequestBody>, MultipartBody.Part> {
        val url = URL_SEND_FILE

        val inputStream: InputStream = contentResolver.openInputStream(path)
            ?: throw Exception("Không thể mở tệp từ URI: $path")

        // Lưu tệp vào tạm thời trên bộ nhớ để tạo đối tượng File
        val tempFile = File.createTempFile("temp", "audio")
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        // Tạo MediaType cho file (ở đây là audio/mpeg, có thể thay đổi nếu là định dạng khác)
        val mediaType = "audio/mpeg".toMediaTypeOrNull()

        // Tạo request body từ file tạm thời
        val requestFile = tempFile.asRequestBody(mediaType)
        val filePart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

        // Tạo partMap với các tham số cần thiết
        val partMap = mapOf(
            "api_token" to API_TOKEN.toRequestBody("text/plain".toMediaTypeOrNull()),
            "accurate_offsets" to "true".toRequestBody("text/plain".toMediaTypeOrNull()),
            "skip" to "3".toRequestBody("text/plain".toMediaTypeOrNull()),
            "every" to "1".toRequestBody("text/plain".toMediaTypeOrNull())
        )

        // Trả về URL, partMap và filePart trong một Triple
        return Triple(url, partMap, filePart)
    }
}