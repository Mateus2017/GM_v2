package br.com.devjr.gremminck.Helper.Forum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.com.devjr.gremminck.Helper.Calendario;
import br.com.devjr.gremminck.Helper.Connection;
import br.com.devjr.gremminck.Helper.Dialogue;
import br.com.devjr.gremminck.Helper.ToastCustom;
import br.com.devjr.gremminck.Modificadores.Mensagem;
import br.com.devjr.gremminck.R;

public class RecyclerAdapter extends AppCompatActivity {

    private static Query caminho;

    private static int totalPosts;
    private static boolean apenasUser;
    private static int totalPostsUsuario;

    private static Calendario calendario = new Calendario();
    private static FirebaseUser user = Connection.getFirebaseUser();
    private static FirebaseRecyclerAdapter<Mensagem, MensagemViewHolder> adapter;
    private static DatabaseReference forumDataBase = FirebaseDatabase.getInstance().getReference("Forum/Mensagens");


    public static FirebaseRecyclerAdapter initAdapter(final View v, RecyclerView Recy, final boolean apenasDoUsuario) {
        try {
            caminho = forumDataBase;
            apenasUser = false;

            if (apenasDoUsuario) {
                caminho = forumDataBase.orderByChild("uid").equalTo(user.getUid());
                apenasUser = true;
            }

            Recy.setLayoutManager(new LinearLayoutManager(v.getContext()));
            FirebaseRecyclerOptions DadosBanco = new FirebaseRecyclerOptions.Builder<Mensagem>().setQuery(caminho, Mensagem.class).build();
            adapter = new FirebaseRecyclerAdapter<Mensagem, MensagemViewHolder>(DadosBanco) {
                @NonNull
                @Override
                protected void onBindViewHolder(@NonNull final MensagemViewHolder holder, final int position, @NonNull final Mensagem model) {

                    holder.titulo.setText(model.getTitulo());
                    holder.mensagem.setText(treatStringSize(model.getMensagem(), 85));
                    String nomeFull[] = model.getNome().split(" ");
                    String nomePerson = nomeFull[0]+" "+nomeFull[1];
                    holder.nome.setText(nomePerson);
                    holder.data.setText(calendario.formatData("data", model.getData()));
                    holder.materia.setText(model.getMateria());
                    String likesString = "" + model.getLikes();

                    if (likesString.length() < 3) {
                        if (likesString.length() == 2) {
                            likesString = "0" + likesString;
                        } else {
                            likesString = "00" + likesString;
                        }
                    }
                    holder.likesCount.setText(likesString);
                    holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Dialogue.SupViewMensagem(v, model);
                            return true;
                        }
                    });

                    holder.options.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // GET HEIGHT PARENT AND SET CHILD
                            holder.optionsView.setMinimumHeight(holder.principalView.getHeight());

                            // SET VISIBILITY OF OPTIONSVIEW
                            holder.optionsView.setVisibility(View.VISIBLE);

                            // POST CHECK
                            if (model.getUID().equals(user.getUid())) {
                                holder.optionsButtonsUser.setVisibility(View.VISIBLE);
                            } else {
                                holder.optionsButtonsUser.setVisibility(View.GONE);
                                holder.optionsButtonsForum.setVisibility(View.VISIBLE);
                            }

                            // LISTENER BUTTON OF CLOSE
                            holder.optionsClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.optionsView.setVisibility(View.GONE);

                                }
                            });

                            // LISTENER BUTTON OF EDIT
                            holder.optionsEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });

                            // LISTENER BUTTON OF DELETE
                            holder.optionsDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String title = "tem certeza ? ";
                                    String mensagem = "ao exluir esse post você não podera mais recuperar ele !";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setInverseBackgroundForced(true);
                                    builder.setTitle(title.toUpperCase());
                                    builder.setMessage(mensagem.toString().toUpperCase());

                                    builder.setPositiveButton("excluir".toString().toUpperCase(), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            forumDataBase.child(model.getID()).setValue(null);
                                            ToastCustom.show(true, "DELETADO !!!");
                                        }
                                    });
                                    builder.setNegativeButton("não excluir".toString().toUpperCase(), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });
                                    AlertDialog CaixaDialagoForum = builder.create();
                                    CaixaDialagoForum.show();
                                }
                            });
                        }
                    });

                }

                @NonNull
                @Override
                public MensagemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.forum_mensagem, viewGroup, false);
                    final MensagemViewHolder viewHolder = new MensagemViewHolder(view);
                    if (apenasDoUsuario) {
                        totalPostsUsuario = adapter.getItemCount();
                    } else {
                        totalPosts = adapter.getItemCount();
                    }
                    return viewHolder;
                }
            };
            return adapter;

        } catch (Exception ERRO_BUSCA_MENSAGENS_FORUM) {
            Log.e("RECYCLER_ADAPTER", ERRO_BUSCA_MENSAGENS_FORUM.getMessage());
            ToastCustom.show(false, "Erro ao fazer a busca no banco de dados");
            return null;
        }
    }


    public static int getTotal() {
        try {
            if (apenasUser) {
                if (totalPostsUsuario <= 0) {
                    throw new ArithmeticException("Nenhum post do usuario encontrado");
                } else {
                    Log.i("TotalPostUsers", "==> Foram encontrado um total de " + totalPostsUsuario + " Mensagens seus.");
                    return totalPostsUsuario;
                }
            } else {
                if (totalPosts <= 0) {
                    throw new ArithmeticException("Nenhum mensagem do forum encontrada");
                } else {
                    Log.i("TotalPostsForum", "==> Foram encontrado um total de " + totalPosts + " Mensagens.");
                    return totalPosts;
                }
            }

        } catch (ArithmeticException total_exc) {
            Log.i("TotalResultErro", "==> " + total_exc.getMessage());
            return 0;
        }
    }

    public static String treatStringSize(String input, int sizeMax) {
        String output = null;
        if (!input.isEmpty()) {
            if (input.length() < sizeMax) {
                return input;
            }else{
                String frases[] = input.split(" ");

                for (int x = 0; frases.length > x; x++) {
                    if (x == 0) {
                        output = frases[x] + " ";
                    } else {
                        if (output.length() < sizeMax) {
                            output += frases[x] + " ";
                        } else {
                            output += "[...]";
                            return output;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static class MensagemViewHolder extends RecyclerView.ViewHolder {

        public static TextView nome, titulo, materia, mensagem, data, likesCount;
        public static ImageView like, options;

        public View optionsView, principalView, optionsButtonsForum, optionsButtonsUser, root;

        /**
         * options
         **/

        public Button optionsClose, optionsEdit, optionsDelete, optionsReport;

        public MensagemViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.fragment_card_forum);

            principalView = (View) itemView.findViewById(R.id.ForumConstraintLayout);
            titulo = itemView.findViewById(R.id.ForumMensagemDisplayTitulo);
            materia = itemView.findViewById(R.id.ForumMensagemDisplayMateria);
            mensagem = itemView.findViewById(R.id.ForumMensagemDisplayMensagem);
            nome = itemView.findViewById(R.id.ForumMensagemDisplayNome);
            data = itemView.findViewById(R.id.ForumMensagemDisplayData);
            like = (ImageView) itemView.findViewById(R.id.ForumMensagemDisplayLike);
            likesCount = itemView.findViewById(R.id.ForumMensagemQuantidadeLikes);
            options = (ImageView) itemView.findViewById(R.id.ForumMensagemDisplayOptions);


            optionsView = (View) itemView.findViewById(R.id.ForumMensagemOptions);
            optionsButtonsUser = itemView.findViewById(R.id.ForumMensagemOptionsBodyUsuario);
            optionsButtonsForum = itemView.findViewById(R.id.ForumMensagemOptionsBodyUsuarios);

            optionsClose = (Button) itemView.findViewById(R.id.ForumMensagemOptionsBodyDadosHeaderClose);
            optionsEdit = (Button) itemView.findViewById(R.id.ForumMensagemButtonEditar);
            optionsDelete = (Button) itemView.findViewById(R.id.ForumMensagemButtonExcluir);
            optionsReport = (Button) itemView.findViewById(R.id.ForumMensagemOptionsBodyReportar);
        }
    }
}
