package com.xicko.xposed.application

import androidx.appcompat.app.AppCompatDelegate
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.xicko.xposed.BuildConfig
import de.robv.android.xposed.XSharedPreferences

class DefaultApplication : ModuleApplication() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}