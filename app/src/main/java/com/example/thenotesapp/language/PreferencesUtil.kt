package com.example.thenotesapp.language

import android.content.Context

object PreferencesUtil {

    private const val SP_FILE_NAME = "system_config"


    fun putString(context: Context, key: String, value: String?) {
        val sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(context: Context, key: String, defValue: String? = null): String? {
        val sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defValue)
    }


    fun putLong(context: Context, key: String, value: Long) {
        val sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(context: Context, key: String, defValue: Long = 0): Long {
        val sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getLong(key, defValue)
    }


    fun putInt(context: Context, key: String, value: Int) {
        val sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(context: Context, key: String, defValue: Int = 0): Int {
        val sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, defValue)
    }



    fun putBoolean(context: Context, key: String, value: Boolean) {
        val sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(context: Context, key: String, defValue: Boolean = false): Boolean {
        val sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key, defValue)
    }


    fun hasKey(context: Context, key: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.contains(key)
    }
}