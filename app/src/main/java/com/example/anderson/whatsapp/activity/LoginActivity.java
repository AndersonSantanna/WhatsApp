package com.example.anderson.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private String codeSend;
    private TextInputEditText number;
    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bar = findViewById(R.id.progressBar);
        number = findViewById(R.id.numberCel);

        auth = ConfiguracaoFirebase.getFirebaseAuth();

        findViewById(R.id.buttonEntrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarCode();

            }
        });
    }
    public void enviarCode(){
        String phoneNumber = number.getText().toString();
        if (phoneNumber.isEmpty()){
            number.setError("Digite seu numero");
            number.requestFocus();
            return;
        }else if (phoneNumber.length() < 11){
            number.setError("Numero invalido");
            number.requestFocus();
            return;
        }else {
            bar.setVisibility(View.VISIBLE);
            phoneNumber = "+55" + phoneNumber;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, LoginActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                /*Intent intent = new Intent(getApplicationContext(), CodeActivity.class);
                intent.putExtra("code", codeSend);
                startActivity(intent);*/
                signInWithPhoneAuthCredential(phoneAuthCredential);
                bar.setVisibility(View.GONE);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                codeSend =s;
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            abrirTelaPrincipal();
                            Toast.makeText(getApplicationContext(), "Sucesso", Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                String excecao = "";
                                try {
                                    throw task.getException();
                                }catch (FirebaseAuthInvalidUserException e){
                                    excecao = "Usuario não esta cadastrado";
                                }catch (FirebaseAuthInvalidCredentialsException e){
                                    excecao = "Numero não corresponde";
                                }catch (Exception e){
                                    excecao = "Erro ao logar usuario;" + e.getMessage();
                                }
                                Toast.makeText(getApplicationContext(), excecao, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }
    public void cadastrar(View view){
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            abrirTelaPrincipal();
        }
    }
}
