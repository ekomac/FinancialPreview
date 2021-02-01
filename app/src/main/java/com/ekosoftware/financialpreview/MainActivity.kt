package com.ekosoftware.financialpreview

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.financialpreview.app.Constants
import com.ekosoftware.financialpreview.databinding.ActivityMainBinding
import com.ekosoftware.financialpreview.presentation.NavigationViewModel
import com.ekosoftware.financialpreview.util.hide
import com.ekosoftware.financialpreview.util.show
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navViewModel: NavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachNavigationChanges()

        val navView: BottomNavigationView = binding.bottomNavigationView
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.home_page_fragment, R.id.pending_page_fragment))

        //setupActionBarWithNavController(this, navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        /*NavigationUI.setupWithNavController(navView, navController, appBarConfiguration)*/

        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id in arrayOf(
                    R.id.pending_page_fragment,
                    R.id.accounts,
                    R.id.records
                )
            ) binding.mainFab.show() else binding.mainFab.hide()

            if (destination.id in arrayOf(
                    R.id.home_page_fragment,
                    R.id.pending_page_fragment,
                    R.id.accounts,
                    R.id.records
                )
            ) navView.show() else navView.hide()

            if (destination.id == R.id.accounts) navViewModel.setOptionForCompose(NavigationViewModel.OPTION_ADD_ACCOUNT)
            if (destination.id == R.id.records) navViewModel.setOptionForCompose(NavigationViewModel.OPTION_ADD_RECORD)
        }

        setFabListener()
    }

    private fun attachNavigationChanges() = with(navViewModel) {
        currentOptionForCompose.observe(this@MainActivity, tabCurrentPositionObserver)
    }

    private var currentTabPosition = 0

    private val tabCurrentPositionObserver = Observer<Int> {
        currentTabPosition = it
    }

    private fun setFabListener() = binding.mainFab.setOnClickListener { navigateToCompose(currentTabPosition) }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    private fun navigateToCompose(position: Int) {
        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
            }
        }

        val action = when (position) {
            NavigationViewModel.OPTION_ADD_MOVEMENT -> MainNavGraphDirections.actionGlobalEditMovementFragment(movementId = Constants.nan, movementUI = null)
            NavigationViewModel.OPTION_ADD_BUDGET -> MainNavGraphDirections.actionGlobalEditBudgetFragment()
            NavigationViewModel.OPTION_ADD_GROUP -> MainNavGraphDirections.actionGlobalEditSettleGroupFragment(null)
            NavigationViewModel.OPTION_ADD_ACCOUNT -> MainNavGraphDirections.actionGlobalEditAccountFragment()
            else -> MainNavGraphDirections.actionGlobalSettleFragment(0, "")
        }
        findNavController(R.id.nav_host_fragment).navigate(action)
    }
}