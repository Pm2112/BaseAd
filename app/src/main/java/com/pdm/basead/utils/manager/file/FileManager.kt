package com.pdm.basead.utils.manager.file

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class FileManager(
    private val activity: ComponentActivity
) {
    private val tag = "DebugFileManager"

    private val filePickerLauncher: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onPickerResult?.invoke(result.data?.data!!)
        } else {
            Log.d(tag, "onPicked: $result")
        }
    }

    private var onPickerResult: ((uri: Uri) -> Unit)? = null

    //region File Picker
    /**
     * Mở thư viện ảnh để chọn một hình ảnh từ thiết bị.
     *
     * Khi chọn xong, kết quả sẽ được trả về thông qua onImagePickedResult().
     */
    fun mPickImageLauncher(onImagePickerResult: (Uri) -> Unit) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        onPickerResult = onImagePickerResult
        filePickerLauncher.launch(intent)
    }

    /**
     * Mở thư viện video để chọn một video từ thiết bị.
     *
     * Khi chọn xong, kết quả sẽ được trả về thông qua onVideoPickedResult().
     */
    fun mPickVideoLauncher(onVideoPickerResult: (Uri) -> Unit) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        onPickerResult = onVideoPickerResult
        filePickerLauncher.launch(intent)
    }

    /**
     * Mở trình chọn tệp để chọn một tệp âm thanh từ thiết bị.
     *
     * Khi chọn xong, kết quả sẽ được trả về thông qua onAudioPickedResult().
     */
    fun mPickAudioLauncher(onAudioPickerResult: (Uri) -> Unit) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "audio/*"
        onPickerResult = onAudioPickerResult
        filePickerLauncher.launch(intent)
    }

    /**
     * Mở trình chọn tệp để chọn bất kỳ tệp nào từ thiết bị.
     *
     * Khi chọn xong, kết quả sẽ được trả về thông qua onFilePickedResult().
     */
    fun mPickFileLauncher(onFilePickerResult: (Uri) -> Unit) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"
        onPickerResult = onFilePickerResult
        filePickerLauncher.launch(intent)
    }
    //endregion

    //region Function
    /**
     * Sao chép tệp từ URI vào bộ nhớ trong của ứng dụng.
     * @param context Context để lấy ContentResolver.
     * @param fileUri URI của tệp cần sao chép.
     * @param outputFileName Tên tệp sau khi lưu vào bộ nhớ trong.
     * Nếu không truyền tham số này thì mặc định là tên của tệp gốc.
     * @return Đường dẫn tệp đã lưu hoặc null nếu thất bại.
     */
    fun saveFile(
        context: Context,
        fileUri: Uri,
        outputFileName: String? = null
    ): String? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(fileUri)
            val fileName = outputFileName ?: getFileName(context, fileUri) ?: "file"
            val outputFile = File(context.filesDir, fileName)

            if (inputStream != null) {
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                inputStream.close()
                outputFile.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Lấy tên tệp từ URI
     * @param context Context để truy cập ContentResolver.
     * @param fileUri URI của tệp.
     * @return Tên tệp hoặc null nếu không tìm thấy.
     */
    fun getFileName(context: Context, fileUri: Uri): String? {
        var fileName: String? = null
        val contentResolver: ContentResolver = context.contentResolver

        // Truy vấn dữ liệu từ URI
        val cursor: Cursor? = contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                fileName = it.getString(nameIndex)
            }
        }

        return fileName
    }

    /**
     * Sao chép tệp từ URI vào thư mục nội bộ của ứng dụng và trả về File.
     * @param context Context để truy cập contentResolver.
     * @param fileUri URI của tệp nguồn.
     * @return Tệp được lưu trong bộ nhớ trong hoặc null nếu thất bại và xóa khi destroy.
     */
    fun getFile(context: Context, fileUri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(fileUri) ?: return null
        val fileName = getFileName(context, fileUri) ?: return null
        val file = File(context.cacheDir, fileName) // Lưu vào thư mục cache của app

        return try {
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            inputStream.close()
        }
    }
    //endregion
}
