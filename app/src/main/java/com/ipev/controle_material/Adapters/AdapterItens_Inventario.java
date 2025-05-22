package com.ipev.controle_material.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ipev.controle_material.EditarItens;
import com.ipev.controle_material.Model.Data_bmp;
import com.ipev.controle_material.Model.ItensModel;
import com.ipev.controle_material.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AdapterItens_Inventario extends RecyclerView.Adapter<AdapterItens_Inventario.MyViewHolder> {

    private Context context;

    private ArrayList<ItensModel> list;

    private String nome_inventario;

    String status_usuario;

    FirebaseDatabase database;

    DatabaseReference databaseReference;;
    ArrayList<String> itens_checados;



    public void filterList (ArrayList<ItensModel> filterListSetor){
        this.list = filterListSetor;
        notifyDataSetChanged();
    }

    public void setFilterBMP(ArrayList<ItensModel> filterBMP){
        this.list = filterBMP;
        notifyDataSetChanged();
    }

    public AdapterItens_Inventario(Context context, ArrayList<ItensModel> list, String nome_inventario, String status) {
        itens_checados = new ArrayList<>();
        buscarDados(nome_inventario);
        this.context = context;
        this.nome_inventario = nome_inventario;
        this.list = list;
        this.status_usuario = status;
    }




    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_itens_inventario, parent, false);
        return new MyViewHolder(view);
    }


    private void buscarDados(String nome_inventario) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Inventarios").child(nome_inventario).child("itens");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                itens_checados.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Data_bmp dataBmp = dataSnapshot.getValue(Data_bmp.class);
                    itens_checados.add(dataBmp.getBMP());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        buscarDados(nome_inventario);
        ItensModel itensModel = list.get(position);
        holder.valor_predio.setText(itensModel.getPredio());
        holder.valor_sala.setText(itensModel.getSala());
        holder.valor_bmp.setText(itensModel.getBMP());
        holder.valor_obs.setText(itensModel.getObservacao());
        holder.valor_setor.setText(itensModel.getSetor());
        holder.valor_desc.setText(itensModel.getDescricao());
        holder.valor_num_serie.setText(itensModel.getSerial());


        if (status_usuario.equals("admin")) {
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

        if (itens_checados.contains(itensModel.getBMP())){

            holder.cardEncontrado.setCardBackgroundColor(Color.parseColor("#4CAF50"));
            holder.cardNaoEncontrado.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.checkText.setTextColor(Color.parseColor("#FFFFFF"));
            holder.noCheckText.setTextColor(Color.parseColor("#9C9C9C"));
            holder.checkImage.setColorFilter(ContextCompat.getColor(context, R.color.white));
            holder.noCheckImagem.setColorFilter(ContextCompat.getColor(context, R.color.cinza_claro));

            //holder.check.setBackgroundColor(Color.parseColor("#4CAF50"));
            //holder.check.setText("ITEM ENCONTRADO");
        } else {

            holder.cardEncontrado.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.cardNaoEncontrado.setCardBackgroundColor(Color.parseColor("#BC2727"));
            holder.checkText.setTextColor(Color.parseColor("#9C9C9C"));
            holder.noCheckText.setTextColor(Color.parseColor("#FFFFFF"));
            holder.checkImage.setColorFilter(ContextCompat.getColor(context, R.color.cinza_claro));
            holder.noCheckImagem.setColorFilter(ContextCompat.getColor(context, R.color.white));

        }

        holder.cardEncontrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference = FirebaseDatabase.getInstance().getReference("Inventarios").child(nome_inventario).child("itens");

                Query query = databaseReference.orderByChild("BMP").equalTo(itensModel.getBMP());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            databaseReference.push().child("BMP").setValue(itensModel.getBMP())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            holder.cardEncontrado.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                                            holder.cardNaoEncontrado.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                                            holder.checkText.setTextColor(Color.parseColor("#FFFFFF"));
                                            holder.noCheckText.setTextColor(Color.parseColor("#9C9C9C"));
                                            holder.checkImage.setColorFilter(ContextCompat.getColor(context, R.color.white));
                                            holder.noCheckImagem.setColorFilter(ContextCompat.getColor(context, R.color.cinza_claro));
                                            Toast.makeText(context, "ITEM CHECADO", Toast.LENGTH_SHORT).show();
                                            AtualizaDataDB();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "ERRO AO CHECAR ITEM, TENTE NOVAMENTE", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manipule o erro, se necessário
                    }
                });

            }
        });

        holder.cardNaoEncontrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference = FirebaseDatabase.getInstance().getReference("Inventarios").child(nome_inventario).child("itens");

                Query query = databaseReference.orderByChild("BMP").equalTo(itensModel.getBMP());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Deseja remover o material do inventário ?");

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        // Recupere a chave do nó que contém o valor a ser excluído
                                        String key = snapshot.getKey();

                                        databaseReference.child(key).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(context, "ITEM REMOVIDO", Toast.LENGTH_SHORT).show();
                                                        holder.cardEncontrado.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                                                        holder.cardNaoEncontrado.setCardBackgroundColor(Color.parseColor("#BC2727"));
                                                        holder.checkText.setTextColor(Color.parseColor("#9C9C9C"));
                                                        holder.noCheckText.setTextColor(Color.parseColor("#FFFFFF"));
                                                        holder.checkImage.setColorFilter(ContextCompat.getColor(context, R.color.cinza_claro));
                                                        holder.noCheckImagem.setColorFilter(ContextCompat.getColor(context, R.color.white));
                                                        AtualizaDataDB();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context, "FALHA AO REMOVER O ITEM, TENTE NOVAMENTE", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                        // Remova o nó usando a chave recuperada

                                    }

                                }
                            });

                            builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel(); // Fecha o AlertDialog
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manipule o erro, se necessário
                    }
                });

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void AtualizaDataDB(){


        Date dataAtual = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = dateFormat.format(dataAtual);
        Map<String, Object> map = new HashMap<>();

        map.put("data_atual",dataFormatada );

        FirebaseDatabase.getInstance().getReference().child("Inventarios")
                .child(nome_inventario)
                .updateChildren(map);

    }



    public static class MyViewHolder extends  RecyclerView.ViewHolder {

        TextView valor_predio, valor_setor, valor_bmp , valor_obs, valor_sala, valor_desc, valor_num_serie, checkText, noCheckText;
        Button checkON, checkOFF, editar;
        CardView cardEncontrado , cardNaoEncontrado;
        ImageView checkImage, noCheckImagem;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            checkImage = itemView.findViewById(R.id.icon_encontrado);
            noCheckImagem = itemView.findViewById(R.id.icon_nao_encontrado);
            checkText = itemView.findViewById(R.id.icon_text_encontrado);
            noCheckText = itemView.findViewById(R.id.icon_text_nao_encontrado);
            cardEncontrado = itemView.findViewById(R.id.card_encontrado);
            cardNaoEncontrado = itemView.findViewById(R.id.card_nao_encontrado);
            valor_predio = itemView.findViewById(R.id.valor_predio);
            valor_setor = itemView.findViewById(R.id.valor_setor);
            valor_bmp = itemView.findViewById(R.id.valor_bmp);
            valor_obs = itemView.findViewById(R.id.valor_obs);
            valor_sala = itemView.findViewById(R.id.valor_sala);
            valor_desc = itemView.findViewById(R.id.valor_descricao);
            editar = itemView.findViewById(R.id.btn_editar_itens);
            valor_num_serie = itemView.findViewById(R.id.valor_num_serie);





        }
    }
}