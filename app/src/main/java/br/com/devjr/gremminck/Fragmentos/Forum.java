package br.com.devjr.gremminck.Fragmentos;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import br.com.devjr.gremminck.Helper.Forum.RecyclerAdapter;
import br.com.devjr.gremminck.R;

public class Forum extends Fragment {

    private View ForumView;
    private RecyclerView ForumRecyMensagens;
    private FirebaseRecyclerAdapter adapter;

    private TextView Description;


    //    CRIANDO A VIEW
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ForumView =  inflater.inflate(R.layout.fragment_forum, container, false);
        ForumRecyMensagens = (RecyclerView) ForumView.findViewById(R.id.ForumRecyclerViewMesagens);
        Description = ForumView.findViewById(R.id.ForumMensagemHeaderSeach);

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter();
        adapter = recyclerAdapter.initAdapter(ForumView, ForumRecyMensagens, false);
        ForumRecyMensagens.setAdapter(adapter);
        int total = RecyclerAdapter.getTotal();
        if (total <= 0){
            Description.setHint("NENHUMA MENSAGEM");
        }else{
            Description.setHint(total+" MENSAGENS ENCONTRADAS");
        }
        adapter.startListening();


        return ForumView;
    }

    //    PREPARANDO DADOS
    @Override
    public void onStart() {
        super.onStart();
    }
}
