package com.fitsionary.momspt.util.rx

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

fun ui(): Scheduler = AndroidSchedulers.mainThread()
fun computation(): Scheduler = Schedulers.computation()
fun newThread(): Scheduler = Schedulers.newThread()
fun io(): Scheduler = Schedulers.io()
fun <T> Single<T>.applyNetworkScheduler(): Single<T> = subscribeOn(io()).observeOn(ui())
