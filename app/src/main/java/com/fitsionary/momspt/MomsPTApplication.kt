package com.fitsionary.momspt

import android.app.Application
import timber.log.Timber

class MomsPTApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}