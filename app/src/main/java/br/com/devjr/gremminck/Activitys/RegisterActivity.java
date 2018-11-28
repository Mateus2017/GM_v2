package br.com.devjr.gremminck.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import br.com.devjr.gremminck.Eventos.EventInput;
import br.com.devjr.gremminck.Helper.Calendario;
import br.com.devjr.gremminck.Helper.Connection;
import br.com.devjr.gremminck.Helper.Load;
import br.com.devjr.gremminck.Helper.MultiStyleAlertDialog;
import br.com.devjr.gremminck.Helper.ToastCustom;
import br.com.devjr.gremminck.Modificadores.Usuario;
import br.com.devjr.gremminck.R;

public class RegisterActivity extends AppCompatActivity {

    private static ToastCustom ToastCustom;
    private static MultiStyleAlertDialog MultiStyleAlertDialog;
    private static Load Load;
    private FirebaseAuth authent = Connection.getFirebaseAuth();
    private Usuario NewUsuario;

    private String name, email, senha, confirmaSenha, serie, ensino, nascimento;
    private String acesso = "Aluno";

    private Button btRegister;
    private CheckBox chTermo1, chTermo2;
    private View ContainerButton, ContainerProgress;
    private ProgressBar loading;
    private TextView btVoltar;
    private EditText cpName, cpEmail, cpSenha, cpConfirSenha, cpSerie, cpEnsino, cpNascimento;
    private Spinner cpGenero;

