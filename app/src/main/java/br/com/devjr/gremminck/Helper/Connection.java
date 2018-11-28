package br.com.devjr.gremminck.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.devjr.gremminck.Modificadores.Usuario;

public class Connection {

    private static FirebaseAuth firebaseAuth;
    private static FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseUser firebaseUser;

    private static DatabaseReference Forum = FirebaseDatabase.getInstance().getReference("Forum/Mensagens");
    private static DatabaseReference Usuario = FirebaseDatabase.getInstance().getReference("Usuarios");
    private static DatabaseReference Gremminck;
    private static DatabaseReference ForumRespostas;
    private static Usuario Authenticado = new Usuario();

    public Connection(){}

    public static FirebaseAuth getFirebaseAuth(){
        if (firebaseAuth == null){
            initFirebaseAuth();
        }
        return firebaseAuth;
    }

    private static void initFirebaseAuth() {
        firebaseAuth = firebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    firebaseUser = user;
                }else{
                    Log.d("AUTH", "onAuthStateChanged:signed_out");
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static FirebaseUser getFirebaseUser(){
        return firebaseUser;
    }

    public static void logOut(){
        firebaseAuth.signOut();
    }

    @Nullable
    public static Usuario getCurrentUser(){
        try{
            DatabaseReference DataBaseUsuarios = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
            DataBaseUsuarios.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Authenticado.setUID(firebaseUser.getUid());
                    Authenticado.setNome(dataSnapshot.child("nome").getValue().toString());
                    Authenticado.setEmail(dataSnapshot.child("email").getValue().toString());
                    Authenticado.setAcesso(dataSnapshot.child("acesso").getValue().toString());
                    Authenticado.setCurso(dataSnapshot.child("curso").getValue().toString());
                    Authenticado.setEnsino(dataSnapshot.child("ensino").getValue().toString());

                    Log.i("SUCESSO_BUSCA/USUARIO", "BUSCA EFETUADA");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ERRO_BUSCA/USUARIO", databaseError.getMessage());
                }
            });

            return Authenticado;

        }catch (Exception ERRO_BUSCA_USUARIO){
            Log.e("ERRO_BUSCA/USUARIO", " => " + ERRO_BUSCA_USUARIO.getMessage());
            return null;
        }
    }

    public static DatabaseReference getForum(){
        return Forum;
    }

    public static DatabaseReference getUser(){
        return Usuario;
    }

    public static DatabaseReference getBanco(String caminho){
        try {
            if (caminho == null || caminho.isEmpty()){
                throw new Exception("Passe um caminho");
            }else{
                Gremminck = FirebaseDatabase.getInstance().getReference(caminho);
            }
        }catch (Exception erro_caminho){
            Log.e("ERRO/CONEXÃO", erro_caminho.getMessage());
        }
        return Gremminck;
    }

    public static DatabaseReference getForumRespostas(String uid) {
        return getForum().child(uid).child("respostas");
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
            return true;
        else
            return false;
    }
}
