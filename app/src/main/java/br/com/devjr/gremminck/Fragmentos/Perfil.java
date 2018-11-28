package br.com.devjr.gremminck.Fragmentos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;

import br.com.devjr.gremminck.Helper.Connection;
import br.com.devjr.gremminck.Helper.Forum.RecyclerAdapter;
import br.com.devjr.gremminck.Helper.Load;
import br.com.devjr.gremminck.R;
import br.com.devjr.gremminck.Modificadores.Usuario;

public class Perfil extends Fragment {

    //    BANCO DE DADOS

    private Load load;
    private Usuario aluno;
    private FirebaseAuth auth;
    private RecyclerAdapter recyclerAdapter;


    //    COMPONENTES
    private EditText campoBusca;
    private RecyclerView perfilMiniForum;
    private View perfilView, progressDadosForum;
    private TextView alunoEmail, alunoNome, alunoAcesso, alunoEnsino, alunoCurso;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        perfilView = inflater.inflate(R.layout.fragment_perfil, null);
        aluno = new Usuario();
        recyclerAdapter = new RecyclerAdapter();
        load = new Load(progressDadosForum, null);
        perfilMiniForum = perfilView.findViewById(R.id.PerflRecylerViewMensagens);

        SetElementos();
        auth = Connection.getFirebaseAuth();
        aluno = Connection.getCurrentUser();

        alunoNome.setText(aluno.getNome());
        alunoEmail.setText(aluno.getEmail());
        alunoAcesso.setText(aluno.getAcesso());
        alunoCurso.setText(aluno.getCurso());
        alunoEnsino.setText("Ensino "+aluno.getEnsino());

        FirebaseRecyclerAdapter adapter = RecyclerAdapter.initAdapter(perfilView, perfilMiniForum, true);
        perfilMiniForum.setAdapter(adapter);
        adapter.startListening();

        load.start();
        int total = recyclerAdapter.getTotal();
        if (total <= 0) {
            campoBusca.setHint("VOCÊ NÃO TEM POSTAGEM AINDA =(");
        } else {
            campoBusca.setHint("BUSQUE ENTRE " + recyclerAdapter.getTotal() + " MENSAGENS SUAS =D");
        }
        load.stop();

        return perfilView;
    }

    /**
     * BUSCA OS CAMPOS NESSESARIOS
     **/
    private void SetElementos() {
        alunoNome = perfilView.findViewById(R.id.PerfilFragmentNome);
        alunoEmail = perfilView.findViewById(R.id.PerfilFragmentEmail);
        alunoEnsino = perfilView.findViewById(R.id.PerfilFragmentEnsino);
        alunoAcesso = perfilView.findViewById(R.id.PerfilFragmentAcesso);
        alunoCurso = perfilView.findViewById(R.id.PerfilFragmentCurso);

        progressDadosForum = perfilView.findViewById(R.id.PerfilFragmentInformacoes);

        campoBusca = perfilView.findViewById(R.id.PerfilFragmentEditBusca);

    }
}
