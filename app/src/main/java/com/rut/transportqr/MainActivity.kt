package com.rut.transportqr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "-----onCreate-----")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "-----onStart-----")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "-----onResume-----")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "-----onPause-----")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "-----onStop-----")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "-----onDestroy-----")
    }
}
