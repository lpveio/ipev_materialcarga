package com.iae.controle_material;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iae.controle_material.Model.ExcelEditor;
import com.iae.controle_material.Model.ItensModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    CardView cadastrar, busca_local, busca_bmp, exportar_local , exportar_dados, inventario;

    ArrayList<ItensModel> itensModelArrayList;

    DatabaseReference database;

    private DatabaseReference mDatabase;

    String usuario;

    String versionApp , versionALastApp, UrlApk;

    TextView versao_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        usuario = intent.getStringExtra("USERNAME");

        cadastrar = findViewById(R.id.card_cadastrar);

        busca_local = findViewById(R.id.card_pesquisa_local);

        busca_bmp = findViewById(R.id.card_pesquisa_bmp);

        //atualizar_bd = findViewById(R.id.btn_a);

        versao_text = findViewById(R.id.appVersion);

        inventario = findViewById(R.id.card_inventario);

        exportar_local = findViewById(R.id.card_exportar_local);

        exportar_dados = findViewById(R.id.card_exportar_banco);



        checkVersionApp();
        checkVersionFirebase();


        exportar_dados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               buscarDados();
            }
        });


        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usuario.equals("admin")) {
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

                Intent intent = new Intent(MainActivity.this, Exportar_Local.class);
                intent.putExtra("USERNAME", usuario);
                startActivity(intent);

            }
        });

        inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, inventario.class);
                intent.putExtra("USERNAME", usuario);
                startActivity(intent);

            }
        });

        /*
        atualizar_bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Aviso");
                builder.setMessage("Para Atualizar o Banco, contate o administrador");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        */
    }


    private void buscarDados(){

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



            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


        ExcelEditor.preencherExcelFullAsync(this, itensModelArrayList);
    }

    private  void checkVersionApp(){

        PackageManager packageManager = getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionApp = packageInfo.versionName;
            versao_text.setText("v"+versionApp);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void compareVersion(){

        if (!versionApp.contentEquals(versionALastApp)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Existe uma atualização para seu aplicativo, deseja atualizar agora?");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    DownloadApk downloadApk = new DownloadApk(MainActivity.this);

                    downloadApk.startDownloadingApk(UrlApk);

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