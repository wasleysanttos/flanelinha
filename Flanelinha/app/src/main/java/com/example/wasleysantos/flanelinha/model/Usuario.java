package com.example.wasleysantos.flanelinha.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by WasleySantos on 31/05/2016.
 */
public class Usuario implements Serializable {

    private long id;
    private String email;
    private String nome;
    private String password;
    @SerializedName("palavrasecreta")
    private String palavraSecreta;


    public Usuario(long id, String email, String nome, String password, String palavraSecreta) {
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.password = password;
        this.palavraSecreta = palavraSecreta;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPalavraSecreta() {
        return palavraSecreta;
    }

    public void setPalavraSecreta(String palavraSecreta) {
        this.palavraSecreta = palavraSecreta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
