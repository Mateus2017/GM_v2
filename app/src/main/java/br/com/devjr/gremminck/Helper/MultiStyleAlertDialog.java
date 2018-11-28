package br.com.devjr.gremminck.Helper;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import br.com.devjr.gremminck.R;

public class MultiStyleAlertDialog {

    private static android.app.AlertDialog.Builder alertDialog;
    private static View layout;
    private static Context contexto;

    public MultiStyleAlertDialog(Context contexto){
        if (contexto == null){
            throw new NullPointerException("Contexto do MultiStyleAlertDialog null");
        }
        alertDialog = new android.app.AlertDialog.Builder(contexto);
        contexto = contexto;
    }

    public boolean showForCheckBox(String titulo, String mensagem, final CheckBox check){
        try {
            if (titulo.isEmpty()) {
               throw new Exception("PRECISA DE UM TITULO");
            } else if (mensagem.isEmpty()) {
                throw new Exception("PRECISA DE UMA CORPO");
            }else if (check == null){
                throw new Exception("PRECISA INFORMA O CHECKBOX");
            }else{
                alertDialog.setTitle(titulo);
                alertDialog.setMessage(mensagem);
                alertDialog.setPositiveButton("ACEITO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        check.setChecked(true);
                    }
                });
                alertDialog.setNegativeButton("NÃO ACEITO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        check.setChecked(false);
                    }
                });
                android.app.AlertDialog CaixaDialagoForum = alertDialog.create();
                CaixaDialagoForum.show();
                return true;
            }
        }catch (Exception ShowForCheckBoxErro){
            Log.e("showForCheckBox", ShowForCheckBoxErro.getMessage());
            return false;
        }
    }


}
