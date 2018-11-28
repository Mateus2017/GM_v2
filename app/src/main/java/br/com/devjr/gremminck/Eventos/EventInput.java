package br.com.devjr.gremminck.Eventos;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;

import br.com.devjr.gremminck.R;


public class EventInput {

    private static EditText inputView;

    private static int rsc = R.drawable.layout_button_retangulo_error;
    private static String colorTextErro = "#ffffff";
    private static String colorHintErro = "#dbdbdb";

    private static Drawable backgroundInput;
    private static int colorTextCurrent;
    private static int colorHintCurrent;

    private static int ColorTextSet;
    private static int ColorHintSet;


    public static void setError(@NonNull EditText input){
        try {
            if (input == null){
                throw new Exception("setError require a input type View");
            }else{
                inputView = input;

                backgroundInput = input.getBackground();
                colorTextCurrent = input.getCurrentTextColor();

                input.setBackgroundResource(rsc);
            }
        }catch (Exception erro_seterro){
            Log.e("EventInput", erro_seterro.getMessage());
        }
    }

    public static void callBack(){
        try {
            if (inputView == null){
                throw new Exception("inputView is onject null");
            }else{
                inputView.setBackground(backgroundInput);
                inputView.setHintTextColor(colorHintCurrent);
                inputView.setTextColor(colorTextCurrent);
            }
        }catch (Exception erro_callback){
            Log.e("EventInput", erro_callback.getMessage());
        }
    }
}
