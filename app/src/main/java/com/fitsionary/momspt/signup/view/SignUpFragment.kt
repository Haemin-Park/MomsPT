package com.fitsionary.momspt.signup.view

import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentSignUpBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.signup.viewmodel.SignUpViewModel

class SignUpFragment :
    BaseFragment<FragmentSignUpBinding, SignUpViewModel>(R.layout.fragment_sign_up) {
    override val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(this).get(SignUpViewModel::class.java)
    }
}