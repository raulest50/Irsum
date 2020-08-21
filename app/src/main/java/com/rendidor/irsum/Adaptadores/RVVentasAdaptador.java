package com.rendidor.irsum.Adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rendidor.irsum.Definiciones.Venta;

import java.util.List;

import com.rendidor.irsum.Definiciones.Venta;
import com.rendidor.irsum.MainAct;
import com.rendidor.irsum.R;

public class RVVentasAdaptador extends Adapter<RVVentasAdaptador.VentaViewHolder> {
    public Context context;
    public List<Venta> listaVentas;
    public MainAct mact;

    public class VentaViewHolder extends ViewHolder {
        Button BRetirar;
        TextView Descripcion;
        Button Num;

        /* renamed from: Pr */
        TextView f17Pr;
        TextView PvPublico;
        Button UPN;

        public VentaViewHolder(View itemView) {
            super(itemView);
            this.Descripcion = (TextView) itemView.findViewById(R.id.NombreVenta);
            this.PvPublico = (TextView) itemView.findViewById(R.id.PvPublico);
            this.BRetirar = (Button) itemView.findViewById(R.id.BRetirar);
            this.UPN = (Button) itemView.findViewById(R.id.upn);
            this.Num = (Button) itemView.findViewById(R.id.bNum);
            this.f17Pr = (TextView) itemView.findViewById(R.id.labpr);
            this.BRetirar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    try {
                        int quan = ((Venta) RVVentasAdaptador.this.listaVentas.get(VentaViewHolder.this.getLayoutPosition())).getCant();
                        if (quan > 1) {
                            ((Venta) RVVentasAdaptador.this.listaVentas.get(VentaViewHolder.this.getLayoutPosition())).setCant(quan - 1);
                            RVVentasAdaptador.this.notifyItemChanged(VentaViewHolder.this.getLayoutPosition());
                            RVVentasAdaptador.this.mact.listaVenta = RVVentasAdaptador.this.listaVentas;
                            RVVentasAdaptador.this.triggerSuma();
                            return;
                        }
                        RVVentasAdaptador.this.listaVentas.remove(VentaViewHolder.this.getLayoutPosition());
                        RVVentasAdaptador.this.notifyItemRemoved(VentaViewHolder.this.getLayoutPosition());
                        RVVentasAdaptador.this.mact.listaVenta = RVVentasAdaptador.this.listaVentas;
                        RVVentasAdaptador.this.triggerSuma();
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });
            this.UPN.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ((Venta) RVVentasAdaptador.this.listaVentas.get(VentaViewHolder.this.getLayoutPosition())).setCant(((Venta) RVVentasAdaptador.this.listaVentas.get(VentaViewHolder.this.getLayoutPosition())).getCant() + 1);
                    RVVentasAdaptador.this.notifyItemChanged(VentaViewHolder.this.getLayoutPosition());
                    RVVentasAdaptador.this.mact.listaVenta = RVVentasAdaptador.this.listaVentas;
                    RVVentasAdaptador.this.triggerSuma();
                }
            });
            this.Num.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    View promptView = LayoutInflater.from(RVVentasAdaptador.this.context).inflate(R.layout.popup, null);
                    Builder alertDialogBuilder = new Builder(RVVentasAdaptador.this.context);
                    alertDialogBuilder.setView(promptView);
                    EditText input = (EditText) promptView.findViewById(R.id.userInput);
                    final EditText editText = input;
                    DialogInterface.OnClickListener metodoOK = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int canti = Integer.parseInt(editText.getText().toString());
                            if (canti > 0) {
                                ((Venta) RVVentasAdaptador.this.listaVentas.get(VentaViewHolder.this.getLayoutPosition())).setCant(canti);
                                RVVentasAdaptador.this.notifyItemChanged(VentaViewHolder.this.getLayoutPosition());
                                RVVentasAdaptador.this.mact.listaVenta = RVVentasAdaptador.this.listaVentas;
                                RVVentasAdaptador.this.triggerSuma();
                                return;
                            }
                            RVVentasAdaptador.this.listaVentas.remove(VentaViewHolder.this.getLayoutPosition());
                            RVVentasAdaptador.this.notifyItemRemoved(VentaViewHolder.this.getLayoutPosition());
                            RVVentasAdaptador.this.mact.listaVenta = RVVentasAdaptador.this.listaVentas;
                            RVVentasAdaptador.this.triggerSuma();
                        }
                    };
                    alertDialogBuilder.setCancelable(false).setPositiveButton((CharSequence) "OK", metodoOK).setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
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
                    RVVentasAdaptador.this.SetListenerToCustomNumKey((Button) promptView.findViewById(R.id.appk0), "0", input);
                    RVVentasAdaptador.this.SetListenerToCustomNumKey(bt1, "1", input);
                    RVVentasAdaptador.this.SetListenerToCustomNumKey(bt2, "2", input);
                    RVVentasAdaptador.this.SetListenerToCustomNumKey(bt3, "3", input);
                    RVVentasAdaptador.this.SetListenerToCustomNumKey(bt4, "4", input);
                    RVVentasAdaptador.this.SetListenerToCustomNumKey(bt5, "5", input);
                    RVVentasAdaptador.this.SetListenerToCustomNumKey(bt6, "6", input);
                    RVVentasAdaptador.this.SetListenerToCustomNumKey(bt7, "7", input);
                    RVVentasAdaptador.this.SetListenerToCustomNumKey(bt8, "8", input);
                    RVVentasAdaptador.this.SetListenerToCustomNumKey(bt9, "9", input);
                    RVVentasAdaptador.this.SetListenerToCustomNumKey(btb, "b", input);
                }
            });
        }
    }

    public RVVentasAdaptador(List<Venta> listaProductos, MainAct mact2, Context context2) {
        this.listaVentas = listaProductos;
        this.mact = mact2;
        this.context = context2;
    }

    public VentaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new VentaViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.venta_lay, viewGroup, false));
    }

    public void onBindViewHolder(VentaViewHolder pvh, int i) {
        pvh.Descripcion.setText(((Venta) this.listaVentas.get(i)).getNombreProducto());
        pvh.PvPublico.setText(Integer.toString(((Venta) this.listaVentas.get(i)).getPvPublico()));
        pvh.Num.setText(Integer.toString(((Venta) this.listaVentas.get(i)).getCant()));
        pvh.f17Pr.setText(Integer.toString(((Venta) this.listaVentas.get(i)).getPvPublico() * ((Venta) this.listaVentas.get(i)).getCant()));
    }

    public int getItemCount() {
        return this.listaVentas.size();
    }

    public void cancelar() {
        this.listaVentas.clear();
        notifyAll();
        this.mact.listaVenta = this.listaVentas;
        triggerSuma();
    }

    public void triggerSuma() {
        this.mact.runOnUiThread(new Runnable() {
            public void run() {
                RVVentasAdaptador.this.mact.CalcularSuma();
            }
        });
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
