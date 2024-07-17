package com.iae.controle_material;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iae.controle_material.Model.ExcelEditor;
import com.iae.controle_material.Model.ItensModel;
import com.iae.controle_material.Model.siloms_itens;

import java.util.ArrayList;

public class resultado_pendencias extends AppCompatActivity {

    ArrayList<Integer> BMP_exclusivos, BMP_Cadastrados;



    String nome_inventario;

    int tipo;

    RecyclerView recyclerView;

    ArrayList<siloms_itens> siloms_list;

    Button gerar_relatorio;

    AdapterBMP adapterItens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resultado_pendencias);
        getDadosIntent();
        recyclerView = findViewById(R.id.recycler_resultado);
        gerar_relatorio = findViewById(R.id.btn_gerar_relatorio);
        adapterItens = new AdapterBMP(this, siloms_list, BMP_exclusivos, tipo, BMP_Cadastrados);
        recyclerView.setAdapter(adapterItens);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gerar_relatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExportarLista();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void ExportarLista(){
        ExcelEditor.preencherExcelPendentes(this, siloms_list, nome_inventario);
    }
    public void getDadosIntent(){

        Intent intent = getIntent();
        BMP_exclusivos = intent.getIntegerArrayListExtra("BMP");
        BMP_Cadastrados = intent.getIntegerArrayListExtra("BMP_Cadastrados");


        siloms_list = (ArrayList<siloms_itens>) intent.getSerializableExtra("siloms_lista");
        tipo = intent.getIntExtra("tipo", 0);
        nome_inventario = intent.getStringExtra("nome");


    }

}