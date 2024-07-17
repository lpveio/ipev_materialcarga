package com.iae.controle_material;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.iae.controle_material.Model.InventarioModel;

import com.iae.controle_material.Model.SelectInventario;

import java.util.ArrayList;


public class AdapterListaInventarios extends RecyclerView.Adapter<AdapterListaInventarios.ViewHolder> {

    private ArrayList<InventarioModel> list;

    private Context context;

    private int selectedItem = RecyclerView.NO_POSITION;

    private SelectInventario listener;



    public AdapterListaInventarios(ArrayList<InventarioModel> list, SelectInventario listener) {
        this.listener = listener;
        this.list = list;
    }

    public void atualizarDados(ArrayList<InventarioModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_proj_inventarios, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventarioModel inventarioModel = list.get(position);

        holder.nome_inventario.setText(inventarioModel.getNome());
        holder.criador.setText(inventarioModel.getCriado());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousSelectedItem = selectedItem;
                selectedItem = holder.getAdapterPosition();
                notifyItemChanged(previousSelectedItem);

                notifyItemChanged(selectedItem);
                listener.OnItemClicked(list.get(holder.getAdapterPosition()));

            }
        });

        holder.apagar_projeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null){

                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        listener.DeleteItemClicked(list.get(holder.getAdapterPosition()));
                    }
                }

            }

        });

        if (position == selectedItem) {
            holder.linearLayout.setBackgroundColor(Color.parseColor("#9C9C9C")); // Cor de fundo quando selecionado
        } else {
            holder.linearLayout.setBackgroundColor(Color.TRANSPARENT); // Cor de fundo padrão
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome_inventario, criador;

        ImageView apagar_projeto;

        CardView cardView;

        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            apagar_projeto = itemView.findViewById(R.id.apagar_projeto);
            nome_inventario = itemView.findViewById(R.id.valor_inventario);
            criador = itemView.findViewById(R.id.valor_criado);
            cardView = itemView.findViewById(R.id.card_projetos);
            linearLayout = itemView.findViewById(R.id.linear_projetos_calib);

        }
    }
}


