package com.fitsionary.momspt.presentation.home.view

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

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    override val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            vm = viewModel
            rvRoutine.adapter = object : BaseRecyclerView<FragmentHomeBinding, RoutineItem>(
                layoutResId = R.layout.item_routine,
                bindingVariableId = BR.RoutineItem
            ) {}
        }
    }
}