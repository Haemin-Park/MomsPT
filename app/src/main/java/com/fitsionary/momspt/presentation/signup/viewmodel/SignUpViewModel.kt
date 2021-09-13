package com.fitsionary.momspt.presentation.signup.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.api.request.NicknameDuplicateCheckRequest
import com.fitsionary.momspt.data.api.request.SignUpRequest
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import timber.log.Timber
import java.util.regex.Pattern

class SignUpViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val NICKNAME_VALIDATION = application.getString(R.string.validation_nickname)
    private val NICKNAME_WRONG_FORMAT = application.getString(R.string.wrong_format_nickname)
    private val NICKNAME_DUPLICATE = application.getString(R.string.duplicate_nickname)
    private val NICKNAME_EMPTY = application.getString(R.string.empty_nickname)

    // 닉네임엔 완성형 한글, 영어, 숫자만 가능
    private val pattern = Pattern.compile("^[가-힣a-zA-Z0-9]+$")
    val nickname = MutableLiveData<String?>()
    val validationResultText = MutableLiveData<String>(null)
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
    val birthDay = MutableLiveData<String?>()
    val preWeight = MutableLiveData<String?>()
    val currentWeight = MutableLiveData<String?>()
    val currentHeight = MutableLiveData<String?>()
    val signUpButtonEnable = Transformations.switchMap(successValidation) { isValid ->
        Transformations.map(birthDay) { birthDay ->
            isValid && birthDay != null && birthDay.isNotEmpty()
        }
    }

    fun nicknameValidationCheck() {
        nickname.value?.let { nickname ->
            if (nickname.isNotEmpty()) {
                if (pattern.matcher(nickname).matches()) {
                    addDisposable(
                        NetworkService.api.nicknameDuplicateCheck(
                            NicknameDuplicateCheckRequest(
                                nickname
                            )
                        ).subscribe({
                            if (it.message == "Success") {
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

    fun signUp() {
        addDisposable(
            NetworkService.api.signUp(
                SignUpRequest(
                    nickname = nickname.value!!,
                    babyDue = birthDay.value!!,
                    weightBeforePregnancy = if (isNumeric(preWeight.value)) preWeight.value!!.toInt() else null,
                    weightNow = if (isNumeric(currentWeight.value)) currentWeight.value!!.toInt() else null,
                    heightNow = if (isNumeric(currentHeight.value)) currentHeight.value!!.toInt() else null
                )
            ).subscribe({
                Timber.i("회원가입 $it.message")
            }, {})
        )
    }

    private fun isNumeric(strNum: String?): Boolean {
        if (strNum.isNullOrEmpty()) {
            return false
        }
        try {
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }
}