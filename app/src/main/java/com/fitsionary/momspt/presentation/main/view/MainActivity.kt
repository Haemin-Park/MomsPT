package com.fitsionary.momspt.presentation.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityMainBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.presentation.daily.view.DailyFragment
import com.fitsionary.momspt.presentation.home.view.HomeFragment
import com.fitsionary.momspt.presentation.main.viewmodel.MainViewModel
import com.fitsionary.momspt.presentation.mypage.view.MyPageFragment
import com.fitsionary.momspt.presentation.training.view.TrainingFragment

class MainActivity :
    BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.bottomNavigationMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.main_home -> bottomNavigationReplaceFragment(HomeFragment())
                R.id.main_trainig -> bottomNavigationReplaceFragment(TrainingFragment())
                R.id.main_daily -> bottomNavigationReplaceFragment(DailyFragment())
                R.id.main_mypage -> bottomNavigationReplaceFragment(MyPageFragment())
                else -> false
            }
        }
        binding.bottomNavigationMain.selectedItemId = R.id.main_home
    }

    private fun bottomNavigationReplaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(binding.frameMain.id, fragment).commit()
        return true
    }
}