package com.example.anderson.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.helper.Permissao;

public class ConfiguracaoActivity extends AppCompatActivity {
    private  String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        //Validar permissao
        Permissao.validarPermission(permission, this,10);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
