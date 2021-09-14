package com.fitsionary.momspt.presentation.daily.view


import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentDailyBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.daily.viewmodel.DailyViewModel

class DailyFragment :
    BaseFragment<FragmentDailyBinding, DailyViewModel>(R.layout.fragment_daily) {
    override val viewModel: DailyViewModel by lazy {
        ViewModelProvider(this).get(DailyViewModel::class.java)
    }
}