package br.com.devjr.gremminck.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.devjr.gremminck.Helper.Calendario;
import br.com.devjr.gremminck.Modificadores.Mensagem;
import br.com.devjr.gremminck.R;

public class RespostasAdapter extends BaseAdapter {

    private final List<Mensagem> repostas;
    private final View view;

    public RespostasAdapter(List<Mensagem> repostas, View view) {
        this.repostas = repostas;
        this.view = view;
    }

    @Override
    public int getCount() {
        return repostas.size();
    }

    @Override
    public Object getItem(int position) {
        return repostas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View viewRsp = inflater.inflate(R.layout.respostas_layout, null);

        TextView cpNome, cpMensagem, cpData;
        String   nome = repostas.get(position).getNome()
                , mensagem = repostas.get(position).getMensagem()
                , data = Calendario.formatData("data", repostas.get(position).getData());

        cpNome = viewRsp.findViewById(R.id.RespostasLayoutNome);
        cpMensagem = viewRsp.findViewById(R.id.RespostasLayoutMensagem);
        cpData = viewRsp.findViewById(R.id.RespostasLayoutData);

        cpNome.setText(nome);
        cpMensagem.setText(mensagem);
        cpData.setText(data);

        return viewRsp;
    }
}
