package com.fitsionary.momspt.presentation.daily.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fitsionary.momspt.data.api.response.MonthlyStatisticsItem
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.MonthlyStatisticsModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import com.prolificinteractive.materialcalendarview.CalendarDay
import timber.log.Timber

class MonthStatisticsViewModel : BaseViewModel() {
    private val _monthlyStatistics = MutableLiveData<MonthlyStatisticsModel>()
    val monthlyStatistics: LiveData<MonthlyStatisticsModel>
        get() = _monthlyStatistics

    val calendarDayList = Transformations.map(monthlyStatistics) {
        makeCalendarDates(it.monthlyStatistics, it.year, it.month)
    }

    fun getUserMonthlyStatistic(year: Int, month: Int) {
        NetworkService.api.getMonthlyStatistics(year, month)
            .applyNetworkScheduler()
            .subscribe({
                Timber.i(it.toString())
                _monthlyStatistics.value = it.toModel(year, month)
            }, {})
    }

    private fun makeCalendarDates(
        dates: List<MonthlyStatisticsItem>,
        year: Int,
        month: Int
    ): List<CalendarDay> {
        val calendarDates = mutableListOf<CalendarDay>()
        for (dayItem in dates) {
            if (dayItem.status == COMPLETE)
                calendarDates.add(CalendarDay.from(year, month, dayItem.day))
        }
        return calendarDates
    }
}

private val COMPLETE = "COMPLETE"