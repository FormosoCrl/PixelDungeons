package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.network.ApiClient;
import com.example.pixeldungeons.ui.adapter.SalaAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LobbyActivity extends AppCompatActivity {

    private int userId;
    private String username;
    private SalaAdapter salaAdapter;
    private final List<Map<String, Object>> misSalas = new ArrayList<>();
    private TextView misSlasLabel;
    private RecyclerView salasRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.apply(this);
        setContentView(R.layout.activity_lobby);
        ThemeHelper.setup(this, findViewById(R.id.theme_switch));
        ThemeHelper.adjustMarginForStatusBar(findViewById(R.id.theme_toggle));

        userId   = getIntent().getIntExtra("userId", -1);
        username = getIntent().getStringExtra("username");

        misSlasLabel  = findViewById(R.id.mis_salas_label);
        salasRecycler = findViewById(R.id.salas_recycler);

        salaAdapter = new SalaAdapter(misSalas, sala -> {
            int salaId    = ((Number) sala.get("id")).intValue();
            String nombre = (String) sala.get("nombre");
            String codigo = (String) sala.get("codigo");
            Intent intent = new Intent(this, MasterDashboardActivity.class);
            intent.putExtra("salaId",    salaId);
            intent.putExtra("salaNombre", nombre);
            intent.putExtra("salaCodigo", codigo);
            intent.putExtra("userId",    userId);
            intent.putExtra("username",  username);
            startActivity(intent);
        });

        salasRecycler.setLayoutManager(new LinearLayoutManager(this));
        salasRecycler.setAdapter(salaAdapter);

        findViewById(R.id.search_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchRoomActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        findViewById(R.id.create_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateRoomActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMisSalas();
    }

    private void cargarMisSalas() {
        ApiClient.getService().getSalas().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                misSalas.clear();
                for (Map<String, Object> s : response.body()) {
                    Object masterId = s.get("master_id");
                    if (masterId != null && ((Number) masterId).intValue() == userId) {
                        misSalas.add(s);
                    }
                }
                salaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
            }
        });
    }
}

