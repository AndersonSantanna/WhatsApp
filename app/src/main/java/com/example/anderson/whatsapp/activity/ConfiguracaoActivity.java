package com.example.anderson.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.example.anderson.whatsapp.helper.Permissao;
import com.example.anderson.whatsapp.helper.UsuarioFirebase;
import com.example.anderson.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracaoActivity extends AppCompatActivity {
    private  String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private ImageButton camera, photo;
    private ImageView editar;
    private CircleImageView imageView;
    private StorageReference storageReference;
    private TextView nome;
    private static final int CAMERA = 100;
    private static final int PHOTO = 200;
    private Usuario usuario;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
        nome = findViewById(R.id.textViewUser);
        nome.setText(user.getDisplayName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        nome = findViewById(R.id.textViewUser);
        camera = findViewById(R.id.BotaoCamera);
        photo = findViewById(R.id.BotaoPhoto);
        imageView = findViewById(R.id.circleImagePerfil);

        findViewById(R.id.imageViewEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AlterNameActivity.class));
            }
        });

        storageReference = ConfiguracaoFirebase.getFirebasStorage();
        //Validar permissao
        Permissao.validarPermission(permission, this,10);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recuperando dados do usuario
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
        Uri url = user.getPhotoUrl();
        usuario = new Usuario(user.getDisplayName(), user.getPhoneNumber());
        if (url != null){
            Glide.with(ConfiguracaoActivity.this).load(url).into(imageView);

        }else {
            imageView.setImageResource(R.drawable.padrao);
        }

        nome.setText(user.getDisplayName());

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, CAMERA);
                }
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, PHOTO);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case CAMERA:

                        imagem = (Bitmap) data.getExtras().get("data");

                        break;
                    case PHOTO:
                        Uri imagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionada);
                        break;
                }

                if (imagem != null){
                    imageView.setImageBitmap(imagem);

                    /**Recuperar dados da imagem para o firebase*/
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    imagem.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    /**Salvar imagem no firebase*/
                    UploadTask uploadTask = storageReference.child("imagens").child("perfil").child(UsuarioFirebase.getIdUsuario().concat(".jpeg")).putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Upload interrompido", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Upload feito com sucesso", Toast.LENGTH_LONG).show();
                            Uri url = taskSnapshot.getDownloadUrl();
                            atualizarPhotoUser(url);
                            usuario.setFoto(url.toString());
                            usuario.salvar();
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void atualizarPhotoUser(Uri url){
        UsuarioFirebase.atualzarPhotoUser(url);
    }

    /**Aviso caso usuario nao aceite as permissões*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissionResult : grantResults){
            if (permissionResult == PackageManager.PERMISSION_DENIED){
                new AlertDialog.Builder(this, R.style.DialogStyle).setTitle("Permissão Negadas").setMessage("Para utilizar o app é necessario aceitar as permissões").setPositiveButton("Confimar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).create().show();

            }
        }

    }
}
