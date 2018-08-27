package com.example.anderson.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView titulo;
    private CircleImageView circleImageView;
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titulo = findViewById(R.id.tituloChat);
        circleImageView = findViewById(R.id.circleImageFotoChat);

        /**Recuperando dados do Usuario*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            usuario = (Usuario) bundle.getSerializable("chatContato");
            titulo.setText(usuario.getNome());
            if (usuario.getFoto() != null){
                Uri url = Uri.parse(usuario.getFoto());
                Glide.with(ChatActivity.this).load(url).into(circleImageView);
            }else {
                circleImageView.setImageResource(R.drawable.padrao);
            }
        }
    }

}
