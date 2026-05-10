package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.data.GameMapRepository;
import com.example.pixeldungeons.model.GameMap;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    private TextView mapName;
    private TextView mapEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent received = getIntent();
        mapName = findViewById(R.id.map_name);
        mapEmpty = findViewById(R.id.map_empty);

        Button statsButton = findViewById(R.id.btn_stats_from_map);
        Button inventoryButton = findViewById(R.id.btn_inv_from_map);

        statsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, CharacterDetailActivity.class);
            intent.putExtras(received);
            startActivity(intent);
            finish();
        });

        inventoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, InventoryActivity.class);
            intent.putExtras(received);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMap();
    }

    private void refreshMap() {
        GameMap visible = null;
        List<GameMap> maps = GameMapRepository.getMaps();
        for (GameMap map : maps) {
            if (map.isVisible()) {
                visible = map;
                break;
            }
        }
        if (visible != null) {
            mapName.setText(visible.getName());
            mapName.setVisibility(View.VISIBLE);
            mapEmpty.setVisibility(View.GONE);
        } else {
            mapName.setVisibility(View.GONE);
            mapEmpty.setVisibility(View.VISIBLE);
        }
    }
}
