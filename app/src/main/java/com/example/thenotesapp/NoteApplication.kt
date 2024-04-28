package com.example.thenotesapp

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.example.thenotesapp.language.MultiLanguageUtil

class NoteApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        MultiLanguageUtil.INSTANCE.saveSystemCurrentLanguage()
        super.attachBaseContext(base)
        MultiLanguageUtil.INSTANCE.setConfiguration(applicationContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        MultiLanguageUtil.INSTANCE.setConfiguration(applicationContext)
    }

}