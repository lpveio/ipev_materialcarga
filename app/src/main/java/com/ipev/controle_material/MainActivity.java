package com.ipev.controle_material;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.dcastalia.localappupdate.DownloadApk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ipev.controle_material.Model.ExcelEditor;
import com.ipev.controle_material.Model.ItensModel;
import com.ipev.controle_material.Model.SetupView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    CardView cadastrar, busca_local, busca_bmp, exportar_local , exportar_dados, inventario;

    ArrayList<ItensModel> itensModelArrayList;

    DatabaseReference database;

    private DatabaseReference mDatabase;

    String usuario, setor, status;

    String versionApp , versionALastApp, UrlApk;

    TextView versao_text, username, textLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main2);

        cadastrar = findViewById(R.id.card_cadastrar);

        textLogout = findViewById(R.id.text_logout);

        busca_local = findViewById(R.id.card_pesquisa_local);

        busca_bmp = findViewById(R.id.card_pesquisa_bmp);

        //atualizar_bd = findViewById(R.id.btn_a);

        versao_text = findViewById(R.id.appVersion);

        username = findViewById(R.id.user);

        inventario = findViewById(R.id.card_inventario);

        exportar_local = findViewById(R.id.card_exportar_local);

        exportar_dados = findViewById(R.id.card_exportar_banco);


        checkVersionApp();
        checkVersionFirebase();
        usuario = SetupView.getNome(this);
       // buscarUser();

        textLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, MainActivity_login.class));
            finish(); // Finaliza a tela atual
        });


        exportar_dados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               buscarDados();
            }
        });


        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status.equals("admin")) {
                    Intent intent = new Intent(MainActivity.this, Cadastrar.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Você não tem permissão para cadastrar", Toast.LENGTH_SHORT).show();
                }


            }
        });

        busca_bmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Buscar_BMP.class);
                intent.putExtra("USERNAME", usuario);
                startActivity(intent);

            }
        });

        busca_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Buscar_Local.class);
                intent.putExtra("USERNAME", usuario);
                startActivity(intent);

            }
        });

        exportar_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ExportLocal.class);
                intent.putExtra("USERNAME", usuario);
                startActivity(intent);

            }
        });


        inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ///Instrucao para atualizar o banco de dados: colocar TRUE
                boolean atualizar_bd = false;

                if (!atualizar_bd) {
                    Intent intent = new Intent(MainActivity.this, inventario.class);
                    intent.putExtra("USERNAME", usuario);
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(MainActivity.this, atualizar_bd.class);
                    startActivity(intent);

                }
            }
        });

    }




    private void buscarUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null) {
            String uid = usuarioAtual.getUid();

            db.collection("usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            usuario = documentSnapshot.getString("nome");
                             setor = documentSnapshot.getString("setor");
                             status = documentSnapshot.getString("status");

                            // Atualiza a TextView

                            username.setText("Olá " + usuario);

                            // Salva os dados no SharedPreferences
                            SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("uid", uid);
                            editor.putString("nome", usuario);
                            editor.putString("setor", setor);
                            editor.putString("status", status);
                            editor.apply(); // ou commit()
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Erro ao buscar dados do usuário", e);
                    });
        }
    }

    private void buscarDados(){

        itensModelArrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference("Banco_Dados_IPEV").child("itens");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itensModelArrayList.clear();

                Log.d("", "total de itens do banco :" + snapshot.getChildrenCount());

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ItensModel itensModel = dataSnapshot.getValue(ItensModel.class);

                    itensModelArrayList.add(itensModel);
                }

                Log.d("", "total de itens dentro de itensModel:"  + itensModelArrayList.size());

                ExcelEditor.preencherExcelFullAsync(MainActivity.this, itensModelArrayList);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Erro ao buscar dados: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Erro ao buscar dados do banco", Toast.LENGTH_SHORT).show();
            }



        });

    }

    private  void checkVersionApp(){

        versionApp = SetupView.getVersionName(this);
        versao_text.setText("v" + versionApp);

    }

    private void compareVersion(){

        if (!versionApp.contentEquals(versionALastApp)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Existe uma atualização para seu aplicativo, deseja atualizar agora?");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppUpdater updater = new AppUpdater(MainActivity.this);
                    updater.startDownload(UrlApk, "AppUpdate.apk");
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
    private void checkVersionFirebase(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("app");

        // Adicionando um listener para recuperar o valor da chave "versao"
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Recuperando o valor dentro de "versao"
                versionALastApp = dataSnapshot.child("versao").getValue(String.class);
                UrlApk = dataSnapshot.child("link").getValue(String.class);

                compareVersion();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Tratamento de erro, se necessário
            }


        });


    }
}