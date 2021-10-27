package com.fitsionary.momspt.presentation.workout.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fitsionary.momspt.data.api.request.StepWorkoutRequest
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class WorkoutViewModel : BaseViewModel() {
    private val _currentStep = MutableLiveData<Int>()
    private val _currentStepDay = MutableLiveData<Int>()

    private val _step = MutableLiveData(0)
    val step: LiveData<Int>
        get() = _step

    private val _stepDay = MutableLiveData(0)
    val stepDay: LiveData<Int>
        get() = _stepDay

    private val _workoutList = MutableLiveData<List<WorkoutModel>>()
    val workoutList: LiveData<List<WorkoutModel>>
        get() = _workoutList

    val isWorkoutEnabled = Transformations.switchMap(step) { step ->
        Transformations.map(stepDay) { stepDay ->
            step == _currentStep.value && stepDay == _currentStepDay.value
        }
    }

    fun getTodayInfo() {
        addDisposable(
            NetworkService.api.getTodayInfo()
                .applyNetworkScheduler()
                .subscribe({
                    Timber.i(it.toString())
                    _currentStep.value = it.step
                    _currentStepDay.value = it.day
                    _step.value = it.step
                    _stepDay.value = it.day
                }, {
                    Timber.e(it.message!!)
                })
        )
    }

    fun getTodayWorkoutList() {
        addDisposable(
            NetworkService.api.getTodayWorkoutList()
                .applyNetworkScheduler()
                .subscribe({
                    Timber.i(it.toString())
                    _workoutList.value = it.map { response -> response.toModel() }
                }, {
                    Timber.e(it.message)
                })
        )
    }

    fun getStepWorkoutList(step: Int, stepDay: Int) {
        addDisposable(
            NetworkService.api.getStepWorkoutList(StepWorkoutRequest(step, stepDay))
                .applyNetworkScheduler()
                .subscribe({
                    Timber.i(it.toString())
                    _step.value = step
                    _stepDay.value = stepDay
                    _workoutList.value = it.map { response -> response.toModel() }
                }, {
                    Timber.e(it.message)
                })
        )
    }
}