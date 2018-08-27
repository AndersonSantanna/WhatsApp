package com.example.anderson.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.example.anderson.whatsapp.helper.UsuarioFirebase;
import com.example.anderson.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.santalu.widget.MaskEditText;

import java.util.concurrent.TimeUnit;

public class CadastroActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private TextInputEditText nome;
    private MaskEditText number;
    private ProgressBar bar;
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome = findViewById(R.id.nome);
        number =findViewById(R.id.cel);
        bar = findViewById(R.id.progressBar2);

        auth = ConfiguracaoFirebase.getFirebaseAuth();
        reference = ConfiguracaoFirebase.getFirebaseDatabase();

        findViewById(R.id.buttonVerificar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    enviarCode();

                }
            }
        });
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UsuarioFirebase.atualzarNameUser(nome.getText().toString());
                            Toast.makeText(getApplicationContext(), "Bem-vindo, "+ nome.getText().toString(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            bar.setVisibility(View.GONE);
                            usuario = new Usuario(nome.getText().toString(), number.getRawText());
                            usuario.salvar();
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "Falha", Toast.LENGTH_SHORT).show();
                                bar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }
    public void enviarCode(){
        String phoneNumber = number.getRawText();
        bar.setVisibility(View.VISIBLE);
        phoneNumber = "+55" + phoneNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, CadastroActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
            }
        });

    }
    public boolean validarCampos(){
        String phoneNumber = "+55"+number.getRawText();
        String name = nome.getText().toString();

        if (name.isEmpty() && phoneNumber.isEmpty()){

            number.setError("Digite seu numero");
            number.requestFocus();

            nome.setError("Digite seu nome");
            nome.requestFocus();
            return false;

        }else if (name.isEmpty()){
            nome.setError("Digite seu nome");
            nome.requestFocus();
            return false;
        }else if (phoneNumber.isEmpty()){
            number.setError("Digite seu numero");
            number.requestFocus();
            return false;
        }else if (phoneNumber.length() < 14){
            number.setError("Numero invalido");
            number.requestFocus();
            return false;
        }else {
            bar.setVisibility(View.VISIBLE);
            return true;
        }
    }
}
