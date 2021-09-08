package com.fitsionary.momspt.presentation.splash.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentSplashBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.splash.viewmodel.SplashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToSignInFragment())
            }
        }
    }

    companion object {
        private const val SPLASH_TIME: Long = 1000
    }
}