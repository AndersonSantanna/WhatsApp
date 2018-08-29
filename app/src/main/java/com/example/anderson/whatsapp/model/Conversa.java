package com.example.anderson.whatsapp.model;

import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {
    private String idRemetente, idDestinatario, ultMsg;
    private Usuario usuario;

    public Conversa() {
    }

    public void salvar(){
        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
        reference.child("Conversas").child(this.getIdRemetente()).child(this.idDestinatario).setValue(this);
    }

    public Conversa(String idRemetente, String idDestinatario, String ultMsg, Usuario usuario) {
        this.idRemetente = idRemetente;
        this.idDestinatario = idDestinatario;
        this.ultMsg = ultMsg;
        this.usuario = usuario;
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltMsg() {
        return ultMsg;
    }

    public void setUltMsg(String ultMsg) {
        this.ultMsg = ultMsg;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
