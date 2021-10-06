package com.fitsionary.momspt.presentation.daily.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.presentation.base.BaseViewModel

class DayStatisticsDetailViewModel : BaseViewModel() {
    private val _workoutList = MutableLiveData<List<WorkoutModel>>()
    val workoutList: LiveData<List<WorkoutModel>>
        get() = _workoutList
}