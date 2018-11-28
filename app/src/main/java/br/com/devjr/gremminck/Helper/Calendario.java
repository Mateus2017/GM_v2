package br.com.devjr.gremminck.Helper;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.devjr.gremminck.Activitys.RegisterActivity;


public class Calendario {

    private static long timestamp;
    private static SimpleDateFormat dataCompleta, horaFormatada, dataFormatada;
    private static Calendar myCalendar = Calendar.getInstance();

    public Calendario() {

        dataCompleta = new SimpleDateFormat("dd-MM-yy/HH:mm:ss");
        horaFormatada = new SimpleDateFormat("HH:mm:ss");
        dataFormatada = new SimpleDateFormat("dd-MM-yy");
        timestamp = System.currentTimeMillis();

    }

    public static String formatData(@NonNull String Calendario, @NonNull long timestamp) {
        Calendario.toLowerCase().toLowerCase();
        try {
            if (Calendario != null || Calendario.isEmpty()) {
                if (Calendario.equals("hora") || Calendario.equals("data") || Calendario.equals("completo")) {

                    switch (Calendario) {
                        case "completo":
                            return dataCompleta.format(timestamp).toString();
                        case "hora":
                            return horaFormatada.format(timestamp).toString();
                        case "data":
                            return dataFormatada.format(timestamp).toString();
                    }

                } else {
                    throw new Exception("PARAMETRO ( " + Calendario + " ) INVALIDO");
                }
            }else{
                throw new Exception("PARAMETRO VAZIO, PRECISA SER PASSADO");
            }
        } catch (Exception ERR_DATE_TIME_CALENDAR) {
            Log.e("ERRO/CALENDAR", " => " + ERR_DATE_TIME_CALENDAR.getMessage()+" \n LINE :"+ERR_DATE_TIME_CALENDAR.getStackTrace());
            return  null;
        }
        return null;
    }

    public static long timeStamp(){
        return timestamp;
    }

    public static void setInputCalendar(final EditText inputDate){


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

                inputDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        inputDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


}
