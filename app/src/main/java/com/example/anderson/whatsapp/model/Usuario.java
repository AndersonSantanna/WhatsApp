package com.example.anderson.whatsapp.model;

public class Usuario {
    private String nome, cel;

    public Usuario() {
    }

    public Usuario(String nome, String cel) {
        this.nome = nome;
        this.cel = cel;
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
