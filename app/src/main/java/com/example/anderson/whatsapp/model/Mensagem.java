package com.example.anderson.whatsapp.model;

public class Mensagem {
    private String idUsuario, mensagem, imagem ;

    public Mensagem() {
    }

    public Mensagem(String idUsuario, String mensagem, String imagem) {
        this.idUsuario = idUsuario;
        this.mensagem = mensagem;
        this.imagem = imagem;
    }

    public Mensagem(String idUsuario, String mensagem) {
        this.idUsuario = idUsuario;
        this.mensagem = mensagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
