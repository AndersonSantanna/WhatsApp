package com.example.anderson.whatsapp.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;

public class UsuarioFirebase {
    public static String getIdUsuario(){
        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
        return Base64.encodeToString(auth.getCurrentUser().getPhoneNumber().getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
        return  auth.getCurrentUser();
    }

    public static  boolean atualzarPhotoUser(Uri uri){

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
            user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {

                    }
                }
            });

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static  boolean atualzarNameUser(String nome){

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
            user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("atualizar", "Sucesso ao atualizar nome");
                    }
                }
            });

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
