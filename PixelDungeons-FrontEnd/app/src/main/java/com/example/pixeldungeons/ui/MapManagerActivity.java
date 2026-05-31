package com.example.pixeldungeons.ui;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.GameMap;
import com.example.pixeldungeons.network.ApiClient;
import com.example.pixeldungeons.ui.adapter.MapAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapManagerActivity extends AppCompatActivity {

    private MapAdapter mapAdapter;
    private final List<GameMap> maps = new ArrayList<>();
    private int salaId;
    private int mapsRetries = 0;
    private static final int MAX_MAPS_RETRIES = 3;
    private String pendingImageBase64 = "";
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.apply(this);
        setContentView(R.layout.activity_map_manager);
        ThemeHelper.setup(this, findViewById(R.id.theme_switch));

        salaId = getIntent().getIntExtra("salaId", -1);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        pendingImageBase64 = uriToBase64(uri);
                    }
                    showAddMapDialog();
                }
        );

        RecyclerView mapsRecycler = findViewById(R.id.maps_recycler);
        FloatingActionButton uploadButton = findViewById(R.id.upload_map_button);

        mapAdapter = new MapAdapter(maps, position -> {
            GameMap map = maps.get(position);
            boolean mostrarEste = !map.isVisible();

            if (!mostrarEste) {
                activarMapa(map.getId(), false);
                return;
            }

            List<GameMap> aOcultar = new ArrayList<>();
            for (GameMap otro : maps) {
                if (otro.isVisible() && otro.getId() != map.getId()) {
                    aOcultar.add(otro);
                }
            }

            if (aOcultar.isEmpty()) {
                activarMapa(map.getId(), true);
            } else {
                ocultarSecuencialYActivar(aOcultar, 0, map.getId());
            }
        });

        mapsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        mapsRecycler.setAdapter(mapAdapter);

        uploadButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMapas();
    }

    private void ocultarSecuencialYActivar(List<GameMap> aOcultar, int index, int idFinal) {
        if (index >= aOcultar.size()) {
            activarMapa(idFinal, true);
            return;
        }
        Map<String, Object> body = new HashMap<>();
        body.put("visible", false);
        ApiClient.getService().actualizarMapa(aOcultar.get(index).getId(), body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                ocultarSecuencialYActivar(aOcultar, index + 1, idFinal);
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                ocultarSecuencialYActivar(aOcultar, index + 1, idFinal);
            }
        });
    }

    private void activarMapa(int mapaId, boolean visible) {
        Map<String, Object> body = new HashMap<>();
        body.put("visible", visible);
        ApiClient.getService().actualizarMapa(mapaId, body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) cargarMapas();
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(MapManagerActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarMapas() {
        ApiClient.getService().getMapas(salaId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mapsRetries = 0;
                    maps.clear();
                    for (Map<String, Object> m : response.body()) {
                        String img = m.get("image") != null ? (String) m.get("image") : "";
                        GameMap map = new GameMap((String) m.get("name"), (Boolean) m.get("visible"), img);
                        map.setId(((Number) m.get("id")).intValue());
                        maps.add(map);
                    }
                    mapAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                if (mapsRetries < MAX_MAPS_RETRIES) {
                    mapsRetries++;
                    new android.os.Handler(android.os.Looper.getMainLooper())
                            .postDelayed(MapManagerActivity.this::cargarMapas, 800);
                } else {
                    Toast.makeText(MapManagerActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAddMapDialog() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Nombre del mapa");
        container.addView(nameInput);

        ImageView preview = new ImageView(this);
        int previewHeight = (int) (200 * getResources().getDisplayMetrics().density);
        preview.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, previewHeight));
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        preview.setPadding(0, padding / 2, 0, 0);

        if (!pendingImageBase64.isEmpty()) {
            byte[] bytes = Base64.decode(pendingImageBase64, Base64.NO_WRAP);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            preview.setImageBitmap(bmp);
        } else {
            preview.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        container.addView(preview);

        new AlertDialog.Builder(this)
                .setTitle("Añadir mapa")
                .setView(container)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Introduce el nombre del mapa", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<String, Object> body = new HashMap<>();
                    body.put("name", name);
                    body.put("image", pendingImageBase64);
                    pendingImageBase64 = "";
                    ApiClient.getService().crearMapa(salaId, body).enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            if (response.isSuccessful()) {
                                cargarMapas();
                                Toast.makeText(MapManagerActivity.this, "Mapa creado", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            Toast.makeText(MapManagerActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", (d, w) -> pendingImageBase64 = "")
                .show();
    }

    private String uriToBase64(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) return "";
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (bitmap == null) return "";
            // Reescalamos manteniendo la proporción: el lado más largo cae a
            // MAX_SIDE, el otro se calcula a partir del aspect ratio original.
            // Antes forzábamos 400x400 a saco y aplastaba imágenes rectangulares.
            final int MAX_SIDE = 400;
            int origW = bitmap.getWidth();
            int origH = bitmap.getHeight();
            int newW, newH;
            if (origW >= origH) {
                newW = Math.min(origW, MAX_SIDE);
                newH = Math.max(1, Math.round(origH * (newW / (float) origW)));
            } else {
                newH = Math.min(origH, MAX_SIDE);
                newW = Math.max(1, Math.round(origW * (newH / (float) origH)));
            }
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, newW, newH, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }
}

