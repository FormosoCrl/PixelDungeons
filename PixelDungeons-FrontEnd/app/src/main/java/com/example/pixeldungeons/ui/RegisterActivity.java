package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.network.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText usernameInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);
        Button registerButton = findViewById(R.id.register_button);
        Button loginButton = findViewById(R.id.login_button);

        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> body = new HashMap<>();
            body.put("username", username);
            body.put("password", password);

            ApiClient.getService().registro(body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        int userId = ((Double) response.body().get("id")).intValue();
                        String user = (String) response.body().get("username");
                        Toast.makeText(RegisterActivity.this, "Cuenta creada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LobbyActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("username", user);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}
