package com.ipev.controle_material;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ipev.controle_material.Adapters.AdapterItens;
import com.ipev.controle_material.Model.ItensModel;
import com.ipev.controle_material.Model.JsonCacheManager;
import com.ipev.controle_material.Model.SetupView;
import com.ipev.controle_material.databinding.ActivityBuscarLocalBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Buscar_Local extends AppCompatActivity {

    private ActivityBuscarLocalBinding binding;
    RecyclerView recyclerView;
    DatabaseReference database;

    ProgressBar loading;
    AdapterItens adapterItens;

    private final String FILE_NAME = "cache_bmp.json";

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

    public int tipo = 0;

    String status_usuario, username;

    Context context;

    String txt_sala , txt_predio , txt_setor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buscar_local);
        context = getApplicationContext();
        username = SetupView.getNome(this);
        status_usuario = SetupView.getSetor(this);

        //carregarUser();

        String[] setores = this.getResources().getStringArray(R.array.setores);

        initComponentes();


        adapter_setor = new ArrayAdapter<String>(this, R.layout.list_item, setores);

        auto_setor.setAdapter(adapter_setor);

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

        database = FirebaseDatabase.getInstance().getReference("Banco_Dados_IPEV");
        filteredSetorList = new ArrayList<>();
        itensModelArrayList = new ArrayList<>();
        filteredPredioList = new ArrayList<>();
        filteredSalaList = new ArrayList<>();
        SetorList = new ArrayList<>();
        PrediosList = new ArrayList<>();
        SalaList = new ArrayList<>();

        adapterItens = new AdapterItens(this, itensModelArrayList, status_usuario);
        recyclerView.setAdapter(adapterItens);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carregarItens();

    }

    private void initComponentes(){

        recyclerView = findViewById(R.id.recycler_buscar_local);

        auto_setor = findViewById(R.id.auto_complete_2_setor);
        loading = findViewById(R.id.progress_busca_local);

        auto_predio = findViewById(R.id.auto_complete_2_predio);
        auto_predio.setEnabled(false);

        auto_sala = findViewById(R.id.auto_complete_2_sala);
        auto_sala.setEnabled(false);


    }

    private boolean isConectado() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    private void baixarDadosDoFirebase(String novaVersao) {
        database.child("itens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                JSONArray jsonArray = new JSONArray();
                itensModelArrayList.clear();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ItensModel item = itemSnapshot.getValue(ItensModel.class);
                    if (item != null) {
                        itensModelArrayList.add(item);

                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("ID", item.getId());
                            obj.put("BMP", item.getBMP());
                            obj.put("setor", item.getSetor());
                            obj.put("sala", item.getSala());
                            obj.put("predio", item.getPredio());
                            obj.put("serial", item.getSerial());
                            obj.put("descricao", item.getDescricao());
                            obj.put("observacao", item.getObservacao());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonArray.put(obj);
                    }
                }

                salvarArquivoLocal(jsonArray, novaVersao);
                AplicarFiltros();
                adapterItens.notifyDataSetChanged();
                loading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Buscar_Local.this, "Erro ao baixar dados", Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void salvarArquivoLocal(JSONArray dados, String versao) {
        JsonCacheManager.salvar(this, dados, versao);
        Toast.makeText(this, "Banco de Dados Local Atualizado", Toast.LENGTH_LONG).show();
    }

    private String obterVersaoLocal() {
        Log.d("Versao local", JsonCacheManager.obterVersao(this));
        return JsonCacheManager.obterVersao(this);
    }



    private void carregarItens() {
        loading.setVisibility(View.VISIBLE);

        if (isConectado()) {
            database.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String versaoFirebase = snapshot.getValue(String.class);
                    String versaoLocal = obterVersaoLocal();

                    if (!versaoFirebase.contentEquals(versaoLocal)) {
                        baixarDadosDoFirebase(versaoFirebase);
                    } else {
                        carregarDadosDoArquivoLocal();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Erro ao ler versão: " + error.getMessage());
                    carregarDadosDoArquivoLocal(); // fallback offline
                }
            });
        } else {
            carregarDadosDoArquivoLocal(); // offline
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_LONG).show();
        }

        //swipeRefreshLayout.setRefreshing(false);
    }

    private void carregarDadosDoArquivoLocal() {
        try {
            JSONArray array = JsonCacheManager.obterDados(this);
            if (array.length() == 0) {
                Toast.makeText(this, "Sem dados offline disponíveis", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.INVISIBLE);
                return;
            }


            itensModelArrayList.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ItensModel item = new ItensModel();
                item.setId(obj.getInt("ID"));
                item.setBMP(obj.getString("BMP"));
                item.setSetor(obj.getString("setor"));
                item.setSala(obj.getString("sala"));
                item.setPredio(obj.getString("predio"));
                item.setSerial(obj.getString("serial"));
                item.setDescricao(obj.optString("descricao", ""));
                item.setObservacao(obj.optString("observacao", ""));

                itensModelArrayList.add(item);
            }


            AplicarFiltros();

            adapterItens.notifyDataSetChanged();
            Toast.makeText(this, "Dados Carregados Localmente - Versao : " + obterVersaoLocal(), Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao carregar dados locais", Toast.LENGTH_SHORT).show();
        } finally {
            loading.setVisibility(View.INVISIBLE);
        }
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

    private void AplicarFiltros() {

        if (tipo == 0){
            exportedList = itensModelArrayList;

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
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("onRestart", "onRestart called");
        carregarItens();

        }
    }
