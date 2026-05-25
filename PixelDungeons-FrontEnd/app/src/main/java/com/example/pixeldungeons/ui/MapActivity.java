package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.network.ApiClient;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private TextView mapName;
    private TextView mapEmpty;
    private ImageView mapImage;
    private int salaId;
    private Intent received;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        received = getIntent();
        salaId   = received.getIntExtra("salaId", -1);

        mapName  = findViewById(R.id.map_name);
        mapEmpty = findViewById(R.id.map_empty);
        mapImage = findViewById(R.id.map_image);

        Button statsButton     = findViewById(R.id.btn_stats_from_map);
        Button inventoryButton = findViewById(R.id.btn_inv_from_map);

        statsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CharacterDetailActivity.class).putExtras(received));
            finish();
        });

        inventoryButton.setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class).putExtras(received));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMapa();
    }

    private void cargarMapa() {
        ApiClient.getService().getMapas(salaId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String nombre = null;
                    String imagen = "";
                    for (Map<String, Object> m : response.body()) {
                        if (Boolean.TRUE.equals(m.get("visible"))) {
                            nombre = (String) m.get("name");
                            imagen = m.get("image") != null ? (String) m.get("image") : "";
                            break;
                        }
                    }
                    if (nombre != null) {
                        mapName.setText(nombre);
                        mapName.setVisibility(View.VISIBLE);
                        mapEmpty.setVisibility(View.GONE);

                        if (!imagen.isEmpty()) {
                            byte[] bytes = Base64.decode(imagen, Base64.NO_WRAP);
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            mapImage.setImageBitmap(bmp);
                            mapImage.setVisibility(View.VISIBLE);
                        } else {
                            mapImage.setVisibility(View.GONE);
                        }
                    } else {
                        mapName.setVisibility(View.GONE);
                        mapImage.setVisibility(View.GONE);
                        mapEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
