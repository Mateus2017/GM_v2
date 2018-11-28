package br.com.devjr.gremminck.Activitys;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import br.com.devjr.gremminck.Helper.Connection;
import br.com.devjr.gremminck.Helper.ToastCustom;
import br.com.devjr.gremminck.R;

public class OpcoesActivity extends AppCompatActivity {

    private FirebaseAuth auth = Connection.getFirebaseAuth();

    private View view;
    private EditText cpEmail;
    private ImageView imgVoltar, imgEnviarEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcoes);
        setElementos();
        ouvirElementos();

    }

    private void ouvirElementos() {
        imgEnviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = cpEmail.getText().toString();
                if (!email.isEmpty()) {
                    if (Connection.isOnline(OpcoesActivity.this)) {
                        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull final Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ToastCustom.show(true, "Foi enviado um e-mail");
                                    Log.i("RESET", "Foi enviado um e-mail para " + email);
                                } else {
                                    task.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            String erro = ((FirebaseAuthException) task.getException()).getErrorCode();
                                            try {
                                                switch (erro) {
                                                    case "ERROR_INVALID_EMAIL":
                                                        // O endereço de e-mail está mal formatado.
                                                        throw new Exception("O endereço de e-mail está mal formatado.");
                                                    case "INVALID_EMAIL_VERIFIED":
                                                        throw new Exception("Verificação de E-mail pendente");
                                                    case "ERROR_USER_NOT_FOUND":
                                                        throw new Exception("Usuario não encontrado");
                                                    default:
                                                        throw new Exception(erro);
                                                }
                                            } catch (Exception err) {
                                                ToastCustom.show(false, err.getMessage());
                                                Log.e("RESET", "erro ao enviar email para " + email + " motivo " + err.getMessage());
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        ToastCustom.show(false, "Você precisa de internet");
                        Log.e("RESET", "Você precisa de internet");
                    }
                } else {
                    ToastCustom.show(false, "Preencha o campo");
                    Log.e("RESET", "Preencha o campo");
                }
            }
        });

        imgVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void setElementos() {
        view = findViewById(R.id.ResetSenhaView);

        cpEmail = view.findViewById(R.id.ResetSenhaInputEmail);

        imgVoltar = view.findViewById(R.id.ResetSenhaVotlar);
        imgEnviarEmail = view.findViewById(R.id.ResetSenhaButtonEnviarEmail);
    }
}
