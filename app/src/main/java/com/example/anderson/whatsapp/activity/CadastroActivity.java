package com.example.anderson.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class CadastroActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextInputEditText nome, number, codigo;
    private String codeSend;
    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome = findViewById(R.id.nome);
        number =findViewById(R.id.cel);
//        codigo =findViewById(R.id.code);
        bar = findViewById(R.id.progressBar2);

        auth = ConfiguracaoFirebase.getFirebaseAuth();


        findViewById(R.id.buttonVerificar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarCode();
            }
        });

        /*findViewById(R.id.buttonCadastrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarCode();
            }
        });*/



    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sucesso", Toast.LENGTH_SHORT).show();
                            bar.setVisibility(View.GONE);
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
        String phoneNumber = number.getText().toString();
        if (phoneNumber.isEmpty() && nome.getText().toString().isEmpty()){
            nome.setError("Digite seu nome");
            nome.requestFocus();
            number.setError("Digite seu numero");
            number.requestFocus();
            return;
        }else if (nome.getText().toString().isEmpty()){
            nome.setError("Digite seu nome");
            nome.requestFocus();
            return;
        }else if (phoneNumber.length() < 11){
            number.setError("Numero invalido");
            number.requestFocus();
            return;
        }else {

            bar.setVisibility(View.VISIBLE);
            phoneNumber = "+55" + phoneNumber;
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, CadastroActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {

                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeSend = s;
                }
            });
        }
    }
    public void validaCampos(){
        if (!nome.getText().toString().isEmpty()){
            nome.setError("Preencha seu nome");
            nome.requestFocus();
            return;
        }
    }
}
