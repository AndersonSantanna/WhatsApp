package com.example.anderson.whatsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.example.anderson.whatsapp.helper.UsuarioFirebase;
import com.example.anderson.whatsapp.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class AlterNameActivity extends AppCompatActivity {
    private EditText name;
    private Usuario usuario;
    private FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_name);

        name = findViewById(R.id.editText);
        name.setText(user.getDisplayName());
        findViewById(R.id.buttonCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.buttonSalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty()) {
                    UsuarioFirebase.atualzarNameUser(name.getText().toString());
                    usuario = new Usuario(name.getText().toString(), user.getPhoneNumber());
                    usuario.salvar();
                    Toast.makeText(getApplicationContext(), "Alterado com sucesso!", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1000);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Digite seu nome!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
