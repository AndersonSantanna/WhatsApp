package com.example.anderson.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {
    public static Boolean validarPermission(String[] permissao, Activity activity, int requestCode){
        if (Build.VERSION.SDK_INT >= 23){
            List<String> list = new ArrayList<>();

            /**Percorre as permissoes passadas vericando uma a uma, se já tem a permissao liberada ou não*/
            for (String permission : permissao){
                if (!(ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED)){
                    list.add(permission);
                }
            }
            //Caso a lista esteja vazia, não é necessario solicitar permissao
            if (list.isEmpty()) {
                return true;
            }

            String[] novasPermissions = new String[list.size()];
            list.toArray(novasPermissions);
            ActivityCompat.requestPermissions(activity,novasPermissions,requestCode);
        }
        return true;
    }
}
