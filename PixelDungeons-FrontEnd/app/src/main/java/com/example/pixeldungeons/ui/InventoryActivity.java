package com.example.pixeldungeons.ui;

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
import com.example.pixeldungeons.model.Item;
import com.example.pixeldungeons.ui.adapter.ItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

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

        RecyclerView inventoryRecycler = findViewById(R.id.inventory_recycler);
        Button backButton = findViewById(R.id.btn_back_to_map);

        List<Item> items = new ArrayList<>(Arrays.asList(
                new Item("Espada larga", "Arma", 1),
                new Item("Poción de vida", "Consumible", 3),
                new Item("Escudo de roble", "Armadura", 1),
                new Item("Llave oxidada", "Llave", 2),
                new Item("Pergamino arcano", "Hechizo", 5)
        ));

        ItemAdapter itemAdapter = new ItemAdapter(items);
        inventoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        inventoryRecycler.setAdapter(itemAdapter);

        backButton.setOnClickListener(v -> finish());
    }
}