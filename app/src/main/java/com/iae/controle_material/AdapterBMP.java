package com.iae.controle_material;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iae.controle_material.Model.ItensModel;
import com.iae.controle_material.Model.siloms_itens;

import java.util.ArrayList;


public class AdapterBMP extends RecyclerView.Adapter<AdapterBMP.MyViewHolder> {

    private Context context;

    private ArrayList<siloms_itens> list;


    private ArrayList<Integer> list_faltantes , bmp_cadastrados;

    int tipo;


    public AdapterBMP(Context context, ArrayList<siloms_itens> list , ArrayList<Integer> list_faltantes, int tipo, ArrayList<Integer> bmp_cadastrados) {
        this.context = context;
        this.list = list;
        this.list_faltantes = list_faltantes;
        this.tipo = tipo;
        this.bmp_cadastrados = bmp_cadastrados;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_bmps, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        siloms_itens siloms = list.get(position);

        holder.desc_valor.setText(siloms.getDescricao());
        holder.bmp_valor.setText(""+ siloms.getBmp());

        if (bmp_cadastrados.contains(siloms.getBmp())) {
            holder.inserir.setEnabled(false);
            holder.alterar.setEnabled(true);
            holder.inserir.setBackgroundColor(context.getResources().getColor(R.color.cinza_claro));
            holder.alterar.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.alterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, EditarItens.class);
                    intent.putExtra("bmp", siloms.getBmp());
                    intent.putExtra("alterar", 1);
                    context.startActivity(intent);
                }
            });
        } else {
            holder.inserir.setEnabled(true);
            holder.alterar.setEnabled(false);
            holder.alterar.setBackgroundColor(context.getResources().getColor(R.color.cinza_claro));
            holder.inserir.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.inserir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, Cadastrar.class);
                    intent.putExtra("desc", siloms.getDescricao());
                    intent.putExtra("bmp", siloms.getBmp());
                    intent.putExtra("inserir", 1);
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder {

        TextView bmp_valor , desc_valor;

        Button inserir;

        Button alterar;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            bmp_valor = itemView.findViewById(R.id.bmb_result);
            desc_valor = itemView.findViewById(R.id.descricao_resultado);
            inserir = itemView.findViewById(R.id.btn_inserir);
            alterar = itemView.findViewById(R.id.btn_alterar);

        }
    }
}