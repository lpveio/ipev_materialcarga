package com.iae.controle_material;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iae.controle_material.Model.ExcelEditor;
import com.iae.controle_material.Model.ItensModel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Buscar_Local extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;

    ProgressBar loading;
    AdapterItens adapterItens;

    AutoCompleteTextView auto_setor, auto_predio , auto_sala;


    ArrayList<ItensModel> itensModelArrayList;

    ArrayList<ItensModel> filteredSetorList;

    ArrayList<ItensModel> filteredPredioList;

    ArrayList<ItensModel> filteredSalaList;

    ArrayList<ItensModel> exportedList;

    ArrayAdapter<String> adapter_predio;

    ArrayAdapter<String> adapter_setor;

    ArrayAdapter<String> adapter_sala;

    ArrayList<String> PrediosList, SetorList, SalaList , SalasTotal;

    public int tipo;

    String usuario;

    Context context;

    String txt_sala , txt_predio , txt_setor;

    String[] setores= {"APR-P" ,"APR-PPP", "APR-PSC", "LAAI" ,"LAAQ", "LAII", "LAME","LAPM", "LAPR", "LAPT", "LASI" ,"OUTRO"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        Intent intent = getIntent();
        usuario = intent.getStringExtra("USERNAME");

        setContentView(R.layout.activity_buscar_local);

        recyclerView = findViewById(R.id.recycler_buscar_local);

        auto_setor = findViewById(R.id.auto_complete_2_setor);
        loading = findViewById(R.id.progress_busca_local);

        auto_predio = findViewById(R.id.auto_complete_2_predio);
        auto_predio.setEnabled(false);

        auto_sala = findViewById(R.id.auto_complete_2_sala);
        auto_sala.setEnabled(false);

        adapter_setor = new ArrayAdapter<String>(this, R.layout.list_item, setores);

        auto_setor.setAdapter(adapter_setor);

        tipo = 0;

        auto_setor.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                auto_predio.setText("");
                auto_sala.setText("");
                String item = adapterView.getItemAtPosition(i).toString();
                txt_setor = item;
                tipo = 1;
                filterSetorList(item);

                auto_predio.setEnabled(true);

            }
        });

        database = FirebaseDatabase.getInstance().getReference("itens");
        filteredSetorList = new ArrayList<>();
        itensModelArrayList = new ArrayList<>();
        filteredPredioList = new ArrayList<>();
        filteredSalaList = new ArrayList<>();
        SetorList = new ArrayList<>();
        PrediosList = new ArrayList<>();
        SalaList = new ArrayList<>();

        adapterItens = new AdapterItens(this, itensModelArrayList, usuario);
        recyclerView.setAdapter(adapterItens);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buscarDados();

    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void buscarDados(){


        loading.setVisibility(View.VISIBLE);

        if (!isConnectedToInternet()) {
            // Se não houver conexão, exibe uma mensagem após 10 segundos
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Buscar_Local.this);
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
                itensModelArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ItensModel itensModel = dataSnapshot.getValue(ItensModel.class);

                    itensModelArrayList.add(itensModel);

                }

                if (tipo == 0){
                    exportedList = itensModelArrayList;

                    SalasTotal = new ArrayList<>();
                    for (ItensModel item : itensModelArrayList) {
                        SalasTotal.add(item.getSala());
                    }
                    removeDuplicates(SalasTotal);
                    Collections.sort(SalasTotal);
                    Log.d("", "onCreate: " + SalasTotal);

                    adapterItens.notifyDataSetChanged();

                } else if (tipo == 1) {
                    filterSetorList(txt_setor);

                } else if (tipo == 2) {
                    filterSetorList(txt_setor);
                    filterPredioList(txt_predio);

                } else if (tipo == 3) {
                    filterSetorList(txt_setor);
                    filterPredioList(txt_predio);
                    filterSalaList(txt_sala);
                }

                loading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    private void SetPredioList(ArrayList<String> predio_list){

        auto_predio.setEnabled(true);

        adapter_predio = new ArrayAdapter<String>(this, R.layout.list_item, predio_list);

        auto_predio.setAdapter(adapter_predio);

        auto_predio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                tipo = 2;
                String item = adapterView.getItemAtPosition(i).toString();
                txt_predio = item;
                auto_sala.setText("");
                filterPredioList(item);


            }
        });

    }

    private void SetSalaList(ArrayList<String> sala_list){

        auto_sala.setEnabled(true);
        adapter_sala = new ArrayAdapter<String>(this, R.layout.list_item, sala_list);
        auto_sala.setAdapter(adapter_sala);
        auto_sala.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                tipo = 3;
                String item = adapterView.getItemAtPosition(i).toString();
                txt_sala = item;
                filterSalaList(item);

            }
        });

    }

    private void filterSetorList(String setor) {

        filteredSetorList.clear();
        PrediosList.clear();

            for (ItensModel itemfilter : itensModelArrayList) {
                if (itemfilter.getSetor().contentEquals(setor)) {
                    filteredSetorList.add(itemfilter);
                }
            }

           exportedList = filteredSetorList;
            if (filteredSetorList.isEmpty()) {
                Toast.makeText(this, "Nenhum Item Encontrado", Toast.LENGTH_SHORT).show();
                recyclerView.setVisibility(View.GONE);
                adapterItens.filterList(null);
            } else {
                recyclerView.setVisibility(View.VISIBLE);

                for (ItensModel local_filter : filteredSetorList) {
                    PrediosList.add(local_filter.getPredio());
                    removeDuplicates(PrediosList);
                    Collections.sort(PrediosList);
                }
                adapterItens.filterList(filteredSetorList);
            }

        SetPredioList(PrediosList);
    }
    private void filterPredioList(String predio) {
        filteredPredioList.clear();
        SalaList.clear();

            for (ItensModel itemfilter : filteredSetorList) {

                if (itemfilter.getPredio().contentEquals(predio)) {
                    filteredPredioList.add(itemfilter);
                }

            }

            exportedList = filteredPredioList;
            if (filteredPredioList.isEmpty()) {
                Toast.makeText(this, "Nenhum Item Encontrado", Toast.LENGTH_SHORT).show();
            } else {

                for (ItensModel local_filter : filteredPredioList) {
                    SalaList.add(local_filter.getSala());
                    removeDuplicates(SalaList);
                    Collections.sort(SalaList);
                }
                adapterItens.filterList(filteredPredioList);
            }

            SetSalaList(SalaList);

    }
    private void filterSalaList(String sala) {
        filteredSalaList.clear();


        for (ItensModel itemfilter : filteredPredioList){

            if (itemfilter.getSala().contentEquals(sala)){
                filteredSalaList.add(itemfilter);
            }
        }
        exportedList = filteredSalaList;
        if(filteredSalaList.isEmpty()){
            Toast.makeText(this, "Nenhum Item Encontrado2", Toast.LENGTH_SHORT).show();
        } else {

            adapterItens.filterList(filteredSalaList);
        }

    }

    public static void  removeDuplicates(ArrayList<String> lista) {
        // Cria um HashSet para armazenar itens únicos
        HashSet<String> conjunto = new HashSet<>();

        // Itera sobre a lista original
        for (int i = 0; i < lista.size(); i++) {
            // Se o conjunto não contiver o elemento, adiciona ao conjunto e mantém na lista
            if (!conjunto.contains(lista.get(i))) {
                conjunto.add(lista.get(i));

            } else { // Se o conjunto já contém o elemento, remove da lista
                lista.remove(i);
                i--; // Ajusta o índice após a remoção do elemento
            }
        }
    }

    @Override
    protected void onRestart() {

        buscarDados();

        super.onRestart();
    }

}