package com.ipev.controle_material;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ipev.controle_material.Adapters.AdapterListaInventarios;
import com.ipev.controle_material.Model.InventarioModel;
import com.ipev.controle_material.Model.SelectInventario;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class acompanha_inventarios extends AppCompatActivity implements SelectInventario {

    private RecyclerView recyclerView;

    FrameLayout frame_aviso;

    String usuario;

    DatabaseReference database;


    AdapterListaInventarios adapter;

    ConstraintLayout constraintVer_Estat;

    ConstraintLayout constraintRealizaInv;

    boolean select = false;

    CardView cardView;

    ProgressBar loading;

    long quantidadeItens;

    long quantidadeItensChecados;

    ProgressBar progressBar;
    TextView inventario, criador, data_atual, data_criado, porcentagem_checados, sem_Dados;

    String nome_inventario, nome_criador;


    ArrayList<InventarioModel> projetos_Inventarios;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acompanha_inventarios);

        Intent intent = getIntent();
        usuario = intent.getStringExtra("USERNAME");


        cardView = findViewById(R.id.cardView2);
        loading = findViewById(R.id.progress_busca_bmp);
        frame_aviso = findViewById(R.id.frame_aviso);
        inventario = findViewById(R.id.text_nome_inventario);
        criador = findViewById(R.id.text_criado);
        data_atual = findViewById(R.id.textdata_atual);
        data_criado = findViewById(R.id.textdata_inc);
        sem_Dados = findViewById(R.id.frase_sem_dados);
        porcentagem_checados = findViewById(R.id.text_porcentagem);
        inventario.setText("Inventário: ");
        criador.setText("Criado por:");
        data_criado.setText("");
        data_atual.setText("");
        database = FirebaseDatabase.getInstance().getReference("Inventarios");

        constraintRealizaInv = findViewById(R.id.constraintRealizarInventario);
        constraintVer_Estat = findViewById(R.id.constraintEstatisticas);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView_projetos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        projetos_Inventarios = new ArrayList<>();

        buscarDados();
        buscarDBMain();


        adapter = new AdapterListaInventarios(projetos_Inventarios, this);
        recyclerView.setAdapter(adapter);


        constraintRealizaInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (select){

                    Intent intent = new Intent(acompanha_inventarios.this, realizar_inventario.class);
                    intent.putExtra("nome_inventario", nome_inventario);

                    intent.putExtra("USERNAME", usuario);
                    startActivity(intent);
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(acompanha_inventarios.this);
                    builder.setTitle("Aviso");
                    builder.setMessage("Selecione um Inventário para iniciar ");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }



            }
        });

        constraintVer_Estat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (select){

                    Intent intent = new Intent(acompanha_inventarios.this, Estatisticas.class);
                    intent.putExtra("nome_inventario", nome_inventario);

                    startActivity(intent);
                } else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(acompanha_inventarios.this);
                    builder.setTitle("Aviso");
                    builder.setMessage("Selecione um Inventário para ver as Estatísticas");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                }



            }
        });

    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void buscarDados() {

        loading.setVisibility(View.VISIBLE);

        if (!isConnectedToInternet()) {
            // Se não houver conexão, exibe uma mensagem após 10 segundos
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(acompanha_inventarios.this);
                    builder.setTitle("ERRO");
                    builder.setIcon(R.drawable.error);
                    builder.setMessage("Verifique sua conexão com a internet ou reinicie o app");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    loading.setVisibility(View.INVISIBLE);
                }
            }, 5000); // 10 segundos de atraso
            return;
        }

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                projetos_Inventarios.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    InventarioModel inventarioModel = dataSnapshot.getValue(InventarioModel.class);

                    projetos_Inventarios.add(inventarioModel);


                }

                adapter.notifyDataSetChanged();
                loading.setVisibility(View.INVISIBLE);

                if (projetos_Inventarios.isEmpty()) {

                    frame_aviso.setVisibility(View.VISIBLE);
                    sem_Dados.setVisibility(View.VISIBLE);
                } else {

                    frame_aviso.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });






    }


    private void  buscarDBMain(){

        database = FirebaseDatabase.getInstance().getReference("Banco_Dados_IPEV").child("itens");


        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quantidadeItens = dataSnapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Trate o erro aqui
                Log.e("Firebase", "Erro ao obter dados", databaseError.toException());
            }
        });
    }

    private void buscarDBInventarios(String name_inventario){

        database = FirebaseDatabase.getInstance().getReference(name_inventario);

        database = FirebaseDatabase.getInstance().getReference("Inventarios").child(nome_inventario).child("itens");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quantidadeItensChecados = dataSnapshot.getChildrenCount();


                atualizarProgressBAr(quantidadeItensChecados);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Trate o erro aqui
                Log.e("Firebase", "Erro ao obter dados", databaseError.toException());
            }
        });
    }


    @Override
    public void OnItemClicked(InventarioModel inventarioModel) {

        select = true;
        nome_inventario = inventarioModel.getNome();
        nome_criador = inventarioModel.getCriado();
        inventario.setText("Inventário: "+ inventarioModel.getNome());
        criador.setText("Criado por: "+inventarioModel.getCriado());
        data_criado.setText(inventarioModel.getData_inc());
        data_atual.setText(inventarioModel.getData_atual());

        buscarDBInventarios(inventarioModel.getNome());

    }


    public void atualizarProgressBAr(long qtd) {
        double result = ((double) qtd /quantidadeItens ) * 100 ;
        DecimalFormat df = new DecimalFormat("#.##");
        porcentagem_checados.setText(df.format(result) + "%");
        progressBar.setProgress((int) result, true);
        updateProgressBarColor(result);
    }

    private void updateProgressBarColor(double progress) {
        int green = (int) (255 * (progress / 100));
        int red = 255 - green;
        int color = Color.rgb(red, green, 0); // Mistura de vermelho e verde com base na porcentagem

        progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void apagarBancoDeDados(String nome_inventario) {
        // Obtém uma referência para o nó raiz do banco de dados
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Remove todos os dados do nó raiz
        rootRef.child(nome_inventario).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Banco de dados apagado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Falha ao apagar o banco de dados", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void DeleteItemClicked(InventarioModel inventarioModel) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja deletar o Projeto do :  " + inventarioModel.getNome() + ", criado por " + inventarioModel.getCriado() + "?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                database = FirebaseDatabase.getInstance().getReference().child("Inventarios");

                database.child(inventarioModel.getNome()).removeValue();

                adapter.notifyDataSetChanged();

                apagarBancoDeDados(inventarioModel.getNome());



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
