package com.fitsionary.momspt.presentation.daily.view


import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentDailyBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.daily.adapter.DailyAdapter
import com.fitsionary.momspt.presentation.daily.viewmodel.DailyViewModel
import com.google.android.material.tabs.TabLayoutMediator

class DailyFragment :
    BaseFragment<FragmentDailyBinding, DailyViewModel>(R.layout.fragment_daily) {
    override val viewModel: DailyViewModel by lazy {
        ViewModelProvider(this).get(DailyViewModel::class.java)
    }
    private lateinit var dailyAdapter: DailyAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dailyAdapter = DailyAdapter(this)
        binding.pager.adapter = dailyAdapter
        TabLayoutMediator(binding.tabDaily, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.text = "매일"
                1 -> tab.text = "달력"
            }
        }.attach()
    }
}