package com.ipev.controle_material.Model;

import java.io.Serializable;

public class siloms_itens implements Serializable {

    public siloms_itens(int bmp, String descricao) {
        this.bmp = bmp;
        this.descricao = descricao;
    }

    int bmp;

    public siloms_itens() {

    }

    public int getBmp() {
        return bmp;
    }

    public void setBmp(int bmp) {
        this.bmp = bmp;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    String descricao;
}
