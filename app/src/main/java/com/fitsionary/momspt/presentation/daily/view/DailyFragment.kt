package com.fitsionary.momspt.presentation.daily.view


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentDailyBinding
import com.fitsionary.momspt.presentation.analysis.view.AnalysisActivity
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.daily.viewmodel.DailyViewModel
import com.fitsionary.momspt.presentation.main.view.MainActivity

class DailyFragment :
    BaseFragment<FragmentDailyBinding, DailyViewModel>(R.layout.fragment_daily) {
    override val viewModel: DailyViewModel by lazy {
        ViewModelProvider(this).get(DailyViewModel::class.java)
    }
    private lateinit var currentActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity)
            currentActivity = activity as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnTestAnalysis.setOnClickListener {
            startActivity(Intent(currentActivity, AnalysisActivity::class.java))
        }
    }
}