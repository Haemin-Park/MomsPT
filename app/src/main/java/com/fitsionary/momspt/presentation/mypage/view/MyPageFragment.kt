package com.fitsionary.momspt.presentation.mypage.view

import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentMypageBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.mypage.viewmodel.MyPageViewModel

class MyPageFragment :
    BaseFragment<FragmentMypageBinding, MyPageViewModel>(R.layout.fragment_mypage) {
    override val viewModel: MyPageViewModel by lazy {
        ViewModelProvider(this).get(MyPageViewModel::class.java)
    }
}