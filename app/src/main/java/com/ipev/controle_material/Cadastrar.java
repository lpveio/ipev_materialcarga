package com.ipev.controle_material;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ipev.controle_material.Model.ItensModel;
import com.ipev.controle_material.Model.VersaoManager;
import com.ipev.controle_material.Model.siloms_itens;

import java.util.ArrayList;


public class Cadastrar extends AppCompatActivity {

    Button salvar;

    DatabaseReference databaseReference;

    AutoCompleteTextView auto_setor;
    AutoCompleteTextView auto_predio;
    AutoCompleteTextView auto_estado;

    CheckBox sem_sala_check;

    TextInputEditText txt_sala, txt_bmp, txt_obs, txt_desc, txt_sn;

    ArrayAdapter<String> adapter_predio;

    ArrayAdapter<String> adapter_setor;

    ProgressBar loading;

    ArrayAdapter<String> adapter_estado;

    String nome_predio, nome_setor, nome_estado;

    CardView cardView;

    String numero_bmp;

    String numero_serie;
    String text_sala;
    String descricao_item;
    String obs_item;

    int tipo;


    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cadastrar);


        String[] setores = getResources().getStringArray(R.array.setores);


        Intent intent = getIntent();
        tipo = intent.getIntExtra("inserir", 0);
        database = FirebaseDatabase.getInstance();
        auto_setor = findViewById(R.id.auto_complete_setor);
        auto_predio = findViewById(R.id.auto_complete_predio);
        txt_sala = findViewById(R.id.text_edit_sala);
        txt_bmp = findViewById(R.id.text_edit_bmp);
        txt_desc = findViewById(R.id.text_edit_desc);
        txt_sn = findViewById(R.id.text_edit_serial);
        txt_obs = findViewById(R.id.text_edit_obs);
        loading = findViewById(R.id.progress_cadastrar);
        cardView = findViewById(R.id.cardView5);

        if (tipo == 1) {
            txt_bmp.setText("" + intent.getIntExtra("bmp", 0));
            txt_desc.setText(intent.getStringExtra("desc"));
        }

        adapter_setor = new ArrayAdapter<String>(this, R.layout.list_item, setores);

        auto_setor.setAdapter(adapter_setor);

        auto_setor.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                String item = adapterView.getItemAtPosition(i).toString();

                nome_setor = item;
                auto_predio.setEnabled(true);

            }
        });



        auto_predio.setEnabled(false);


        String[] predios = getResources().getStringArray(R.array.predios);

        adapter_predio = new ArrayAdapter<String>(this, R.layout.list_item, predios);

        auto_predio.setAdapter(adapter_predio);

        auto_predio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                String item = adapterView.getItemAtPosition(i).toString();

                nome_predio = item;

            }
        });

        txt_sala.setFilters(new InputFilter[]{
                new InputFilter.AllCaps()
        });

        salvar = findViewById(R.id.btn_salvar_cadastro);

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testaCamposVazios();
            }
        });


    }

    private void testaCamposVazios() {

        numero_bmp = txt_bmp.getText().toString();
        text_sala = txt_sala.getText().toString();
        descricao_item = txt_desc.getText().toString();
        obs_item = txt_obs.getText().toString();
        numero_serie = txt_sn.getText().toString();

        if (!TextUtils.isEmpty(text_sala) && !TextUtils.isEmpty(numero_bmp) && !TextUtils.isEmpty(descricao_item)) {
            loading.setVisibility(View.VISIBLE);
            cardView.setAlpha(0.3f);
           BuscarUltimo();
        } else {

            if (TextUtils.isEmpty(numero_bmp)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Cadastrar.this);
                builder.setTitle("Aviso");
                builder.setMessage("Insira o número de BMP");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else if (TextUtils.isEmpty(text_sala)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Cadastrar.this);
                builder.setTitle("Aviso");
                builder.setMessage("Insira a SALA que está alocado");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Cadastrar.this);
                builder.setTitle("Aviso");
                builder.setMessage("Insira a descrição do Item");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        }

    }

    private void buscarDados() {

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Banco_Dados_IPEV").child("itens");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long quantidadeItens = dataSnapshot.getChildrenCount();

                addCadastro(quantidadeItens);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Trate o erro aqui
                Log.e("Firebase", "Erro ao obter dados", databaseError.toException());
            }
        });
    }


    public void BuscarUltimo() {
        DatabaseReference itensRef = FirebaseDatabase.getInstance().getReference("Banco_Dados_IPEV").child("itens");

        itensRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String ultimaChave = child.getKey(); // Isso retorna "3"

                    addCadastro(Long.parseLong(ultimaChave));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Erro: " + error.getMessage());
            }
        });
    }

    private void atualizaVersaoFirebase() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Banco_Dados_IPEV").child("timestamp");
        database.setValue(VersaoManager.gerarVersaoTimestamp());
    }

    public void addCadastro(long quantidadeItens) {

        quantidadeItens++;

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Banco_Dados_IPEV").child("itens").child(String.valueOf(quantidadeItens));

        databaseReference.child("id").setValue(quantidadeItens);
        databaseReference.child("BMP").setValue(numero_bmp);
        databaseReference.child("setor").setValue(nome_setor);
        databaseReference.child("predio").setValue(nome_predio);
        databaseReference.child("sala").setValue(text_sala);
        databaseReference.child("estado").setValue(nome_estado);
        databaseReference.child("observacao").setValue(obs_item);
        databaseReference.child("descricao").setValue(descricao_item);
        databaseReference.child("serial").setValue(numero_serie)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        loading.setVisibility(View.INVISIBLE);
                        cardView.setAlpha(1f);

                        AlertDialog.Builder builder = new AlertDialog.Builder(Cadastrar.this);
                        builder.setTitle("CADASTRADO");
                        builder.setIcon(R.drawable.ok);
                        builder.setMessage("Item cadastrado com sucesso !");
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        atualizaVersaoFirebase();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "ERRO AO CADRASTRAR O ITEM, TENTE NOVAMENTE", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}