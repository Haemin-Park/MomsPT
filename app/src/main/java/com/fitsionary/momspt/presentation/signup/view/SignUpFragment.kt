package com.fitsionary.momspt.presentation.signup.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentSignUpBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.signup.viewmodel.SignUpViewModel

class SignUpFragment :
    BaseFragment<FragmentSignUpBinding, SignUpViewModel>(R.layout.fragment_sign_up) {
    override val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(this).get(SignUpViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel

        val sageArgs: SignUpFragmentArgs by navArgs()
        val nickname = sageArgs.nickname
        viewModel.nickname.value = nickname

        binding.etNickname.setRightTextClickListener {
            viewModel.nicknameValidationCheck()
        }

        // 닉네임 수정 시 다시 유효성 검사 하도록
        viewModel.nickname.observe(viewLifecycleOwner, {
            viewModel.validationResultText.value = ""
        })
    }
}