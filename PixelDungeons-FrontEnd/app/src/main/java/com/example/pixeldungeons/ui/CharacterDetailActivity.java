package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.StatItem;
import com.example.pixeldungeons.network.ApiClient;
import com.example.pixeldungeons.ui.adapter.StatAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterDetailActivity extends AppCompatActivity {

    private TextView hpText;
    private int maxHp;
    private StatAdapter statAdapter;
    private List<StatItem> statList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.apply(this);
        setContentView(R.layout.activity_character_detail);
        ThemeHelper.setup(this, findViewById(R.id.theme_switch));

        Intent received = getIntent();
        boolean isMaster = received.getBooleanExtra("is_master", false);
        int heroId = received.getIntExtra("heroId", -1);
        int userId = received.getIntExtra("userId", -1);
        int salaId = received.getIntExtra("salaId", -1);

        TextView nameText = findViewById(R.id.detail_char_name);
        hpText = findViewById(R.id.hp_value);
        Button saveButton      = findViewById(R.id.save_button);
        Button navMap          = findViewById(R.id.nav_map);
        Button navInventory    = findViewById(R.id.nav_inventory);

        String name = received.getStringExtra("name");
        int hp      = received.getIntExtra("hp", 20);
        maxHp       = received.getIntExtra("maxHp", 20);
        int str     = received.getIntExtra("str", 0);
        int dex     = received.getIntExtra("dex", 0);
        int defence = received.getIntExtra("defence", 0);
        int mana    = received.getIntExtra("mana", 0);
        int level   = received.getIntExtra("level", 1);
        int xp      = received.getIntExtra("xp", 0);
        int xpNeeded = xpToNext(level);

        if (name != null) nameText.setText(name);

        TextView levelLabel = findViewById(R.id.level_label);
        ProgressBar xpBar   = findViewById(R.id.xp_bar);
        TextView xpText     = findViewById(R.id.xp_text);
        xpBar.setMax(xpNeeded);
        xpBar.setProgress(Math.min(xp, xpNeeded));

        statList = new ArrayList<>();
        statList.add(new StatItem("Fuerza (STR)", str));
        statList.add(new StatItem("Destreza (DEX)", dex));
        statList.add(new StatItem("Defensa (DEF)", defence));
        statList.add(new StatItem("Mana", mana));

        statAdapter = new StatAdapter(statList, isMaster);
        RecyclerView statsRecycler = findViewById(R.id.stats_recycler);
        statsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        statsRecycler.setAdapter(statAdapter);

        navMap.setOnClickListener(v -> {
            startActivity(new Intent(this, MapActivity.class).putExtras(received));
            finish();
        });
        navInventory.setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class).putExtras(received));
            finish();
        });

        if (isMaster) {
            levelLabel.setText(String.valueOf(level));
            replaceWithEditText(levelLabel, InputType.TYPE_CLASS_NUMBER);
            hpText.setText(String.valueOf(hp));
            replaceWithEditText(hpText, InputType.TYPE_CLASS_NUMBER);
            xpText.setText(String.valueOf(xp));
            replaceWithEditText(xpText, InputType.TYPE_CLASS_NUMBER);
            saveButton.setVisibility(android.view.View.VISIBLE);
            saveButton.setOnClickListener(v -> guardarCambios(heroId));
        } else {
            levelLabel.setText("Lv." + level);
            hpText.setText(hp + " / " + maxHp);
            xpText.setText(xp + " / " + xpNeeded);
        }

        // Los extras del Intent son del momento en que se abrió la pantalla
        // anterior; si el jugador acaba de equipar/desequipar/usar un item, el
        // backend ya tiene los stats nuevos. Pedimos el héroe al servidor y
        // repintamos para que los bonus se vean.
        refrescarDesdeServidor(heroId, isMaster);
    }

    private void refrescarDesdeServidor(int heroId, boolean isMaster) {
        ApiClient.getService().getHero(heroId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                Map<String, Object> h = response.body();
                int newHp    = ((Number) h.get("hp")).intValue();
                int newMaxHp = ((Number) h.get("max_hp")).intValue();
                int newStr   = ((Number) h.get("strength")).intValue();
                int newDex   = ((Number) h.get("dex")).intValue();
                int newDef   = ((Number) h.get("defence")).intValue();
                int newMana  = ((Number) h.get("mana")).intValue();
                int newLevel = ((Number) h.get("level")).intValue();
                int newXp    = ((Number) h.get("xp")).intValue();
                int xpNeeded = xpToNext(newLevel);

                maxHp = newMaxHp;

                statList.set(0, new StatItem("Fuerza (STR)", newStr));
                statList.set(1, new StatItem("Destreza (DEX)", newDex));
                statList.set(2, new StatItem("Defensa (DEF)", newDef));
                statList.set(3, new StatItem("Mana", newMana));
                statAdapter.notifyDataSetChanged();

                View hpView    = findViewById(R.id.hp_value);
                View xpView    = findViewById(R.id.xp_text);
                View levelView = findViewById(R.id.level_label);
                ProgressBar bar = findViewById(R.id.xp_bar);
                bar.setMax(xpNeeded);
                bar.setProgress(Math.min(newXp, xpNeeded));

                if (isMaster) {
                    ((EditText) hpView).setText(String.valueOf(newHp));
                    ((EditText) xpView).setText(String.valueOf(newXp));
                    ((EditText) levelView).setText(String.valueOf(newLevel));
                } else {
                    ((TextView) hpView).setText(newHp + " / " + newMaxHp);
                    ((TextView) xpView).setText(newXp + " / " + xpNeeded);
                    ((TextView) levelView).setText("Lv." + newLevel);
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // Silencioso: ya pintamos los valores del Intent como fallback.
            }
        });
    }

    /** XP necesaria para pasar de 'level' al siguiente. Misma curva que el backend. */
    private int xpToNext(int level) {
        return 50 * level * (level + 1);
    }

    private void guardarCambios(int heroId) {
        EditText hpEdit = findViewById(R.id.hp_value);
        EditText xpEdit = findViewById(R.id.xp_text);
        EditText levelEdit = findViewById(R.id.level_label);
        try {
            int hp    = Math.max(0, Math.min(Integer.parseInt(hpEdit.getText().toString().trim()), maxHp));
            int xp    = Math.max(0, Integer.parseInt(xpEdit.getText().toString().trim()));
            int level = Math.max(1, Integer.parseInt(levelEdit.getText().toString().trim()));
            int str = Math.max(0, Math.min(statAdapter.getValue(0), 999));
            int dex = Math.max(0, Math.min(statAdapter.getValue(1), 999));
            int def = Math.max(0, Math.min(statAdapter.getValue(2), 999));
            int man = Math.max(0, Math.min(statAdapter.getValue(3), 999));

            Map<String, Object> body = new HashMap<>();
            body.put("hp",       hp);
            body.put("max_hp",   maxHp);
            body.put("strength", str);
            body.put("dex",      dex);
            body.put("defence",  def);
            body.put("mana",     man);
            body.put("level",    level);
            body.put("xp",       xp);

            ApiClient.getService().actualizarHero(heroId, body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CharacterDetailActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CharacterDetailActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(CharacterDetailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Introduce valores numéricos válidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void replaceWithEditText(TextView source, int inputType) {
        ViewGroup parent = (ViewGroup) source.getParent();
        int index = parent.indexOfChild(source);
        ViewGroup.LayoutParams params = source.getLayoutParams();

        EditText edit = new EditText(this);
        edit.setId(source.getId());
        edit.setText(source.getText());
        edit.setTextColor(source.getCurrentTextColor());
        edit.setTextSize(TypedValue.COMPLEX_UNIT_PX, source.getTextSize());
        edit.setInputType(inputType);
        edit.setGravity(android.view.Gravity.CENTER);
        edit.setLayoutParams(params);

        parent.removeView(source);
        parent.addView(edit, index);
    }
}

