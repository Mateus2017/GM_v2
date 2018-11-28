package br.com.devjr.gremminck.Activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.devjr.gremminck.Fragmentos.Forum;
import br.com.devjr.gremminck.Fragmentos.Perfil;
import br.com.devjr.gremminck.Fragmentos.Topico;
import br.com.devjr.gremminck.Helper.Connection;
import br.com.devjr.gremminck.Helper.ToastCustom;
import br.com.devjr.gremminck.R;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private Fragment currentFragment = null;

    private SharedPreferences cache;
    private static final String alunoCache = "CacheDoAluno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

            loadFragment(new Perfil());
            loadFragment(new Perfil());
            loadFragment(new Forum());

//        CHAMA FUNÇÃO DE OUVIR OS BUTTOM
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.menu_main);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    //     VERIFICA QUAL ACTIVITY DEVERA SER CHAMADA
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_perfil:
                    fragment = new Perfil();
                    break;
                case R.id.navigation_forum:
                    fragment = new Forum();
                    break;
                case R.id.navigation_adicionar_topico:
                    fragment = new Topico();
                    break;
                case R.id.navigation_deslogar:
                    dialogDesconect();
                    break;
            }
            return loadFragment(fragment);
        }
    };

    // SEPARA A
    public static String getCurrentClass(String caminho) {
        String callClass = null;
        if (caminho != null) {
            int i = 0;
            for (String x : caminho.replace(".", "-").split("-")) {
                i++;
                if (i > 5)
                    callClass = x;
            }
        } else {
            throw new NullPointerException("Caminho Vazio");
        }
        return callClass;
    }


    //    CARREGA OS FRAGMENTS
    private boolean loadFragment(Fragment fragment) {
            if (fragment != null) {
                FragmentManager callFragment = getSupportFragmentManager();
                FragmentTransaction trasationFragment = callFragment.beginTransaction();

                if (Connection.isOnline(getApplicationContext())) {
                    if (currentFragment != null) {
                        String classCurrent = getCurrentClass(currentFragment.getClass().toString()); // RETORNA O CLASS CHAMADA DO CAMINHO
                        String classCall = getCurrentClass(fragment.getClass().toString()); // RETORNA O CLASS CHAMADA DO CAMINHO

                        if (!classCurrent.equals(classCall)) {
                            if (classCurrent.equals("Perfil") && (classCall.equals("Forum") || classCall.equals("Topico"))) {
                                // ANIMAÇÃO PARA DIREITA
                                trasationFragment.setCustomAnimations(R.anim.fade_in, R.anim.move_right);
                            } else if (classCurrent.equals("Forum")) {
                                if (classCall.equals("Topico")) {
                                    // ANIMAÇAÕ PARA DIREITA
                                    trasationFragment.setCustomAnimations(R.anim.fade_in, R.anim.move_right);
                                } else {
                                    // ANIMAÇÃO PARA ESQUERDA
                                    trasationFragment.setCustomAnimations(R.anim.fade_in, R.anim.move_left);
                                }
                            } else if (classCurrent.equals("Topico") && (classCall.equals("Perfil") || classCall.equals("Forum"))) {
                                // ANIMAÇÃO PARA ESQUERDA
                                trasationFragment.setCustomAnimations(R.anim.fade_in, R.anim.move_left);
                            }
                        } else {
                            trasationFragment.setCustomAnimations(R.anim.fade_in, R.anim.fade_in);
                            Log.i("PRINCIPAL", "ATUALIZANDO PAGINA");
                        }
                    }
                    trasationFragment.replace(R.id.fragment_main, fragment);
                    trasationFragment.commit();
                    currentFragment = fragment;
                    return true;
                }else {
                    ToastCustom.show(false, "PRECISA DE INTERNET");
                }
            }
        return false;
    }


    //    FUNÇÃO DE CONEXÃO DO BANCO
    @Override
    protected void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
    }

    private void dialogDesconect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PrincipalActivity.this);
        builder.setInverseBackgroundForced(true);
        builder.setTitle("DESEJA SAIR ?");
        builder.setMessage("SE VOCÊ SAIR, SEUS DADOS SERAM APAGADOS E VOCÊ TERA QUE INSERIR NOVAMENTE");

        builder.setPositiveButton("SAIR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Connection.logOut();
                cache = getSharedPreferences(alunoCache, MODE_PRIVATE);
                cache.edit().clear().apply();
                finish();
                Intent intent = new Intent(PrincipalActivity.this, LoginActivity.class);
                ActivityOptionsCompat animacaoActiviti = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_out, R.anim.move_left);
                ActivityCompat.startActivity(PrincipalActivity.this, intent, animacaoActiviti.toBundle());
                ToastCustom.show(true, "DESCONECTANDO");
                Log.w("PRICINPAL", "USUARIO DESCONENCTANDO");
            }
        });
        builder.setNegativeButton("NÃO SAIR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        AlertDialog CaixaDialagoForum = builder.create();
        CaixaDialagoForum.show();
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.move_right, R.anim.fade_in);
        super.finish();
    }
}
