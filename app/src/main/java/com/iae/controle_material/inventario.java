package com.iae.controle_material;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iae.controle_material.Model.InventarioModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class inventario extends AppCompatActivity {

    CardView novo_inventario, inventario, relatorios;

    DatabaseReference databaseReference;

    String nome_do_inventario;

    AlertDialog dialog;

    String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inventario);

        Intent intent = getIntent();
        usuario = intent.getStringExtra("USERNAME");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        novo_inventario = findViewById(R.id.card1);
        inventario = findViewById(R.id.card2);
        relatorios = findViewById(R.id.card3);

        novo_inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usuario.equals("admin")) {
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(inventario.getContext());
                    builder.setTitle("Aviso");
                    builder.setIcon(R.drawable.error);
                    builder.setMessage("Você não tem permissão para criar novo inventário");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }
        });

        relatorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(inventario.this, pendencias.class);
                startActivity(intent);
            }
        });

        inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(inventario.this, acompanha_inventarios.class);
                intent.putExtra("USERNAME", usuario);
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("DIGITE OS DADOS ABAIXO");

        View view =  getLayoutInflater().inflate(R.layout.custom_novo_inventario, null);
        EditText edt_inventario, edt_nome;
        edt_inventario = view.findViewById(R.id.nome_inventario_edt);
        edt_nome = view.findViewById(R.id.nome_criador_edt);
        Button save = view.findViewById(R.id.btn_salvar_inv);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertStringToDatabase(edt_inventario.getText().toString(), edt_nome.getText().toString());
                dialog.dismiss();
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.getWindow().setDimAmount(0.9f);

    }

    private void insertStringToDatabase(String string , String nome) {

        Date dataAtual = Calendar.getInstance().getTime();
        // Formatar a data para o formato desejado
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = dateFormat.format(dataAtual);

        // Create a new data object with the string and key
        InventarioModel data = new InventarioModel(string, nome, dataFormatada, dataFormatada );

        // Set the data at the new child node
        databaseReference.child("Inventarios").child(string).setValue(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getBaseContext(), "INVENTÁRIO CRIADO COM SUCESSO", Toast.LENGTH_SHORT).show();
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "ERRO AO CRIAR UM NOVO ARQUIVO DE INVENTÁRIO, TENTE NOVAMENTE", Toast.LENGTH_SHORT).show();
                    }
                });




    }

    // Define the Data class


    private void novoInventario(){

        final EditText editText = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Digite o nome do inventário que deseje realizar:  ");

        View view =  getLayoutInflater().inflate(R.layout.custom_novo_inventario, null);
        EditText edt_inventario, edt_nome;
        edt_inventario = view.findViewById(R.id.nome_inventario_edt);
        edt_nome = view.findViewById(R.id.nome_criador_edt);
        Button save = view.findViewById(R.id.btn_salvar_inv);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insertStringToDatabase(edt_inventario.getText().toString(), edt_nome.getText().toString());
                dialog.dismiss();
            }
        });

        builder.setView(view);
        dialog = builder.create();
    }
}