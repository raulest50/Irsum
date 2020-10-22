package com.rendidor.irsum.fragmentDialogs

import android.app.AlertDialog
import android.content.Context

class GenericDialogs {

    companion object Dialogs{

        /**
         * equivalente al JOptionPane de java, permite mostrar un mensaje
         * al usuarion en una ventana emergente de manera simple y corta
         * sin necesidad de crear una clase fragmentDialog ni haciendo ningun xml para la vista
         */
        fun ShowDiaglog(title: String, msg: String, context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(msg)
                    .setCancelable(true)
            val alertDialog = builder.create()
            alertDialog.show()
        }

        /**
         * Permite mostrar de manera simple y directa un dialogo de confirmacion.
         * recibe como argumento el mensaje que se deseamostrar al usuario, la actividad que
         * invoca al dialogo y el tercer parametro es la funcion de callback. Si el usuario
         * hace click en "Si", se ejecuta la funcion cback(), deifinida por el usuario.
         * En kotlin ->Unit es el equivalente a return void en java
         */
        fun ConfirmationDiaglo(msg:String, context:Context, cback:()->Unit){
            val builder = AlertDialog.Builder(context)
            builder.setMessage(msg)
                    .setCancelable(true)
                    .setPositiveButton("Si") { dialog, id ->
                        cback()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }
            builder.create().show()
        }

    }
}