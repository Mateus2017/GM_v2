package br.com.devjr.gremminck.Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.devjr.gremminck.Adapters.RespostasAdapter;
import br.com.devjr.gremminck.Modificadores.Mensagem;
import br.com.devjr.gremminck.R;
import br.com.devjr.gremminck.Modificadores.Usuario;

public class Dialogue extends AppCompatActivity {

    private static Context context;
    private static View viewRespostas;
    private static View viewAlert;

    public Dialogue(@Nullable Context context) {
        this.context = context;
    }

    /**
     * CAIXA DE DIALAGO DO FORUM
     * (PRIVATE) SupViewMensagem - Monta a estrutura e os dados.
     **/
    public static void SupViewMensagem(final View view, final Mensagem model) {

        final TextView titulo, mensagem, nome, data, totalResp;
        final EditText viewRsp;
        final List<Mensagem> repostasList = new ArrayList<>();
        final ListView listaView;

        LayoutInflater Fonte = LayoutInflater.from(view.getContext());
        viewRespostas = Fonte.inflate(R.layout.popup_mensagem, null);

//        BUSCANDO OS DADOS
        titulo = viewRespostas.findViewById(R.id.PopupMensagemTitulo);
        mensagem = viewRespostas.findViewById(R.id.PopupMensagemConteudo);
        nome = viewRespostas.findViewById(R.id.PopupMensagemNome);
        data = viewRespostas.findViewById(R.id.PopupMensagemData);
        viewRsp = viewRespostas.findViewById(R.id.PopupMensagemNovaResposta);
        totalResp = viewRespostas.findViewById(R.id.PopupMensagemTotalRespostas);
        listaView = viewRespostas.findViewById(R.id.PopupMensagemCorpo);

        Connection.getForumRespostas(model.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() <= 0) {
                    totalResp.setText("Nenhuma respostas ainda");
                    listaView.setVisibility(View.GONE);
                } else {
                    int tot = (int) dataSnapshot.getChildrenCount();
                    totalResp.setText("UM TOTAL DE " + tot);
                    for (DataSnapshot rsp : dataSnapshot.getChildren()) {
                        Mensagem resposta = new Mensagem();
                        resposta.setNome((String) rsp.child("nome").getValue());
                        resposta.setMensagem((String) rsp.child("mensagem").getValue());
                        resposta.setData((long) rsp.child("data").getValue());
                        repostasList.add(resposta);
                    }
                    RespostasAdapter rspAdapter = new RespostasAdapter(repostasList, viewRespostas);
                    listaView.setAdapter(rspAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ToastCustom.show(false, "Não foi possivel buscar as repostas");
            }
        });
        viewRespostas.findViewById(R.id.PopupMensagemButtonEnviar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String resposta = (String) viewRsp.getText().toString().trim().toUpperCase();
                    if (resposta.isEmpty() || resposta == null) {
                        throw new Exception("Campos vazios");
                    } else {

                        DatabaseReference RefForum = Connection.getBanco("Forum/Mensagens/" + model.getID() + "/respostas").push();

                        Usuario user = Connection.getCurrentUser();

                        Mensagem rsp = new Mensagem();

                        rsp.setNome(user.getNome());
                        rsp.setUID(user.getUID());
                        rsp.setData(Calendario.timeStamp());
                        rsp.setMensagem(resposta);

                        RefForum.setValue(rsp).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                viewRsp.setText("");
                                ToastCustom.show(true, "ENVIADO =D");
                                Log.i("SUCESSO_RSP", "MENSAGEM ENVIADA");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ToastCustom.show(false, "ALGO DEU ERRADO =(");
                                Log.e("ERRO_RSP", "ERRO AO ENVIAR MENSAGEM");
                            }
                        });
                    }
                } catch (Exception ERR_RSP) {
                    ToastCustom.show(false, "Erro :" + ERR_RSP.getMessage());
                    Log.e("ERRO_RSP", "ALGO DE ERRADO : " + ERR_RSP.getMessage());
                }
            }
        });


