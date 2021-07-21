package com.fitsionary.momspt.presentation.routine.view

import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityRoutineBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.presentation.routine.viewmodel.RoutineViewModel

class RoutineActivity :
    BaseActivity<ActivityRoutineBinding, RoutineViewModel>(R.layout.activity_routine) {
    override val viewModel: RoutineViewModel by lazy {
        ViewModelProvider(this).get(RoutineViewModel::class.java)
    }
}