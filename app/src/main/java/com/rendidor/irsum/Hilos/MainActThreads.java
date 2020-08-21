package com.rendidor.irsum.Hilos;

import com.rendidor.irsum.Comunicaciones.InfoComm;
import com.rendidor.irsum.Definiciones.Producto;
import com.rendidor.irsum.Definiciones.Venta;
import com.rendidor.irsum.MainAct;

import java.util.ArrayList;
import java.util.List;


public class MainActThreads {
    MainAct ActMain;
    InfoComm ifc;

    public MainActThreads(MainAct ActMain2, String dirHost) {
        this.ActMain = ActMain2;
        this.ifc = new InfoComm(dirHost);
    }

    public void RegistrarProducto(final String Busqueda, final String tipo, final List<Venta> lv) {
        new Thread() {
            public void run() {
                List<Producto> lp = (List<Producto>) MainActThreads.this.ifc.BuscarProducto(Busqueda, tipo);
                if (lp.isEmpty() || ((Producto) lp.get(0)).getCodigo().equals("NNNRRR")) {
                    MainActThreads.this.MostrarWarningNocod();
                    return;
                }
                lv.add(0, MainActThreads.this.Pro2Ven((Producto) lp.get(0)));
                MainActThreads.this.SetListaVenta(lv);
            }
        }.start();
    }

    public void ImprimirVenta(final List<Venta> lv) {
        new Thread() {
            public void run() {
                String venta = "";
                for (int x = 0; x < lv.size() - 1; x++) {
                    venta = venta + ((Venta) lv.get(x)).getNombreProducto() + ":" + ((Venta) lv.get(x)).getCant() + ":" + ((Venta) lv.get(x)).getPvPublico() + "/-/";
                }
                int aux = lv.size() - 1;
                MainActThreads.this.ifc.ImprimirVenta(venta + ((Venta) lv.get(aux)).getNombreProducto() + ":" + ((Venta) lv.get(aux)).getCant() + ":" + ((Venta) lv.get(aux)).getPvPublico());
            }
        }.start();
    }

    public void SetListaVenta(final List<Venta> lv) {
        this.ActMain.runOnUiThread(new Runnable() {
            public void run() {
                MainActThreads.this.ActMain.MostrarListaVentas(lv);
            }
        });
    }

    public Venta Pro2Ven(Producto p) {
        return new Venta(p.getDescripcion(), Integer.parseInt(p.getPvpublico()));
    }

    public void ShowToast(final String text) {
        this.ActMain.runOnUiThread(new Runnable() {
            public void run() {
                MainActThreads.this.ActMain.ShowT(text);
            }
        });
    }

    public void AgregarVenta(List<Venta> list) {
        this.ActMain.runOnUiThread(new Runnable() {
            public void run() {
                MainActThreads.this.ActMain.CalcularSuma();
            }
        });
    }

    public void MostrarWarningNocod() {
        this.ActMain.runOnUiThread(new Runnable() {
            public void run() {
                MainActThreads.this.ActMain.ShowWarningNocod();
            }
        });
    }
}
