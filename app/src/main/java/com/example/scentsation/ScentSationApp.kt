package com.example.scentsation

import android.app.Application
import android.content.Context


class ScentSationApp : Application() {

    object Globals {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.appContext = applicationContext
    }
}