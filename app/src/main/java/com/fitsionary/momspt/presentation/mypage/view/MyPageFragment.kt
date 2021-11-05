package com.fitsionary.momspt.presentation.mypage.view

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.MomsPTApplication
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.api.response.MyPageInfoResponse
import com.fitsionary.momspt.databinding.FragmentMypageBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.mypage.viewmodel.MyPageViewModel
import kotlinx.coroutines.launch

class MyPageFragment :
    BaseFragment<FragmentMypageBinding, MyPageViewModel>(R.layout.fragment_mypage) {
    override val viewModel: MyPageViewModel by lazy {
        ViewModelProvider(this).get(MyPageViewModel::class.java)
    }
    var myPageInfo: MyPageInfoResponse? = null
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
            myPageInfo?.let { info ->
                findNavController().navigate(
                    MyPageFragmentDirections.actionMainMypageToEditProfileFragment(
                        info
                    )
                )
            }
        }

        binding.tvLogout.setOnClickListener {
            removeToken()
        }

        binding.tvWithdraw.setOnClickListener {
            viewModel.deleteUser()
        }

        viewModel.event.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { success ->
                if (success) {
                    removeToken()
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