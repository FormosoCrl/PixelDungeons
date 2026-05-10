package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.data.HeroRepository;
import com.example.pixeldungeons.ui.adapter.PlayerAdapter;

public class MasterDashboardActivity extends AppCompatActivity {

    private PlayerAdapter playerAdapter;

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

        TextView roomNameTitle = findViewById(R.id.room_name_title);
        RecyclerView playersRecycler = findViewById(R.id.players_recycler);
        Button manageItemsButton = findViewById(R.id.manage_items_button);
        Button manageMapsButton = findViewById(R.id.manage_maps_button);
        Button closeRoomButton = findViewById(R.id.close_room_button);

        String roomName = getIntent().getStringExtra("room_name");
        if (roomName != null && !roomName.isEmpty()) {
            roomNameTitle.setText("Sala: " + roomName);
        }

        playerAdapter = new PlayerAdapter(HeroRepository.getHeroes(), hero -> {
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

        manageItemsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MasterDashboardActivity.this, ItemManagerActivity.class);
            startActivity(intent);
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        if (playerAdapter != null) playerAdapter.notifyDataSetChanged();
    }
}
