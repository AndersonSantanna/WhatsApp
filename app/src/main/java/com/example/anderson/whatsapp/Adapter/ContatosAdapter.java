package com.example.anderson.whatsapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.MyViewHolder> {
    private List<Usuario> list;
    private Context context;
    public ContatosAdapter(List<Usuario> list, Context context){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_contatos,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario usuario = list.get(position);

        holder.nome.setText(usuario.getNome());
        holder.numero.setText(usuario.getCel());

        if (usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());
            Log.i("teste", usuario.getFoto());
            Log.i("teste", "URI: " + uri.toString());
            Glide.with(context).load(uri).into(holder.circleImageView);
        }else {
            holder.circleImageView.setImageResource(R.drawable.padrao);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        final private TextView nome, numero;
        final private CircleImageView circleImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.imageView3);
            nome = itemView.findViewById(R.id.textViewNomeContato);
            numero = itemView.findViewById(R.id.textViewNumero);
        }
    }
}
