package br.com.devjr.gremminck.Fragmentos;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import br.com.devjr.gremminck.Helper.Connection;
import br.com.devjr.gremminck.Helper.Calendario;
import br.com.devjr.gremminck.Helper.ToastCustom;
import br.com.devjr.gremminck.Modificadores.Mensagem;
import br.com.devjr.gremminck.R;
import br.com.devjr.gremminck.Modificadores.Usuario;

public class Topico extends Fragment {


    //    BANCO DE DADOS
    private FirebaseAuth authe;
    private FirebaseUser user;
    private Usuario usuarioLog;

    //    COMPONENTES
    private View topidoADD;
    private EditText tituloCampo, mensagemCampo;
    private TextView nomeCampo, dataCampo;
    private Spinner materiaCampo;
    private Button enviarPost;
    private long data;

    //    ATRIBUTOS
    private String IDPost;
    private String[] postMaterias = new String[]{"Materia", "Português", "Matematica", "Historia", "Biologia", "Ciências", "Quimica", "Filosofia", "Ed. Fisica", "Fisica", "Sociologia", "Palestras", "Eventos", "Trabalhos", "Outros"};

    public Topico() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        topidoADD = inflater.inflate(R.layout.fragment_topico_add, null);
        ManipulaComponentes(topidoADD);
        AdapterSpinner();

        Calendario s = new Calendario();
        data = s.timeStamp();
        usuarioLog = Connection.getCurrentUser();
        nomeCampo.setText(usuarioLog.getNome());
        dataCampo.setText(Calendario.formatData("data", data));


        enviarPost = topidoADD.findViewById(R.id.TopicoAddButton);

        enviarPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loading(true);
                CreateQuery();
            }
        });

        return topidoADD;
    }

    @Override
    public void onStart() {
        super.onStart();
        authe = Connection.getFirebaseAuth();
        user = Connection.getFirebaseUser();

    }

    private void CreateQuery() {

        String PostMensagem = mensagemCampo.getText().toString();
        String PostTitulo = tituloCampo.getText().toString();

        if (TratarDados(PostTitulo, PostMensagem)) {
            final DatabaseReference pushRef = Connection.getForum().push();

            IDPost = pushRef.getKey();

            try {
                Mensagem NewPost = new Mensagem();
                NewPost.setID(IDPost);
                NewPost.setNome(usuarioLog.getNome());
                NewPost.setTitulo(PostTitulo.trim());
                NewPost.setMensagem(PostMensagem.trim());
                NewPost.setMateria(materiaCampo.getSelectedItem().toString());
                NewPost.setUID(user.getUid());
                NewPost.setData(data);
                NewPost.setLikes(0);

                pushRef.setValue(NewPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        tituloCampo.setText("");
                        mensagemCampo.setText("");
                        Loading(false);
                        ToastCustom.show(true, "Post Enviado =D");
                        Log.i("SUCESSO_ENVIO/POST", "POST ENVIADO POR " + user.getUid());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ToastCustom.show(false, "Não foi possivel enviar =(");
                        Log.e("ERRO_ENVIO/POST", e.getMessage());
                    }
                });
            } catch (Exception ERR_UPLOAD_POST) {
                ToastCustom.show(false, ERR_UPLOAD_POST.getMessage());
                Loading(false);
            }
        }
    }

    //    INICIALIZA COMPONENTES
    public void ManipulaComponentes(@Nullable View view) {
        tituloCampo = (EditText) view.findViewById(R.id.TopidoAddTitulo);
        materiaCampo = (Spinner) view.findViewById(R.id.TopidoAddMateria);
        mensagemCampo = (EditText) view.findViewById(R.id.TopidoAddMensagem);
        nomeCampo = view.findViewById(R.id.TopidoAddNome);
        dataCampo = view.findViewById(R.id.TopidoAddData);
    }

    public boolean TratarDados(String Ti, String Me) {

        try {
            if (Ti.isEmpty()) {
                tituloCampo.setError("ERRO");
                throw new Exception("SEU POST PRECISA DE UM TITULO");
            } else if (Ti.length() < 10) {
                tituloCampo.setError("ERRO");
                throw new Exception("SEU TITULO PRECISA SER MAIOR");
            } else {
                if (materiaCampo.getSelectedItemId() == 0) {
                    throw new Exception("SELECIONE A MATERIA");
                } else {
                    if (Me.isEmpty()) {
                        mensagemCampo.setError("ERRO");
                        throw new Exception("SEU POST PRECISA CONTER ALGO");
                    } else if (Me.length() < 85) {
                        mensagemCampo.setError("ERRO");
                        throw new Exception("SEU TEXTO PRECISA SER MAIOR");
                    }
                }
            }
        } catch (Exception ERRO_TRATADO) {
            ToastCustom.show(false, ERRO_TRATADO.getMessage());
            Loading(false);
            return false;
        }
        return true;
    }

    public void AdapterSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_sigle_write, postMaterias) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View spinnerview = super.getDropDownView(position, convertView, parent);

                TextView spinnertextview = (TextView) spinnerview;

                if (position == 0) {
                    //Set the disable spinner item color fade .
                    spinnertextview.setTextColor(Color.parseColor("#bcbcbb"));
                } else {
                    spinnertextview.setTextColor(Color.parseColor("#202020"));
                }
                return spinnerview;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materiaCampo.setAdapter(adapter);
    }


    public void Loading(Boolean Status) {

        ProgressBar ProgressBar = topidoADD.findViewById(R.id.TopicoAddProgress);

        if (Status) {
            enviarPost.setVisibility(View.GONE);
            ProgressBar.setVisibility(View.VISIBLE);
        } else {
            enviarPost.setVisibility(View.VISIBLE);
            ProgressBar.setVisibility(View.GONE);
        }
    }
}
