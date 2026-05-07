package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.Hero;
import com.example.pixeldungeons.ui.adapter.HeroAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_character_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView emptyText = findViewById(R.id.emptycharacter_title);
        RecyclerView recycler = findViewById(R.id.character_selection);
        FloatingActionButton createButton = findViewById(R.id.create_character_button);

        List<Hero> heroes = new ArrayList<>(Arrays.asList(
                new Hero("Aragorn", "Humano", "Guerrero", 30, 30, 30, 15, 18, 5),
                new Hero("Legolas", "Elfo", "Arquero", 22, 22, 18, 28, 12, 10),
                new Hero("Gimli", "Enano", "Berserker", 35, 35, 26, 8, 22, 2)
        ));

        HeroAdapter adapter = new HeroAdapter(heroes, hero -> {
            Intent intent = new Intent(CharacterListActivity.this, CharacterDetailActivity.class);
            intent.putExtra("name", hero.getName());
            intent.putExtra("race", hero.getRace());
            intent.putExtra("heroClass", hero.getHeroClass());
            intent.putExtra("hp", hero.getHp());
            intent.putExtra("maxHp", hero.getMaxHp());
            intent.putExtra("str", hero.getStr());
            intent.putExtra("dex", hero.getDex());
            intent.putExtra("defence", hero.getDefence());
            intent.putExtra("mana", hero.getMana());
            intent.putExtra("is_master", false);
            startActivity(intent);
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        if (heroes.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }

        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(CharacterListActivity.this, CharacterCreateActivity.class);
            startActivity(intent);
        });
    }
}
