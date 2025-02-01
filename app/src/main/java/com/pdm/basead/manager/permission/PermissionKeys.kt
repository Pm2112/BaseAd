package com.pdm.basead.manager.permission

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Enum chứa các key quyền và phiên bản tối thiểu hỗ trợ của quyền.
 */
@Suppress("MissingPermission")
enum class PermissionKeys(val permission: String, val minSdk: Int = Build.VERSION_CODES.BASE) {
    CAMERA(android.Manifest.permission.CAMERA),
    LOCATION(android.Manifest.permission.ACCESS_FINE_LOCATION),

    @RequiresApi(Build.VERSION_CODES.Q)
    BACKGROUND_LOCATION(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
    STORAGE_READ(android.Manifest.permission.READ_EXTERNAL_STORAGE),
    STORAGE_WRITE(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    NOTIFICATION(android.Manifest.permission.POST_NOTIFICATIONS);

    companion object {
        /**
         * Lấy quyền từ key
         * @param key String - Tên key (CAMERA, LOCATION, ...).
         * @return String? - Quyền tương ứng hoặc null nếu không tìm thấy.
         */
        fun getPermission(key: String): String? {
            val permissionKey = entries.find { it.name == key }

            // Kiểm tra điều kiện API trước khi trả về quyền
            return if (permissionKey != null &&
                (!permissionKey.requiresApi() || Build.VERSION.SDK_INT >= permissionKey.getRequiredApi())
            ) {
                permissionKey.permission
            } else {
                null
            }
        }

        /**
         * Lấy danh sách tất cả quyền phù hợp với phiên bản hiện tại
         * @return List<PermissionKeys> - Danh sách quyền.
         */
        fun getAvailablePermissions(): List<PermissionKeys> {
            return entries.filter {
                !it.requiresApi() || Build.VERSION.SDK_INT >= it.getRequiredApi()
            }
        }
    }

    /**
     * Kiểm tra xem quyền này có yêu cầu phiên bản API không
     */
    private fun requiresApi(): Boolean {
        return this.javaClass.getField(this.name).isAnnotationPresent(RequiresApi::class.java)
    }

    /**
     * Kiểm tra quyền có được hỗ trợ trên thiết bị hay không.
     * @return Boolean - True nếu quyền được hỗ trợ trên thiết bị.
     */
    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= minSdk
    }

    /**
     * Lấy phiên bản API tối thiểu của quyền (nếu có)
     */
    private fun getRequiredApi(): Int {
        val annotation = this.javaClass.getField(this.name).getAnnotation(RequiresApi::class.java)
        return annotation?.value ?: Build.VERSION_CODES.BASE
    }
}
