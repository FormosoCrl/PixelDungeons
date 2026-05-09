package com.example.pixeldungeons.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

        boolean isMaster = getIntent().getBooleanExtra("is_master", false);

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

        if (isMaster) {
            addMasterAddItemButton();
        }
    }

    private void addMasterAddItemButton() {
        ConstraintLayout root = findViewById(R.id.main);
        Button addButton = new Button(this);
        addButton.setText("Añadir objeto");

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topMargin = (int) (16 * getResources().getDisplayMetrics().density);
        params.rightMargin = (int) (16 * getResources().getDisplayMetrics().density);
        addButton.setLayoutParams(params);
        addButton.setGravity(Gravity.CENTER);

        addButton.setOnClickListener(v ->
                Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        );

        root.addView(addButton);
    }
}
