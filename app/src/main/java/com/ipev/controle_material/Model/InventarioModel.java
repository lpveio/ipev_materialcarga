package com.ipev.controle_material.Model;

public class InventarioModel {

    public InventarioModel(){

    }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getCriado() {
            return criado;
        }

        public void setCriado(String criado) {
            this.criado = criado;
        }

        public String getData_inc() {
            return data_inc;
        }

        public void setData_inc(String data_inc) {
            this.data_inc = data_inc;
        }

        public String getData_atual() {
            return data_atual;
        }

        public void setData_atual(String data_atual) {
            this.data_atual = data_atual;
        }

        private String nome;
        private String criado;

        public InventarioModel(String nome, String criado, String data_inc, String data_atual) {
            this.nome = nome;
            this.criado = criado;
            this.data_inc = data_inc;
            this.data_atual = data_atual;
        }

        private String data_inc;

        private String data_atual;


}

