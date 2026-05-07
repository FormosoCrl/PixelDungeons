package com.example.pixeldungeons.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.GameMap;
import com.example.pixeldungeons.ui.adapter.MapAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView mapsRecycler = findViewById(R.id.maps_recycler);
        FloatingActionButton uploadButton = findViewById(R.id.upload_map_button);

        List<GameMap> maps = new ArrayList<>(Arrays.asList(
                new GameMap("Cripta nivel 1", true),
                new GameMap("Bosque Oscuro", false),
                new GameMap("Torre del Mago", false),
                new GameMap("Caverna del Dragón", false)
        ));

        MapAdapter mapAdapter = new MapAdapter(maps, position -> {
            for (int i = 0; i < maps.size(); i++) {
                maps.get(i).setVisible(i == position);
            }
            mapsRecycler.getAdapter().notifyDataSetChanged();
        });

        mapsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        mapsRecycler.setAdapter(mapAdapter);

        uploadButton.setOnClickListener(v ->
                Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        );
    }
}
