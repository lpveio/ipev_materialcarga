package com.iae.controle_material;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class atualizar_bd extends AppCompatActivity {

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_bd);

        // Inicializar o Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("itens");
        // Carregar o arquivo Excel e inserir dados no Firebase
        new ReadExcelTask().execute();
    }

    class ReadExcelTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            AssetManager assetManager = getAssets();
            try {
                // Abrir o arquivo Excel a partir da pasta assets
                InputStream inputStream = assetManager.open("xls/planilha.xlsx");

                // Crie um objeto da classe XSSFWorkbook
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

                // Obtenha a primeira planilha do arquivo Excel
                XSSFSheet sheet = workbook.getSheetAt(0);

                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    Cell cell1 = row.getCell(0);
                    Cell cell2 = row.getCell(1);
                    Cell cell3 = row.getCell(2);
                    Cell cell4 = row.getCell(3);
                    Cell cell5 = row.getCell(4);
                    Cell cell6 = row.getCell(5);
                    Cell cell7 = row.getCell(6);

                    //DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    //Date date = cell4.getDateCellValue();
                    //String cellValue4 = df.format(date);

                    double bmp_valor = cell2.getNumericCellValue();
                    String bmp_formatado = String.format("%.0f", bmp_valor);


                    String predio = cell4.getStringCellValue().substring(0, 5).toUpperCase();

                    // Extrai os caracteres restantes

                    String sala;
                    try {
                        sala = cell4.getStringCellValue().substring(6).toUpperCase();

                    } catch (StringIndexOutOfBoundsException e){
                        sala = "SEM SALA";
                    }


                    // Adicionar dados ao Firebase
                    databaseReference.child(String.valueOf(i)).child("id").setValue(cell1.getNumericCellValue());
                    databaseReference.child(String.valueOf(i)).child("BMP").setValue(bmp_formatado.trim());
                    databaseReference.child(String.valueOf(i)).child("setor").setValue(cell3.getStringCellValue().trim());
                    databaseReference.child(String.valueOf(i)).child("predio").setValue(predio.trim());
                    databaseReference.child(String.valueOf(i)).child("sala").setValue(sala.trim());
                    databaseReference.child(String.valueOf(i)).child("estado").setValue(cell5.getStringCellValue().trim());
                    databaseReference.child(String.valueOf(i)).child("descricao").setValue(cell7.getStringCellValue());
                    databaseReference.child(String.valueOf(i)).child("observacao").setValue(cell6.getStringCellValue());
                    // Adicione mais colunas conforme necessário

                }
                // Feche o workbook e arquivo
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

            Toast.makeText(atualizar_bd.this, "Dados inseridos no Firebase", Toast.LENGTH_SHORT).show();
        }
    }
}