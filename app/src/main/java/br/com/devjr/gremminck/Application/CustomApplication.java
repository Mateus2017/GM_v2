package br.com.devjr.gremminck.Application;

import android.app.Application;

import br.com.devjr.gremminck.Modificadores.Usuario;

public class CustomApplication extends Application {
    private static Usuario usr;

    @Override
    public void onCreate() {
        super.onCreate();

        usr = new Usuario();
    }

    public static Usuario getUsr() {
        return usr;
    }

    public static void setUsr(Usuario usr) {
        CustomApplication.usr = usr;
    }
}
