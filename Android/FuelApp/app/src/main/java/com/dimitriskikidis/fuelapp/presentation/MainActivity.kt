package com.dimitriskikidis.fuelapp.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dimitriskikidis.fuelapp.R
import com.dimitriskikidis.fuelapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.signInFragment,
                R.id.loadingFragment,
                R.id.mapFragment,
                R.id.fuelListFragment,
                R.id.settingsFragment,
                R.id.accountFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.main_nav_graph, inclusive = false, saveState = false)
                .setRestoreState(false)
                .build()

            when (item.itemId) {
                R.id.mapFragment -> {
                    navController.navigate(R.id.action_global_mapFragment, null, navOptions)
                }
                R.id.fuelListFragment -> {
                    navController.navigate(R.id.action_global_fuelListFragment, null, navOptions)
                }
                R.id.settingsFragment -> {
                    navController.navigate(R.id.action_global_settingsFragment, null, navOptions)
                }
                R.id.accountFragment -> {
                    navController.navigate(R.id.action_global_accountFragment, null, navOptions)
                }
            }
            true
        }

        binding.bottomNavigationView.setOnItemReselectedListener {  }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.mapFragment, R.id.fuelListFragment, R.id.settingsFragment,
                R.id.accountFragment -> {
                    binding.bottomNavigationView.isVisible = true
                }
                else -> binding.bottomNavigationView.isVisible = false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}