package br.com.devjr.gremminck.Eventos;

import android.util.Log;
import android.widget.Button;

public class OnClickBackground {

    private Button button;

    public static void toChangeBackground(Button btn){
        try {
            if (btn != null){

            }else{
                throw new NullPointerException("Paramentro do Button não passada");
            }
        }catch (NullPointerException erro_btn){
            Log.e("OnClickBackground", erro_btn.getMessage());
        }
    }
}
