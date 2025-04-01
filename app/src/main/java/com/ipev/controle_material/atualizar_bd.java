package com.ipev.controle_material;

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

    String bmp_formatado;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_bd);

        // Inicializar o Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Banco_BMP");
        // Carregar o arquivo Excel e inserir dados no Firebase
        new ReadExcelTask().execute();
    }

    class ReadExcelTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            AssetManager assetManager = getAssets();
            try {
                // Abrir o arquivo Excel a partir da pasta assets
                InputStream inputStream = assetManager.open("xls/planilha2.xlsx");

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
                    Cell cell8 = row.getCell(7);
                    Cell cell9 = row.getCell(8);

                    //DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    //Date date = cell4.getDateCellValue();
                    //String cellValue4 = df.format(date);


                    //double bmp_valor = cell2.getNumericCellValue();

                    //String bmp_formatado = String.format("%.0f", bmp_valor);

                    Log.d("", "type: " + cell2.getCellType());


                    if (cell2.getCellType() == CellType.STRING){
                        bmp_formatado = cell2.getStringCellValue();
                    } else {
                        double bmp_valor = cell2.getNumericCellValue();
                        bmp_formatado = String.format("%.0f", bmp_valor);
                    }


                    String serie_formatado = "SEM S/N";

                    if (cell9.getCellType() == CellType.STRING){
                        serie_formatado = cell9.getStringCellValue();

                    } else if (cell9.getCellType() == CellType.NUMERIC){
                        double serie_valor = cell9.getNumericCellValue();
                        serie_formatado = String.format("%.0f", serie_valor);
                    }

                    // Extrai os caracteres restantes

                    String sala;

                    try {

                        sala = cell5.getStringCellValue();

                        if (sala.equals("")){
                            sala = "SEM SALA";
                        }

                    } catch (StringIndexOutOfBoundsException e){
                        sala = "SEM SALA";
                    }

                    // Adicionar dados ao Firebase
                    databaseReference.child(String.valueOf(i)).child("id").setValue(cell1.getNumericCellValue());
                    databaseReference.child(String.valueOf(i)).child("BMP").setValue(bmp_formatado.trim());
                    databaseReference.child(String.valueOf(i)).child("setor").setValue(cell3.getStringCellValue().trim());
                    databaseReference.child(String.valueOf(i)).child("predio").setValue(cell4.getStringCellValue().trim());
                    databaseReference.child(String.valueOf(i)).child("sala").setValue(sala.trim());
                    databaseReference.child(String.valueOf(i)).child("estado").setValue(cell6.getStringCellValue().trim());
                    databaseReference.child(String.valueOf(i)).child("descricao").setValue(cell8.getStringCellValue());
                    databaseReference.child(String.valueOf(i)).child("observacao").setValue(cell7.getStringCellValue());
                    databaseReference.child(String.valueOf(i)).child("serial").setValue(serie_formatado.trim());
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