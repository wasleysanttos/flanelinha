package com.example.wasleysantos.flanelinha.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by WasleySantos on 14/04/2016.
 */
public class Vaga implements Serializable {

    private long id;
    private double latitude;
    private double longitude;
    private int status;
    private TipoVaga tipovaga;
    private Usuario usuario;

    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    @SerializedName("data_cadastro")
    private Date data_vaga;
    private Date data_ultima_ocupacao;


    @Override
    public String toString() {
        return super.toString();
    }

    //construtores
    public Vaga(long id, double latitude, double longitude, int status, TipoVaga tipovaga, Usuario usuario, String endereco, String bairro, String cidade, String estado, Date data, Date data_ultima_ocupacao) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.tipovaga = tipovaga;
        this.usuario = usuario;
        this.endereco = endereco;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.data_vaga = data;
        this.data_ultima_ocupacao = data_ultima_ocupacao;
    }

    //getter e setter
    public TipoVaga getTipovaga() {
        return tipovaga;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Date getData_ultima_ocupacao() {
        return data_ultima_ocupacao;
    }

    public void setData_ultima_ocupacao(Date data_ultima_ocupacao) {
        this.data_ultima_ocupacao = data_ultima_ocupacao;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setTipovaga(TipoVaga tipovaga) {
        this.tipovaga = tipovaga;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getData_vaga() {
        return data_vaga;
    }

    public void setData_vaga(Date data_vaga) {
        this.data_vaga = data_vaga;
    }


}



