package com.fitsionary.momspt.presentation.mypage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.response.MyPageInfoResponse
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.Event
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class MyPageViewModel : BaseViewModel() {
    private val _myPageInfo = MutableLiveData<MyPageInfoResponse>()
    val myPageInfo: LiveData<MyPageInfoResponse>
        get() = _myPageInfo

    private val _event = MutableLiveData<Event<Boolean>>()
    val event: LiveData<Event<Boolean>>
        get() = _event

    fun getUserMyPageInfo() {
        NetworkService.api.getMyPageInfo()
            .applyNetworkScheduler()
            .subscribe(
                {
                    Timber.i(it.toString())
                    _myPageInfo.value = it
                }, {
                    Timber.e(it.message)
                }
            )
    }

    fun deleteUser() {
        NetworkService.api.deleteUser()
            .applyNetworkScheduler()
            .subscribe({
                Timber.i(it.toString())
                if (it.success)
                    _event.value = Event((true))
            }, {

            })
    }
}