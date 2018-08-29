package com.example.anderson.whatsapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.anderson.whatsapp.Adapter.MensagensAdapter;
import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.example.anderson.whatsapp.helper.UsuarioFirebase;
import com.example.anderson.whatsapp.model.Conversa;
import com.example.anderson.whatsapp.model.Mensagem;
import com.example.anderson.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView titulo;
    private CircleImageView circleImageView;
    private EditText editMensagem;
    private ImageView galeria;
    private Usuario usuario;
    private String idUserDestinatario;
    private RecyclerView recyclerView;
    private MensagensAdapter adapter;
    private List<Mensagem> list = new ArrayList<>();
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference msgRef;
    private static final int CAMERA = 100;
    private static final int PHOTO = 200;
    private StorageReference storageReference = ConfiguracaoFirebase.getFirebasStorage();
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
        galeria = findViewById(R.id.imageView4);

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
        adapter = new MensagensAdapter(list, ChatActivity.this);

        //Conf recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("Selecione!")
                        .setMessage("Câmera ou Galeria ?")
                        .setPositiveButton("Câmera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if(intent.resolveActivity(getPackageManager()) != null){
                                    startActivityForResult(intent, CAMERA);
                                }
                            }
                        })
                        .setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                if (intent.resolveActivity(getPackageManager()) != null){
                                    startActivityForResult(intent, PHOTO);
                                }
                            }
                        }).create().show();*/
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, CAMERA);
                }
            }
        });
        recuperarMensagem();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = (Bitmap) data.getExtras().get("data");

            try {
                /*switch (requestCode){
                    case CAMERA:

                        imagem = (Bitmap) data.getExtras().get("data");

                        break;
                    case PHOTO:
                        Uri imagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionada);
                        break;
                }*/

                if (imagem != null){
                    //galeria.setImageBitmap(imagem);

                    /**Recuperar dados da imagem para o firebase*/
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    /**Salvar imagem no firebase*/
                    StorageReference reference1 = storageReference.child("imagens").child("fotos").child(UsuarioFirebase.getIdUsuario()).child(UUID.randomUUID().toString().concat(".jpeg"));
                    final UploadTask uploadTask = reference1.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Upload interrompido", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Upload feito com sucesso", Toast.LENGTH_LONG).show();

                            Mensagem mensagem = new Mensagem(UsuarioFirebase.getIdUsuario(), "imagem.jpeg", taskSnapshot.getDownloadUrl().toString());
                            //remetente
                            salvarMensagem(UsuarioFirebase.getIdUsuario(), idUserDestinatario, mensagem);
                            //destinatario
                            salvarMensagem(idUserDestinatario, UsuarioFirebase.getIdUsuario(), mensagem);

                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public  void enviar(View view){
        if (!editMensagem.getText().toString().isEmpty()){
            Mensagem msg = new Mensagem(UsuarioFirebase.getIdUsuario(), editMensagem.getText().toString());

            //sabando mensagem para Remetente
            salvarMensagem(UsuarioFirebase.getIdUsuario(), idUserDestinatario, msg);

            //sabando mensagem para Destinatario
            salvarMensagem(idUserDestinatario, UsuarioFirebase.getIdUsuario(), msg);
            salvarConversa(msg);
            notificarMensagem();
        }else {

        }
        editMensagem.setText("");
    }

    private  void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){
        reference.child("Mensagem").child(idRemetente).child(idDestinatario).push().setValue(msg);
    }

    private void salvarConversa(Mensagem msg){
        Conversa conversa = new Conversa(UsuarioFirebase.getIdUsuario(), idUserDestinatario,msg.getMensagem(),usuario);
        conversa.salvar();
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

    public void notificarMensagem(){

        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mensagem);
        if (mediaPlayer != null){
            mediaPlayer.start();
            mediaPlayer.release();
        }
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
