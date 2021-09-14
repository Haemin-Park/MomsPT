package com.fitsionary.momspt.presentation.signin.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentSignInBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.intro.view.IntroActivity
import com.fitsionary.momspt.presentation.signin.viewmodel.SignInViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import timber.log.Timber

class SignInFragment :
    BaseFragment<FragmentSignInBinding, SignInViewModel>(R.layout.fragment_sign_in) {
    override val viewModel: SignInViewModel by lazy {
        ViewModelProvider(this).get(SignInViewModel::class.java)
    }
    private lateinit var currentActivity: IntroActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IntroActivity)
            currentActivity = activity as IntroActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Timber.e("로그인 실패 $error")
            } else if (token != null) {
                Timber.i("로그인 성공 $token")
                UserApiClient.instance.me { user, _ ->
                    if (user != null) {
                        val nickname = user.kakaoAccount?.profile?.nickname
                        findNavController().navigate(
                            SignInFragmentDirections.actionSignInFragmentToSignUpFragment(
                                nickname ?: ""
                            )
                        )
                    }
                }
            }
        }

        binding.btnKakaoSignIn.setOnClickListener {
            currentActivity.apply {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                    UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                }
            }
        }
    }
}