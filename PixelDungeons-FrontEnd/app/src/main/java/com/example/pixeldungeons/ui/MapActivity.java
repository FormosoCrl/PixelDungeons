package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pixeldungeons.R;

public class MapActivity extends AppCompatActivity {

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

        boolean isMaster = getIntent().getBooleanExtra("is_master", false);

        Button statsButton = findViewById(R.id.btn_stats_from_map);
        Button inventoryButton = findViewById(R.id.btn_inv_from_map);

        statsButton.setOnClickListener(v -> finish());

        inventoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, InventoryActivity.class);
            intent.putExtra("is_master", isMaster);
            startActivity(intent);
        });
    }
}