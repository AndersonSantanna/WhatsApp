package com.example.anderson.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.helper.UsuarioFirebase;
import com.example.anderson.whatsapp.model.Mensagem;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {

    private List<Mensagem> list;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public MensagensAdapter(List<Mensagem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TIPO_REMETENTE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_remetente, parent, false);
        }else if (viewType == TIPO_DESTINATARIO){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario, parent, false);
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Mensagem mensagem =  list.get(position);

        if (mensagem.getImagem() != null ){

            Uri uri = Uri.parse(mensagem.getImagem());
            Glide.with(context).load(uri).into(holder.foto);
//            holder.foto.setImageResource(R.drawable.logo);
            Log.i("URI", uri.toString());

            //Esconder texto
            holder.msg.setVisibility(View.GONE);

        }else{

            holder.msg.setText(mensagem.getMensagem());
            holder.foto.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {

        Mensagem mensagem =  list.get(position);
        if (UsuarioFirebase.getIdUsuario().equals(mensagem.getIdUsuario())){
            return TIPO_REMETENTE;
        }else {
            return TIPO_DESTINATARIO;
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView msg;
        ImageView foto;
        public MyViewHolder(View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.textMsg);
            foto = itemView.findViewById(R.id.imageMsg);
        }
    }
}
