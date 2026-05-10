package com.example.pixeldungeons.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.data.ItemRepository;
import com.example.pixeldungeons.data.PlayerInventoryRepository;
import com.example.pixeldungeons.model.Item;
import com.example.pixeldungeons.ui.adapter.ItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private ItemAdapter itemAdapter;
    private TextView emptyText;
    private RecyclerView inventoryRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent received = getIntent();
        boolean isMaster = received.getBooleanExtra("is_master", false);

        emptyText = findViewById(R.id.inventory_empty);
        inventoryRecycler = findViewById(R.id.inventory_recycler);
        Button statsButton = findViewById(R.id.btn_stats_from_inv);
        Button mapButton = findViewById(R.id.btn_map_from_inv);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_item);

        itemAdapter = new ItemAdapter(PlayerInventoryRepository.getInventory(), isMaster, position -> {
            Item item = PlayerInventoryRepository.getInventory().get(position);
            if (isMaster) {
                PlayerInventoryRepository.removeOne(position);
                Toast.makeText(this, "Quitado " + item.getName() + " (x1)", Toast.LENGTH_SHORT).show();
            } else if (item.isConsumable()) {
                PlayerInventoryRepository.useItem(position);
                Toast.makeText(this, "Has usado " + item.getName(), Toast.LENGTH_SHORT).show();
            } else {
                PlayerInventoryRepository.equipItem(position);
                Toast.makeText(this, "Equipado: " + item.getName(), Toast.LENGTH_SHORT).show();
            }
            itemAdapter.notifyDataSetChanged();
        });
        inventoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        inventoryRecycler.setAdapter(itemAdapter);

        if (isMaster) {
            fabAdd.setVisibility(View.VISIBLE);
            fabAdd.setOnClickListener(v -> showAddItemDialog());
        }

        statsButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, CharacterDetailActivity.class);
            intent.putExtras(received);
            startActivity(intent);
            finish();
        });

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, MapActivity.class);
            intent.putExtras(received);
            startActivity(intent);
            finish();
        });
    }

    private void showAddItemDialog() {
        List<Item> catalog = ItemRepository.getItems();
        if (catalog.isEmpty()) {
            Toast.makeText(this, "No hay objetos en el catálogo", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] itemNames = new String[catalog.size()];
        for (int i = 0; i < catalog.size(); i++) {
            itemNames[i] = catalog.get(i).getName() + " (" + catalog.get(i).getType() + ")";
        }
        new AlertDialog.Builder(this)
                .setTitle("Dar objeto al jugador")
                .setItems(itemNames, (dialog, which) -> {
                    PlayerInventoryRepository.giveItem(catalog.get(which));
                    itemAdapter.notifyDataSetChanged();
                    refreshEmptyState();
                    Toast.makeText(this, "+" + catalog.get(which).getName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void refreshEmptyState() {
        if (PlayerInventoryRepository.getInventory().isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            inventoryRecycler.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            inventoryRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (itemAdapter != null) {
            itemAdapter.notifyDataSetChanged();
            refreshEmptyState();
        }
    }
}
