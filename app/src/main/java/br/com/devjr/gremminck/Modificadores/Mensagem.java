package br.com.devjr.gremminck.Modificadores;

import java.util.ArrayList;
import java.util.List;

public class Mensagem {

    private String ID;
    private int Likes = 0;
    private String UID;
    private String Nome;
    private long Data;
    private String Titulo;
    private String Materia;
    private String Mensagem;

    public Mensagem() {
    }

    public Mensagem(String id, int like, String UID, String nome, long data, String titulo, String materia, String mensagem) {
        ID = id;
        Likes = like;
        UID = UID;
        Nome = nome;
        Data = data;
        Titulo = titulo;
        Materia = materia;
        Mensagem = mensagem;

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getLikes() {
        return Likes;
    }

    public void setLikes(int likes) {
        Likes = likes;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public long getData() {
        return Data;
    }

    public void setData(long data) {
        Data = data;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getMateria() {
        return Materia;
    }

    public void setMateria(String materia) {
        Materia = materia;
    }

    public String getMensagem() {
        return Mensagem;
    }

    public void setMensagem(String mensagem) {
        Mensagem = mensagem;
    }
}
