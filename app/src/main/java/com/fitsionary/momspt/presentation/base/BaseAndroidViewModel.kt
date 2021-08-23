package com.fitsionary.momspt.presentation.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject

abstract class BaseAndroidViewModel(application: Application) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()
    val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}