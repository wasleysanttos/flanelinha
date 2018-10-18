package com.example.wasleysantos.flanelinha.model;

import java.io.Serializable;
import java.util.ArrayList;

public class TipoVaga implements Serializable  {

    private long id;
    private String descricao;

    public TipoVaga(long id) {
        this.id = id;
    }

    public TipoVaga(long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    @Override
    public String toString(){
//        return (this.getId()+"-"+ this.getDescricao());
        return (this.getDescricao());
    }

}
