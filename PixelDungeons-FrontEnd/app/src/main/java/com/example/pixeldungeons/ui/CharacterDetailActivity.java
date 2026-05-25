package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.network.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterDetailActivity extends AppCompatActivity {

    private TextView hpText, strText, dexText, defText, manaText;
    private int maxHp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);

        Intent received = getIntent();
        boolean isMaster = received.getBooleanExtra("is_master", false);
        int heroId = received.getIntExtra("heroId", -1);
        int userId = received.getIntExtra("userId", -1);
        int salaId = received.getIntExtra("salaId", -1);

        TextView nameText = findViewById(R.id.detail_char_name);
        hpText   = findViewById(R.id.hp_value);
        strText  = findViewById(R.id.str_value);
        dexText  = findViewById(R.id.dex_value);
        defText  = findViewById(R.id.defence_value);
        manaText = findViewById(R.id.mana_value);
        Button mapButton       = findViewById(R.id.map_button);
        Button inventoryButton = findViewById(R.id.inventory_button);

        String name = received.getStringExtra("name");
        int hp      = received.getIntExtra("hp", 20);
        maxHp       = received.getIntExtra("maxHp", 20);
        int str     = received.getIntExtra("str", 0);
        int dex     = received.getIntExtra("dex", 0);
        int defence = received.getIntExtra("defence", 0);
        int mana    = received.getIntExtra("mana", 0);

        if (name != null) nameText.setText(name);
        hpText.setText(hp + " / " + maxHp);
        strText.setText(String.valueOf(str));
        dexText.setText(String.valueOf(dex));
        defText.setText(String.valueOf(defence));
        manaText.setText(String.valueOf(mana));

        if (isMaster) {
            ConstraintLayout content = findViewById(R.id.detail_content);
            hpText.setText(String.valueOf(hp));
            replaceWithEditText(hpText,   content, InputType.TYPE_CLASS_NUMBER);
            replaceWithEditText(strText,  content, InputType.TYPE_CLASS_NUMBER);
            replaceWithEditText(dexText,  content, InputType.TYPE_CLASS_NUMBER);
            replaceWithEditText(defText,  content, InputType.TYPE_CLASS_NUMBER);
            replaceWithEditText(manaText, content, InputType.TYPE_CLASS_NUMBER);

            inventoryButton.setText("Guardar");
            inventoryButton.setOnClickListener(v -> guardarCambios(heroId));

            mapButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtras(received);
                startActivity(intent);
                finish();
            });
        } else {
            mapButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtras(received);
                startActivity(intent);
                finish();
            });

            inventoryButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, InventoryActivity.class);
                intent.putExtras(received);
                startActivity(intent);
                finish();
            });
        }
    }

    private void guardarCambios(int heroId) {
        EditText hpEdit   = findViewById(R.id.hp_value);
        EditText strEdit  = findViewById(R.id.str_value);
        EditText dexEdit  = findViewById(R.id.dex_value);
        EditText defEdit  = findViewById(R.id.defence_value);
        EditText manaEdit = findViewById(R.id.mana_value);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("hp",       Integer.parseInt(hpEdit.getText().toString().trim()));
            body.put("max_hp",   maxHp);
            body.put("strength", Integer.parseInt(strEdit.getText().toString().trim()));
            body.put("dex",      Integer.parseInt(dexEdit.getText().toString().trim()));
            body.put("defence",  Integer.parseInt(defEdit.getText().toString().trim()));
            body.put("mana",     Integer.parseInt(manaEdit.getText().toString().trim()));

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

    private void replaceWithEditText(TextView source, ConstraintLayout parent, int inputType) {
        int index = parent.indexOfChild(source);
        ViewGroup.LayoutParams params = source.getLayoutParams();

        EditText edit = new EditText(this);
        edit.setId(source.getId());
        edit.setText(source.getText());
        edit.setTextColor(source.getCurrentTextColor());
        edit.setTextSize(TypedValue.COMPLEX_UNIT_PX, source.getTextSize());
        edit.setInputType(inputType);
        edit.setLayoutParams(params);

        parent.removeView(source);
        parent.addView(edit, index);
    }
}
