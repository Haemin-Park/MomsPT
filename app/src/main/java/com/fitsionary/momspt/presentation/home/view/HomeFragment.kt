package com.fitsionary.momspt.presentation.home.view

import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentHomeBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.home.viewmodel.HomeViewModel

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    override val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }
}