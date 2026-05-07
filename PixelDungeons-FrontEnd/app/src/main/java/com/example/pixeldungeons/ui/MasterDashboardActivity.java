package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.Hero;
import com.example.pixeldungeons.ui.adapter.PlayerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MasterDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_master_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView playersRecycler = findViewById(R.id.players_recycler);
        Button manageMapsButton = findViewById(R.id.manage_maps_button);
        Button closeRoomButton = findViewById(R.id.close_room_button);

        List<Hero> players = new ArrayList<>(Arrays.asList(
                new Hero("Aragorn", "Humano", "Guerrero", 30, 30, 15, 18, 5, 0),
                new Hero("Legolas", "Elfo", "Arquero", 22, 22, 10, 28, 3, 12),
                new Hero("Gimli", "Enano", "Berserker", 35, 35, 22, 8, 8, 0)
        ));

        PlayerAdapter playerAdapter = new PlayerAdapter(players, hero -> {
            Intent intent = new Intent(MasterDashboardActivity.this, CharacterDetailActivity.class);
            intent.putExtra("name", hero.getName());
            intent.putExtra("race", hero.getRace());
            intent.putExtra("heroClass", hero.getHeroClass());
            intent.putExtra("hp", hero.getHp());
            intent.putExtra("maxHp", hero.getMaxHp());
            intent.putExtra("str", hero.getStr());
            intent.putExtra("dex", hero.getDex());
            intent.putExtra("defence", hero.getDefence());
            intent.putExtra("mana", hero.getMana());
            intent.putExtra("is_master", true);
            startActivity(intent);
        });

        playersRecycler.setLayoutManager(new LinearLayoutManager(this));
        playersRecycler.setAdapter(playerAdapter);

        manageMapsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MasterDashboardActivity.this, MapManagerActivity.class);
            startActivity(intent);
        });

        closeRoomButton.setOnClickListener(v -> {
            Intent intent = new Intent(MasterDashboardActivity.this, LobbyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}