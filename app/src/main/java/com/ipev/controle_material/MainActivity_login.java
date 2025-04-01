package com.ipev.controle_material;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity_login extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_login);

        usernameEditText = findViewById(R.id.username);

        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.equals("admin") && password.equals("aprp2024")) {
                    Intent intent = new Intent(MainActivity_login.this, MainActivity.class);
                    intent.putExtra("USERNAME", username);  // Pass the username to MainActivity
                    startActivity(intent);
                    finish();
                } else if (username.equals("usuario") && password.equals("aprp2024")) {
                    Intent intent = new Intent(MainActivity_login.this, MainActivity.class);
                    intent.putExtra("USERNAME", username);  // Pass the username to MainActivity
                    startActivity(intent);
                    finish();
                } else {
                    usernameEditText.setError("Usúario ou Senha está incorreta");
                    passwordEditText.setError("Usúario ou Senha está incorreta");
                }


            }
        });

    }
}