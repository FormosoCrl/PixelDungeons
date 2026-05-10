package com.example.pixeldungeons.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.data.GameMapRepository;
import com.example.pixeldungeons.model.GameMap;
import com.example.pixeldungeons.ui.adapter.MapAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapManagerActivity extends AppCompatActivity {

    private MapAdapter mapAdapter;

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

        mapAdapter = new MapAdapter(GameMapRepository.getMaps(), position -> {
            GameMapRepository.setVisible(position);
            mapAdapter.notifyDataSetChanged();
        });

        mapsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        mapsRecycler.setAdapter(mapAdapter);

        uploadButton.setOnClickListener(v -> showAddMapDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapAdapter != null) mapAdapter.notifyDataSetChanged();
    }

    private void showAddMapDialog() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Nombre del mapa");
        container.addView(nameInput);

        new AlertDialog.Builder(this)
                .setTitle("Añadir mapa")
                .setView(container)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Introduce el nombre del mapa", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    GameMapRepository.addMap(new GameMap(name, false));
                    mapAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Mapa creado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
