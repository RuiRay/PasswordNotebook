package com.ionesmile.cipherbox.application

import android.app.Application

/**
 * Created by ionesmile on 18/08/2017.
 */

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrashHandler.getInstance().init(this)
    }
}
