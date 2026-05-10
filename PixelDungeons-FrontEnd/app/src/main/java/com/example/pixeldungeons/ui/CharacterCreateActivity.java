package com.example.pixeldungeons.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.data.HeroRepository;
import com.example.pixeldungeons.model.Hero;

public class CharacterCreateActivity extends AppCompatActivity {

    private static final String[] RACES = {"Humano", "Elfo", "Enano", "Orco", "Mediano"};
    private static final String[] CLASSES = {"Guerrero", "Arquero", "Mago", "Berserker", "Pícaro", "Clérigo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_character_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText nameInput = findViewById(R.id.name_input);
        Spinner raceSpinner = findViewById(R.id.race_spiner);
        Spinner classSpinner = findViewById(R.id.class_spiner);
        Button createButton = findViewById(R.id.create_character_button);

        ArrayAdapter<String> raceAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, RACES);
        raceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        raceSpinner.setAdapter(raceAdapter);

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, CLASSES);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(classAdapter);

        createButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Introduce el nombre del personaje", Toast.LENGTH_SHORT).show();
                return;
            }

            String race = (String) raceSpinner.getSelectedItem();
            String heroClass = (String) classSpinner.getSelectedItem();

            int[] stats = generateStatsForClass(heroClass);
            Hero newHero = new Hero(name, race, heroClass,
                    stats[0], stats[0], stats[1], stats[2], stats[3], stats[4]);

            HeroRepository.addHero(newHero);
            Toast.makeText(this, "Personaje creado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private int[] generateStatsForClass(String heroClass) {
        switch (heroClass) {
            case "Guerrero":   return new int[]{30, 18, 12, 14, 0};
            case "Arquero":    return new int[]{22, 10, 24, 8, 6};
            case "Mago":       return new int[]{18, 6, 12, 6, 20};
            case "Berserker":  return new int[]{35, 22, 8, 10, 0};
            case "Pícaro":     return new int[]{20, 12, 22, 8, 4};
            case "Clérigo":    return new int[]{24, 12, 10, 12, 16};
            default:           return new int[]{20, 10, 10, 10, 0};
        }
    }
}
