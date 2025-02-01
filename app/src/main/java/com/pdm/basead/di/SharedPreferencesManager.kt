package com.pdm.basead.di

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    // Function to save a string value
    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    // Function to get a string value
    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    // Function to save an integer value
    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    // Function to get an integer value
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    // Function to save a boolean value
    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    // Function to get a boolean value
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Function to save a long value
    fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    // Function to get a long value
    fun getLong(key: String, defaultValue: Long = 0): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    // Function to remove a specific key
    fun removeKey(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    // Function to clear all data
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}

// example
//val sharedPreferencesManager = SharedPreferencesManager.getInstance(context)
//
//// Lưu một giá trị
//sharedPreferencesManager.putString("username", "JohnDoe")
//
//// Lấy một giá trị
//val username = sharedPreferencesManager.getString("username")
//
//// Lưu một giá trị boolean
//sharedPreferencesManager.putBoolean("isLoggedIn", true)
//
//// Lấy một giá trị boolean
//val isLoggedIn = sharedPreferencesManager.getBoolean("isLoggedIn")
//
//// Xóa một khóa
//sharedPreferencesManager.removeKey("username")
//
//// Xóa toàn bộ dữ liệu
//sharedPreferencesManager.clearAll()