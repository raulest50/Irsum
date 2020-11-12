package com.rendidor.irsum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.Gson
import com.rendidor.irsum.Definiciones.ItemVenta
import com.rendidor.irsum.databinding.ActivityMainBinding
import com.rendidor.irsum.fragmentDialogs.InfoIpDialog
import com.rendidor.irsum.fragmentDialogs.ManualRegDialog
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.lang.IllegalArgumentException
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navControlador: NavController

    /**
     * Home Fragment guarda la lista de ventas en esta variable cuando se navega entre fragmentos.
     * En la navegacion de fragmentos, estos son construidos y destruidos segun se haga la navegacion
     * pero la actividad permacene, por lo que es ideal para el intercambio de datos entre fragmentos
     * y para guardar datos en ciertos casos.
     */
    var saved_list = LinkedList<ItemVenta>()

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
            try {
                navControlador.navigate(R.id.action_homeFragment_to_settingsFragment)
                binding.imgbtnSettings.visibility = View.GONE
                binding.btnImprimir.visibility = View.GONE
            } catch (e:IllegalArgumentException){ } // en caso de presionar varias veces boton
            // de ir a configuracion para evitar crash.
        }
    }


}