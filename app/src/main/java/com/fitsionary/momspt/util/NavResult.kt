package com.fitsionary.momspt.util

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.data.model.WorkoutModel
import kotlinx.android.parcel.Parcelize

sealed class NavResult : Parcelable {
    @Parcelize
    object Cancel : NavResult()

    @Parcelize
    object Ok : NavResult()

    @Parcelize
    data class TodayWeight(val weight: Double) : NavResult()

    @Parcelize
    data class ChoiceResult(val step: Int, val stepDay: Int) : NavResult()
}

const val NAV_RESULT_KEY = "nav_result"
inline fun LifecycleOwner.navResult(
    navController: NavController,
    crossinline onResult: (NavResult?) -> Unit
) {
    val navId = navController.currentDestination!!.id
    val navBackStackEntry = navController.getBackStackEntry(navId)
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains(
                NAV_RESULT_KEY
            )
        ) {
            val result =
                navBackStackEntry.savedStateHandle.get<NavResult>(NAV_RESULT_KEY); onResult(result)
            navBackStackEntry.savedStateHandle.remove<String>(
                NAV_RESULT_KEY
            )
        }
    }
    navBackStackEntry.lifecycle.addObserver(observer)
    this.lifecycle.addObserver(
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
}

inline fun Fragment.navResult(crossinline onResult: (NavResult?) -> Unit) {
    viewLifecycleOwner.navResult(findNavController(), onResult)
}

fun Fragment.setNavResult(result: NavResult) {
    findNavController().previousBackStackEntry?.apply {
        savedStateHandle.set<NavResult>(
            NAV_RESULT_KEY,
            result
        )
    }
}