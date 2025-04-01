package com.ipev.controle_material;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ipev.controle_material.Model.Data_bmp;
import com.ipev.controle_material.Model.ItensModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Estatisticas extends AppCompatActivity {

    ProgressBar progressBar_LAAI;

    ProgressBar progressBar_OUTRO;

    ProgressBar progressBar_APR_P;

    ProgressBar progressBar_APR_PPP;

    ProgressBar progressBar_APR_PSC;

    DatabaseReference database;

    DatabaseReference databaseReference;


    ArrayList<ItensModel> itensModelArrayList;

    ProgressBar loading;

    ProgressBar progressBar_LAAQ;

    ProgressBar progressBar_LAPM;

    ProgressBar progressBar_LASI;

    ProgressBar progressBar_LAME;

    ProgressBar progressBar_LAPT;
    ProgressBar progressBar_LAPR;

    ArrayList<String> itens_checados;
    ProgressBar progressBar_LAII;


    int qtd_apr_p = 0;
    int qtd_apr_ppp= 0;
    int qtd_apr_psc= 0;
    int qtd_laai = 0;
    int qtd_laaq = 0;
    int  qtd_laii= 0;
    int qtd_lame= 0;
    int qtd_lapm= 0;

    int qtd_lapr = 0;
    int qtd_lapt = 0;
    int qtd_lasi = 0;
    int qtd_outro = 0;

    int qtd_apr_p_check = 0;
    int qtd_apr_ppp_check= 0;
    int qtd_apr_psc_check= 0;
    int qtd_laai_check = 0;
    int qtd_laaq_check = 0;
    int  qtd_laii_check= 0;
    int qtd_lame_check= 0;
    int qtd_lapm_check= 0;

    int qtd_lapr_check = 0;
    int qtd_lapt_check = 0;
    int qtd_lasi_check = 0;
    int qtd_outro_check = 0;



    TextView valor_laii_inicial, valor_laii_total, valor_lapr_inicial, valor_lapr_total, valor_lapt_inicial, valor_lapt_total, valor_lame_inicial, valor_lame_total, valor_lasi_inicial, valor_lasi_total, valor_lapm_inicial, valor_lapm_total, valor_laaq_inicial, valor_laaq_total, valor_apr_psc_inicial, valor_apr_psc_total, valor_laai_inicial, valor_laai_total, valor_outro_inicial, valor_outro_total,  valor_apr_p_inicial, valor_apr_p_total,  valor_apr_ppp_inicial, valor_apr_ppp_total;

    String nome_banco;

    TextView txt_laai, txt_outro, txt_apr_p , txt_apr_ppp, txt_apr_psc, txt_laaq, txt_lapm, txt_lasi, txt_lame, txt_lapt, txt_lapr , txt_laii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_estatisticas);

        Intent intent = getIntent();
        nome_banco = intent.getStringExtra("nome_inventario");

        progressBar_APR_P = findViewById(R.id.progressBar_1);
        progressBar_APR_PPP = findViewById(R.id.progressBar_2);
        progressBar_APR_PSC = findViewById(R.id.progressBar_3);
        progressBar_LAAI = findViewById(R.id.progressBar_4);
        progressBar_LAAQ = findViewById(R.id.progressBar_5);
        progressBar_LAII = findViewById(R.id.progressBar_6);
        progressBar_LAME = findViewById(R.id.progressBar_7);
        progressBar_LAPM = findViewById(R.id.progressBar_8);
        progressBar_LAPR = findViewById(R.id.progressBar_10);
        progressBar_LAPT = findViewById(R.id.progressBar_11);
        progressBar_LASI = findViewById(R.id.progressBar_12);
        progressBar_OUTRO = findViewById(R.id.progressBar_13);

        txt_apr_p = findViewById(R.id.progress_1_1);
        valor_apr_p_inicial = findViewById(R.id.valor_encontrado_1);
        valor_apr_p_total = findViewById(R.id.valor_total_1);
        txt_apr_ppp = findViewById(R.id.progress_1_2);
        valor_apr_ppp_inicial = findViewById(R.id.valor_encontrado_2);
        valor_apr_ppp_total = findViewById(R.id.valor_total_2);
        txt_apr_psc = findViewById(R.id.progress_1_3);
        valor_apr_psc_inicial = findViewById(R.id.valor_encontrado_3);
        valor_apr_psc_total = findViewById(R.id.valor_total_3);
        txt_laai = findViewById(R.id.progress_1_4);
        valor_laai_inicial = findViewById(R.id.valor_encontrado_4);
        valor_laai_total = findViewById(R.id.valor_total_4);
        txt_laaq = findViewById(R.id.progress_1_5);
        valor_laaq_inicial = findViewById(R.id.valor_encontrado_5);
        valor_laaq_total = findViewById(R.id.valor_total_5);
        txt_laii = findViewById(R.id.progress_1_6);
        valor_laii_inicial = findViewById(R.id.valor_encontrado_6);
        valor_laii_total = findViewById(R.id.valor_total_6);
        txt_lame = findViewById(R.id.progress_1_7);
        valor_lame_inicial = findViewById(R.id.valor_encontrado_7);
        valor_lame_total = findViewById(R.id.valor_total_7);
        txt_lapm = findViewById(R.id.progress_1_8);
        valor_lapm_inicial = findViewById(R.id.valor_encontrado_8);
        valor_lapm_total = findViewById(R.id.valor_total_8);
        txt_lapr = findViewById(R.id.progress_1_10);
        valor_lapr_inicial = findViewById(R.id.valor_encontrado_10);
        valor_lapr_total = findViewById(R.id.valor_total_10);
        txt_lapt = findViewById(R.id.progress_1_11);
        valor_lapt_inicial = findViewById(R.id.valor_encontrado_11);
        valor_lapt_total = findViewById(R.id.valor_total_11);
        txt_lasi = findViewById(R.id.progress_1_12);
        valor_lasi_inicial = findViewById(R.id.valor_encontrado_12);
        valor_lasi_total = findViewById(R.id.valor_total_12);
        txt_outro = findViewById(R.id.progress_1_13);
        valor_outro_inicial = findViewById(R.id.valor_encontrado_13);
        valor_outro_total = findViewById(R.id.valor_total_13);

        itensModelArrayList = new ArrayList<>();
        itens_checados = new ArrayList<>();

        buscarDados();

    }

    private void buscarDadosEncontrados(String nome_inventario) {

        databaseReference = FirebaseDatabase.getInstance().getReference().child(nome_inventario);



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                itens_checados.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Data_bmp dataBmp = dataSnapshot.getValue(Data_bmp.class);
                    itens_checados.add(dataBmp.getBMP());
                }

               // loading.setVisibility(View.INVISIBLE);
                getDados();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }

    private void buscarDados(){


        database = FirebaseDatabase.getInstance().getReference("Banco_BMP");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itensModelArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ItensModel itensModel = dataSnapshot.getValue(ItensModel.class);
                    itensModelArrayList.add(itensModel);
                }

                buscarDadosEncontrados(nome_banco);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDados(){


        for (String bmp : itens_checados){

        }

        for (ItensModel itens : itensModelArrayList ) {

            if (itens.getSetor().contentEquals("APR-P")){

                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_apr_p_check++;
                    }
                }

                qtd_apr_p++;


            }

            if (itens.getSetor().contentEquals("APR-PPP")){

                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_apr_ppp_check++;
                    }
                }
                qtd_apr_ppp++;
            }
            if (itens.getSetor().contentEquals("APR-PSC")){
                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_apr_psc_check++;
                    }
                }
                qtd_apr_psc++;
            }
            if (itens.getSetor().contentEquals("LAAI")){
                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_laai_check++;

                    }
                }
                qtd_laai++;
            }
            if (itens.getSetor().contentEquals("LAAQ")){
                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_laaq_check++;

                    }
                }
                qtd_laaq++;
            }
            if (itens.getSetor().contentEquals("LAII")){
                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_laii_check++;

                    }
                }
                qtd_laii++;
            }
            if (itens.getSetor().contentEquals("LAME")){
                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_lame_check++;

                    }
                }
                qtd_lame++;
            }
            if (itens.getSetor().contentEquals("LAPM")){
                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_lapm_check++;

                    }
                }
                qtd_lapm++;
            }
            if (itens.getSetor().contentEquals("LAPR")){
                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_lapr_check++;

                    }
                }
                qtd_lapr++;
            }
            if (itens.getSetor().contentEquals("LAPT")){

                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_lapt_check++;

                    }
                }
                qtd_lapt++;
            }

            if (itens.getSetor().contentEquals("LASI")){
                for (String bmp : itens_checados){
                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_lasi_check++;

                    }

                }
                qtd_lasi++;
            }
            if (itens.getSetor().contentEquals("OUTRO")){
                for (String bmp : itens_checados){

                    if (itens.getBMP().contentEquals(bmp)){

                        qtd_outro_check++;
                        Log.d("LASI", "Qtd Dados: "+qtd_lasi_check);
                    }
                }
                qtd_outro++;
            }

        }

        setDados();

    }

    private void setDados(){

        DecimalFormat df = new DecimalFormat("#.##");
        double progress = 0;
        int color = 0;


        valor_apr_p_total.setText(""+qtd_apr_p);
        Log.d("" , "progress: "+qtd_apr_p);
        valor_apr_p_inicial.setText(""+ qtd_apr_p_check);
        Log.d("" , "progress: "+qtd_apr_p_check);
        progress = calcularProgress(qtd_apr_p_check, qtd_apr_p);
        Log.d("" , "progress: "+progress);
        txt_apr_p.setText(df.format(progress) + "%");
        progressBar_APR_P.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_APR_P.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);


        valor_apr_ppp_total.setText(""+qtd_apr_ppp);
        valor_apr_ppp_inicial.setText(""+ qtd_apr_ppp_check);
        progress = calcularProgress(qtd_apr_ppp_check, qtd_apr_ppp);
        txt_apr_ppp.setText(df.format(progress) + "%");
        progressBar_APR_PPP.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_APR_PPP.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        valor_apr_psc_total.setText(""+qtd_apr_psc);
        valor_apr_psc_inicial.setText(""+ qtd_apr_psc_check);
        progress = calcularProgress(qtd_apr_psc_check, qtd_apr_psc);
        txt_apr_psc.setText(df.format(progress) + "%");
        progressBar_APR_PSC.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_APR_PSC.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        valor_laai_total.setText(""+qtd_laai);
        valor_laai_inicial.setText(""+ qtd_laai_check);
        progress = calcularProgress(qtd_laai_check, qtd_laai);
        txt_laai.setText(df.format(progress) + "%");
        progressBar_LAAI.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_LAAI.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        valor_laaq_total.setText(""+qtd_laaq);
        valor_laaq_inicial.setText(""+ qtd_laaq_check);
        progress = calcularProgress(qtd_laaq_check, qtd_laaq);
        txt_laaq.setText(df.format(progress) + "%");
        progressBar_LAAQ.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_LAAQ.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        valor_laii_total.setText(""+qtd_laii);
        valor_laii_inicial.setText(""+ qtd_laii_check);
        progress = calcularProgress(qtd_laii_check, qtd_laii);
        txt_laii.setText(df.format(progress) + "%");
        progressBar_LAII.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_LAII.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        valor_lapm_total.setText(""+qtd_lapm);
        valor_lapm_inicial.setText(""+ qtd_lapm_check);
        progress = calcularProgress(qtd_lapm_check, qtd_lapm);
        txt_lapm.setText(df.format(progress) + "%");
        progressBar_LAPM.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_LAPM.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        valor_lame_total.setText(""+qtd_lame);
        valor_lame_inicial.setText(""+ qtd_lame_check);
        progress = calcularProgress(qtd_lame_check, qtd_lame);
        txt_lame.setText(df.format(progress) + "%");
        progressBar_LAME.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_LAME.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        valor_lasi_total.setText(""+qtd_lasi);
        valor_lasi_inicial.setText(""+ qtd_lasi_check);
        progress = calcularProgress(qtd_lasi_check, qtd_lasi);
        txt_lasi.setText(df.format(progress) + "%");
        progressBar_LASI.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_LASI.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        valor_lapr_total.setText(""+qtd_lapr);
        valor_lapr_inicial.setText(""+ qtd_lapr_check);
        progress = calcularProgress(qtd_lapr_check, qtd_lapr);
        txt_lapr.setText(df.format(progress) + "%");
        progressBar_LAPR.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_LAPR.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        valor_lapt_total.setText(""+qtd_lapt);
        valor_lapt_inicial.setText(""+ qtd_lapt_check);
        progress = calcularProgress(qtd_lapt_check, qtd_lapt);
        txt_lapt.setText(df.format(progress) + "%");
        progressBar_LAPT.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_LAPT.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        valor_outro_total.setText(""+qtd_outro);
        valor_outro_inicial.setText(""+ qtd_outro_check);
        progress = calcularProgress(qtd_outro_check, qtd_outro);
        txt_outro.setText(df.format(progress) + "%");
        progressBar_OUTRO.setProgress((int) progress);
        color = updateProgressBarColor(progress);
        progressBar_OUTRO.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);


    }

    public double calcularProgress(long qtd_check, long qtd_total) {

        if (qtd_total == 0) {
            // Retorna 0 ou outro valor adequado, dependendo da lógica do seu programa
            return 0.0; // ou lançar uma exceção, dependendo do contexto
        }

        double result = ((double) qtd_check /qtd_total ) * 100 ;
        return   result;
    }

    private int updateProgressBarColor(double progress) {
        int green = (int) (255 * (progress / 100));
        int red = 255 - green;
        int color = Color.rgb(red, green, 0); // Mistura de vermelho e verde com base na porcentagem

        return color;
    }
}