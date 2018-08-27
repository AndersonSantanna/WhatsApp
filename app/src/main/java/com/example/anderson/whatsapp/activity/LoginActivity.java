package com.example.anderson.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.example.anderson.whatsapp.helper.Permissao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.santalu.widget.MaskEditText;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private String codeSend;
    private MaskEditText number;
    private ProgressBar bar;
    private  String[] permission = new String[]{Manifest.permission.READ_SMS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bar = findViewById(R.id.progressBar);
        number = findViewById(R.id.numberCel);

        auth = ConfiguracaoFirebase.getFirebaseAuth();
        Permissao.validarPermission(permission, LoginActivity.this, 1);
        findViewById(R.id.buttonEntrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    enviarCode();
                }
            }
        });
    }
    public void enviarCode(){
        String numero = "+55" + number.getRawText();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(numero, 60, TimeUnit.SECONDS, LoginActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                bar.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                bar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Falha ao se conectar, verfique sua conexão com a internet e tente mais tarde!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(getApplicationContext(),"Tente novamente mais tarde", Toast.LENGTH_LONG).show();
                bar.setVisibility(View.GONE);
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
                            abrirTelaPrincipal(MainActivity.class);
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
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public boolean validarCampos(){
        String phoneNumber = number.getText().toString();
        if (phoneNumber.isEmpty()){
            number.setError("Digite seu numero");
            number.requestFocus();
            return false;
        }else if (phoneNumber.length() < 11){
            number.setError("Numero invalido");
            number.requestFocus();
            return false;
        }else {
            bar.setVisibility(View.VISIBLE);
            return true;
        }
    }

    public void abrirTelaPrincipal(Class cls){
        Intent intent = new Intent(getApplicationContext(), cls);
//        intent.putExtra("cell", number.getText().toString());
        startActivity(intent);

    }
    public void cadastrar(View view){
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            abrirTelaPrincipal(MainActivity.class);
        }
    }
    /**Aviso caso usuario nao aceite as permissões*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissionResult : grantResults){
            if (permissionResult == PackageManager.PERMISSION_DENIED){
                new AlertDialog.Builder(this, R.style.DialogStyle).setTitle("Permissão Negadas").setMessage("Para utilizar o app é necessario aceitar as permissões, volte quando quiser dar permissão").setPositiveButton("Confimar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).create().show();
            }

        }

    }

}
