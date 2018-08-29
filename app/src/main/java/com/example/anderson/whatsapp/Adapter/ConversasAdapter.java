package com.example.anderson.whatsapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.model.Conversa;
import com.example.anderson.whatsapp.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder>{
    private List<Conversa> list;
    private Context context;

    public ConversasAdapter(List<Conversa> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_conversas,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Conversa conversa = list.get(position);
        holder.ultmsg.setText(conversa.getUltMsg());
        Usuario usuario = conversa.getUsuario();
        holder.nome.setText(usuario.getNome());
        if (usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else {
            holder.foto.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView nome, ultmsg;
        public MyViewHolder(View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.imageViewConversa);
            nome = itemView.findViewById(R.id.textViewNome);
            ultmsg = itemView.findViewById(R.id.textViewUltMsg);
        }
    }
}
