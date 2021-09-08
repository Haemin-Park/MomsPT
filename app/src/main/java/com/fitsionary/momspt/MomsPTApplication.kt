package com.fitsionary.momspt

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import timber.log.Timber

class MomsPTApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        KakaoSdk.init(this, "NATIVE_APP_KEY")
    }
}