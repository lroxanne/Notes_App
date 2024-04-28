package com.example.thenotesapp.language

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.LocaleList
import com.example.thenotesapp.R
import java.util.Locale

@SuppressWarnings("ALL")
class MultiLanguageUtil private constructor() {


    private var mCurrentSystemLocal = Locale.ENGLISH

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MultiLanguageUtil() }

        private const val SAVE_LANGUAGE = "save_language"

        private const val LANGUAGE_FOLLOW_SYSTEM = 0 //跟随系统

        private const val LANGUAGE_EN = 1 //英文

        private const val LANGUAGE_CHINESE_SIMPLIFIED = 2 //简体

    }


    fun getLanguageLocale(context: Context): Locale {
        val languageType = PreferencesUtil.getInt(context, SAVE_LANGUAGE, LANGUAGE_FOLLOW_SYSTEM)
        return when (languageType) {
            LANGUAGE_FOLLOW_SYSTEM -> mCurrentSystemLocal
            LANGUAGE_EN -> Locale.ENGLISH
            LANGUAGE_CHINESE_SIMPLIFIED -> Locale.SIMPLIFIED_CHINESE
            else -> mCurrentSystemLocal
        }
    }

    private fun getLanguage(locale: Locale): String {
        return locale.language + "_" + locale.country
    }

    fun getSysLocale(): Locale {
        return mCurrentSystemLocal
    }

    fun saveSystemCurrentLanguage() {
        mCurrentSystemLocal = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
    }

    fun updateLanguage(context: Context, languageType: Int) {
        PreferencesUtil.putInt(context, SAVE_LANGUAGE, languageType)
        setConfiguration(context)
    }

    fun getLanguageName(context: Context): String {
        val languageType = PreferencesUtil.getInt(context, SAVE_LANGUAGE, LANGUAGE_FOLLOW_SYSTEM)
        return when (languageType) {
            LANGUAGE_EN -> context.getString(R.string.setting_language_english)
            LANGUAGE_CHINESE_SIMPLIFIED -> context.getString(R.string.setting_simplified_chinese)
            else -> context.getString(R.string.setting_language_auto)
        }
    }


    fun getLanguageType(context: Context): Int {
        return PreferencesUtil.getInt(context, SAVE_LANGUAGE, LANGUAGE_FOLLOW_SYSTEM)
    }

    fun attachBaseContext(context: Context): Context {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context)
        } else {
            setConfiguration(context)
            context
        }
    }


    fun setConfiguration(context: Context?) {
        if (context == null) {
            return
        }
        val appContext = context.applicationContext

        val targetLocale = getLanguageLocale(appContext)
        Locale.setDefault(targetLocale)
        val configuration = appContext.resources.configuration
        configuration.setLocale(targetLocale)
        context.createConfigurationContext(configuration)
        val resources = appContext.resources
        val dm = resources.displayMetrics
        resources.updateConfiguration(configuration, dm)
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context): Context {
        val resources = context.resources
        val configuration = resources.configuration
        val locale: Locale = getLanguageLocale(context)
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    fun setApplicationLanguage(context: Context) {
        val resources = context.resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        val locale = getLanguageLocale(context)
        config.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            context.createConfigurationContext(config)
            Locale.setDefault(locale)
        }
        resources.updateConfiguration(config, dm)
    }

}