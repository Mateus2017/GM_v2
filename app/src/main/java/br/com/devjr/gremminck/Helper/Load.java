package br.com.devjr.gremminck.Helper;

import android.util.Log;
import android.view.View;


/** @Author Mateus Nicolau [CEO/DEV]
 @Company Dev.jr
 @Email devjuniorocompany@gmail.com
 @Date  28/10/18 **/

public class Load {


    private static View progressBar;
    private static View outputDisplay;

    private static int style = 1;
    private static int SP;
    private static int SO;

    public Load(View viewGone, View viewVisible) {
        try {
            if (viewGone == null && viewVisible == null){
                throw new Exception(" ==> (progressbar) OBRIGATORIO, (viewVisible) OPCIONAL EM "+ viewGone.getContext());
            }else{
                if(viewGone != null){
                    if (viewVisible != null){
                        style = 2;
                        Load.progressBar = viewGone;
                        Load.outputDisplay = viewVisible;
                    }else {
                        style = 1;
                        Load.progressBar = viewGone;
                        Log.i("LOAD", "==> (viewVisible) Não passado em "+viewGone.getContext()+", campo não obrigatorio");
                    }
                }
            }
        }catch (Exception exc){
            Log.e("Exception", exc.getMessage());
        }
    }

    public static void start(){
        Log.i("LOAD", "INICIALIZANDO O CARREGAMENTO EM "+ progressBar.getContext());

        SP = progressBar.getVisibility();
        SO = outputDisplay.getVisibility();

        switch (style){
            case 1:
                if (SP != View.VISIBLE){
                    progressBar.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if (SP != View.VISIBLE && SO != View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                    outputDisplay.setVisibility(View.GONE);
                }
                break;
        }
    }

    public static void stop(){
        Log.i("LOAD", "FINALIZAÇÃO DO CARREGAMENTO EM "+ progressBar.getContext());
        SP = progressBar.getVisibility();
        SO = outputDisplay.getVisibility();

        switch (style){
            case 1:
                if (SP != View.GONE){
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case 2:
                if (SP != View.GONE && SO != View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                    outputDisplay.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
