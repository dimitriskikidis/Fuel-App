package com.dimitriskikidis.admin.fuelapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.dimitriskikidis.admin.fuelapp.R
import com.dimitriskikidis.admin.fuelapp.databinding.ActivityMainBinding
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
                R.id.mainMenuFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            val x = controller.backQueue
//            for (y: NavBackStackEntry in x) {
//                Log.d("NAV -> ", y.destination.displayName + " " + y)
//            }
//            Log.d("NAV -> ","-----")
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}