package com.fitsionary.momspt.presentation.workoutplay.viewmodel

import android.app.Application
import android.text.format.DateUtils
import android.view.View
import androidx.lifecycle.*
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.enum.WorkoutAnalysisTypeEnum
import com.fitsionary.momspt.database.getDatabase
import com.fitsionary.momspt.domain.WorkoutLandmarkDomainModel
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.repository.WorkoutPoseLandmarkRepository
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.*

class WorkoutPlayViewModel(
    application: Application,
    workoutCode: String,
    workoutAnalysisType: WorkoutAnalysisTypeEnum
) :
    BaseAndroidViewModel(application) {
    private val database = getDatabase(application)
    private val workoutPoseLandmarkRepository =
        WorkoutPoseLandmarkRepository(database)

    val isAiGuide: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    val workoutLandmarks: LiveData<WorkoutLandmarkDomainModel> =
        workoutPoseLandmarkRepository.workoutLandmarks(workoutCode)

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _cumulativeScore = MutableLiveData<Int>()
    val cumulativeScore: LiveData<Int>
        get() = _cumulativeScore

    val currentScore = Transformations.map(score) {
        when (workoutAnalysisType) {
            WorkoutAnalysisTypeEnum.SCORING -> application.getString(R.string.score_format, it)
            WorkoutAnalysisTypeEnum.COUNTING -> application.getString(R.string.count_format, it)
        }
    }

    val workoutAnalysisType: String = workoutAnalysisType.name

    private lateinit var timer: Timer

    private val _timerCountDown = MutableLiveData<Long>()
    private val _aiGuideCountDown = MutableLiveData<Long>()

    val cnt = MutableLiveData<Int>()

    val formattedTimer = Transformations.map(_timerCountDown) { time ->
        DateUtils.formatElapsedTime(time / 1000)
    }

    private val _aiGuideStart = MutableLiveData(false)
    val aiGuideStart: LiveData<Boolean>
        get() = _aiGuideStart

    val aiGuideVisible = Transformations.map(_aiGuideStart) {
        if (it == null || it) {
            View.VISIBLE
        } else
            View.INVISIBLE
    }

    init {
        _score.value = 0
        _cumulativeScore.value = 0
        cnt.value = 0
    }

    fun timerSet(total: Long, guideTime: Long) {
        _timerCountDown.value = total
        _aiGuideCountDown.value = guideTime
    }

    fun countDownTimerStart() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (_aiGuideCountDown.value == 0L) {
                    isAiGuide.onNext(true)
                }
                if (_aiGuideCountDown.value == -1000L) {
                    isAiGuide.onNext(false)
                    _aiGuideStart.postValue(true)
                }
                _timerCountDown.postValue(_timerCountDown.value?.minus(1000))
                _aiGuideCountDown.postValue(_aiGuideCountDown.value?.minus(1000))
            }
        }, 1000, 1000)
    }

    fun countDownTimerStop() {
        if (::timer.isInitialized) timer.cancel()
    }

    fun setScore(score: Int) {
        if (_aiGuideStart.value == true && _timerCountDown.value != 0L) {
            _score.postValue(score)
            if (workoutAnalysisType == WorkoutAnalysisTypeEnum.SCORING.name) {
                _cumulativeScore.postValue(_cumulativeScore.value?.plus(score))
                cnt.postValue(cnt.value?.plus(1))
            }
        }
    }

    class ViewModelFactory(
        private val application: Application,
        private val workoutCode: String,
        private val workoutType: WorkoutAnalysisTypeEnum
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(WorkoutPlayViewModel::class.java)) {
                WorkoutPlayViewModel(application, workoutCode, workoutType) as T
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}