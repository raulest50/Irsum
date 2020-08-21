package com.rendidor.irsum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ParamAct extends SuperAct {
    Editor edit;
    Button guardar;
    SharedPreferences pre;
    EditText txHost;
    Button volver;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.act_param);
        this.volver = (Button) findViewById(R.id.Button_volver);
        this.guardar = (Button) findViewById(R.id.Button_Save_Config);
        this.txHost = (EditText) findViewById(R.id.Tx_Direccion);
        this.pre = getSharedPreferences("configuracion_irsum", 0);
        this.edit = this.pre.edit();
        this.txHost.setText(this.pre.getString("host", ""));
        this.guardar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ParamAct.this.edit.putString("host", ParamAct.this.txHost.getText().toString());
                ParamAct.this.edit.apply();
                ParamAct.this.ShowT("guardando...");
            }
        });
        this.volver.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ParamAct.this.startActivity(new Intent(ParamAct.this, MainAct.class));
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_param, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
