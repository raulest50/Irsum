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

    lateinit var binding: ActivityMainBinding
    private lateinit var navControlador: NavController

    lateinit var pl:PrefLoader

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

        this.pl = PrefLoader(this)

        /*
        binding.tvToolbarTitle.setOnClickListener({
            if(binding.tvToolbarTitle.text.equals("Ventas")){
                binding.tvToolbarTitle.text = pl.getString(PrefLoader.Keys.manual_ip_value, "NaN")
            } else{
                binding.tvToolbarTitle.text = "Ventas"
            }
        })
        */

        binding.toolbar.setOnLongClickListener {
            var infoDialog = InfoIpDialog()
            infoDialog.show(supportFragmentManager, "Satelink IP")
            return@setOnLongClickListener true
        }

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