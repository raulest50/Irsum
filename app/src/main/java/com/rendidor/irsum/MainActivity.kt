package com.rendidor.irsum

import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.rendidor.irsum.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    public lateinit var binding: ActivityMainBinding
    private lateinit var navControlador: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // para acceder los views, reemplaza findViewByID()
        val view = binding.root
        setContentView(view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment
        navControlador = navHostFragment.navController

        setSupportActionBar(binding.toolbar) // pone el toolbar
        //agrega el boton de ir atras cuando se navega a otro fragmento

        setupActionBarWithNavController(navControlador)

        SetupNavegacion()

        binding.toolbar.setOnClickListener({})
    }

    /**
     * hace que el boton de ir atras funcione en la navegacion entre fragmentos
     */
    override fun onSupportNavigateUp(): Boolean {
        binding.imgbtnSettings.visibility = View.VISIBLE
        binding.btnImprimir.visibility = View.VISIBLE
        val navController = navControlador
        //val navController = findNavController(R.id.main_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun SetupNavegacion(){

        binding.imgbtnSettings.setOnClickListener {
            navControlador.navigate(R.id.action_homeFragment_to_settingsFragment)
            binding.imgbtnSettings.visibility = View.GONE
            binding.btnImprimir.visibility = View.GONE
        }
    }


}