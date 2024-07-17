package com.iae.controle_material.Model;

import java.io.Serializable;

public class ItensModel implements Serializable {

    public ItensModel(){

    }
    private String setor;
    private String predio;

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    private String sala;


    private String estado;
    private String BMP;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    private String observacao;

    private String descricao;


    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getPredio() {
        return predio;
    }

    public void setPredio(String predio) {
        this.predio = predio;
    }


    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getBMP() {
        return BMP;
    }

    public void setBMP(String BMP) {
        this.BMP = BMP;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ItensModel(String sala, int id, String setor, String predio, String estado, String BMP, String observacao, String descricao) {
        this.setor = setor;
        this.predio = predio;
        this.estado = estado;
        this.BMP = BMP;
        this.sala = sala;
        this.observacao = observacao;
        this.descricao = descricao;
        this.id = id;

    }



}
