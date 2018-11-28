package br.com.devjr.gremminck.Helper;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.devjr.gremminck.R;

public class ToastCustom extends AppCompatActivity {


    private static Toast ToastCust;
    private static View Layout;
    private static Context Contexto;

    public ToastCustom(Context contento) {
        if (contento == null){
            throw new NullPointerException("ToastCustom have a Context");
        }else {
            Contexto = contento;
            MontaToast();
        }
    }


    private void MontaToast(){
        LayoutInflater inflater = LayoutInflater.from(Contexto);
        Layout = inflater.inflate(R.layout.toast_layout,null);
        ToastCust = new Toast(Contexto);
    }

    public static void show(boolean type, @Nullable String msg){
        int tempo = ToastCust.LENGTH_LONG;
        if (msg == null && type == false) {
            msg = "ESTAMOS TRABALHANDO NISSO!";
        }else{
            TextView toastText = Layout.findViewById(R.id.toast_text);
            ImageView toastImg = Layout.findViewById(R.id.toast_image);
            if (type){
                Layout.setBackgroundResource(R.drawable.toast_sucess_layout);
                toastImg.setImageResource(R.drawable.ic_toasticon_sucess);
                tempo = ToastCust.LENGTH_SHORT;
            }else{
                Layout.setBackgroundResource(R.drawable.toast_failed_layout);
                toastImg.setImageResource(R.drawable.ic_toasticon_failed);
                tempo = ToastCust.LENGTH_LONG;
            }
            toastText.setText(msg);
            ToastCust.setGravity(Gravity.BOTTOM, 0,80);
            ToastCust.setDuration(tempo);
            ToastCust.setView(Layout);
            ToastCust.show();
        }
    }
}
