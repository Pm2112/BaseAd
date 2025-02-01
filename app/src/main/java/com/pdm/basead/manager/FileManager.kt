package com.pdm.basead.manager

import android.content.Intent
import android.util.Log

object FileManager {
    /**
     * Mở file chọn từ điện thoại (Intent).
     *
     * @return Intent để chọn file.
     */
    fun createFilePickerIntent(): Intent {
        Log.d("FileManager", "createFilePickerIntent")
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Cho phép chọn tất cả các loại file, bạn có thể tùy chỉnh ví dụ "application/pdf"
        }
    }
}