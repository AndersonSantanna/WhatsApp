package com.example.anderson.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class CodeActivity extends AppCompatActivity {
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
    private String number, codeSend;
    private TextInputEditText codigo;
    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        bar = findViewById(R.id.progressBar3);
//        bar.setDrawingCacheBackgroundColor();
//        Bundle dados = getIntent().getExtras();
//        number = String.valueOf(dados.getBundle("cell"));
//        Log.i("teste", ""+ number);

        /**Ação do botão*/
        findViewById(R.id.buttonLogar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarCode();
            }
        });

        String code = codigo.getText().toString();
        if (code.isEmpty()){
            codigo.setError("Digite seu numero");
            codigo.requestFocus();
            return;
        }else if (code.length() < 6){
            codigo.setError("Codigo invalido");
            codigo.requestFocus();
            return;
        }else {
            bar.setVisibility(View.VISIBLE);
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, CodeActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(),"Falha ao se conectar, verfique sua conexão com a internet e tente mais tarde!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                codeSend = s;
            }
        });

    }
    private void verificarCode() {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSend, codigo.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sucesso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "Verifique seu acesso a internet", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }
}
