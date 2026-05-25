package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.network.ApiClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);

        int userId = getIntent().getIntExtra("userId", -1);
        String username = getIntent().getStringExtra("username");

        EditText codigoInput = findViewById(R.id.name_input);
        Button searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> {
            String codigo = codigoInput.getText().toString().trim();

            if (codigo.isEmpty()) {
                Toast.makeText(this, "Introduce el código de la sala", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiClient.getService().buscarSala(codigo).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        int salaId = ((Double) response.body().get("id")).intValue();
                        String nombre = (String) response.body().get("nombre");
                        Intent intent = new Intent(SearchRoomActivity.this, CharacterListActivity.class);
                        intent.putExtra("salaId", salaId);
                        intent.putExtra("salaNombre", nombre);
                        intent.putExtra("userId", userId);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SearchRoomActivity.this, "Sala no encontrada", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(SearchRoomActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
