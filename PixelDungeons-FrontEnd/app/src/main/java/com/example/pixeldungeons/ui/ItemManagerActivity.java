package com.example.pixeldungeons.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.example.pixeldungeons.model.Item;
import com.example.pixeldungeons.ui.adapter.ItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ItemManagerActivity extends AppCompatActivity {

    private static final String[] TYPES = {"Consumible", "Arma", "Armadura", "Hechizo", "Llave", "Otro"};
    private static final String[] STAT_LABELS = {"Ninguna", "Fuerza (STR)", "Destreza (DEX)", "Defensa (DEF)", "Maná", "Vida (HP)"};
    private static final String[] STAT_KEYS   = {"none",    "str",          "dex",            "def",          "mana", "hp"};

    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView itemsRecycler = findViewById(R.id.items_recycler);
        FloatingActionButton uploadButton = findViewById(R.id.upload_item_button);

        itemAdapter = new ItemAdapter(ItemRepository.getItems(), false, null);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(this));
        itemsRecycler.setAdapter(itemAdapter);

        uploadButton.setOnClickListener(v -> showCreateItemDialog());
    }

    private void showCreateItemDialog() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Nombre del objeto");
        container.addView(nameInput);

        Spinner typeSpinner = new Spinner(this);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, TYPES);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        container.addView(typeSpinner);

        CheckBox consumableCheck = new CheckBox(this);
        consumableCheck.setText("¿Es consumible?");
        container.addView(consumableCheck);

        EditText descriptionInput = new EditText(this);
        descriptionInput.setHint("Efectos o estadísticas");
        container.addView(descriptionInput);

        TextView statLabel = new TextView(this);
        statLabel.setText("Estadística que mejora al equipar:");
        container.addView(statLabel);

        Spinner statSpinner = new Spinner(this);
        ArrayAdapter<String> statAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, STAT_LABELS);
        statAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statSpinner.setAdapter(statAdapter);
        container.addView(statSpinner);

        EditText bonusInput = new EditText(this);
        bonusInput.setHint("Valor del bonus (ej: 2)");
        bonusInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        container.addView(bonusInput);

        new AlertDialog.Builder(this)
                .setTitle("Crear objeto")
                .setView(container)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();

                    if (name.isEmpty() || description.isEmpty()) {
                        Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String type = (String) typeSpinner.getSelectedItem();
                    boolean consumable = consumableCheck.isChecked();
                    String bonusStat = STAT_KEYS[statSpinner.getSelectedItemPosition()];
                    String bonusStr = bonusInput.getText().toString().trim();
                    int bonusValue = bonusStr.isEmpty() ? 0 : Integer.parseInt(bonusStr);

                    ItemRepository.addItem(new Item(name, type, 0, consumable, description, bonusStat, bonusValue));
                    itemAdapter.notifyItemInserted(ItemRepository.getItems().size() - 1);
                    Toast.makeText(this, "Objeto creado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (itemAdapter != null) itemAdapter.notifyDataSetChanged();
    }
}
