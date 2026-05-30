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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.Item;
import com.example.pixeldungeons.network.ApiClient;
import com.example.pixeldungeons.ui.adapter.ItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemManagerActivity extends AppCompatActivity {

    private static final String[] TYPES = {"Consumible", "Arma", "Armadura", "Hechizo", "Llave", "Otro"};
    private static final String[] STAT_LABELS = {"Ninguna", "Fuerza (STR)", "Destreza (DEX)", "Defensa (DEF)", "Maná", "Vida (HP)"};
    private static final String[] STAT_KEYS   = {"none", "str", "dex", "def", "mana", "hp"};

    private ItemAdapter itemAdapter;
    private final List<Item> items = new ArrayList<>();
    private int salaId;
    private int itemsRetries = 0;
    private static final int MAX_ITEMS_RETRIES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.apply(this);
        setContentView(R.layout.activity_item_manager);
        ThemeHelper.setup(this, findViewById(R.id.theme_switch));

        salaId = getIntent().getIntExtra("salaId", -1);

        RecyclerView itemsRecycler = findViewById(R.id.items_recycler);
        FloatingActionButton uploadButton = findViewById(R.id.upload_item_button);

        itemAdapter = new ItemAdapter(items, false, position -> {
            Item item = items.get(position);
            ApiClient.getService().eliminarItem(item.getId()).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        cargarItems();
                        Toast.makeText(ItemManagerActivity.this, "Objeto eliminado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(ItemManagerActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        itemsRecycler.setLayoutManager(new LinearLayoutManager(this));
        itemsRecycler.setAdapter(itemAdapter);

        uploadButton.setOnClickListener(v -> showCreateItemDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarItems();
    }

    private void cargarItems() {
        ApiClient.getService().getItems(salaId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemsRetries = 0;
                    items.clear();
                    for (Map<String, Object> m : response.body()) {
                        Item item = new Item(
                                (String) m.get("name"),
                                (String) m.get("item_type"),
                                0,
                                (Boolean) m.get("consumable"),
                                (String) m.get("description"),
                                (String) m.get("bonus_stat"),
                                ((Number)m.get("bonus_value")).intValue()
                        );
                        item.setId(((Number)m.get("id")).intValue());
                        items.add(item);
                    }
                    itemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                if (itemsRetries < MAX_ITEMS_RETRIES) {
                    itemsRetries++;
                    new android.os.Handler(android.os.Looper.getMainLooper())
                            .postDelayed(ItemManagerActivity.this::cargarItems, 800);
                } else {
                    Toast.makeText(ItemManagerActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        typeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TYPES));
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
        statSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, STAT_LABELS));
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
                    String bonusStr = bonusInput.getText().toString().trim();
                    int bonusValue = bonusStr.isEmpty() ? 0 : Integer.parseInt(bonusStr);

                    Map<String, Object> body = new HashMap<>();
                    body.put("name", name);
                    body.put("item_type", typeSpinner.getSelectedItem());
                    body.put("consumable", consumableCheck.isChecked());
                    body.put("description", description);
                    body.put("bonus_stat", STAT_KEYS[statSpinner.getSelectedItemPosition()]);
                    body.put("bonus_value", bonusValue);
                    body.put("sala_id", salaId);

                    ApiClient.getService().crearItem(body).enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            if (response.isSuccessful()) {
                                cargarItems();
                                Toast.makeText(ItemManagerActivity.this, "Objeto creado", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            Toast.makeText(ItemManagerActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

