package com.fitsionary.momspt.presentation.signup.view

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.enum.DirectionEnum
import com.fitsionary.momspt.databinding.FragmentSignUpBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.signup.viewmodel.SignUpViewModel
import com.fitsionary.momspt.util.NavResult
import com.fitsionary.momspt.util.navResult
import timber.log.Timber


class SignUpFragment :
    BaseFragment<FragmentSignUpBinding, SignUpViewModel>(R.layout.fragment_sign_up) {
    override val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(this).get(SignUpViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        val safeArgs: SignUpFragmentArgs by navArgs()
        val nickname = safeArgs.nickname
        viewModel.nickname.value = nickname

        navResult(findNavController()) { result ->
            when (result) {
                is NavResult.Date -> {
                    viewModel.birthDay.value =
                        getString(R.string.date_format, result.year, result.month, result.day)
                }
                else -> Timber.i("unexpected result type")
            }
        }

        binding.etNickname.setRightTextClickListener {
            viewModel.nicknameValidationCheck()
        }

        binding.tvBirthDay.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToCustomDatePickerDialog())
        }

        binding.containerSignUp.setOnTouchListener { _, _ ->
            hideKeyboard(activity)
            true
        }

        // 닉네임 수정 시 다시 유효성 검사 하도록
        viewModel.nickname.observe(viewLifecycleOwner, {
            viewModel.validationResultText.value = ""
        })

        binding.btnNext.setOnClickListener {
            findNavController().navigate(
                SignUpFragmentDirections.actionSignUpFragmentToAnalysisFragment(
                    DirectionEnum.TO_MAIN, viewModel.getSignUpRequest()
                )
            )
        }
    }

    private fun hideKeyboard(activity: Activity?) {
        activity?.run {
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}