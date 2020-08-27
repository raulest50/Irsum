package com.rendidor.irsum;

import android.app.AlertDialog;
import android.content.Context;

public class GenericDialogs {

    public static void ShowDiaglog(String title, String msg, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg)
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
