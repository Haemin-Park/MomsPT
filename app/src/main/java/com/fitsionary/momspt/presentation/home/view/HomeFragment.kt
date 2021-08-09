package com.fitsionary.momspt.presentation.home.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.BR
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.api.request.TodayWorkoutListRequest
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.FragmentHomeBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.base.BaseRecyclerView
import com.fitsionary.momspt.presentation.home.viewmodel.HomeViewModel
import com.fitsionary.momspt.presentation.main.view.MainActivity
import com.fitsionary.momspt.presentation.workout.view.WorkoutPlayActivity
import com.fitsionary.momspt.presentation.workout.view.WorkoutPlayActivity.Companion.WORKOUT_NAME
import com.fitsionary.momspt.util.DateUtil
import com.fitsionary.momspt.util.listener.OnItemClickListener


class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    override val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }
    private lateinit var currentActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity)
            currentActivity = activity as MainActivity
    }

    private val routineAdapter = object : BaseRecyclerView<FragmentHomeBinding, WorkoutModel>(
        layoutResId = R.layout.item_workout_medium,
        bindingVariableItemId = BR.MediumWorkoutItem,
        bindingVariableListenerId = BR.MediumWorkoutItemListener
    ) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            vm = viewModel
            rvRecommendWorkout.adapter = routineAdapter
        }

        viewModel.getTodayComment("fit")

        viewModel.comment.observe(viewLifecycleOwner, {
            binding.commentItem = it
        })

        viewModel.getTodayWorkoutList(
            TodayWorkoutListRequest(
                DateUtil.getRequestDateFormat(),
                "fit"
            )
        )

        routineAdapter.onItemClickListener = object : OnItemClickListener<WorkoutModel> {
            override fun onClick(item: WorkoutModel) {
                startActivity(
                    Intent(currentActivity, WorkoutPlayActivity::class.java).putExtra(
                        WORKOUT_NAME, item.name
                    )
                )
            }
        }
    }
}