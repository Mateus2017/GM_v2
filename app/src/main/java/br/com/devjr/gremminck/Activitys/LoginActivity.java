package br.com.devjr.gremminck.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import br.com.devjr.gremminck.Eventos.EventInput;
import br.com.devjr.gremminck.Helper.Connection;
import br.com.devjr.gremminck.Helper.Dialogue;
import br.com.devjr.gremminck.Helper.Load;
import br.com.devjr.gremminck.Helper.ToastCustom;
import br.com.devjr.gremminck.R;

public class LoginActivity extends AppCompatActivity {

    //    COMPONENTES
    private EditText campoEmail, campoSenha;
    private TextView novaConta;
    private Button button;
    private View containerProgress, containerButton, containerDuvida;
    private CheckBox LembreMe;

    private String EmailAluno;
    private String SenhaAluno;

    //    BANCO DE DADOS
    private FirebaseAuth auth;

    //   SHAREDPREFERENCE
    private SharedPreferences Cache;
    private static final String ALUNO_CACHE = "CacheDoAluno";

    //    TOAS CUSTOM
    private ToastCustom ToastCustom;
    private Dialogue SupTela;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ToastCustom = new ToastCustom(getApplicationContext());
        SupTela = new Dialogue(LoginActivity.this);


        BuscarComponentes();
        Load Load = new Load(containerProgress, containerButton);

        Cache = getSharedPreferences(ALUNO_CACHE, MODE_PRIVATE);

        if (Cache.contains("AlunoEmail") && Cache.contains("AlunoSenha")) {
//            CHAMANDO O LOADING
            Load.start();

//            COLOCANDO DADOS NAS VARIAVEIS
            EmailAluno = Cache.getString("AlunoEmail", "");
            SenhaAluno = Cache.getString("AlunoSenha", "");

//            COLOCANDO ELEMENTOS NOS CAMPOS
            campoEmail.setText(EmailAluno);
            campoSenha.setText(SenhaAluno);
            LembreMe.setChecked(true);

//            EFETUANDO LOGIN
            EfuarLogin(EmailAluno, SenhaAluno, LembreMe.isChecked());
        }

        OuvirEventos();
    }


    //  OUVINDO EVENTOS DE CLICKS
    private void OuvirEventos() {
        //      OUVINDO O BOTÃO DE ENTRAR
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Load.start();
                EmailAluno = campoEmail.getText().toString().trim();
                SenhaAluno = campoSenha.getText().toString().trim();

                if (ValidandoDados(EmailAluno, SenhaAluno)) {
                    EfuarLogin(EmailAluno, SenhaAluno, LembreMe.isChecked());
                } else {
                    Load.stop();
                }
            }
        });
