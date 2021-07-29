package com.fitsionary.momspt.presentation.home.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.response.WorkoutItem
import com.fitsionary.momspt.data.api.response.getSampleRoutineItem
import com.fitsionary.momspt.presentation.base.BaseViewModel

class HomeViewModel : BaseViewModel() {
    val workoutList = MutableLiveData<List<WorkoutItem>>()

    init {
        workoutList.postValue(getSampleRoutineItem())
    }
}