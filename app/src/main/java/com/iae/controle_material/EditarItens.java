
package com.iae.controle_material;

        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Color;
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
        import android.widget.ProgressBar;
        import android.widget.Toast;

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
        import com.google.firebase.database.Query;
        import com.google.firebase.database.ValueEventListener;
        import com.iae.controle_material.Model.ItensModel;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.Objects;
        import java.util.concurrent.CountDownLatch;


public class EditarItens extends AppCompatActivity {

    Button salvar;

    CardView cardView;

    public ArrayList<ItensModel> itensModelArrayList;

    ProgressBar loading;

    AutoCompleteTextView auto_setor;
    AutoCompleteTextView auto_predio;
    AutoCompleteTextView auto_estado;

    TextInputEditText txt_sala , txt_bmp , txt_obs, txt_desc;

    ArrayAdapter<String> adapter_predio;

    ArrayAdapter<String> adapter_setor;

    DatabaseReference database;

    ArrayAdapter<String> adapter_estado;

    String nome_predio, nome_setor, nome_estado;

    int Id_child;

    int tipo;

    int BMP;

    AutoCompleteTextView autoSala;

    CheckBox sem_sala_check;

    String str_setor, str_predio, str_sala, str_obs, str_desc, str_bmp, str_estado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editar_itens);

        getDadosIntent();


        String[] salasuggestions = {"001", "BANHEIRO FEMININO", "BOX 01", "BOX 02", "BOX 03", "BOX 04", "BOX 05", "COPA", "CORREDOR", "CORREDOR E ÁREA EXTERNA", "CORREDOR SUPERIOR", "FUNDOS", "HALL ENTRADA", "LAME", "LAPM-SC", "LAPR", "LATERAL (ÁREA ESTUFA SEGREGADA)", "SALA 01", "SALA 02", "SALA 03", "SALA 04", "SALA 05", "SALA 06", "SALA 07", "SALA 07 (COPA)", "SALA 09", "SALA 1", "SALA 10", "SALA 101", "SALA 102", "SALA 103", "SALA 103 (ALMOXARIFADO)", "SALA 104", "SALA 106", "SALA 108", "SALA 109", "SALA 11", "SALA 110", "SALA 111", "SALA 112", "SALA 113", "SALA 114", "SALA 115", "SALA 116", "SALA 117", "SALA 118","SALA 119", "SALA 12", "SALA 12 (APM)", "SALA 120", "SALA 120 (COPA)", "SALA 121", "SALA 123", "SALA 13", "SALA 14", "SALA 15", "SALA 16", "SALA 17", "SALA 19", "SALA 2", "SALA 20", "SALA 3", "SALA 4", "SALA 5 - ALMOXARIFADO", "SALA 7", "SALA 8", "SALA 8 - COPA", "SALA 9", "SALA DE ESTOCAGEM - PISO SUPERIOR", "SALA DO SERVIDOR", "SEM SALA", "WC FEMININO, WC MASCULINO", "ÁREA EXTERNA", "ÁREA EXTERNA (AO LADO DO PRÉDIO DA AIE)", "ÁREA EXTERNA (EM FRENTE SALA ESTUFAS)"};

        String[] setores= {"APR-P" ,"APR-PPP", "APR-PSC", "LAAI" ,"LAAQ", "LAII", "LAME","LAPM", "LAPR", "LAPT", "LASI" ,"OUTRO"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, salasuggestions);

        auto_setor = findViewById(R.id.auto_complete_setor_edit);
        auto_predio = findViewById(R.id.auto_complete_predio_edit);
        auto_estado = findViewById(R.id.auto_complete_estado_edit);
        autoSala = findViewById(R.id.text_edit_sala_edit);
        //txt_sala = findViewById(R.id.text_edit_sala_edit);
        autoSala.setAdapter(adapter);
        autoSala.setThreshold(1);
        autoSala.setFilters(new InputFilter[] {
                new InputFilter.AllCaps()
        });
        autoSala.setText(str_sala);

        txt_bmp = findViewById(R.id.text_edit_bmp_edit);
        txt_bmp.setText(str_bmp);
        txt_desc = findViewById(R.id.text_edit_desc_edit);
        txt_desc.setText(str_desc);
        txt_desc.setEnabled(false);
        txt_bmp.setEnabled(false);
        txt_obs = findViewById(R.id.text_edit_obs_edit);
        txt_obs.setText(str_obs);
        sem_sala_check = findViewById(R.id.checkBox3);
        auto_setor.setText(str_setor);
        loading = findViewById(R.id.progress_editar);
        cardView = findViewById(R.id.cardview_edit);

        adapter_setor = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, setores);

        auto_setor.setAdapter(adapter_setor);


        auto_setor.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                String item = adapterView.getItemAtPosition(i).toString();

                nome_setor = item;

            }
        });

        String[] predios= {"E0031", "E0033","E0037", "E0037(NOVO)","E0042","E0043","E0044","E0045","E0046","E0050","E0051","E0053","E0076","E0077",};

        auto_predio.setText(str_predio);
        adapter_predio = new ArrayAdapter<String>(this, R.layout.list_item, predios);

        auto_predio.setAdapter(adapter_predio);
        sem_sala_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sem_sala_check.isChecked()) {
                    autoSala.setText("SEM SALA");
                } else {
                    autoSala.setText("");
                }
            }
        });


        auto_predio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                String item = adapterView.getItemAtPosition(i).toString();

                nome_predio = item;

            }
        });

        String[] estado= {"Em utilização", "Separado para Descarga","Emprestado", "Tranferido", "Sem nº de BMP"};
        auto_estado.setText(str_estado);
        adapter_estado = new ArrayAdapter<String>(this, R.layout.list_item, estado);

        auto_estado.setAdapter(adapter_estado);


        auto_estado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                String item = adapterView.getItemAtPosition(i).toString();

                nome_estado = item;

            }
        });



        salvar = findViewById(R.id.btn_salvar_item);

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                str_bmp = txt_bmp.getText().toString();
                str_sala = autoSala.getText().toString();
                str_desc = txt_desc.getText().toString();
                str_obs = txt_obs.getText().toString();
                str_estado = auto_estado.getText().toString();
                str_predio = auto_predio.getText().toString();
                str_setor = auto_setor.getText().toString();

                if (!TextUtils.isEmpty(str_sala) && !TextUtils.isEmpty(str_bmp) && !TextUtils.isEmpty(str_desc)){
                    loading.setVisibility(View.VISIBLE);
                    cardView.setAlpha(0.3f);
                    addCadastro();
                } else {

                    if (TextUtils.isEmpty(str_bmp)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditarItens.this);
                        builder.setTitle("Aviso");
                        builder.setMessage("Insira o número de BMP");
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else if (TextUtils.isEmpty(str_sala)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditarItens.this);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditarItens.this);
                        builder.setTitle("Aviso");
                        builder.setMessage("Insira a Descrição do Item");
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }



            }
        });



    }

    public void addCadastro(){

        Map<String, Object> map = new HashMap<>();

        map.put("BMP", str_bmp);
        map.put("setor", str_setor);
        map.put("predio", str_predio);
        map.put("sala", str_sala);
        map.put("estado", str_estado);
        map.put("descricao", str_desc);
        map.put("observacao", str_obs);

        FirebaseDatabase.getInstance().getReference().child("itens")
                .child(String.valueOf(Id_child))
                .updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                loading.setVisibility(View.INVISIBLE);
                                Toast.makeText(getBaseContext(), "ITEM EDITADO COM SUCESSO", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditarItens.this);
                        builder.setTitle("Aviso");
                        builder.setMessage("Erro ao editar o item");
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

    }


    private void buscarDadosCadastrados(){

        itensModelArrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference("itens");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itensModelArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ItensModel itensModel = dataSnapshot.getValue(ItensModel.class);
                    itensModelArrayList.add(itensModel);

                }

                getDadosInsereEditar();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });


    }

    public void getDadosInsereEditar(){


        for (ItensModel item : itensModelArrayList) {

            String numero_bmp = String.valueOf(BMP);

            if (numero_bmp.equals(item.getBMP())){

                Id_child = item.getId();
                txt_sala.setText(item.getSala());
                txt_bmp.setText(item.getBMP());
                txt_desc.setText(item.getDescricao());
                txt_desc.setEnabled(false);
                txt_bmp.setEnabled(false);
                txt_obs.setText(item.getObservacao());
                auto_predio.setText(item.getPredio());
                auto_estado.setText(item.getEstado());
                auto_setor.setText(item.getSetor());

            } else {
                Log.d("", "nenhum encontrado");
            }
        }
    }
    public void getDadosIntent(){

        Intent intent = getIntent();
        tipo = intent.getIntExtra("alterar", 0);

        if (tipo == 1) {

            BMP = intent.getIntExtra("bmp", 0);
            buscarDadosCadastrados();


        } else {
            Id_child = intent.getIntExtra("id", 0);
            str_setor =  intent.getStringExtra("setor");
            str_predio = intent.getStringExtra("predio");
            str_sala = intent.getStringExtra("sala");
            str_bmp = intent.getStringExtra("bmp");
            str_desc = intent.getStringExtra("descricao");
            str_obs = intent.getStringExtra("observacao");
            str_estado = intent.getStringExtra("estado");
        }



    }
}
