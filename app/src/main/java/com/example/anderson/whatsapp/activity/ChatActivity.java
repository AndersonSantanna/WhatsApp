package com.example.anderson.whatsapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.anderson.whatsapp.Adapter.MensagensAdapter;
import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.example.anderson.whatsapp.helper.UsuarioFirebase;
import com.example.anderson.whatsapp.model.Mensagem;
import com.example.anderson.whatsapp.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView titulo;
    private CircleImageView circleImageView;
    private EditText editMensagem;
    private Usuario usuario;
    private String idUserDestinatario;
    private RecyclerView recyclerView;
    private MensagensAdapter adapter;
    private List<Mensagem> list = new ArrayList<>();
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference msgRef;
    private ChildEventListener childEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //referenciando
        titulo = findViewById(R.id.tituloChat);
        circleImageView = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerView = findViewById(R.id.recyclerChat);

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
            //Recuperara dados do destinatario
            idUserDestinatario = Base64.encodeToString(usuario.getCel().getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
            msgRef = ConfiguracaoFirebase.getFirebaseDatabase();
        }

        //Conf adapter
        adapter = new MensagensAdapter(list, getApplicationContext());

        //Conf recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        recuperarMensagem();

    }
    public  void enviar(View view){
        if (!editMensagem.getText().toString().isEmpty()){
            Mensagem msg = new Mensagem(UsuarioFirebase.getIdUsuario(), editMensagem.getText().toString());

            //sabando mensagem para Remetente
            salvarMensagem(UsuarioFirebase.getIdUsuario(), idUserDestinatario, msg);

            //sabando mensagem para Destinatario
            salvarMensagem(idUserDestinatario, UsuarioFirebase.getIdUsuario(), msg);
        }else {

        }
        editMensagem.setText("");
    }

    private  void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){
        reference.child("Mensagem").child(idRemetente).child(idDestinatario).push().setValue(msg);
    }

    private void recuperarMensagem(){
        msgRef = reference.child("Mensagem").child(UsuarioFirebase.getIdUsuario()).child(idUserDestinatario);
        childEventListener = msgRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                list.add(dataSnapshot.getValue(Mensagem.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        reference.removeEventListener(childEventListener);

    }
}
