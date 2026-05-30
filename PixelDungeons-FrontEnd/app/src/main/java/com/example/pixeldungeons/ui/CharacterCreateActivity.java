package com.example.pixeldungeons.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.network.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterCreateActivity extends AppCompatActivity {

    // Valores canónicos que se envían al backend (deben coincidir con las
    // tablas de crecimiento del servidor). No tocar el texto.
    private static final String[] RACES = {"Humano", "Elfo", "Enano", "Orco", "Mediano"};
    private static final String[] CLASSES = {"Guerrero", "Arquero", "Mago", "Berserker", "Pícaro", "Clérigo"};

    // Texto que se muestra en el spinner: indica los bonus de stats por nivel.
    // Las razas suman a esos stats; las clases marcan su mejor (↑) y peor (↓) atributo.
    private static final String[] RACES_DISPLAY = {
            "Humano (+todo)",
            "Elfo (+DEX/+MANA)",
            "Enano (+HP/+DEF)",
            "Orco (+STR/+HP)",
            "Mediano (+DEX/+DEF)"
    };
    private static final String[] CLASSES_DISPLAY = {
            "Guerrero (↑STR ↓MANA)",
            "Arquero (↑DEX ↓MANA)",
            "Mago (↑MANA ↓STR)",
            "Berserker (↑STR ↓DEF)",
            "Pícaro (↑DEX ↓DEF)",
            "Clérigo (↑MANA ↓STR)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.apply(this);
        setContentView(R.layout.activity_character_create);
        ThemeHelper.setup(this, findViewById(R.id.theme_switch));

        int userId = getIntent().getIntExtra("userId", -1);
        int salaId = getIntent().getIntExtra("salaId", -1);

        EditText nameInput = findViewById(R.id.name_input);
        Spinner raceSpinner = findViewById(R.id.race_spiner);
        Spinner classSpinner = findViewById(R.id.class_spiner);
        Button createButton = findViewById(R.id.create_character_button);

        raceSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, RACES_DISPLAY));
        classSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CLASSES_DISPLAY));

        createButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Introduce el nombre del personaje", Toast.LENGTH_SHORT).show();
                return;
            }

            // El spinner muestra el texto con bonus, pero enviamos el valor canónico.
            String race = RACES[raceSpinner.getSelectedItemPosition()];
            String heroClass = CLASSES[classSpinner.getSelectedItemPosition()];
            int[] stats = generateStatsForClass(heroClass);

            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("race", race);
            body.put("hero_class", heroClass);
            body.put("hp", stats[0]);
            body.put("max_hp", stats[0]);
            body.put("strength", stats[1]);
            body.put("dex", stats[2]);
            body.put("defence", stats[3]);
            body.put("mana", stats[4]);
            body.put("owner_id", userId);
            body.put("sala_id", salaId);

            ApiClient.getService().crearHero(body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CharacterCreateActivity.this, "Personaje creado", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CharacterCreateActivity.this, "Error al crear el personaje", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(CharacterCreateActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private int[] generateStatsForClass(String heroClass) {
        switch (heroClass) {
            case "Guerrero":  return new int[]{30, 18, 12, 14, 0};
            case "Arquero":   return new int[]{22, 10, 24, 8,  6};
            case "Mago":      return new int[]{18, 6,  12, 6,  20};
            case "Berserker": return new int[]{35, 22, 8,  10, 0};
            case "Pícaro":    return new int[]{20, 12, 22, 8,  4};
            case "Clérigo":   return new int[]{24, 12, 10, 12, 16};
            default:          return new int[]{20, 10, 10, 10, 0};
        }
    }
}
