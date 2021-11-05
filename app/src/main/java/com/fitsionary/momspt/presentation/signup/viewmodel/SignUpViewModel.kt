package com.fitsionary.momspt.presentation.signup.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fitsionary.momspt.data.api.request.SignUpRequest
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.NICKNAME_DUPLICATE
import com.fitsionary.momspt.util.NICKNAME_EMPTY
import com.fitsionary.momspt.util.NICKNAME_VALIDATION
import com.fitsionary.momspt.util.NICKNAME_WRONG_FORMAT
import timber.log.Timber
import java.util.regex.Pattern

class SignUpViewModel : BaseViewModel() {
    // 닉네임엔 완성형 한글, 영어, 숫자만 가능
    private val pattern = Pattern.compile("^[가-힣a-zA-Z0-9]+$")
    val nickname = MutableLiveData<String?>()
    val id = MutableLiveData<String?>()
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

    fun getSignUpRequest() = SignUpRequest(
        nickname = nickname.value!!,
        babyDue = birthDay.value!!,
        weightBeforePregnancy = if (isNumeric(preWeight.value)) preWeight.value!!.toInt() else null,
        weightNow = if (isNumeric(currentWeight.value)) currentWeight.value!!.toInt() else null,
        heightNow = if (isNumeric(currentHeight.value)) currentHeight.value!!.toInt() else null,
        id.value!!
    )

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