package com.fitsionary.momspt.presentation.workoutplay.viewmodel

import android.app.Application
import android.text.format.DateUtils
import android.view.View
import androidx.lifecycle.*
import com.fitsionary.momspt.database.getDatabase
import com.fitsionary.momspt.domain.WorkoutLandmarkDomainModel
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.repository.WorkoutPoseLandmarkRepository
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber
import java.util.*

class WorkoutPlayViewModel(application: Application, workoutCode: String) :
    BaseAndroidViewModel(application) {
    private val database = getDatabase(application)
    private val workoutPoseLandmarkRepository =
        WorkoutPoseLandmarkRepository(database)
    val isGuide: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    val workoutLandmarks: LiveData<WorkoutLandmarkDomainModel> =
        workoutPoseLandmarkRepository.workoutLandmarks(workoutCode)

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _cumulativeScore = MutableLiveData<Double>()
    val cumulativeScore: LiveData<Double>
        get() = _cumulativeScore

    private lateinit var timer: Timer

    private val _timerCountDown = MutableLiveData<Long>()
    private val _guideCountDown = MutableLiveData<Long>()

    private val _isWorkoutEnd = MutableLiveData<Boolean>(false)
    val isWorkoutEnd: LiveData<Boolean>
        get() = _isWorkoutEnd

    val cnt = MutableLiveData<Int>()

    val formattedTimer = Transformations.map(_timerCountDown) { time ->
        if (time <= 0L) {
            countDownTimerStop()
            _isWorkoutEnd.value = true
        }
        DateUtils.formatElapsedTime(time / 1000)
    }

    private val _guideStart = MutableLiveData(false)
    val guideVisible = Transformations.map(_guideStart) {
        if (it == null || it)
            View.VISIBLE
        else
            View.INVISIBLE
    }

    init {
        _score.value = 0
        _cumulativeScore.value = 0.0
        cnt.value = 0
    }

    fun timerSet(total: Long, guideTime: Long) {
        _timerCountDown.value = total
        _guideCountDown.value = guideTime
    }

    fun countDownTimerStart() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (_guideCountDown.value == 0L) {
                    isGuide.onNext(true)
                }
                if (_guideCountDown.value == -1000L) {
                    isGuide.onNext(false)
                    _guideStart.postValue(true)
                }
                _timerCountDown.postValue(_timerCountDown.value?.minus(1000))
                _guideCountDown.postValue(_guideCountDown.value?.minus(1000))
            }
        }, 1000, 1000)
    }

    fun countDownTimerStop() {
        if (::timer.isInitialized) timer.cancel()
    }

    fun setScore(score: Int) {
        if (_guideStart.value == true && _timerCountDown.value != 0L) {
            _score.postValue(score)
            _cumulativeScore.postValue(_cumulativeScore.value?.plus(score))
            cnt.postValue(cnt.value?.plus(1))
        }
    }

    class ViewModelFactory(private val application: Application, private val workoutCode: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(WorkoutPlayViewModel::class.java)) {
                WorkoutPlayViewModel(application, workoutCode) as T
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}