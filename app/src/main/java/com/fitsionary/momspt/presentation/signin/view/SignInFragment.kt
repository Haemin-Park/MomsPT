package com.fitsionary.momspt.presentation.signin.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.MomsPTApplication
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentSignInBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.signin.viewmodel.SignInViewModel
import com.fitsionary.momspt.util.CurrentUser
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import timber.log.Timber


class SignInFragment :
    BaseFragment<FragmentSignInBinding, SignInViewModel>(R.layout.fragment_sign_in) {
    override val viewModel: SignInViewModel by lazy {
        ViewModelProvider(this).get(SignInViewModel::class.java)
    }
    var userNickname = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id = ""
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Timber.e("로그인 실패 $error")
            } else if (token != null) {
                Timber.i("로그인 성공 $token")
                UserApiClient.instance.me { user, _ ->
                    if (user != null) {
                        id = user.id.toString()
                        CurrentUser.token = token.accessToken
                        userNickname = user.kakaoAccount?.profile?.nickname ?: ""
                        viewModel.signIn(id)
                    }
                }
            }
        }

        binding.btnKakaoSignIn.setOnClickListener {
            requireActivity().apply {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                    UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                }
            }
        }

        viewModel.result.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { loginResult ->
                if (loginResult) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        MomsPTApplication.getInstance().getTokenDataStore()
                            .saveToken(CurrentUser.token)
                        findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToMainHome())
                    }
                } else {
                    findNavController().navigate(
                        SignInFragmentDirections.actionSignInFragmentToSignUpFragment(
                            userNickname,
                            id
                        )
                    )
                }
            }
        })
    }
}