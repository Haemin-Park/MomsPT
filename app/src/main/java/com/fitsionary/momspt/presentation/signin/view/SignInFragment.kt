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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber


class SignInFragment :
    BaseFragment<FragmentSignInBinding, SignInViewModel>(R.layout.fragment_sign_in) {
    override val viewModel: SignInViewModel by lazy {
        ViewModelProvider(this).get(SignInViewModel::class.java)
    }
    var userNickname = ""
    var userToken = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            MomsPTApplication.getInstance().getTokenDataStore().token.collect {
                Timber.i("토큰 $it")
                CurrentUser.token = it
            }
        }

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Timber.e("로그인 실패 $error")
            } else if (token != null) {
                Timber.i("로그인 성공 $token")
                UserApiClient.instance.me { user, _ ->
                    if (user != null) {
                        viewModel.signIn(user.id)
                        userToken = token.accessToken
                        userNickname = user.kakaoAccount?.profile?.nickname ?: ""
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
                        MomsPTApplication.getInstance().getTokenDataStore().saveToken(userToken)
                    }
                    findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToMainHome())
                } else {
                    findNavController().navigate(
                        SignInFragmentDirections.actionSignInFragmentToSignUpFragment(userNickname)
                    )
                }
            }
        })
    }
}