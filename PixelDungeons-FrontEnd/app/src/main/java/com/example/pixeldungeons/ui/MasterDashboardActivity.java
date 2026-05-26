package com.example.pixeldungeons.ui;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.Hero;
import com.example.pixeldungeons.network.ApiClient;
import com.example.pixeldungeons.ui.adapter.PlayerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasterDashboardActivity extends AppCompatActivity {

    private PlayerAdapter playerAdapter;
    private final List<Hero> heroes = new ArrayList<>();
    private int salaId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.apply(this);
        setContentView(R.layout.activity_master_dashboard);
        ThemeHelper.setup(this, findViewById(R.id.theme_switch));
        ThemeHelper.adjustMarginForStatusBar(findViewById(R.id.theme_toggle));

        salaId = getIntent().getIntExtra("salaId", -1);
        userId = getIntent().getIntExtra("userId", -1);
        String salaNombre = getIntent().getStringExtra("salaNombre");
        String salaCodigo = getIntent().getStringExtra("salaCodigo");
        String username   = getIntent().getStringExtra("username");

        TextView roomNameTitle = findViewById(R.id.room_name_title);
        TextView roomCodeLabel = findViewById(R.id.room_code_label);
        if (salaNombre != null) roomNameTitle.setText("Sala: " + salaNombre);
        if (salaCodigo != null) roomCodeLabel.setText("CÃ³digo: " + salaCodigo);

        RecyclerView playersRecycler = findViewById(R.id.players_recycler);
        Button manageItemsButton = findViewById(R.id.manage_items_button);
        Button manageMapsButton  = findViewById(R.id.manage_maps_button);
        Button closeRoomButton   = findViewById(R.id.close_room_button);

        playerAdapter = new PlayerAdapter(heroes, hero -> {
            Intent intent = new Intent(this, CharacterDetailActivity.class);
            intent.putExtra("heroId",    hero.getId());
            intent.putExtra("name",      hero.getName());
            intent.putExtra("race",      hero.getRace());
            intent.putExtra("heroClass", hero.getHeroClass());
            intent.putExtra("hp",        hero.getHp());
            intent.putExtra("maxHp",     hero.getMaxHp());
            intent.putExtra("str",       hero.getStr());
            intent.putExtra("dex",       hero.getDex());
            intent.putExtra("defence",   hero.getDefence());
            intent.putExtra("mana",      hero.getMana());
            intent.putExtra("userId",    userId);
            intent.putExtra("salaId",    salaId);
            intent.putExtra("is_master", true);
            startActivity(intent);
        });

        playersRecycler.setLayoutManager(new LinearLayoutManager(this));
        playersRecycler.setAdapter(playerAdapter);

        manageItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ItemManagerActivity.class);
            intent.putExtra("salaId", salaId);
            startActivity(intent);
        });

        manageMapsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapManagerActivity.class);
            intent.putExtra("salaId", salaId);
            startActivity(intent);
        });

        closeRoomButton.setOnClickListener(v ->
            ApiClient.getService().eliminarSala(salaId).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    volverAlLobby(username);
                }
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    volverAlLobby(username);
                }
            })
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHeroes();
    }

    private void volverAlLobby(String username) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void cargarHeroes() {
        ApiClient.getService().getHeroesDeSala(salaId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    heroes.clear();
                    for (Map<String, Object> m : response.body()) {
                        Hero h = new Hero(
                                (String) m.get("name"),
                                (String) m.get("race"),
                                (String) m.get("hero_class"),
                                ((Number)m.get("hp")).intValue(),
                                ((Number)m.get("max_hp")).intValue(),
                                ((Number)m.get("strength")).intValue(),
                                ((Number)m.get("dex")).intValue(),
                                ((Number)m.get("defence")).intValue(),
                                ((Number)m.get("mana")).intValue()
                        );
                        h.setId(((Number)m.get("id")).intValue());
                        heroes.add(h);
                    }
                    playerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(MasterDashboardActivity.this, "Error de conexiÃ³n", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

