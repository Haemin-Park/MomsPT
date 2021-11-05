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
            // 로그아웃
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Timber.i("로그아웃 실패. SDK에서 토큰 삭제됨 $error")
                } else {
                    Timber.i("로그아웃 성공. SDK에서 토큰 삭제됨")
                    removeToken()
                    findNavController().navigate(MyPageFragmentDirections.actionMainMypageToSplashFragment())
                }
            }
        }

        binding.tvWithdraw.setOnClickListener {
            viewModel.deleteUser()
        }

        viewModel.event.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { success ->
                if (success) {
                    removeToken()
                    // 연결 끊기
                    UserApiClient.instance.unlink { error ->
                        if (error != null) {
                            Timber.e("연결 끊기 실패 $error")
                        } else {
                            Timber.i("연결 끊기 성공. SDK에서 토큰 삭제 됨")
                        }
                    }
                    findNavController().navigate(MyPageFragmentDirections.actionMainMypageToSplashFragment())
                }
            }
        })
    }

    private fun removeToken() {
        viewLifecycleOwner.lifecycleScope.launch {
            MomsPTApplication.getInstance().getTokenDataStore().removeToken()
        }
    }
}