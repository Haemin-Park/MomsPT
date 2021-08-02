package com.fitsionary.momspt.presentation.workout.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.response.WorkoutItem
import com.fitsionary.momspt.data.api.response.getSampleRoutineItem
import com.fitsionary.momspt.presentation.base.BaseViewModel

class WorkoutViewModel : BaseViewModel() {
    val workoutList = MutableLiveData<List<WorkoutItem>>()

    init {
        workoutList.postValue(getSampleRoutineItem())
    }
}