    private String generos[] = new String[]{"Escolha Genero", "Masculino", "Feminino"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        ToastCustom = new ToastCustom(getApplicationContext());
        MultiStyleAlertDialog = new MultiStyleAlertDialog(RegisterActivity.this);
        setViews();
        AdapterSpinner();
        NewUsuario = new Usuario();
        Calendario.setInputCalendar(cpNascimento);
        Load = new Load(ContainerProgress, ContainerButton);

        cpEnsino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cpSerie.requestFocus();
            }
        });
        chTermo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chTermo1.isChecked()) {
                    politica();
                }
            }
        });
        chTermo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chTermo2.isChecked()) {
                    usoPrivacidade();
                }
            }
        });
        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        SimpleMaskFormatter mask = new SimpleMaskFormatter("N/N");
        MaskTextWatcher inputPlusMask = new MaskTextWatcher(cpSerie, mask);
        cpSerie.addTextChangedListener(inputPlusMask);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Load.start();
                registerUser();
            }
        });
        cpSerie.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                serie = cpSerie.getText().toString();
                String sEc[] = serie.split("/");
                String seriee = sEc[0];
                if (!hasFocus) {
                    try {
                        if (serie.length() >= 3 || serie.length() <= 0) {
                            if (seriee.equals("1") || seriee.equals("2") || seriee.equals("3")) {
                                ensino = "Ensino Medio";
                            } else if (seriee.equals("6") || seriee.equals("7") || seriee.equals("8") || seriee.equals("9")) {
                                ensino = "Ensino Fundamental";
                            } else {
                                ToastCustom.show(false, "Você precisar estar pelo menos no 6 ano =( ");
                                finish();
                            }
                            NewUsuario.setEnsino(ensino);
                            cpEnsino.setText(ensino);
                        } else {
                            cpSerie.setError("ERRO");
                            throw new Exception("Preencha sua serie corretamente EX : 2/1");
                        }
                    } catch (Exception erro_serie) {
                        if (!cpEnsino.getText().toString().isEmpty())
                            cpEnsino.setText("");
                        ToastCustom.show(false, erro_serie.getMessage());
                        Log.e("REGISTERACTIVITY", "ALGO DEU ERRADO NA SERIE " + erro_serie.getMessage());
                    }

                }
            }
        });
    }

    private void registerUser() {
        if (treatDado()) {

            Load.start();
            authent.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String UID = authent.getUid();
                        final DatabaseReference db = Connection.getUser();
                        DatabaseReference dbUsr = db.push();
                        FirebaseUser usr = Connection.getFirebaseUser();
                        usr.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ToastCustom.show(true, "Verifique seu e-mail");
                                    Log.i("CADASTRO", "E-mail de verificação enviado");
                                } else {
                                    task.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            ToastCustom.show(true, "Não foi possivel enviar um e-mail");
                                            Log.e("CADASTRO", "E-mail não enviado, motivo :" + e.getMessage());
                                        }
                                    });
                                }
                            }
                        });
                        NewUsuario.setUID(UID);
                        db.child(UID).setValue(NewUsuario);
                        ToastCustom.show(true, "SEU NOVO PERFIL FOI PARA APROVAÇÃO, EM PREVE TERÁ RESPOSTA");
                        Log.i("CADASTRO", "CADASTRO FEITO COM SUCESSO");
                        Load.stop();
                        finish();

                    } else {
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String erro = ((FirebaseAuthException) task.getException()).getErrorCode();
                                try {
                                    switch (erro) {
                                        case "ERROR_INVALID_EMAIL":
                                            // EMAIL INVALIDO
                                            EventInput.setError(cpEmail);
                                            throw new Exception("EMAIL INVALIDO");
                                        case "ERROR_EMAIL_ALREADY_IN_USE":
                                            // EMAIL CADASTRADO
                                            EventInput.setError(cpEmail);
                                            throw new Exception("EMAIL CADASTRADO");

                                        case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                            // Esta credencial já está associada a uma conta de usuário diferente.
                                            throw new Exception("Esta credencial já está associada a uma conta de usuário diferente.");
                                        case "EMAIL-ALREADY-EXISTS":
                                            EventInput.setError(cpEmail);
                                            throw new Exception(" E-mail fornecido já está em uso por outro usuário");

                                    }
                                } catch (Exception erro_login) {
                                    ToastCustom.show(false, erro_login.getMessage());
                                    Log.e("CADASTRO - " + erro, erro_login.getMessage());
                                    Load.stop();
                                }
                            }
                        });
                    }
                }
            });
        } else {
            Load.stop();
        }
    }

    private boolean treatDado() {
        EventInput.callBack();
        try {
            name = cpName.getText().toString();
            email = cpEmail.getText().toString();
            senha = cpSenha.getText().toString();
            serie = cpSerie.getText().toString();
            nascimento = cpNascimento.getText().toString();
            confirmaSenha = cpConfirSenha.getText().toString();

            if (!name.isEmpty()) {
                if (name.length() >= 20) {
                    if (!email.isEmpty()) {
                        if (!senha.isEmpty()) {
                            if (!cpConfirSenha.getText().toString().isEmpty()) {
                                if (senha.length() > 8) {
                                    if (confirmaSenha.length() > 8) {
                                        if (senha.equals(confirmaSenha)) {
                                            if (serie.length() <= 0 || serie.length() >= 3) {
                                                if (cpGenero.getSelectedItemId() != 0) {
                                                    if (!nascimento.isEmpty()) {
                                                        if (chTermo1.isChecked() && chTermo2.isChecked()) {
                                                            NewUsuario.setNome(name.toLowerCase());
                                                            NewUsuario.setEmail(email);
                                                            NewUsuario.setAcesso(acesso);
                                                            NewUsuario.setCurso(serie);
                                                            NewUsuario.setGenero(cpGenero.getSelectedItem().toString());
                                                            NewUsuario.setNascimento(nascimento);
                                                            // FALTA ENSINO
                                                            Log.i("CADASTRO", "DADOS LIMPOS E PRONTO PARA CADASTRO");
                                                            return true;
                                                        } else {
                                                            throw new Exception("Você precisa aceitar os termos");
                                                        }
                                                    } else {
                                                        throw new Exception("Preencha o campo nascimento");
                                                    }

                                                } else {
                                                    throw new Exception("Escolha seu genero");
                                                }
                                            } else {
                                                cpSerie.setError("ERRO");
                                                cpSerie.requestFocus();
                                                throw new Exception("Preencha sua serie corretamente EX : 2/1");
                                            }
                                        } else {
                                            EventInput.setError(cpConfirSenha);
                                            EventInput.setError(cpSenha);
                                            cpSenha.requestFocus();
                                            throw new Exception("Senhas não batem");
                                        }
                                    } else {
                                        EventInput.setError(cpConfirSenha);
                                        cpConfirSenha.requestFocus();
                                        throw new Exception("Preencha com os mesmos caracteres da sua senha");
                                    }
                                } else {
                                    EventInput.setError(cpSenha);
                                    cpSenha.requestFocus();
                                    throw new Exception("Senha precisa ser maior que " + senha.length());
                                }
                            } else {
                                EventInput.setError(cpConfirSenha);
                                cpConfirSenha.requestFocus();
                                throw new Exception("Reescreva sua senha");
                            }
                        } else {
                            EventInput.setError(cpSenha);
                            cpSenha.requestFocus();
                            throw new Exception("Crie uma senha de acesso");
                        }

                    } else {
                        EventInput.setError(cpEmail);
                        cpEmail.requestFocus();
                        throw new Exception("Preencha com seu email");
                    }
                } else {
                    EventInput.setError(cpName);
                    cpName.requestFocus();
                    throw new Exception("Nome precisa ser completo");
                }
            } else {
                EventInput.setError(cpName);
                cpName.requestFocus();
                throw new Exception("Preencha com seu nome");
            }
        } catch (NullPointerException NullPointer) {
            Log.e("CADASTRO", NullPointer.getMessage());
            return false;
        } catch (Exception erroReg) {
            ToastCustom.show(false, erroReg.getMessage());
            Log.e("CADASTRO", erroReg.getMessage());
            Load.stop();
            return false;
        }
    }

    public void setViews() {
        cpName = findViewById(R.id.CadastroActivityNome);
        cpEmail = findViewById(R.id.CadastroActivityEmail);
        cpSenha = findViewById(R.id.CadastroActivitySenha);
        cpConfirSenha = findViewById(R.id.CadastroActivitySenhaConfirme);
        cpSerie = findViewById(R.id.CadastroActivitySerie);
        cpEnsino = findViewById(R.id.CadastroActivityEnsino);
        cpNascimento = findViewById(R.id.CadastroActivityNascimento);
        cpGenero = findViewById(R.id.CadastroActivityFlagGenero);
        loading = findViewById(R.id.CadastroActivityProgressBar);

        chTermo1 = findViewById(R.id.CadastroActivityTermo1);
        chTermo2 = findViewById(R.id.CadastroActivityTermo2);

        ContainerButton = findViewById(R.id.ContainerButton);
        ContainerProgress = findViewById(R.id.ContainerProgress);

        btRegister = findViewById(R.id.CadastroActivityButtonRegister);
        btVoltar = findViewById(R.id.CadastroActivityVoltar);
    }

    public void politica() {
        String titulo = chTermo1.getText().toString().toUpperCase();
        String mensagem = " Esse aplicativo contem restrições de idade, com certificado IARC, previsto pela google. tendo isso, com a idade minima para uso desse aplicativo é classificada para maiores de 12 anos.\n" +
                "\n As politicas de uso são necessárias para que se mantenha um padrão de uso, e respeito a comunidade, assim ficando informado sobre oque é permitido e oque não é, dentro do software Gremminck, para que possa prosseguir com o seu cadastro é obrigatório que leia os termos de uso, a seguir : \n" +
                "\n É de extrema importância que o respeito entre os usuários seja mantido, e o tema desse aplicativo, caso contrario, será aplicado uma punição ao membro que desrespeitou esse termo, sua conta será excluída e terá seu IPV6 bloqueado. tendo assim seu acesso, bloqueado.\n" +
                "\n Suas publicações são publicas então quando se adiciona uma mensagem no fórum, todos os membros poderão visualizar e interagir com sua mensagem, é obrigatório que sua mensagem seja focada a temas escolares e a fins de duvidas ou publicações que possam ajudar no Ensino, caso fuja do tema sua mensagem será deleta, e sua conta ficara suspensa por alguns meses.\n" +
                "\n É previsto no artigo 163 do Código penal, que dado as coisas \"alheias\" prevê pena máxima de 3 anos em caso de destruição, deterioração o inutilização contra patrimônio públicos ou privado, as penas seriam cumulativas, em caso de violação de segurança ou burlar o sistema Gremminck, o individuo será encaminhado ao órgão responsável e terá todas as medidas cabíveis.\n\n";
        if (!MultiStyleAlertDialog.showForCheckBox(titulo, mensagem, chTermo1)) {
            ToastCustom.show(false, "algo deu errado");
        }
    }

    public void usoPrivacidade() {
        String titulo = chTermo2.getText().toString().toUpperCase();
        String mensagem = "Todas as suas informações pessoais recolhidas, serão usadas para tornar a sua visita no nosso aplicativo o mais produtiva e agradável possível.\n" +
                "\n A garantia da confidencialidade dos dados pessoais dos utilizadores do nosso aplicativo é importante para o Gremminck.\n" +
                "\n Todas as informações pessoais relativas a membros, usuarios ou visitantes que usem o Gremminck serão tratadas em concordância com a Lei da Proteção de Dados Pessoais de 26 de outubro de 1998 (Lei n.º 67/98).\n" +
                "\n A informação pessoal recolhida pode incluir o seu nome, e-mail, serie, ensino, data de nascimento e/ou outros.\n" +
                "\n O uso do Gremminck pressupõe a aceitação deste Acordo de privacidade. E a equipe do Gremminck reserva-se ao direito de alterar este acordo sem aviso prévio. Deste modo, recomendamos que consulte a nossa política de privacidade com regularidade de forma a estar sempre atualizado. \n" +

                "\n Os Cookies e Web Beacons\n" +
                "\n Utilizamos cookies para armazenar informações, tais como as suas preferências pessoas quando visita o nosso website. Isto poderá incluir um simples popup, ou uma ligação em vários serviços que providenciamos, tais como fóruns.\n" +
                "\n Em adição também utilizamos publicidade de terceiros no nosso aplicativo para suportar os custos de manutenção. Alguns destes publicitários, poderão utilizar tecnologias como os cookies e/ou web beacons quando publicam  no nosso aplicativo, o que fará com que esses publicitários (como o Google através do Google AdSense) também recebam a sua informação pessoal, como o endereço IP, o seu ISP, o seu browser, etc. Esta função é geralmente utilizada para geotargeting (mostrar publicidade de Lisboa apenas aos leitores oriundos de Lisboa por ex.) ou apresentar publicidade direcionada a um tipo de utilizador (como mostrar publicidade de restaurante a um utilizador que visita sites de culinária regularmente, por ex.).\n" +
                "\n Você detém o poder de desligar os seus cookies, nas opções do seu browser, ou efetuando alterações nas ferramentas de programas Anti-Virus, como o Norton Internet Security. No entanto, isso poderá alterar a forma como interagem com o nosso aplicativo, ou outros aplicativo. Isso poderá afetar ou não permitir que faça logins em programas, sites ou fóruns da nossa e de outras redes. \n" +

                "\n Ligações a Sites de terceiros\n" +
                "\n O Gremminck possui ligações para outros sites, os quais, a nosso ver, podem conter informações / ferramentas úteis para os nossos visitantes. A nossa política de privacidade não é aplicada a sites de terceiros, pelo que, caso visite outro site a partir do nosso deverá ler a politica de privacidade do mesmo.\n" +
                "\n Não nos responsabilizamos pela política de privacidade ou conteúdo presente nesses mesmos sites.";
        if (!MultiStyleAlertDialog.showForCheckBox(titulo, mensagem, chTermo2)) {
            ToastCustom.show(false, "algo deu errado");
        }
    }

    public void AdapterSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(cpGenero.getContext(), R.layout.spinner_item_sigle_black, generos) {
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
        cpGenero.setAdapter(adapter);
    }
}
