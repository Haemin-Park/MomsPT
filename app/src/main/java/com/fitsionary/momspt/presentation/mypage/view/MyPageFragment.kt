package com.fitsionary.momspt.presentation.mypage.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentMypageBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.binding.setProfileImageFromImageUrl
import com.fitsionary.momspt.presentation.mypage.viewmodel.MyPageViewModel

class MyPageFragment :
    BaseFragment<FragmentMypageBinding, MyPageViewModel>(R.layout.fragment_mypage) {
    override val viewModel: MyPageViewModel by lazy {
        ViewModelProvider(this).get(MyPageViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProfileImageFromImageUrl(
            binding.ivProfile,
            "https://www.dementianews.co.kr/news/photo/201902/1501_1270_5524.jpg"
        )

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(MyPageFragmentDirections.actionMainMypageToEditProfileFragment())
        }
    }
}