//        POPULANDO A CAIXA DE DIALAGO
        titulo.setText(model.getTitulo());
        mensagem.setText(model.getMensagem());
        nome.setText(model.getNome());
        String fulldata[] = Calendario.formatData("completo", model.getData()).split("/");
        String dataFormat[] = fulldata[0].split("-");
        String horaFormat[] = fulldata[1].split(":");
        String printDate = "Enviado as "+horaFormat[0]+":"+horaFormat[1]+" em "+dataFormat[0]+"-"+dataFormat[1]+"-"+dataFormat[2];
        data.setText(printDate);

//        CRIANDO A CAIXA DE DIALAGO
        AlertDialog.Builder builder = new AlertDialog.Builder(Fonte.getContext());
        builder.setView(viewRespostas);
        final AlertDialog CaixaDialagoForum = builder.create();
        CaixaDialagoForum.show();

//        OUVINDO O BOTÃO DE FECHAR
        viewRespostas.findViewById(R.id.PopupMensagemButtonFechar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaixaDialagoForum.dismiss();
            }
        });
    }


    /**
     * CAIXA DE DIALAGO DE ALERTAS
     * (PRIVATE) MontarCaixaAlert - Monta a estrutura, e tambem o preenche
     * (PUBLIC) ShowSupAlert - Chama o metodo de montar e passa os parametros
     **/
    public static void MontarCaixaAlert(String type, String titulo, String mensagem) {

        /*
            CABECHALOS
                sucesso FF1CA700
                aviso d5dd00
                erro FFA70000
            CORPO
                sucesso e6178600
                aviso e6b8be00
                erro e64c0000
         */
        try {
            LayoutInflater Fonte = LayoutInflater.from(context);
            viewAlert = Fonte.inflate(R.layout.popup_alert, null);

            if (titulo.isEmpty() || titulo == null && mensagem.isEmpty() || mensagem == null) {
                throw new Exception("VALORES NÃO PASSADO (titulo, Mensagem)");
            } else if (type == null || type.isEmpty()) {
                throw new Exception("PARAMETRO TYPE PRECISA SER PASSADO");
            } else {

                LinearLayout Header = viewAlert.findViewById(R.id.PopupAlertLinearHead);
                LinearLayout Body = viewAlert.findViewById(R.id.PopupAlertLinearBody);

                switch (type) {
                    case "sucesso":
                        Header.setBackgroundColor(Color.parseColor("#FF1CA700"));
                        Body.setBackgroundColor(Color.parseColor("#e6178600"));
                        break;
                    case "aviso":
                        Header.setBackgroundColor(Color.parseColor("#d5dd00"));
                        Body.setBackgroundColor(Color.parseColor("#e6b8be00"));
                        break;
                    case "erro":
                        Header.setBackgroundColor(Color.parseColor("#FFA70000"));
                        Body.setBackgroundColor(Color.parseColor("#e64c0000"));
                        break;
                    default:
                        throw new Exception("PARAMETRO TYPE -> (" + type + ") INVALIDO, TENTE (sucesso, aviso, erro)");
                }

                TextView CampoTitulo, CampoMensagem;
                CampoTitulo = viewAlert.findViewById(R.id.PopupAlertTitulo);
                CampoMensagem = viewAlert.findViewById(R.id.PopupAlertMensagem);

                CampoTitulo.setText(titulo);
                CampoMensagem.setText(mensagem);

                AlertDialog.Builder builder = new AlertDialog.Builder(Fonte.getContext());
                builder.setView(viewAlert);
                final AlertDialog CaixaDialagoAlert = builder.create();
                CaixaDialagoAlert.show();

                viewAlert.findViewById(R.id.PopupAlertButtonFechar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CaixaDialagoAlert.dismiss();
                    }
                });

            }
            Log.i("SUCESSO_INFLAR/ALERT", "ALERT INFLADO");

        } catch (Exception ERRO_INFLAR_DIALOG_ALERT) {
            Log.e("ERRO_INFLAR/ALERT", ERRO_INFLAR_DIALOG_ALERT.getMessage());
            ToastCustom.show(false, "ERRO AO INFLAR");
        }
    }


}
