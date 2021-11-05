package com.fitsionary.momspt.presentation.splash.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.MomsPTApplication
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentSplashBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.splash.viewmodel.SplashViewModel
import com.fitsionary.momspt.util.CurrentUser
import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class SplashFragment :
    BaseFragment<FragmentSplashBinding, SplashViewModel>(R.layout.fragment_splash) {
    override val viewModel: SplashViewModel by lazy {
        ViewModelProvider(this).get(SplashViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            delay(SPLASH_TIME)
            launch(Dispatchers.Main) {
                checkAutoLogin()
            }
        }
    }

    private fun checkAutoLogin() {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Timber.e("토큰 정보 보기 실패 $error")
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToSignInFragment())
            } else if (tokenInfo != null) {
                Timber.i("토큰 정보 보기 성공 $tokenInfo")
                viewLifecycleOwner.lifecycleScope.launch {
                    MomsPTApplication.getInstance().getTokenDataStore().token.collect {
                        if (it != "") {
                            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToMainHome())
                            val token =
                                TokenManagerProvider.instance.manager.getToken()?.accessToken.toString()
                            CurrentUser.token = token
                            if (it != token) {
                                MomsPTApplication.getInstance().getTokenDataStore().saveToken(token)
                            }
                        } else {
                            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToSignInFragment())
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val SPLASH_TIME: Long = 1000
    }
}