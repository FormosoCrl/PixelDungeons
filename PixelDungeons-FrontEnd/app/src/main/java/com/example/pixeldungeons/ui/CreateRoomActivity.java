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
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        int userId = getIntent().getIntExtra("userId", -1);
        String username = getIntent().getStringExtra("username");

        EditText nameInput = findViewById(R.id.name_input);
        Button createButton = findViewById(R.id.create_button);

        createButton.setOnClickListener(v -> {
            String nombre = nameInput.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, "Introduce el nombre de la sala", Toast.LENGTH_SHORT).show();
                return;
            }

            String codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

            Map<String, Object> body = new HashMap<>();
            body.put("nombre", nombre);
            body.put("codigo", codigo);
            body.put("master_id", userId);

            ApiClient.getService().crearSala(body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        int salaId = ((Double) response.body().get("id")).intValue();
                        Toast.makeText(CreateRoomActivity.this, "Código de sala: " + codigo, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CreateRoomActivity.this, MasterDashboardActivity.class);
                        intent.putExtra("salaId", salaId);
                        intent.putExtra("salaNombre", nombre);
                        intent.putExtra("salaCodigo", codigo);
                        intent.putExtra("userId", userId);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CreateRoomActivity.this, "Error al crear la sala", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(CreateRoomActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
