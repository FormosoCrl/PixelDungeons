package com.example.pixeldungeons.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class InventoryActivity extends AppCompatActivity {

    private ItemAdapter itemAdapter;
    private final List<Item> inventory = new ArrayList<>();
    private final List<Item> catalog = new ArrayList<>();
    private TextView emptyText;
    private RecyclerView inventoryRecycler;
    private int heroId, userId, salaId;
    private boolean isMaster;
    private Intent received;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        received = getIntent();
        isMaster = received.getBooleanExtra("is_master", false);
        heroId   = received.getIntExtra("heroId", -1);
        userId   = received.getIntExtra("userId", -1);
        salaId   = received.getIntExtra("salaId", -1);

        emptyText         = findViewById(R.id.inventory_empty);
        inventoryRecycler = findViewById(R.id.inventory_recycler);
        Button statsButton = findViewById(R.id.btn_stats_from_inv);
        Button mapButton   = findViewById(R.id.btn_map_from_inv);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_item);

        itemAdapter = new ItemAdapter(inventory, isMaster, position -> {
            Item item = inventory.get(position);
            if (isMaster) {
                quitarItem(item, position);
            } else if (item.isConsumable()) {
                usarItem(item, position);
            } else {
                equiparItem(item, position);
            }
        });

        inventoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        inventoryRecycler.setAdapter(itemAdapter);

        if (isMaster) {
            fabAdd.setVisibility(View.VISIBLE);
            fabAdd.setOnClickListener(v -> cargarCatalogoYMostrarDialog());
        }

        statsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CharacterDetailActivity.class).putExtras(received));
            finish();
        });

        mapButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MapActivity.class).putExtras(received));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarInventario();
    }

    private void cargarInventario() {
        ApiClient.getService().getInventario(heroId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    inventory.clear();
                    for (Map<String, Object> m : response.body()) {
                        Item item = new Item(
                                (String) m.get("name"),
                                (String) m.get("item_type"),
                                ((Double) m.get("quantity")).intValue(),
                                (Boolean) m.get("consumable"),
                                (String) m.get("description"),
                                (String) m.get("bonus_stat"),
                                ((Double) m.get("bonus_value")).intValue()
                        );
                        item.setId(((Double) m.get("item_id")).intValue());
                        item.setEquipped((Boolean) m.get("equipped"));
                        inventory.add(item);
                    }
                    itemAdapter.notifyDataSetChanged();
                    refreshEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(InventoryActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void equiparItem(Item item, int position) {
        Map<String, Object> body = new HashMap<>();
        body.put("equipped", !item.isEquipped());
        ApiClient.getService().equiparItem(heroId, item.getId(), body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    cargarInventario();
                    String msg = item.isEquipped() ? "Desequipado: " : "Equipado: ";
                    Toast.makeText(InventoryActivity.this, msg + item.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(InventoryActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void usarItem(Item item, int position) {
        ApiClient.getService().getHero(heroId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                int currentHp = ((Double) response.body().get("hp")).intValue();
                int maxHp     = ((Double) response.body().get("max_hp")).intValue();

                aplicarEfectoYConsumir(item, currentHp, maxHp);
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(InventoryActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aplicarEfectoYConsumir(Item item, int currentHp, int maxHp) {
        String stat = item.getBonusStat();
        int bonus   = item.getBonusValue();

        Map<String, Object> statBody = new HashMap<>();
        if ("hp".equals(stat) && bonus > 0) {
            statBody.put("hp", Math.min(currentHp + bonus, maxHp));
        }

        Runnable consumir = () -> {
            if (item.getQuantity() > 1) {
                Map<String, Object> q = new HashMap<>();
                q.put("quantity", item.getQuantity() - 1);
                ApiClient.getService().equiparItem(heroId, item.getId(), q)
                        .enqueue(new Callback<Map<String, Object>>() {
                            @Override
                            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                if (response.isSuccessful()) {
                                    cargarInventario();
                                    Toast.makeText(InventoryActivity.this, "Has usado " + item.getName(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                Toast.makeText(InventoryActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                ApiClient.getService().quitarItem(heroId, item.getId())
                        .enqueue(new Callback<Map<String, Object>>() {
                            @Override
                            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                                if (response.isSuccessful()) {
                                    cargarInventario();
                                    Toast.makeText(InventoryActivity.this, "Has usado " + item.getName(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                Toast.makeText(InventoryActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        };

        if (!statBody.isEmpty()) {
            ApiClient.getService().actualizarHero(heroId, statBody).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    consumir.run();
                }
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(InventoryActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            consumir.run();
        }
    }

    private void quitarItem(Item item, int position) {
        ApiClient.getService().quitarItem(heroId, item.getId()).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    cargarInventario();
                    Toast.makeText(InventoryActivity.this, "Quitado: " + item.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(InventoryActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarCatalogoYMostrarDialog() {
        ApiClient.getService().getItems(salaId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    catalog.clear();
                    for (Map<String, Object> m : response.body()) {
                        Item item = new Item(
                                (String) m.get("name"),
                                (String) m.get("item_type"),
                                0,
                                (Boolean) m.get("consumable"),
                                (String) m.get("description"),
                                (String) m.get("bonus_stat"),
                                ((Double) m.get("bonus_value")).intValue()
                        );
                        item.setId(((Double) m.get("id")).intValue());
                        catalog.add(item);
                    }
                    mostrarDialogDarItem();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(InventoryActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogDarItem() {
        if (catalog.isEmpty()) {
            Toast.makeText(this, "No hay objetos en el catálogo", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] names = new String[catalog.size()];
        for (int i = 0; i < catalog.size(); i++) {
            names[i] = catalog.get(i).getName() + " (" + catalog.get(i).getType() + ")";
        }
        new AlertDialog.Builder(this)
                .setTitle("Dar objeto al jugador")
                .setItems(names, (dialog, which) -> {
                    Item chosen = catalog.get(which);
                    Map<String, Object> body = new HashMap<>();
                    body.put("item_id", chosen.getId());
                    body.put("quantity", 1);
                    ApiClient.getService().darItem(heroId, body).enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            if (response.isSuccessful()) {
                                cargarInventario();
                                Toast.makeText(InventoryActivity.this, "+" + chosen.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            Toast.makeText(InventoryActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void refreshEmptyState() {
        if (inventory.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            inventoryRecycler.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            inventoryRecycler.setVisibility(View.VISIBLE);
        }
    }
}
