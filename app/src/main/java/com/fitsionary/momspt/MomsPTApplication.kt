package com.fitsionary.momspt

import android.app.Application
import com.fitsionary.momspt.datastore.TokenDataStore
import com.kakao.sdk.common.KakaoSdk
import timber.log.Timber


class MomsPTApplication : Application() {
    private lateinit var tokenDataStore: TokenDataStore

    override fun onCreate() {
        super.onCreate()
        application = this
        Timber.plant(Timber.DebugTree())
        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))
        tokenDataStore = TokenDataStore(this)
    }

    fun getTokenDataStore() = tokenDataStore

    companion object {
        private lateinit var application: MomsPTApplication
        fun getInstance(): MomsPTApplication = application
    }
}