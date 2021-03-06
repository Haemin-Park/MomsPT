package com.fitsionary.momspt.presentation.mypage.view

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.MomsPTApplication
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.model.MyPageInfoModel
import com.fitsionary.momspt.databinding.FragmentMypageBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.mypage.viewmodel.MyPageViewModel
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import timber.log.Timber

class MyPageFragment :
    BaseFragment<FragmentMypageBinding, MyPageViewModel>(R.layout.fragment_mypage) {
    override val viewModel: MyPageViewModel by lazy {
        ViewModelProvider(this).get(MyPageViewModel::class.java)
    }
    lateinit var myPageInfo: MyPageInfoModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        viewModel.getUserMyPageInfo()

        viewModel.myPageInfo.observe(viewLifecycleOwner, {
            it?.let { info ->
                myPageInfo = info
            }
        })

        binding.btnEditProfile.setOnClickListener {
            myPageInfo.let { info ->
                findNavController().navigate(
                    MyPageFragmentDirections.actionMainMypageToEditProfileFragment(
                        info
                    )
                )
            }
        }

        binding.tvLogout.setOnClickListener {
            // ????????????
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Timber.i("???????????? ??????. SDK?????? ?????? ????????? $error")
                } else {
                    Timber.i("???????????? ??????. SDK?????? ?????? ?????????")
                    viewLifecycleOwner.lifecycleScope.launch {
                        MomsPTApplication.getInstance().getTokenDataStore().removeToken()
                        findNavController().navigate(MyPageFragmentDirections.actionMainMypageToSplashFragment())
                    }
                }
            }
        }

        binding.tvWithdraw.setOnClickListener {
            viewModel.deleteUser()
        }

        viewModel.event.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { success ->
                if (success) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        MomsPTApplication.getInstance().getTokenDataStore().removeToken()
                        // ?????? ??????
                        UserApiClient.instance.unlink { error ->
                            if (error != null) {
                                Timber.e("?????? ?????? ?????? $error")
                            } else {
                                Timber.i("?????? ?????? ??????. SDK?????? ?????? ?????? ???")
                            }
                        }
                        findNavController().navigate(MyPageFragmentDirections.actionMainMypageToSplashFragment())
                    }
                }
            }
        })
    }
}