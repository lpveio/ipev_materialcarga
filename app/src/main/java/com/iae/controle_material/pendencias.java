package com.iae.controle_material;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.LocaleDisplayNames;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.iae.controle_material.Model.InventarioModel;
import com.iae.controle_material.Model.ItensModel;
import com.iae.controle_material.Model.siloms_itens;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class pendencias extends AppCompatActivity {

    DatabaseReference database;

    AlertDialog dialog;

    ArrayList<ItensModel> itensModelArrayList;

    long quantidadeItens;

    ValueEventListener mListener;

    ArrayList<siloms_itens> siloms_itens_faltantes;

    ArrayList<Integer> BMP_Exclusivos, BMP_Cadastrados;;

    Uri file_uri;

    ArrayList<siloms_itens> siloms_list;

    ArrayAdapter<String> adapter;

    ArrayList<Integer> bd_list_int , siloms_list_int;

    String nome_banco;

    int tipo;

    ArrayList<String> list;

    TextView text_planilha;
    AutoCompleteTextView banco_dados;

    Button gerar_relatorio , busca_siloms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_relatorios);

        text_planilha = findViewById(R.id.text_siloms);
        gerar_relatorio = findViewById(R.id.btn_gerar_relatorio);
        gerar_relatorio.setEnabled(false);
        banco_dados = findViewById(R.id.auto_complete_banco_dados);
        banco_dados.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                String item = adapterView.getItemAtPosition(i).toString();

                nome_banco = item;

                busca_siloms.setEnabled(true);

            }
        });

        busca_siloms = findViewById(R.id.btn_buscar_file);
        busca_siloms.setEnabled(false);
        list = new ArrayList<>();





        adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);

        gerar_relatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLoading();
                buscarDBMain();


            }
        });

        busca_siloms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFilePickerXLS();
            }
        });


        buscarInventarios();
    }

    private void startFilePickerXLS() {

        filePickerLauncher.launch("application/vnd.ms-excel");
    }

    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),

            uri -> {
                if (uri != null) {

                    file_uri = uri;
                    text_planilha.setText(getFileNameFromUri(uri));
                    gerar_relatorio.setEnabled(true);

                }

            }
    );

    public void LerExcelFile() {

        new ReadExcelTask().execute();
    }


    class ReadExcelTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            siloms_list = new ArrayList<>();
            siloms_list.clear();
            siloms_list_int = new ArrayList<>();
            siloms_list_int.clear();

            try {
                // Abrir o arquivo Excel a partir da pasta assets
                InputStream inputStream = getContentResolver().openInputStream(file_uri);

                // Crie um objeto da classe XSSFWorkbook
                HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

                // Obtenha a primeira planilha do arquivo Excel
                HSSFSheet sheet = workbook.getSheetAt(0);

                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {

                    siloms_itens siloms_itens = new siloms_itens();
                    Row row = sheet.getRow(i);

                    Cell cell0 = row.getCell(0);

                    if (cell0 != null && cell0.getCellType() != CellType.BLANK) {
                        Cell cell1 = row.getCell(3);
                        int bmp = (int) cell1.getNumericCellValue();
                        siloms_itens.setBmp((bmp));
                        siloms_list_int.add(bmp);
                        Cell cell2 = row.getCell(4);
                        siloms_itens.setDescricao(cell2.getStringCellValue());

                        siloms_list.add(siloms_itens);
                    }
                }

                workbook.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(pendencias.this, "Planilha lida com sucesso", Toast.LENGTH_SHORT).show();
            buscarDados();
        }
    }


    private String getFileNameFromUri(Uri uri) {
        String fileName = "default_file_name"; // Nome padrão se não puder obter o nome do arquivo
        Cursor cursor = null;

        try {
            String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
            cursor = this.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return fileName;
    }


    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void setLoading(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.progressdialog2, null);
        ProgressBar progressBar = dialogView.findViewById(R.id.progress_dados);
        TextView textViewMessage = dialogView.findViewById(R.id.loading);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false); // Impede que o usuário cancele o diálogo
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void MostrarResultado(ArrayList<siloms_itens> siloms_faltantes){

        if (siloms_faltantes.size() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aviso");
            builder.setMessage("Foram encontrado(s)   " + siloms_faltantes.size() + "  item(s) pendentes" );
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(pendencias.this, resultado_pendencias.class);


                    intent.putIntegerArrayListExtra("BMP_Cadastrados", BMP_Cadastrados );
                    intent.putIntegerArrayListExtra("BMP", BMP_Exclusivos );
                    intent.putExtra("siloms_lista", (Serializable) siloms_faltantes);
                    intent.putExtra("tipo", tipo);
                    intent.putExtra("nome", nome_banco);
                    startActivity(intent);
                    database.removeEventListener(mListener);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aviso");
            builder.setMessage("Não foi encontrado itens pendentes. Todos os BMP`s da base de dados do App ou do Inventário foram cadastrados ou encontrados" );
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }


    private static void searchByBmp(String bmpValue) {
        // Create a Firebase reference to the "itens" table
        Query query = FirebaseDatabase.getInstance().getReference("itens"). orderByChild("bmp").equalTo(bmpValue);

        Log.d("Nome:", "busvando");
        // Add a listener to the query
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Loop through the results and print the other values
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String nome = itemSnapshot.child("id").getValue(String.class);
                    String descricao = itemSnapshot.child("descricao").getValue(String.class);
                    Log.d("Nome:", nome);
                    Log.d("Descrição:", descricao);
                    // ...
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle any errors
            }
        });
    }

    private void  buscarDBMain(){

        BMP_Cadastrados = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference("itens");
        Log.i("bmb", "buscando");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ItensModel itensModel = dataSnapshot.getValue(ItensModel.class);
                    BMP_Cadastrados.add(Integer.valueOf(itensModel.getBMP()));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        LerExcelFile();

    }

    private void buscarInventarios() {

        if (!isConnectedToInternet()) {
            // Se não houver conexão, exibe uma mensagem após 10 segundos
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(pendencias.this);
                    builder.setTitle("ERRO");
                    builder.setIcon(R.drawable.error);
                    builder.setMessage("Verifique sua conexão com a internet ou reinicie o app");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }, 5000); // 10 segundos de atraso
            return;
        }

        database = FirebaseDatabase.getInstance().getReference("Inventarios");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                list.add("BMP`s Cadastrados");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    InventarioModel inventarioModel = dataSnapshot.getValue(InventarioModel.class);
                    list.add(inventarioModel.getNome());
                }
                banco_dados.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }

    public ArrayList<siloms_itens> getDadosSiloms(ArrayList<siloms_itens> siloms_list , ArrayList<Integer> bmp_list) {
        ArrayList<siloms_itens> itens_faltantes = new ArrayList<>();

        for (int bmb_value : bmp_list){

            for (siloms_itens siloms : siloms_list){

                if (bmb_value == siloms.getBmp()){
                    siloms_itens siloms_falante_itens = new siloms_itens();
                    siloms_falante_itens.setBmp(siloms.getBmp());
                    siloms_falante_itens.setDescricao(siloms.getDescricao());
                    itens_faltantes.add(siloms_falante_itens);
                }
            }
        }
        return itens_faltantes;
    }

    public void GerarRelatorio(){

        BMP_Exclusivos = new ArrayList<>();
        BMP_Exclusivos.clear();
        BMP_Exclusivos = siloms_list_int;
        BMP_Exclusivos.removeAll(bd_list_int);
        siloms_itens_faltantes = getDadosSiloms(siloms_list , BMP_Exclusivos);
        dialog.dismiss();

        MostrarResultado(siloms_itens_faltantes);



    }



    private void buscarDados() {

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                bd_list_int = new ArrayList<>();
                bd_list_int.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int bmb2 = Integer.parseInt(String.valueOf(dataSnapshot.child("BMP").getValue()));
                    bd_list_int.add(bmb2);
                }

                GerarRelatorio();
                // Lógica para lidar com os dados alterados
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Lógica para lidar com erros de leitura do Firebase
            }
        };

        if (nome_banco.contentEquals("BMP`s Cadastrados")) {
            database = FirebaseDatabase.getInstance().getReference("itens");
            tipo = 1;
            database.addValueEventListener(mListener);

        } else {
            database = FirebaseDatabase.getInstance().getReference(nome_banco);
            tipo = 2;
            database.addValueEventListener(mListener);

        }
    }
}