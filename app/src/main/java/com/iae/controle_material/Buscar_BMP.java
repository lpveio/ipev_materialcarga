package com.iae.controle_material;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iae.controle_material.Model.EditClick;
import com.iae.controle_material.Model.ItensModel;

import java.util.ArrayList;
import java.util.List;


public class Buscar_BMP extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;

    String usuario;

    ProgressBar loading;
    AdapterItens adapterItens;

    boolean filtered = false;

    String filterText;

    ArrayList<ItensModel> itensModelArrayList;

    private SearchView searchview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buscar_bmp);

        Intent intent = getIntent();
        usuario = intent.getStringExtra("USERNAME");

        recyclerView = findViewById(R.id.recycler_buscar_bmp);
        database = FirebaseDatabase.getInstance().getReference("itens");
        itensModelArrayList = new ArrayList<>();
        loading = findViewById(R.id.progress_busca_bmp);

        adapterItens = new AdapterItens(this, itensModelArrayList, usuario);
        recyclerView.setAdapter(adapterItens);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buscarDados();

        searchview = findViewById(R.id.searchView);
        searchview.clearFocus();
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBMB(newText);
                filterText = newText;

                return false;
            }
        });

    }

    private void filterBMB(String bmp){
        ArrayList<ItensModel> ListFilterBmp = new ArrayList<>();

        for (ItensModel itens : itensModelArrayList){
            if (itens.getBMP().contains(bmp)){
                ListFilterBmp.add(itens);
            }
        }

        if (ListFilterBmp.isEmpty()){
            Toast.makeText(this, "Nenhum BMP encontrado", Toast.LENGTH_LONG);
            recyclerView.setVisibility(View.GONE);
        } else {
            adapterItens.setFilterBMP(ListFilterBmp);
            recyclerView.setVisibility(View.VISIBLE);
            filtered = true;
        }

    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (filtered){
            filterBMB(filterText);
        }

    }

    private void buscarDados(){

        loading.setVisibility(View.VISIBLE);

        if (!isConnectedToInternet()) {
            // Se não houver conexão, exibe uma mensagem após 10 segundos
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Buscar_BMP.this);
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
                    adapterItens.notifyDataSetChanged();

                loading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }
}