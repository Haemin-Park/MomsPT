package com.fitsionary.momspt.presentation.daily.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fitsionary.momspt.presentation.daily.view.DayStatisticsFragment
import com.fitsionary.momspt.presentation.daily.view.MonthStatisticsFragment

class DailyAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DayStatisticsFragment()
            else -> MonthStatisticsFragment()
        }
    }
}