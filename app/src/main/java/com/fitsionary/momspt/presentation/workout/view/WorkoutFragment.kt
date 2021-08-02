package com.fitsionary.momspt.presentation.workout.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.BR
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.api.response.WorkoutItem
import com.fitsionary.momspt.databinding.FragmentWorkoutBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.base.BaseRecyclerView
import com.fitsionary.momspt.presentation.main.view.MainActivity
import com.fitsionary.momspt.presentation.workout.viewmodel.WorkoutViewModel
import com.fitsionary.momspt.util.listener.OnItemClickListener

class WorkoutFragment :
    BaseFragment<FragmentWorkoutBinding, WorkoutViewModel>(R.layout.fragment_workout) {
    override val viewModel: WorkoutViewModel by lazy {
        ViewModelProvider(this).get(WorkoutViewModel::class.java)
    }

    private lateinit var currentActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity)
            currentActivity = activity as MainActivity
    }

    private val routineAdapter = object : BaseRecyclerView<FragmentWorkoutBinding, WorkoutItem>(
        layoutResId = R.layout.item_workout_fragment_workout,
        bindingVariableItemId = BR.WorkoutItem,
        bindingVariableListenerId = BR.Listener
    ) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            vm = viewModel
            rvWorkout.adapter = routineAdapter
        }

        routineAdapter.onItemClickListener = object : OnItemClickListener<WorkoutItem> {
            override fun onClick(item: WorkoutItem) {
                val intent = Intent(currentActivity, WorkoutStartActivity::class.java)
                startActivity(intent)
            }
        }
    }
}