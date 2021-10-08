package com.fitsionary.momspt.presentation.main.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityMainBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.presentation.main.viewmodel.MainViewModel


class MainActivity :
    BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = this.findNavController(R.id.main_nav_host_fragment)
        val bottomMenuId = setOf(
            R.id.main_home,
            R.id.main_workout,
            R.id.main_daily,
            R.id.main_mypage
        )
        appBarConfiguration = AppBarConfiguration(bottomMenuId)
        NavigationUI.setupWithNavController(binding.toolbarMain, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.bottomNavigationMain, navController)
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, _: Bundle? ->
            if (nd.id in bottomMenuId) {
                binding.toolbarMain.visibility = View.VISIBLE
                binding.toolbarMain.setTitleMargin(22, 0, 0, 0)
                binding.bottomNavigationMain.visibility = View.VISIBLE
                if (nd.id == R.id.main_home) {
                    binding.toolbarMain.setLogo(R.drawable.ic_logo2)
                } else {
                    binding.toolbarMain.logo = null
                }
            } else {
                when (nd.id) {
                    R.id.splashFragment, R.id.signInFragment -> binding.toolbarMain.visibility =
                        View.INVISIBLE
                    else -> {
                        if (nd.id == R.id.analysisResultFragment)
                            binding.toolbarMain.navigationIcon = null
                        binding.toolbarMain.visibility = View.VISIBLE
                        binding.toolbarMain.apply {
                            contentInsetStartWithNavigation = 0
                            setTitleMargin(0, 0, 0, 0)
                            setNavigationIcon(R.drawable.ic_back)
                            logo = null
                        }
                        binding.bottomNavigationMain.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.main_nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}