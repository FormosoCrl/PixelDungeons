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
    private int heroesRetries = 0;
    private static final int MAX_HEROES_RETRIES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.apply(this);
        setContentView(R.layout.activity_character_list);
        ThemeHelper.setup(this, findViewById(R.id.theme_switch));

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
            intent.putExtra("level", hero.getLevel());
            intent.putExtra("xp", hero.getXp());
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
                    heroesRetries = 0;
                    heroes.clear();
                    for (Map<String, Object> m : response.body()) {
                        Object ownerRaw = m.get("owner_id");
                        if (ownerRaw == null) continue;
                        if (((Number)ownerRaw).intValue() != userId) continue;
                        Hero h = new Hero(
                                (String) m.get("name"),
                                (String) m.get("race"),
                                (String) m.get("hero_class"),
                                ((Number)m.get("hp")).intValue(),
                                ((Number)m.get("max_hp")).intValue(),
                                ((Number)m.get("strength")).intValue(),
                                ((Number)m.get("dex")).intValue(),
                                ((Number)m.get("defence")).intValue(),
                                ((Number)m.get("mana")).intValue()
                        );
                        h.setId(((Number)m.get("id")).intValue());
                        if (m.get("level") != null) h.setLevel(((Number)m.get("level")).intValue());
                        if (m.get("xp") != null) h.setXp(((Number)m.get("xp")).intValue());
                        heroes.add(h);
                    }
                    adapter.notifyDataSetChanged();
                    refreshEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                if (heroesRetries < MAX_HEROES_RETRIES) {
                    heroesRetries++;
                    new android.os.Handler(android.os.Looper.getMainLooper())
                            .postDelayed(CharacterListActivity.this::cargarHeroes, 800);
                } else {
                    Toast.makeText(CharacterListActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
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

