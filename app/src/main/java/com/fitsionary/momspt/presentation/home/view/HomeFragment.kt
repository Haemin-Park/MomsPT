package com.fitsionary.momspt.presentation.home.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.BR
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.api.response.RoutineItem
import com.fitsionary.momspt.databinding.FragmentHomeBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.base.BaseRecyclerView
import com.fitsionary.momspt.presentation.home.viewmodel.HomeViewModel
import com.fitsionary.momspt.presentation.main.view.MainActivity
import com.fitsionary.momspt.presentation.workout.view.WorkoutStartActivity
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

    private val routineAdapter = object : BaseRecyclerView<FragmentHomeBinding, RoutineItem>(
        layoutResId = R.layout.item_routine,
        bindingVariableItemId = BR.RoutineItem,
        bindingVariableListenerId = BR.Listener
    ) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            vm = viewModel
            rvRoutine.adapter = routineAdapter
        }

        routineAdapter.onItemClickListener = object : OnItemClickListener<RoutineItem> {
            override fun onClick(item: RoutineItem) {
                val intent = Intent(currentActivity, WorkoutStartActivity::class.java)
                startActivity(intent)
            }
        }
    }
}