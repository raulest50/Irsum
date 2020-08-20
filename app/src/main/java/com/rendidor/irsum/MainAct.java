package com.rendidor.irsum;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.rendidor.irsum.Adaptadores.RVVentasAdaptador;
import com.rendidor.irsum.Definiciones.Venta;
import com.rendidor.irsum.Hilos.MainActThreads;

import java.util.ArrayList;
import java.util.List;


import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;

public class MainAct extends SuperAct {
    private static final int REQUEST_CODE = 1234;
    public String Host; // direccion ip del servidor nodejs
    public TextView LCobro;
    public LayoutManager Llm;
    public Button botonCancelar; // borrar la lista del listview
    public Button botonManual; // ingresar un codigo de producto de manera manual
    public Button botonNoCod; // boton para ingresar un valor de dinero arbitrario para un producto no codificado
    public Button botonRegistrar; // boton para registrar venta
    final Context context = this;
    public EditText etx; // campo de texto para ingresar codigo de barras con ayuda del lector
    public List<Venta> listaVenta; // lista de productos registrados en la venta.
    MainActThreads mat;

    /* renamed from: rv */
    public RecyclerView rv;
    public RVVentasAdaptador rvpa;
    public int tipoBusqueda = 0;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.act_main);
        this.Host = getSharedPreferences("configuracion_irsum", 0).getString("host", "http://192.168.0.13:8084/Servidor_granero/Vestibulo");
        this.botonRegistrar = (Button) findViewById(R.id.bRegistrar);
        this.botonCancelar = (Button) findViewById(R.id.bCancelar);
        this.botonManual = (Button) findViewById(R.id.buttonManual);
        this.botonNoCod = (Button) findViewById(R.id.buttonNoCod);
        this.etx = (EditText) findViewById(R.id.campo_buscar);
        this.rv = (RecyclerView) findViewById(R.id.ListaProductos);
        this.Llm = new LinearLayoutManager(this);
        this.rv.setLayoutManager(this.Llm);
        this.LCobro = (TextView) findViewById(R.id.LCobro);
        this.listaVenta = new ArrayList();
        set_cursor(this);
        this.mat = new MainActThreads(this, this.Host);
        set_etx_listener();
        this.botonRegistrar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });
        this.botonCancelar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainAct.this.listaVenta.clear();
                MainAct.this.MostrarListaVentas(MainAct.this.listaVenta);
            }
        });
        this.botonManual.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainAct.this.DialogManual();
            }
        });
        this.botonNoCod.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainAct.this.DialogAddNoCod();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        EditText editText = (EditText) findViewById(R.id.campo_buscar);
        switch (item.getItemId()) {
            case R.id.parametros /*2131493003*/:
                startActivity(new Intent(this, ParamAct.class));
                break;
            case R.id.reportar /*2131493004*/:
                startActivity(new Intent(this, ReportAct.class));
                break;
            case R.id.imprimir /*2131493005*/:
                if (!this.listaVenta.isEmpty()) {
                    this.mat.ImprimirVenta(this.listaVenta);
                    break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("lp", (ArrayList) this.listaVenta);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        MostrarListaVentas(savedState.getParcelableArrayList("lp"));
    }

    public void close_softkey() {
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(this.etx.getWindowToken(), 0);
    }

    public void MostrarListaVentas(List<Venta> lv) {
        try {
            this.rvpa = new RVVentasAdaptador(lv, this, this.context);
            this.rv.setAdapter(this.rvpa);
            this.listaVenta = lv;
            CalcularSuma();
        } catch (NullPointerException e) {
            List<Venta> lv2 = new ArrayList<>();
            lv.add(new Venta("NullpointerException", 0));
            this.rvpa = new RVVentasAdaptador(lv2, this, this.context);
            this.rv.setAdapter(this.rvpa);
        }
    }

    public void BuscarP(final String busqueda) {
        disable_etx_listener();
        new Thread() {
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String tipo = "";
                switch (MainAct.this.tipoBusqueda) {
                    case 0:
                        tipo = "3";
                        break;
                    case 1:
                        tipo = "2";
                        break;
                    case 2:
                        tipo = "1";
                        break;
                }
                MainAct.this.mat.RegistrarProducto(busqueda, tipo, MainAct.this.listaVenta);
                if (MainAct.this.tipoBusqueda == 0) {
                    MainAct.this.runOnUiThread(new Runnable() {
                        public void run() {
                            MainAct.this.etx.requestFocus();
                            MainAct.this.etx.setText("");
                        }
                    });
                } else {
                    MainAct.this.runOnUiThread(new Runnable() {
                        public void run() {
                            MainAct.this.etx.requestFocus();
                            MainAct.this.close_softkey();
                        }
                    });
                }
                MainAct.this.set_etx_listener();
            }
        }.start();
    }

    public void set_cursor(MainAct ActMain) {
        Thread t = new Thread() {
            public void run() {
                while (true) {
                    MainAct.this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (MainAct.this.tipoBusqueda == 0) {
                                MainAct.this.etx.requestFocus();
                            }
                        }
                    });
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    public void set_etx_listener() {
        this.etx.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                try {
                    if (keyEvent.getKeyCode() != 66) {
                        return false;
                    }
                    MainAct.this.etx.requestFocus();
                    if (!MainAct.this.etx.getText().toString().equals("")) {
                        MainAct.this.BuscarP(MainAct.this.etx.getText().toString());
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }

    public void disable_etx_listener() {
        this.etx.setOnKeyListener(null);
    }

    public void MostrarToast(String tex) {
        Toast.makeText(getApplicationContext(), tex, 0);
    }

    public void CalcularSuma() {
        int suma = 0;
        for (int i = 0; i < this.listaVenta.size(); i++) {
            suma += ((Venta) this.listaVenta.get(i)).getCant() * ((Venta) this.listaVenta.get(i)).getPvPublico();
            this.LCobro.setText(Integer.toString(suma));
        }
        if (this.listaVenta.isEmpty()) {
            this.LCobro.setText("0");
        }
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", "android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS");
        intent.putExtra("android.speech.extra.PROMPT", "Diga El Valor :)");
        startActivityForResult(intent, REQUEST_CODE);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == -1) {
            try {
                this.listaVenta.add(0, new Venta("Producto Invocado Por Voz", Integer.parseInt((String) data.getStringArrayListExtra("android.speech.extra.RESULTS").get(0))));
                MostrarListaVentas(this.listaVenta);
            } catch (NumberFormatException e) {
                MostrarToast("Debe decir solo un numero");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void DialogManual() {
        View promptView = LayoutInflater.from(this.context).inflate(R.layout.addpro_nocod, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(promptView);
        final EditText EtxManualCodi = (EditText) promptView.findViewById(R.id.etxValCodi);
        C02649 r0 = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainAct.this.BuscarP(EtxManualCodi.getText().toString());
            }
        };
        alertDialogBuilder.setCancelable(false).setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) r0).setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.create().show();
        Button bt1 = (Button) promptView.findViewById(R.id.appk1);
        Button bt2 = (Button) promptView.findViewById(R.id.appk2);
        Button bt3 = (Button) promptView.findViewById(R.id.appk3);
        Button bt4 = (Button) promptView.findViewById(R.id.appk4);
        Button bt5 = (Button) promptView.findViewById(R.id.appk5);
        Button bt6 = (Button) promptView.findViewById(R.id.appk6);
        Button bt7 = (Button) promptView.findViewById(R.id.appk7);
        Button bt8 = (Button) promptView.findViewById(R.id.appk8);
        Button bt9 = (Button) promptView.findViewById(R.id.appk9);
        Button btb = (Button) promptView.findViewById(R.id.appkb);
        SetListenerToCustomNumKey((Button) promptView.findViewById(R.id.appk0), "0", EtxManualCodi);
        SetListenerToCustomNumKey(bt1, "1", EtxManualCodi);
        SetListenerToCustomNumKey(bt2, "2", EtxManualCodi);
        SetListenerToCustomNumKey(bt3, "3", EtxManualCodi);
        SetListenerToCustomNumKey(bt4, "4", EtxManualCodi);
        SetListenerToCustomNumKey(bt5, "5", EtxManualCodi);
        SetListenerToCustomNumKey(bt6, "6", EtxManualCodi);
        SetListenerToCustomNumKey(bt7, "7", EtxManualCodi);
        SetListenerToCustomNumKey(bt8, "8", EtxManualCodi);
        SetListenerToCustomNumKey(bt9, "9", EtxManualCodi);
        SetListenerToCustomNumKey(btb, "b", EtxManualCodi);
    }

    public void DialogAddNoCod() {
        View promptView = LayoutInflater.from(this.context).inflate(R.layout.popup, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(promptView);
        EditText etxUnitVal = (EditText) promptView.findViewById(R.id.userInput);
        final EditText editText = etxUnitVal;
        C025011 r0 = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int valorss;
                try {
                    valorss = Integer.parseInt(editText.getText().toString());
                } catch (NullPointerException e) {
                    valorss = -1;
                } catch (NumberFormatException e2) {
                    valorss = -1;
                }
                if (valorss > 0) {
                    MainAct.this.listaVenta.add(0, new Venta("producto Ingresado Manualmenete", valorss));
                    MainAct.this.MostrarListaVentas(MainAct.this.listaVenta);
                }
            }
        };
        alertDialogBuilder.setCancelable(false).setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) r0).setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.create().show();
        Button bt1 = (Button) promptView.findViewById(R.id.appk1);
        Button bt2 = (Button) promptView.findViewById(R.id.appk2);
        Button bt3 = (Button) promptView.findViewById(R.id.appk3);
        Button bt4 = (Button) promptView.findViewById(R.id.appk4);
        Button bt5 = (Button) promptView.findViewById(R.id.appk5);
        Button bt6 = (Button) promptView.findViewById(R.id.appk6);
        Button bt7 = (Button) promptView.findViewById(R.id.appk7);
        Button bt8 = (Button) promptView.findViewById(R.id.appk8);
        Button bt9 = (Button) promptView.findViewById(R.id.appk9);
        Button btb = (Button) promptView.findViewById(R.id.appkb);
        SetListenerToCustomNumKey((Button) promptView.findViewById(R.id.appk0), "0", etxUnitVal);
        SetListenerToCustomNumKey(bt1, "1", etxUnitVal);
        SetListenerToCustomNumKey(bt2, "2", etxUnitVal);
        SetListenerToCustomNumKey(bt3, "3", etxUnitVal);
        SetListenerToCustomNumKey(bt4, "4", etxUnitVal);
        SetListenerToCustomNumKey(bt5, "5", etxUnitVal);
        SetListenerToCustomNumKey(bt6, "6", etxUnitVal);
        SetListenerToCustomNumKey(bt7, "7", etxUnitVal);
        SetListenerToCustomNumKey(bt8, "8", etxUnitVal);
        SetListenerToCustomNumKey(bt9, "9", etxUnitVal);
        SetListenerToCustomNumKey(btb, "b", etxUnitVal);
    }

    public void ShowWarningNocod() {
        View promptView = LayoutInflater.from(this.context).inflate(R.layout.warning_lay, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false).setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setNegativeButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.create().show();
    }

    public void SetListenerToCustomNumKey(Button theB, final String n, final EditText TheEtx) {
        theB.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (n.equals("b")) {
                    String actual = TheEtx.getText().toString();
                    if (actual.length() == 1) {
                        TheEtx.setText("");
                    }
                    if (actual.length() >= 2) {
                        TheEtx.setText(actual.substring(0, actual.length() - 1));
                        return;
                    }
                    return;
                }
                TheEtx.setText(TheEtx.getText().toString() + n);
            }
        });
    }
}
