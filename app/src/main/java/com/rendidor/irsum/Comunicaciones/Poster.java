package com.rendidor.irsum.Comunicaciones;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Poster {
    public String dirHost = "http://192.168.0.13:8080/s/info";

    public Poster(String dirHost2) {
        this.dirHost = dirHost2;
    }

    public String HacerPoster(String Parametros) {
        String serv_print = "";
        try {
            URL url = new URL(this.dirHost);
            try {
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                out.write(Parametros);
                out.flush();
                out.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while (true) {
                    String respuesta = in.readLine();
                    if (respuesta == null) {
                        break;
                    }
                    serv_print = serv_print + respuesta;
                    in.close();
                }
            } catch (MalformedURLException e) {

                URL url2 = url;
                e.printStackTrace();
                return serv_print;
            } catch (IOException e) {
                e.printStackTrace();
                return serv_print;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return serv_print;
        }
        return serv_print;
    }

    public String HacerPosterVentas(String Parametros) {
        String serv_print = "";
        try {
            URL url = new URL(this.dirHost.replace("info", "v"));
            try {
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                out.write(Parametros);
                out.flush();
                out.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while (true) {
                    String respuesta = in.readLine();
                    if (respuesta == null) {
                        break;
                    }
                    serv_print = serv_print + respuesta;
                    in.close();
                }
            } catch (MalformedURLException e) {
                e = e;
                URL url2 = url;
                e.printStackTrace();
                return serv_print;
            } catch (IOException e) {
                URL url3 = url;
                e.printStackTrace();
                return serv_print;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return serv_print;
        }
        return serv_print;
    }
}
