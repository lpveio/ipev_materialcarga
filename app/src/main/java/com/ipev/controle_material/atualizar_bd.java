package com.ipev.controle_material;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

public class atualizar_bd extends AppCompatActivity {

    DatabaseReference databaseReference;

    String bmp_formatado, predio, sala;

    String[] partes;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_bd);

        // Inicializar o Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Banco_Dados_IPEV").child("itens");
        // Carregar o arquivo Excel e inserir dados no Firebase
        new ReadExcelTask().execute();
    }

    class ReadExcelTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            AssetManager assetManager = getAssets();
            try {
                // Abrir o arquivo Excel a partir da pasta assets
                InputStream inputStream = assetManager.open("xls/planilha5.xlsx");

                // Crie um objeto da classe XSSFWorkbook
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

                // Obtenha a primeira planilha do arquivo Excel
                XSSFSheet sheet = workbook.getSheetAt(0);

                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    Cell cellID = row.getCell(0);
                    Cell cellBMP = row.getCell(1);
                    Cell cellDESC = row.getCell(2);
                    Cell cellSETOR = row.getCell(3);
                    Cell cellPREDIO = row.getCell(4);
                    Cell cellSN = row.getCell(5);


                    if (cellBMP.getCellType() == CellType.STRING){
                        bmp_formatado = cellBMP.getStringCellValue();
                    } else {
                        double bmp_valor = cellBMP.getNumericCellValue();
                        bmp_formatado = String.format("%.0f", bmp_valor);
                    }


                    partes = cellPREDIO.getStringCellValue().split(" / ");

                    // Garantindo que existem duas partes
                    if (partes.length == 2) {
                        predio = partes[0].trim(); // "Prédio da Administração"
                        sala = partes[1].trim(); // "08"
                    } else {

                    }


                    // Extrai os caracteres restantes

                    String serialNumber;

                    try {

                        if (cellSN.getCellType() == CellType.STRING){
                            serialNumber = cellSN.getStringCellValue();

                        } else {
                            double serial = cellSN.getNumericCellValue();
                            serialNumber = String.format("%.0f", serial);

                        }

                        if (serialNumber.equals("0")){
                            serialNumber = "SEM S/N";;
                        }

                    } catch (StringIndexOutOfBoundsException e){
                        serialNumber = "SEM S/N";;
                    }



                    // Adicionar dados ao Firebase
                    databaseReference.child(String.valueOf(i)).child("id").setValue(cellID.getNumericCellValue());
                    Log.d("", "ID:" + cellID.getNumericCellValue());
                    databaseReference.child(String.valueOf(i)).child("BMP").setValue(bmp_formatado.trim());
                    databaseReference.child(String.valueOf(i)).child("descricao").setValue(cellDESC.getStringCellValue());
                    databaseReference.child(String.valueOf(i)).child("setor").setValue(cellSETOR.getStringCellValue().trim());
                    databaseReference.child(String.valueOf(i)).child("predio").setValue(predio.trim());
                    databaseReference.child(String.valueOf(i)).child("sala").setValue(sala.trim());
                    databaseReference.child(String.valueOf(i)).child("serial").setValue(serialNumber.trim());
                    databaseReference.child(String.valueOf(i)).child("observacao").setValue("");

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