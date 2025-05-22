
package com.ipev.controle_material;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ipev.controle_material.Model.ItensModel;
import com.ipev.controle_material.Model.JsonCacheManager;
import com.ipev.controle_material.Model.SincronizadorDeDados;
import com.ipev.controle_material.Model.VersaoManager;
import com.ipev.controle_material.databinding.ActivityEditarItensBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EditarItens extends AppCompatActivity {

    Button salvar;
    Long versao;
    CardView cardView;

    public ArrayList<ItensModel> itensModelArrayList;
    ProgressBar loading;
    AutoCompleteTextView auto_setor;
    AutoCompleteTextView auto_predio;
    AutoCompleteTextView auto_estado;
    TextInputEditText txt_sala , txt_bmp , txt_obs, txt_desc, txt_num_serie;
    ArrayAdapter<String> adapter_predio;
    ArrayAdapter<String> adapter_setor;
    ArrayList<String> salas_local, predio_local;
    DatabaseReference database;
    ArrayAdapter<String> adapter_estado;
    String nome_predio, nome_setor, nome_estado;
    int Id_child, tipo, BMP;
    AutoCompleteTextView autoSala;
    private ActivityEditarItensBinding binding;
    String str_setor, str_predio, str_sala, str_obs, str_desc, str_bmp, str_estado, str_num_serie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditarItensBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getDadosIntent();
        preencherCampos();
        setupDropdowns();
        //checkVersaoFirebase();

        binding.btnSalvarItem.setOnClickListener(v -> salvarItem());

    }

    private void showDialogCamposVazios() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarItens.this);
        builder.setTitle("Aviso");
        if (TextUtils.isEmpty(str_bmp)) {
            builder.setMessage("Insira o número de BMP").setPositiveButton("OK", null);
        } else if (TextUtils.isEmpty(str_sala)) {
            builder.setMessage("Insira a SALA que está alocado").setPositiveButton("OK", null);
        } else {
            builder.setMessage("Insira a Descrição do Item").setPositiveButton("OK", null);
        }
        builder.create().show();
    }

    private void salvarItem() {
        str_bmp = binding.textEditBmpEdit.getText().toString();
        str_sala = binding.textEditSalaEdit.getText().toString();
        str_desc = binding.textEditDescEdit.getText().toString();
        str_obs = binding.textEditObsEdit.getText().toString();
        str_predio = binding.autoCompletePredioEdit.getText().toString();
        str_setor = binding.autoCompleteSetorEdit.getText().toString();
        str_num_serie = binding.textEditSerial.getText().toString();

        if (!TextUtils.isEmpty(str_sala) && !TextUtils.isEmpty(str_bmp) && !TextUtils.isEmpty(str_desc)) {
            binding.progressEditar.setVisibility(View.VISIBLE);
            binding.cardviewEdit.setAlpha(0.3f);
            addCadastro();
        } else {
            showDialogCamposVazios();
        }
    }

    private void deleteItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarItens.this);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("itens");

        String idDoItem = "2";

        databaseRef.child(idDoItem).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Sucesso ao deletar
                    Toast.makeText(this, "Item deletado com sucesso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Falha ao deletar
                    Toast.makeText(this, "Erro ao deletar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void setupDropdowns() {
        String[] setores = getResources().getStringArray(R.array.setores);
        adapter_setor = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, setores);
        binding.autoCompleteSetorEdit.setAdapter(adapter_setor);

        String[] predios = getResources().getStringArray(R.array.predios);
        adapter_predio = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, predios);
        binding.autoCompletePredioEdit.setAdapter(adapter_predio);

        String[] salas = getResources().getStringArray(R.array.salas);
        ArrayAdapter<String> adapterSala = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, salas);
        binding.textEditSalaEdit.setAdapter(adapterSala);
        binding.textEditSalaEdit.setThreshold(1);
        binding.textEditSalaEdit.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        binding.autoCompleteSetorEdit.setOnItemClickListener((parent, view, position, id) -> str_setor = parent.getItemAtPosition(position).toString());
        binding.autoCompletePredioEdit.setOnItemClickListener((parent, view, position, id) -> str_predio = parent.getItemAtPosition(position).toString());

    }

    private void preencherCampos() {
        binding.textEditSalaEdit.setText(str_sala);
        binding.textEditBmpEdit.setText(str_bmp);
        binding.textEditSerial.setText(str_num_serie);
        binding.textEditDescEdit.setText(str_desc);
        binding.textEditObsEdit.setText(str_obs);
        binding.autoCompleteSetorEdit.setText(str_setor);
        binding.autoCompletePredioEdit.setText(str_predio);
        binding.textEditDescEdit.setEnabled(false);
        binding.textEditBmpEdit.setEnabled(false);
    }

    public static void  removeDuplicates(ArrayList<String> lista) {
        // Cria um HashSet para armazenar itens únicos
        HashSet<String> conjunto = new HashSet<>();

        // Itera sobre a lista original
        for (int i = 0; i < lista.size(); i++) {
            // Se o conjunto não contiver o elemento, adiciona ao conjunto e mantém na lista
            if (!conjunto.contains(lista.get(i))) {
                conjunto.add(lista.get(i));

            } else { // Se o conjunto já contém o elemento, remove da lista
                lista.remove(i);
                i--; // Ajusta o índice após a remoção do elemento
            }
        }
    }

    private void carregarDadosDoArquivoLocal() {

        salas_local = new ArrayList<>();
        predio_local = new ArrayList<>();

        try {
            JSONArray array = JsonCacheManager.obterDados(this);
            if (array.length() == 0) {
                Toast.makeText(this, "Sem dados offline disponíveis", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.INVISIBLE);
                return;
            }

            salas_local.clear();
            predio_local.clear();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
               salas_local.add(obj.getString("sala"));
               predio_local.add(obj.getString("predio"));
            }

            removeDuplicates(salas_local);
            Collections.sort(salas_local);
            removeDuplicates(predio_local);
            Collections.sort(predio_local);

            Toast.makeText(this, "Salas Carregados Localmente - Versao : ", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao carregar dados locais", Toast.LENGTH_SHORT).show();
        }
    }

    public void addCadastro(){

        Map<String, Object> map = new HashMap<>();

        map.put("BMP", str_bmp);
        map.put("setor", str_setor);
        map.put("predio", str_predio);
        map.put("sala", str_sala);
        map.put("descricao", str_desc);
        map.put("observacao", str_obs);
        map.put("serial", str_num_serie);

        FirebaseDatabase.getInstance().getReference("Banco_Dados_IPEV").child("itens")
                .child(String.valueOf(Id_child))
                .updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.progressEditar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getBaseContext(), "ITEM EDITADO COM SUCESSO", Toast.LENGTH_SHORT).show();
                        atualizaVersaoFirebase();
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditarItens.this);
                        builder.setTitle("Erro");
                        builder.setMessage("Erro ao editar o item");
                        builder.setPositiveButton("OK", null);
                        builder.show();
                    }
                });

    }

    private void checkVersaoFirebase(){

        DatabaseReference versaoRef = FirebaseDatabase.getInstance().getReference("Banco_Dados_IPEV").child("timestamp");

        versaoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    versao = snapshot.getValue(Long.class);
                }  else {
                    Toast.makeText(EditarItens.this, "Banco de Dados OFFLINE", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditarItens.this, "Erro ao buscar versão: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizaVersaoFirebase() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Banco_Dados_IPEV").child("timestamp");
        database.setValue(VersaoManager.gerarVersaoTimestamp());
    }

    private void buscarDadosCadastrados(){

        itensModelArrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference("Banco_Dados_IPEV").child("itens");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itensModelArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ItensModel itensModel = dataSnapshot.getValue(ItensModel.class);
                    itensModelArrayList.add(itensModel);

                }

                getDadosInsereEditar();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });


    }

    public void getDadosInsereEditar(){

        for (ItensModel item : itensModelArrayList) {

            String numero_bmp = String.valueOf(BMP);

            if (numero_bmp.equals(item.getBMP())){

                Id_child = item.getId();
                txt_sala.setText(item.getSala());
                txt_bmp.setText(item.getBMP());
                txt_desc.setText(item.getDescricao());
                txt_desc.setEnabled(false);
                txt_bmp.setEnabled(false);
                txt_num_serie.setText(item.getSerial());
                txt_obs.setText(item.getObservacao());
                auto_predio.setText(item.getPredio());
                auto_estado.setText(item.getEstado());
                auto_setor.setText(item.getSetor());

            } else {
                Log.d("", "nenhum encontrado");
            }
        }
    }
    public void getDadosIntent(){

        Intent intent = getIntent();
        tipo = intent.getIntExtra("alterar", 0);

        if (tipo == 1) {

            BMP = intent.getIntExtra("bmp", 0);
            buscarDadosCadastrados();


        } else {
            Id_child = intent.getIntExtra("id", 0);
            str_setor =  intent.getStringExtra("setor");
            str_predio = intent.getStringExtra("predio");
            str_sala = intent.getStringExtra("sala");
            str_bmp = intent.getStringExtra("bmp");
            str_desc = intent.getStringExtra("descricao");
            str_obs = intent.getStringExtra("observacao");
            str_estado = intent.getStringExtra("estado");
            str_num_serie = intent.getStringExtra("serial");

        }



    }
}
