package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.data.PlayerInventoryRepository;

public class CharacterDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_character_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView nameText = findViewById(R.id.detail_char_name);
        TextView hpText = findViewById(R.id.hp_value);
        TextView strText = findViewById(R.id.str_value);
        TextView dexText = findViewById(R.id.dex_value);
        TextView defText = findViewById(R.id.defence_value);
        TextView manaText = findViewById(R.id.mana_value);
        Button mapButton = findViewById(R.id.map_button);
        Button inventoryButton = findViewById(R.id.inventory_button);

        Intent received = getIntent();
        boolean isMaster = received.getBooleanExtra("is_master", false);
        String name = received.getStringExtra("name");
        if (name != null) {
            nameText.setText(name);
            int hp = received.getIntExtra("hp", 20);
            int maxHp = received.getIntExtra("maxHp", 20);

            int strBonus  = isMaster ? 0 : PlayerInventoryRepository.getEquippedBonus("str");
            int dexBonus  = isMaster ? 0 : PlayerInventoryRepository.getEquippedBonus("dex");
            int defBonus  = isMaster ? 0 : PlayerInventoryRepository.getEquippedBonus("def");
            int manaBonus = isMaster ? 0 : PlayerInventoryRepository.getEquippedBonus("mana");
            int hpBonus   = isMaster ? 0 : PlayerInventoryRepository.getEquippedBonus("hp");

            hpText.setText((hp + hpBonus) + " / " + (maxHp + hpBonus));
            strText.setText(String.valueOf(received.getIntExtra("str", 0) + strBonus));
            dexText.setText(String.valueOf(received.getIntExtra("dex", 0) + dexBonus));
            defText.setText(String.valueOf(received.getIntExtra("defence", 0) + defBonus));
            manaText.setText(String.valueOf(received.getIntExtra("mana", 0) + manaBonus));
        }

        if (isMaster) {
            ConstraintLayout content = findViewById(R.id.detail_content);
            replaceWithEditText(hpText, content, InputType.TYPE_CLASS_TEXT);
            replaceWithEditText(strText, content, InputType.TYPE_CLASS_NUMBER);
            replaceWithEditText(dexText, content, InputType.TYPE_CLASS_NUMBER);
            replaceWithEditText(defText, content, InputType.TYPE_CLASS_NUMBER);
            replaceWithEditText(manaText, content, InputType.TYPE_CLASS_NUMBER);
        }

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(CharacterDetailActivity.this, MapActivity.class);
            intent.putExtras(received);
            startActivity(intent);
            finish();
        });

        inventoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(CharacterDetailActivity.this, InventoryActivity.class);
            intent.putExtras(received);
            startActivity(intent);
            finish();
        });
    }

    private void replaceWithEditText(TextView source, ConstraintLayout parent, int inputType) {
        int index = parent.indexOfChild(source);
        ViewGroup.LayoutParams params = source.getLayoutParams();

        EditText edit = new EditText(this);
        edit.setId(source.getId());
        edit.setText(source.getText());
        edit.setTextColor(source.getCurrentTextColor());
        edit.setTypeface(source.getTypeface(), source.getTypeface() != null ? source.getTypeface().getStyle() : 0);
        edit.setTextSize(TypedValue.COMPLEX_UNIT_PX, source.getTextSize());
        edit.setInputType(inputType);
        edit.setLayoutParams(params);

        parent.removeView(source);
        parent.addView(edit, index);
    }
}
