package com.fitsionary.momspt.presentation.home.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.response.RoutineItem
import com.fitsionary.momspt.data.api.response.getSampleRoutineItem
import com.fitsionary.momspt.presentation.base.BaseViewModel

class HomeViewModel : BaseViewModel() {
    val routineList = MutableLiveData<List<RoutineItem>>()

    init {
        routineList.postValue(getSampleRoutineItem())
    }
}