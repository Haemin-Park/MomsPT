package com.fitsionary.momspt.presentation.workout.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.BR
import com.fitsionary.momspt.R
import com.fitsionary.momspt.TEST_USER_NAME
import com.fitsionary.momspt.data.api.request.TodayWorkoutListRequest
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.FragmentWorkoutBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.base.BaseRecyclerViewAdapter
import com.fitsionary.momspt.presentation.home.view.HomeFragment.Companion.WORKOUT_NAME
import com.fitsionary.momspt.presentation.main.view.MainActivity
import com.fitsionary.momspt.presentation.workout.viewmodel.WorkoutViewModel
import com.fitsionary.momspt.util.DateUtil
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

    private val routineAdapter =
        object : BaseRecyclerViewAdapter<FragmentWorkoutBinding, WorkoutModel>(
            layoutResId = R.layout.item_workout_large,
            bindingVariableItemId = BR.LargeWorkoutItem,
            bindingVariableListenerId = BR.LargeWorkoutItemListener,
        ) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            vm = viewModel
            rvWorkout.adapter = routineAdapter
        }

        viewModel.getTodayComment(TEST_USER_NAME)

        viewModel.getTodayWorkoutList(
            TodayWorkoutListRequest(
                DateUtil.getRequestDateFormat(),
                TEST_USER_NAME
            )
        )

        routineAdapter.onItemClickListener = object : OnItemClickListener<WorkoutModel> {
            override fun onClick(item: WorkoutModel) {
                startActivity(
                    Intent(currentActivity, WorkoutDetailActivity::class.java).putExtra(
                        WORKOUT_NAME, item
                    )
                )
            }
        }
    }
}