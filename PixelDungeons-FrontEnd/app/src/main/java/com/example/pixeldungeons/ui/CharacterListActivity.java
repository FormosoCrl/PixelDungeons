package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.Hero;
import com.example.pixeldungeons.network.ApiClient;
import com.example.pixeldungeons.ui.adapter.HeroAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterListActivity extends AppCompatActivity {

    private HeroAdapter adapter;
    private final List<Hero> heroes = new ArrayList<>();
    private TextView emptyText;
    private RecyclerView recycler;
    private int userId, salaId;
    private String username, salaNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

        userId = getIntent().getIntExtra("userId", -1);
        salaId = getIntent().getIntExtra("salaId", -1);
        username = getIntent().getStringExtra("username");
        salaNombre = getIntent().getStringExtra("salaNombre");

        emptyText = findViewById(R.id.emptycharacter_title);
        recycler = findViewById(R.id.character_selection);
        FloatingActionButton createButton = findViewById(R.id.create_character_button);

        adapter = new HeroAdapter(heroes, hero -> {
            Intent intent = new Intent(this, CharacterDetailActivity.class);
            intent.putExtra("heroId", hero.getId());
            intent.putExtra("name", hero.getName());
            intent.putExtra("race", hero.getRace());
            intent.putExtra("heroClass", hero.getHeroClass());
            intent.putExtra("hp", hero.getHp());
            intent.putExtra("maxHp", hero.getMaxHp());
            intent.putExtra("str", hero.getStr());
            intent.putExtra("dex", hero.getDex());
            intent.putExtra("defence", hero.getDefence());
            intent.putExtra("mana", hero.getMana());
            intent.putExtra("userId", userId);
            intent.putExtra("salaId", salaId);
            intent.putExtra("username", username);
            intent.putExtra("is_master", false);
            startActivity(intent);
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CharacterCreateActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("salaId", salaId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHeroes();
    }

    private void cargarHeroes() {
        ApiClient.getService().getHeroesDeSala(salaId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    heroes.clear();
                    for (Map<String, Object> m : response.body()) {
                        Object ownerRaw = m.get("owner_id");
                        if (ownerRaw == null) continue;
                        if (((Double) ownerRaw).intValue() != userId) continue;
                        Hero h = new Hero(
                                (String) m.get("name"),
                                (String) m.get("race"),
                                (String) m.get("hero_class"),
                                ((Double) m.get("hp")).intValue(),
                                ((Double) m.get("max_hp")).intValue(),
                                ((Double) m.get("strength")).intValue(),
                                ((Double) m.get("dex")).intValue(),
                                ((Double) m.get("defence")).intValue(),
                                ((Double) m.get("mana")).intValue()
                        );
                        h.setId(((Double) m.get("id")).intValue());
                        heroes.add(h);
                    }
                    adapter.notifyDataSetChanged();
                    refreshEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(CharacterListActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshEmptyState() {
        if (heroes.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }
}
