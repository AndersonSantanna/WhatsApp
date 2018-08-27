package com.example.anderson.whatsapp.model;

import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.example.anderson.whatsapp.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nome, cel, foto;

    public Usuario() {
    }

    public Usuario(String nome, String cel) {
        this.nome = nome;
        this.cel = cel;
    }

    public void salvar(){
        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
        reference.child("Usuario").child(UsuarioFirebase.getIdUsuario()).setValue(this);
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCel() {
        return cel;
    }

    public void setCel(String cel) {
        this.cel = cel;
    }
}
