package com.fitsionary.momspt.presentation.mypage.viewmodel

import android.view.View
import androidx.lifecycle.*
import com.fitsionary.momspt.data.api.request.EditProfileRequest
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.*
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber
import java.io.File
import java.util.regex.Pattern


class EditProfileViewModel(private val originalNickname: String) : BaseViewModel() {
    // 닉네임엔 완성형 한글, 영어, 숫자만 가능
    private val pattern = Pattern.compile("^[가-힣a-zA-Z0-9]+$")
    val nickname = MutableLiveData(originalNickname)
    val validationResultText = MutableLiveData<String>()
    val validationResultTextVisible = Transformations.map(validationResultText) {
        if (it != null && it.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
    var successValidation = Transformations.map(validationResultText) {
        it == NICKNAME_VALIDATION
    }
    val editButtonEnable = Transformations.map(successValidation) { isValid ->
        isValid
    }

    private val _event = MutableLiveData<Event<Boolean>>()
    val event: LiveData<Event<Boolean>>
        get() = _event

    fun nicknameValidationCheck() {
        nickname.value?.let { nickname ->
            if (nickname == originalNickname) {
                validationResultText.postValue(NICKNAME_VALIDATION)
            } else if (nickname.isNotEmpty()) {
                if (pattern.matcher(nickname).matches()) {
                    addDisposable(
                        NetworkService.api.nicknameDuplicateCheck(nickname).subscribe({
                            if (it.success) {
                                validationResultText.postValue(NICKNAME_VALIDATION)
                            }
                        }, {
                            Timber.i(it.message)
                            validationResultText.postValue(NICKNAME_DUPLICATE)
                        })
                    )
                } else {
                    validationResultText.value = NICKNAME_WRONG_FORMAT
                }
            } else if (nickname == "") {
                validationResultText.value = NICKNAME_EMPTY
            }
        }
    }

    fun editProfileImage(file: File) {
        addDisposable(
            NetworkService.api.editProfileImage(
                FormDataUtil.getImageBody(FormDataUtil.FILE, file)
            ).applyNetworkScheduler()
                .doOnSubscribe { isLoading.onNext(true) }
                .doAfterTerminate { isLoading.onNext(false) }
                .subscribe({
                    Timber.i(it.toString())
                }, {
                    Timber.e(it.message!!)
                })
        )
    }

    fun editProfile(nickname: String, babyName: String) {
        addDisposable(
            NetworkService.api.editProfile(
                EditProfileRequest(nickname, babyName)
            ).applyNetworkScheduler()
                .subscribe({
                    Timber.i(it.toString())
                    if (it.success) {
                        _event.value = Event(true)
                    } else {
                        _event.value = Event(false)
                    }
                }, {
                    Timber.e(it.message!!)
                })
        )
    }

    class ViewModelFactory(private val originalNickname: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
                EditProfileViewModel(originalNickname) as T
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}