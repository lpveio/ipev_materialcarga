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

        String[] setores = {"APR-P", "APR-PPP", "APR-PSC", "LAAI", "LAAQ", "LAII", "LAME", "LAPM", "LAPR", "LAPT", "LASI", "OUTRO", "?"};

        Intent intent = getIntent();
        tipo = intent.getIntExtra("inserir", 0);
        database = FirebaseDatabase.getInstance();
        auto_setor = findViewById(R.id.auto_complete_setor);
        auto_predio = findViewById(R.id.auto_complete_predio);
        auto_estado = findViewById(R.id.auto_complete_estado);
        txt_sala = findViewById(R.id.text_edit_sala);
        txt_bmp = findViewById(R.id.text_edit_bmp);
        txt_desc = findViewById(R.id.text_edit_desc);
        txt_sn = findViewById(R.id.text_edit_serial);
        txt_obs = findViewById(R.id.text_edit_obs);
        sem_sala_check = findViewById(R.id.checkBox2);
        loading = findViewById(R.id.progress_cadastrar);
        cardView = findViewById(R.id.cardView5);

        if (tipo == 1) {
            txt_bmp.setText("" + intent.getIntExtra("bmp", 0));
            txt_desc.setText(intent.getStringExtra("desc"));
        }

        sem_sala_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sem_sala_check.isChecked()) {
                    txt_sala.setText("SEM SALA");
                } else {
                    txt_sala.setText("");
                }
            }
        });
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

        String[] predios = {"E0031", "E0033", "E0037", "E0037 B", "E0042", "E0043", "E0044", "E0045", "E0046", "E0050", "E0051", "E0053", "E0076", "E0077",};


        auto_predio.setEnabled(false);


        adapter_predio = new ArrayAdapter<String>(this, R.layout.list_item, predios);

        auto_predio.setAdapter(adapter_predio);

        auto_predio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                String item = adapterView.getItemAtPosition(i).toString();

                nome_predio = item;

                auto_estado.setEnabled(true);

            }
        });

        String[] estado = {"Em utilização", "Separado para Descarga", "Emprestado", "Tranferido", "Sem nº de BMB"};


        auto_estado.setEnabled(false);

        adapter_estado = new ArrayAdapter<String>(this, R.layout.list_item, estado);

        auto_estado.setAdapter(adapter_estado);

        auto_estado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                String item = adapterView.getItemAtPosition(i).toString();

                nome_estado = item;

                txt_sala.setEnabled(true);
                txt_bmp.setEnabled(true);
                txt_obs.setEnabled(true);
                txt_sn.setEnabled(true);
                txt_desc.setEnabled(true);


            }
        });


        txt_sala.setEnabled(false);

        txt_sala.setFilters(new InputFilter[]{
                new InputFilter.AllCaps()
        });


        txt_bmp.setEnabled(false);
        txt_sn.setEnabled(false);
        txt_obs.setEnabled(false);
        txt_obs.setEnabled(false);

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
            buscarDados();
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
                builder.setMessage("Insira a SALA que está alocado, ou caso o item não esteje em nenhuma sala clique em SEM SALA");
                builder.setPositiveButton("SEM SALA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txt_sala.setText("SEM SALA");
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
        databaseReference = database.getReference("Banco_BMP");

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

    public void addCadastro(long quantidadeItens) {

        quantidadeItens++;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Banco_BMP").child(String.valueOf(quantidadeItens));
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