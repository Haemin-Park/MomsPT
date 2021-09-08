package com.fitsionary.momspt.presentation.intro.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityIntroBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.presentation.intro.viewmodel.IntroViewModel

class IntroActivity : BaseActivity<ActivityIntroBinding, IntroViewModel>(R.layout.activity_intro) {
    override val viewModel: IntroViewModel by lazy {
        ViewModelProvider(this).get(IntroViewModel::class.java)
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = this.findNavController(R.id.intro_nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.signInFragment))
        NavigationUI.setupWithNavController(
            binding.toolbarIntro,
            navController,
            appBarConfiguration
        )

        navController.addOnDestinationChangedListener { _: NavController, nd: NavDestination, _: Bundle? ->
            if (nd.id == R.id.splashFragment || nd.id == R.id.signInFragment) {
                binding.toolbarIntro.visibility = View.GONE
            } else {
                binding.toolbarIntro.visibility = View.VISIBLE
                binding.toolbarIntro.apply {
                    contentInsetStartWithNavigation = 0
                    setTitleMargin(0, 0, 0, 0)
                    setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.intro_nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}