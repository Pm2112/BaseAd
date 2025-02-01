package com.pdm.basead.manager.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

class PermissionManager(private val activity: ComponentActivity) {
    // Launcher được đăng ký trước khi sử dụng
    private val permissionLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onPermissionResult?.invoke(isGranted)
        }

    private var onPermissionResult: ((Boolean) -> Unit)? = null

    /**
     * Kiểm tra xem quyền đã được cấp hay chưa.
     * @param context Context
     * @param permissionKey PermissionKeys - Enum chứa quyền cần kiểm tra.
     * @return Boolean - True nếu quyền đã được cấp, False nếu không.
     */
    fun isPermissionGranted(context: Context, permissionKey: PermissionKeys): Boolean {
        if (!permissionKey.isSupported()) {
            return false
        }
        return context.checkSelfPermission(permissionKey.permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Xin quyền runtime trong Activity.
     * @param permissionKey PermissionKeys - Enum chứa quyền cần xin.
     * @param onPermissionResult (Boolean) -> Unit - Callback khi xin quyền xong.
     */
    fun requestPermission(permissionKey: PermissionKeys, onPermissionResult: (Boolean) -> Unit) {
        if (!permissionKey.isSupported()) {
            onPermissionResult(false) // Quyền không hỗ trợ trên phiên bản hiện tại
            return
        }

        this.onPermissionResult = onPermissionResult
        Log.d("PermissionManager", "Requesting permission: $onPermissionResult")
        permissionLauncher.launch(permissionKey.permission)
    }

    companion object {
        /**
         * Xin quyền runtime trong Fragment.
         * @param fragment Fragment - Fragment hiện tại.
         * @param permissionKey PermissionKeys - Enum chứa quyền cần xin.
         * @param onPermissionResult (Boolean) -> Unit - Callback khi xin quyền xong.
         */
        fun requestPermissionInFragment(
            fragment: Fragment,
            permissionKey: PermissionKeys,
            onPermissionResult: (Boolean) -> Unit
        ) {
            if (!permissionKey.isSupported()) {
                onPermissionResult(false)
                return
            }

            val requestPermissionLauncher =
                fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    onPermissionResult(isGranted)
                }

            requestPermissionLauncher.launch(permissionKey.permission)
        }
    }
}