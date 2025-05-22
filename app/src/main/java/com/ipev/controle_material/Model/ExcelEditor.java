package com.ipev.controle_material.Model;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ipev.controle_material.R;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExcelEditor {

    static AlertDialog dialog;
    public static void preencherExcelAsync(final Context context, final List<ItensModel> itens) {

         showProgressDialog(context);

        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return preencherExcel(context, itens);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                dialog.dismiss();
                if (success) {
                    Toast.makeText(context, "Lista exportada com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Erro ao exportar a lista. Tente novamente", Toast.LENGTH_SHORT).show();
                }
            }
        };

        asyncTask.execute();
    }

    public static void preencherExcelPendentes(final Context context, final ArrayList<siloms_itens> itens, final String inventario) {

        showProgressDialog(context);

        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return preencherPendenciaExcel(context, itens, inventario);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                dialog.dismiss();
                if (success) {
                    Toast.makeText(context, "Lista exportada com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Erro ao exportar a lista. Tente novamente", Toast.LENGTH_SHORT).show();
                }
            }
        };

        asyncTask.execute();
    }
    public static void preencherExcelFullAsync(final Context context, final List<ItensModel> itens) {

        showProgressDialog(context);

        Log.d("", "preencherExcelFullAsync itens 0 :"  + itens.size());

        Log.d("", "preencherExcelFullAsync 01");

        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {

                Log.d("", "preencherExcelFullAsync 02");



                return preencherExcelFullBD(context, itens);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                dialog.dismiss();
                if (success) {
                    Toast.makeText(context, "Lista exportada com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Erro ao exportar a lista. Tente novamente", Toast.LENGTH_SHORT).show();
                }
            }
        };

        asyncTask.execute();
    }


    public static void showProgressDialog(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.progressdialog, null);
        ProgressBar progressBar = dialogView.findViewById(R.id.progress_dados);
        TextView textViewMessage = dialogView.findViewById(R.id.loading);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false); // Impede que o usuário cancele o diálogo
        dialog = dialogBuilder.create();
        dialog.show();
    }
    private static boolean preencherExcel(Context context, List<ItensModel> itens) {
        try {
            String nome_file = itens.get(0).getSetor() + "_" + itens.get(0).getPredio() + "_" + itens.get(0).getSala();

            InputStream in = context.getAssets().open("xls/template.xlsx");


            Workbook workbook = new XSSFWorkbook(in);
            Sheet sheet = workbook.getSheetAt(0); // Obtém a primeira planilha

            Cell cellD12 = sheet.getRow(11).getCell(3);
            if (cellD12 == null){
                cellD12 = sheet.getRow(12).createCell(3);
            }
            cellD12.setCellValue(itens.get(0).getSala());

            Cell cellF12 = sheet.getRow(11).getCell(4);
            if (cellF12 == null){
                cellF12 = sheet.getRow(12).createCell(4);
            }
            cellF12.setCellValue(itens.get(0).getSetor());

            sheet.shiftRows(14, sheet.getLastRowNum(), itens.size());

            CellStyle bordaStyle = workbook.createCellStyle();
            bordaStyle.setBorderTop(BorderStyle.THIN);
            bordaStyle.setBorderBottom(BorderStyle.THIN);
            bordaStyle.setBorderLeft(BorderStyle.THIN);
            bordaStyle.setBorderRight(BorderStyle.THIN);

            int linha = 13;

            for (int i = 0; i < itens.size(); i++) {
                linha++;
                Row row = sheet.createRow(linha);

                Cell cell1 = row.createCell(0);
                cell1.setCellStyle(bordaStyle);
                Cell cell2 = row.createCell(1);
                cell2.setCellStyle(bordaStyle);
                Cell cell3 = row.createCell(2);
                cell3.setCellStyle(bordaStyle);
                Cell cell4 = row.createCell(3);
                cell4.setCellStyle(bordaStyle);
                Cell cell5 = row.createCell(4);
                cell5.setCellStyle(bordaStyle);
                Cell cell6 = row.createCell(5);
                cell6.setCellStyle(bordaStyle);
                Cell cell7 = row.createCell(6);
                cell7.setCellStyle(bordaStyle);

                sheet.addMergedRegion(new CellRangeAddress(linha, linha , 1, 6));


                Cell cell8 = row.createCell(7);
                cell8.setCellStyle(bordaStyle);
                Cell cell9 = row.createCell(8);
                cell9.setCellStyle(bordaStyle);

                sheet.addMergedRegion(new CellRangeAddress(linha, linha , 7, 8));

                Cell cell10 = row.createCell(9);
                cell10.setCellStyle(bordaStyle);

                cell1.setCellValue(i+1);
                cell2.setCellValue(itens.get(i).getDescricao());
                cell8.setCellValue(itens.get(i).getBMP());
                cell10.setCellValue("1");

            }

            in.close();

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            FileOutputStream outputStream = new FileOutputStream(new File(path, nome_file +".xlsx"));

            workbook.write(outputStream);
            outputStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static boolean preencherPendenciaExcel(Context context, ArrayList<siloms_itens> itens, String inventario) {
        try {
            String nome_file = "ITENS_PENDENTES_APR-P";

            Log.d("", "nome inv:" + inventario);
            InputStream in = context.getAssets().open("xls/template_pendencia.xlsx");

            Workbook workbook = new XSSFWorkbook(in);
            Sheet sheet = workbook.getSheetAt(0); // Obtém a primeira planilha

            Cell cellD13 = sheet.getRow(12).getCell(3);
            if (cellD13 == null){
                cellD13 = sheet.getRow(12).createCell(3);
            }
            cellD13.setCellValue(inventario);


            sheet.shiftRows(15, sheet.getLastRowNum(), itens.size());

            CellStyle bordaStyle = workbook.createCellStyle();
            bordaStyle.setBorderTop(BorderStyle.THIN);
            bordaStyle.setBorderBottom(BorderStyle.THIN);
            bordaStyle.setBorderLeft(BorderStyle.THIN);
            bordaStyle.setBorderRight(BorderStyle.THIN);

            int linha = 14;

            for (int i = 0; i < itens.size(); i++) {
                linha++;
                Row row = sheet.createRow(linha);

                Cell cell1 = row.createCell(0);
                cell1.setCellStyle(bordaStyle);
                Cell cell2 = row.createCell(1);
                cell2.setCellStyle(bordaStyle);
                Cell cell3 = row.createCell(2);
                cell3.setCellStyle(bordaStyle);
                Cell cell4 = row.createCell(3);
                cell4.setCellStyle(bordaStyle);
                Cell cell5 = row.createCell(4);
                cell5.setCellStyle(bordaStyle);
                Cell cell6 = row.createCell(5);
                cell6.setCellStyle(bordaStyle);
                Cell cell7 = row.createCell(6);
                cell7.setCellStyle(bordaStyle);
                Cell cell8 = row.createCell(7);
                cell8.setCellStyle(bordaStyle);
                Cell cell9 = row.createCell(8);
                cell9.setCellStyle(bordaStyle);
                Cell cell10 = row.createCell(9);
                cell10.setCellStyle(bordaStyle);

                sheet.addMergedRegion(new CellRangeAddress(linha, linha , 1, 9));

                cell1.setCellValue(itens.get(i).getBmp());
                cell2.setCellValue(itens.get(i).getDescricao());

            }

            in.close();

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            FileOutputStream outputStream = new FileOutputStream(new File(path, nome_file +".xlsx"));
            workbook.write(outputStream);
            outputStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean preencherExcelFullBD(Context context, List<ItensModel> itens) {
        try {


            Date dataAtual = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
            String dataFormatada = dateFormat.format(dataAtual);
            SimpleDateFormat horaFormat = new SimpleDateFormat("HH_mm_ss");
            String hora = horaFormat.format(dataAtual);
            String nome_file = "IPEV_CARGA_" + dataFormatada + "_" + hora;

            InputStream in = context.getAssets().open("xls/planilha_full.xlsx");

            Workbook workbook = new XSSFWorkbook(in);
            Sheet sheet = workbook.getSheetAt(0); // Obtém a primeira planilha

            CellStyle bordaStyle = workbook.createCellStyle();
            bordaStyle.setBorderTop(BorderStyle.THIN);
            bordaStyle.setBorderBottom(BorderStyle.THIN);
            bordaStyle.setBorderLeft(BorderStyle.THIN);
            bordaStyle.setBorderRight(BorderStyle.THIN);

            int linha = 3;

            for (int i = 0; i < itens.size(); i++) {
                linha++;
                Row row = sheet.createRow(linha);

                Cell cell1 = row.createCell(0);
                cell1.setCellStyle(bordaStyle);
                Cell cell2 = row.createCell(1);
                cell2.setCellStyle(bordaStyle);
                Cell cell3 = row.createCell(2);
                cell3.setCellStyle(bordaStyle);
                Cell cell4 = row.createCell(3);
                cell4.setCellStyle(bordaStyle);
                Cell cell5 = row.createCell(4);
                cell5.setCellStyle(bordaStyle);
                Cell cell6 = row.createCell(5);
                cell6.setCellStyle(bordaStyle);
                Cell cell7 = row.createCell(6);
                cell7.setCellStyle(bordaStyle);
                Cell cell8 = row.createCell(7);
                cell8.setCellStyle(bordaStyle);

                cell1.setCellValue(itens.get(i).getId());
                cell2.setCellValue(itens.get(i).getBMP());
                cell3.setCellValue(itens.get(i).getSetor());
                cell4.setCellValue(itens.get(i).getPredio());
                cell5.setCellValue(itens.get(i).getSala());
                cell6.setCellValue(itens.get(i).getDescricao());
                cell7.setCellValue(itens.get(i).getSerial());
                cell8.setCellValue(itens.get(i).getObservacao());

            }

            in.close();

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            FileOutputStream outputStream = new FileOutputStream(new File(path, nome_file +".xlsx"));

            workbook.write(outputStream);
            outputStream.close();

            return true;
        } catch (IOException e) {
            Log.d("", "erro preencherExcelFullDB");

            e.printStackTrace();
            return false;
        }
    }
}