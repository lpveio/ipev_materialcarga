package com.ipev.controle_material.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ipev.controle_material.EditarItens;
import com.ipev.controle_material.Model.ItensModel;
import com.ipev.controle_material.R;

import java.util.ArrayList;


public class AdapterItens extends RecyclerView.Adapter<AdapterItens.MyViewHolder> {

    private Context context;

    private ArrayList<ItensModel> list;

    private String usuario;



    public void filterList (ArrayList<ItensModel> filterListSetor){
        this.list = filterListSetor;
        notifyDataSetChanged();
    }

    public void setFilterBMP(ArrayList<ItensModel> filterBMP){
        this.list = filterBMP;
        notifyDataSetChanged();
    }

    public AdapterItens(Context context, ArrayList<ItensModel> list, String usuario) {
        this.context = context;
        this.list = list;
        this.usuario = usuario;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_itens, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ItensModel itensModel = list.get(position);
        holder.valor_predio.setText(itensModel.getPredio());
        holder.valor_sala.setText(itensModel.getSala());
        holder.valor_bmp.setText(itensModel.getBMP());
        holder.valor_obs.setText(itensModel.getObservacao());
        holder.valor_setor.setText(itensModel.getSetor());
        holder.valor_desc.setText(itensModel.getDescricao());
        holder.valor_num_serie.setText(itensModel.getSerial());

        if (usuario.equals("admin")) {
            holder.editar.setVisibility(View.VISIBLE);
            holder.editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, EditarItens.class);
                    intent.putExtra("id", itensModel.getId());
                    intent.putExtra("setor", itensModel.getSetor());
                    intent.putExtra("predio", itensModel.getPredio());
                    intent.putExtra("sala", itensModel.getSala());
                    intent.putExtra("bmp", itensModel.getBMP());
                    intent.putExtra("descricao", itensModel.getDescricao());
                    intent.putExtra("observacao", itensModel.getObservacao());
                    intent.putExtra("estado", itensModel.getEstado());
                    intent.putExtra("serial", itensModel.getSerial());
                    context.startActivity(intent);

                }
            });
        } else {
            holder.editar.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder {

        TextView valor_predio, valor_setor, valor_bmp , valor_obs, valor_sala, valor_desc, valor_num_serie;
        Button editar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            valor_predio = itemView.findViewById(R.id.valor_predio);
            valor_setor = itemView.findViewById(R.id.valor_setor);
            valor_bmp = itemView.findViewById(R.id.valor_bmp);
            valor_obs = itemView.findViewById(R.id.valor_obs);
            valor_sala = itemView.findViewById(R.id.valor_sala);
            valor_desc = itemView.findViewById(R.id.valor_descricao);
            editar = itemView.findViewById(R.id.btn_editar);
            valor_num_serie = itemView.findViewById(R.id.valor_num_serie);

        }
    }
}