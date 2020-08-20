package com.rendidor.irsum;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ReportAct extends SuperAct {
    Button borrar;
    Button enviar;
    EditText reporte;
    Button volver;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0267R.layout.act_report);
        this.volver = (Button) findViewById(C0267R.C0269id.BotonVolverReporte);
        this.borrar = (Button) findViewById(C0267R.C0269id.BotonBorrar);
        this.enviar = (Button) findViewById(C0267R.C0269id.BotonEnviar);
        this.reporte = (EditText) findViewById(C0267R.C0269id.TxReporte);
        this.borrar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ReportAct.this.reporte.setText("");
            }
        });
        this.volver.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ReportAct.this.startActivity(new Intent(ReportAct.this, MainAct.class));
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0267R.C0270menu.menu_report, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
