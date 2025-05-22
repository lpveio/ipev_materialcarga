package com.ipev.controle_material;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ipev.controle_material.Adapters.AdapterItens;
import com.ipev.controle_material.Model.ItensModel;
import com.ipev.controle_material.Model.JsonCacheManager;
import com.ipev.controle_material.Model.SetupView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Buscar_BMP extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ProgressBar loadingBar;
    private SearchView searchView;
    private AdapterItens adapterItens;
    private ArrayList<ItensModel> itensList;
    private boolean isFiltered = false;
    private String lastFilterText = "";
    private String status_usuario;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_bmp);

        status_usuario = SetupView.getSetor(this);

        recyclerView = findViewById(R.id.recycler_buscar_bmp);
        searchView = findViewById(R.id.searchView);
        loadingBar = findViewById(R.id.progress_busca_bmp);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::carregarItens);

        databaseReference = FirebaseDatabase.getInstance().getReference("Banco_Dados_IPEV");

        itensList = new ArrayList<>();
        adapterItens = new AdapterItens(this, itensList, status_usuario);

        recyclerView.setAdapter(adapterItens);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //deletar();

        carregarItens();

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarItensPorBMP(newText);
                lastFilterText = newText;
                return false;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isFiltered) {
            filtrarItensPorBMP(lastFilterText);
        }
    }


    private boolean isConectado() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }


     private void deletar(){

         FirebaseDatabase database = FirebaseDatabase.getInstance();
         // Obtém a referência para o nó raiz ("/")
         DatabaseReference myRef = database.getReference("itens");

         // Remove o valor no nó raiz
         databaseReference.removeValue()
                 .addOnSuccessListener(aVoid -> {
                     // Lógica de sucesso
                     System.out.println("Nó raiz deletado com sucesso!");
                 })
                 .addOnFailureListener(e -> {
                     // Lógica de falha
                     System.out.println("Falha ao deletar o nó raiz: " + e.getMessage());
                 });

     }

    private void carregarItens() {
        loadingBar.setVisibility(View.VISIBLE);

        if (isConectado()) {
            databaseReference.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String versaoFirebase = snapshot.getValue(String.class);
                    Log.d("Versao Firebase BMP", versaoFirebase);
                    String versaoLocal = obterVersaoLocal();

                    if (!String.valueOf(versaoFirebase).equals(versaoLocal)) {
                        baixarDadosDoFirebase(String.valueOf(versaoFirebase));
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

        swipeRefreshLayout.setRefreshing(false);
    }

    private void baixarDadosDoFirebase(String novaVersao) {
        databaseReference.child("itens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                JSONArray jsonArray = new JSONArray();
                itensList.clear();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ItensModel item = itemSnapshot.getValue(ItensModel.class);
                    if (item != null) {
                        itensList.add(item);

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
                adapterItens.notifyDataSetChanged();
                loadingBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Buscar_BMP.this, "Erro ao baixar dados", Toast.LENGTH_SHORT).show();
                loadingBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void carregarDadosDoArquivoLocal() {
        try {
            JSONArray array = JsonCacheManager.obterDados(this);
            if (array.length() == 0) {
                Toast.makeText(this, "Sem dados offline disponíveis", Toast.LENGTH_LONG).show();
                loadingBar.setVisibility(View.INVISIBLE);
                return;
            }

            itensList.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ItensModel item = new ItensModel();
                item.setId(obj.getInt("ID"));
                item.setBMP(obj.getString("BMP"));
                item.setSetor(obj.getString("setor"));
                item.setSala(obj.getString("sala"));
                item.setPredio(obj.getString("predio"));
                item.setSerial(obj.getString("serial"));
                item.setDescricao(obj.getString("descricao"));
                item.setObservacao(obj.getString("observacao"));

                itensList.add(item);
            }

            adapterItens.notifyDataSetChanged();
            Toast.makeText(this, "Dados Carregados Localmente - Versão : " + obterVersaoLocal(), Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao carregar dados locais", Toast.LENGTH_SHORT).show();
        } finally {
            loadingBar.setVisibility(View.INVISIBLE);
        }
    }


    private void salvarArquivoLocal(JSONArray dados, String versao) {
        JsonCacheManager.salvar(this, dados, versao);
    }

    private String obterVersaoLocal() {
        return JsonCacheManager.obterVersao(this);
    }

    /*

    private void carregarDadosDoArquivoLocal() {
        try {
            File file = new File(getFilesDir(), FILE_NAME);
            if (!file.exists()) {
                Toast.makeText(this, "Sem dados offline disponíveis", Toast.LENGTH_LONG).show();
                loadingBar.setVisibility(View.INVISIBLE);
                return;
            }

            FileInputStream fis = openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder builder = new StringBuilder();
            String linha;

            while ((linha = reader.readLine()) != null) {
                builder.append(linha);
            }

            JSONObject root = new JSONObject(builder.toString());
            JSONArray array = root.getJSONArray("dados");

            itensList.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ItensModel item = new ItensModel();
                item.setId(obj.getInt("ID"));
                item.setBMP(obj.getString("BMP"));
                item.setSetor(obj.getString("setor"));
                item.setSala(obj.getString("sala"));
                item.setPredio(obj.getString("predio"));
                item.setSerial(obj.optString("sn", ""));
                item.setDescricao(obj.optString("descricao", ""));
                item.setObservacao(obj.optString("observacao", ""));

                itensList.add(item);
            }

            adapterItens.notifyDataSetChanged();

            Toast.makeText(this, "Dados Carregados Localmente - Versao : " + obterVersaoLocal(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao carregar dados locais", Toast.LENGTH_SHORT).show();
        } finally {
            loadingBar.setVisibility(View.INVISIBLE);
        }
    }

    private void salvarArquivoLocal(JSONArray dados, String versao) {
        JSONObject root = new JSONObject();
        try {
            root.put("versao", versao);
            root.put("dados", dados);
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(root.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String obterVersaoLocal() {
        try {
            File file = new File(getFilesDir(), FILE_NAME);
            if (!file.exists()) return "";

            FileInputStream fis = openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder builder = new StringBuilder();
            String linha;

            while ((linha = reader.readLine()) != null) {
                builder.append(linha);
            }

            JSONObject root = new JSONObject(builder.toString());
            return root.optString("versao", "");
        } catch (Exception e) {
            return "";
        }
    }


     */
    private void filtrarItensPorBMP(String searchText) {
        if (searchText.isEmpty()) {
            adapterItens.setFilterBMP(itensList);
            recyclerView.setVisibility(View.VISIBLE);
            isFiltered = false;
            return;
        }

        ArrayList<ItensModel> listaFiltrada = new ArrayList<>();
        for (ItensModel item : itensList) {
            if (item.getBMP().contains(searchText)) {
                listaFiltrada.add(item);
            }
        }

        if (listaFiltrada.isEmpty()) {
            Toast.makeText(this, "Nenhum BMP encontrado", Toast.LENGTH_LONG).show();
            recyclerView.setVisibility(View.GONE);
        } else {
            adapterItens.setFilterBMP(listaFiltrada);
            recyclerView.setVisibility(View.VISIBLE);
            isFiltered = true;
        }
    }
}