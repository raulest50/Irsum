package com.rendidor.irsum.Comunicaciones;

import java.util.ArrayList;
import java.util.List;
import com.rendidor.irsum.Definiciones.Cliente;
import com.rendidor.irsum.Definiciones.Producto;
import com.rendidor.irsum.Definiciones.Proveedor;

public class InfoComm {

    /* renamed from: p */
    public Poster p;

    public InfoComm(String dirHost) {
        this.p = new Poster(dirHost);
    }

    public void ImprimirVenta(String venta) {
        this.p.HacerPosterVentas("op=0&venta=" + venta);
    }

    public List<Producto> BuscarProducto(String b, String tipo) {
        new ArrayList();
        return GenerarLProductos(this.p.HacerPoster("op=1&type=" + tipo + "&search=" + b));
    }

    public List<Cliente> BuscarCliente(String b, String tipo) {
        return new ArrayList<>();
    }

    public List<Proveedor> BuscarProveedor(String b, String tipo) {
        return new ArrayList<>();
    }

    public ArrayList<Producto> GenerarLProductos(String r) {
        ArrayList<Producto> LProductos = new ArrayList<>();
        try {
            if (r.equals("NNNRRR") || r.equals("") || r.equals(null)) {
                LProductos.add(new Producto("NNNRRR", "", "", "", "", "", "", ""));
                return LProductos;
            }
            try {
                String[] productos = r.split("--///--");
                for (String split : productos) {
                    String[] str = split.split("/-/");
                    LProductos.add(new Producto(str[0], str[1], str[2], str[3], str[4], str[5], str[6], str[7]));
                }
                return LProductos;
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return LProductos;
            }
        } catch (NullPointerException e2) {
            return new ArrayList<>();
        }
    }
}
