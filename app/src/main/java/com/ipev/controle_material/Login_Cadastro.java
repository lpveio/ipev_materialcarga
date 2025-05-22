package com.ipev.controle_material;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class Login_Cadastro extends AppCompatActivity {

    private EditText editTextEmail, editTextSenha, editTextNome;
    private AutoCompleteTextView autoCompleteSetor;
    private Button buttonCadastrar;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cadastro); // use o nome do seu arquivo XML

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        String[] setores = getResources().getStringArray(R.array.setores);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        editTextNome = findViewById(R.id.editTextNome);
        autoCompleteSetor = findViewById(R.id.auto_complete_setor_cadastro);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);

        // Configura o AutoCompleteTextView com os setores
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, setores);
        autoCompleteSetor.setAdapter(adapter);

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarUsuario();
            }
        });
    }

    private void cadastrarUsuario() {
        String email = editTextEmail.getText().toString().trim();
        String senha = editTextSenha.getText().toString().trim();
        String nome = editTextNome.getText().toString().trim();
        String setor = autoCompleteSetor.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty() || setor.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostra a progress bar e desabilita o botão
        progressBar.setVisibility(View.VISIBLE);
        buttonCadastrar.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", email);
                            userMap.put("setor", setor);
                            userMap.put("nome", nome);

                            db.collection("usuarios").document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(Login_Cadastro.this, "Cadastro completo!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        buttonCadastrar.setEnabled(true);

                                        //avanca para tela de login
                                        Intent intent = new Intent(Login_Cadastro.this, MainActivity.class);
                                        startActivity(intent);
                                        finish(); // finaliza Login_Cadastro para não voltar
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Login_Cadastro.this, "Erro ao salvar setor: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        buttonCadastrar.setEnabled(true);
                                    });

                        } else {
                            Toast.makeText(Login_Cadastro.this, "Erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            buttonCadastrar.setEnabled(true);
                        }
                    }
                });
    }
}