//        OUVINDO O USUARIO PEDIR PARA REDEFINIR SENHA
        containerDuvida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, OpcoesActivity.class);
                startActivity(intent);
            }
        });

        novaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    //    BUSCANDO DADOS DOS COMPONENTES
    private void BuscarComponentes() {
        novaConta = findViewById(R.id.LoginActivityNovaConta);
        button = findViewById(R.id.LoginActivityButton);
        campoEmail = (EditText) findViewById(R.id.LoginActivityEmail);
        campoSenha = (EditText) findViewById(R.id.LoginActivitySenha);
        LembreMe = findViewById(R.id.LoginActivityCheckBox);

        containerButton = findViewById(R.id.ContainerLoginButton);
        containerProgress = findViewById(R.id.ContainerLoginProgress);
        containerDuvida = findViewById(R.id.LoginActivityDuvidas);
    }

    private void EfuarLogin(final String EmailUser, final String SenhaUser, final boolean salvar) {
        if (Connection.isOnline(getApplicationContext())) {
            auth = Connection.getFirebaseAuth();
            auth.signInWithEmailAndPassword(EmailUser, SenhaUser).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(final Task<AuthResult> task) {
                    try {
                        if (task.isSuccessful()) {
                            FirebaseUser usr = Connection.getFirebaseUser();
                            if (salvar) {
                                SharedPreferences.Editor Preencha = Cache.edit();
                                Preencha.putBoolean("Lembrar", true);
                                Preencha.putString("AlunoEmail", EmailUser.toString());
                                Preencha.putString("AlunoSenha", SenhaUser.toString());
                                Preencha.commit();
                            } else {
                                Cache.edit().clear().apply();
                            }
                            if (usr != null) {
                                if (usr.isEmailVerified()) {
                                    Log.w("LOGIN/SUCESSO", task.getException());
                                    finish();
                                    Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                                    ActivityOptionsCompat animacaoActiviti = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_out, R.anim.move_left);
                                    ActivityCompat.startActivity(LoginActivity.this, intent, animacaoActiviti.toBundle());
                                    Load.stop();
                                } else {
                                    throw new Exception("Seu email precisa ser verificado");
                                }
                            }
                        } else {
                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String erro = ((FirebaseAuthException) task.getException()).getErrorCode();
                                    try {
                                        switch (erro) {
                                            case "ERROR_INVALID_CREDENTIAL":
                                                //A credencial de autorização fornecida está incorreta ou expirou
                                                throw new Exception("A credencial de autorização fornecida está incorreta ou expirou");
                                            case "ERROR_INVALID_EMAIL":
                                                // O endereço de e-mail está mal formatado.
                                                EventInput.setError(campoEmail);
                                                campoEmail.requestFocus();
                                                throw new Exception("O endereço de e-mail está mal formatado.");
                                            case "ERROR_WRONG_PASSWORD":
                                                //A senha é inválida
                                                EventInput.setError(campoSenha);
                                                campoSenha.requestFocus();
                                                throw new Exception("A senha é inválida");
                                            case "ERROR_USER_DISABLED":
                                                // A conta do usuário esta desativada
                                                throw new Exception("A conta do usuário esta desativada");
                                            case "ERROR_USER_NOT_FOUND":
                                                //Não há registro de usuário correspondente a esse identificador. O usuário pode ter sido excluído.
                                                throw new Exception("Não há registro de usuário correspondente a esse identificador.");
                                            case "ERROR_WEAK_PASSWORD":
                                                // A senha dada é inválida.
                                                EventInput.setError(campoSenha);
                                                campoSenha.requestFocus();
                                                throw new Exception("A senha dada é inválida.");
                                            case "INVALID-EMAIL-VERIFIED":
                                                EventInput.setError(campoEmail);
                                                campoEmail.requestFocus();
                                                throw new Exception("Verificação de E-mail pendente");
                                        }
                                    } catch (Exception erro_log) {
                                        ToastCustom.show(false, erro_log.getMessage());
                                        Log.e("LOGIN - " + erro, erro_log.getMessage());
                                        Load.stop();
                                    }
                                }
                            });
                        }
                    } catch (Exception erro_login) {
                        ToastCustom.show(false, erro_login.getMessage());
                        Log.e("LOGIN", erro_login.getMessage());
                        Load.stop();
                    }
                }
            });
        } else {
            ToastCustom.show(false, "Você precisa de internet");
            Log.e("LOGIN", "Dispositivo sem internet");
            Load.stop();
        }
    }

    //    FUNÇÃO DE BANCO DE DADOS
    @Override
    public void onStart() {
        super.onStart();

        auth = Connection.getFirebaseAuth();

    }

    //    VERIFICANDO DADOS
    private boolean ValidandoDados(String email, String senha) {
        EventInput.callBack();
        try {
            if (!senha.isEmpty() && email.isEmpty()) {
                return true;
            } else {
                if (email.isEmpty()) {
                    EventInput.setError(campoEmail);
                    throw new Exception("Preencha o campo E-mail");
                } else if (senha.isEmpty()) {
                    EventInput.setError(campoSenha);
                    throw new Exception("Preencha o campo senha");
                }
            }
        } catch (Exception ERR_LOGIN) {
            ToastCustom.show(false, ERR_LOGIN.getMessage());
            Log.e("LOGIN", ERR_LOGIN.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.move_right, R.anim.fade_in);
        super.finish();
    }
}

