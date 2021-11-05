package com.fitsionary.momspt.presentation.signin.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.request.SignInRequest
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.util.Event
import timber.log.Timber

class SignInViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _result = MutableLiveData<Event<Boolean>>()
    val result: LiveData<Event<Boolean>>
        get() = _result

    fun signIn(id: String) {
        addDisposable(
            NetworkService.api.signIn(SignInRequest(id))
                .subscribe({
                    Timber.i("성공 $it")
                    _result.postValue(Event(true))
                }, {
                    Timber.i("실패 $it")
                    _result.postValue(Event(false))
                })
        )
    }
}