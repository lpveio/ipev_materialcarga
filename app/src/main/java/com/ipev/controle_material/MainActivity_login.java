package com.ipev.controle_material;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.ipev.controle_material.Model.SetupView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity_login extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button cadastroButton;
    private FirebaseAuth mAuth;
    private TextView appVersionLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            // Já está logado, vai para a tela principal
            startActivity(new Intent(MainActivity_login.this, MainActivity.class));
            finish();
            return;
        }


        setContentView(R.layout.activity_main_login);

        mAuth = FirebaseAuth.getInstance();

        usernameEditText = findViewById(R.id.username);
        appVersionLogin = findViewById(R.id.appVersionLogin);
        appVersionLogin.setText("v" + SetupView.getVersionName(this));
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();
            }
        });


        cadastroButton = findViewById(R.id.cadastro_button);

        cadastroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity_login.this, Login_Cadastro.class);
                startActivity(intent);
                Log.d("", "cadastrando");

            }
        });

        TextView forgotPassword = findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria o Dialog com layout personalizado
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
                EditText emailInput = dialogView.findViewById(R.id.email_reset_input);
                Button resetButton = dialogView.findViewById(R.id.reset_button);

                androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity_login.this)
                        .setView(dialogView)
                        .setTitle("Redefinir Senha")
                        .create();

                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = emailInput.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            emailInput.setError("Digite seu e-mail");
                            return;
                        }

                        mAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity_login.this,
                                                "Se o Email estiver cadastrado, você receberá um link de redefinição em " + email,
                                                Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(MainActivity_login.this,
                                                "Erro: " + task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });

                dialog.show();
            }
        });

    }

    private void loginUsuario() {
        String email = usernameEditText.getText().toString().trim();
        String senha = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(MainActivity_login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login OK, vai para MainActivity
                            Toast.makeText(MainActivity_login.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity_login.this, MainActivity.class));
                            finish(); // Finaliza tela de login
                        } else {
                            Toast.makeText(MainActivity_login.this, "Erro no login: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void navigateToMainActivity() {
        // Substitua MainActivity pela sua Activity principal
        Intent mainIntent = new Intent(MainActivity_login.this, MainActivity.class);
        startActivity(mainIntent);
        finish(); // Opcional: fecha a tela de login para que o usuário não possa voltar para ela
    }

    private void loginUser() {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validação básica dos campos
        if (TextUtils.isEmpty(email)) {
            usernameEditText.setError("O email é obrigatório.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("A senha é obrigatória.");
            return;
        }

        // Tenta fazer o login com email e senha
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login bem-sucedido
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity_login.this, "Login realizado com sucesso.",
                                    Toast.LENGTH_SHORT).show();
                            // Navega para a tela principal
                            navigateToMainActivity();
                        } else {
                            // Falha no login
                            Toast.makeText(MainActivity_login
                                            .this, "Falha na autenticação: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}