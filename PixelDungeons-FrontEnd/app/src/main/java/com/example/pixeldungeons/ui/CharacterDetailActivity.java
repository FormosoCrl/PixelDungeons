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
            hpText.setText(hp + " / " + maxHp);
            strText.setText(String.valueOf(received.getIntExtra("str", 0)));
            dexText.setText(String.valueOf(received.getIntExtra("dex", 0)));
            defText.setText(String.valueOf(received.getIntExtra("defence", 0)));
            manaText.setText(String.valueOf(received.getIntExtra("mana", 0)));
        }

        if (isMaster) {
            ConstraintLayout root = findViewById(R.id.main);
            replaceWithEditText(hpText, root, InputType.TYPE_CLASS_TEXT);
            replaceWithEditText(strText, root, InputType.TYPE_CLASS_NUMBER);
            replaceWithEditText(dexText, root, InputType.TYPE_CLASS_NUMBER);
            replaceWithEditText(defText, root, InputType.TYPE_CLASS_NUMBER);
            replaceWithEditText(manaText, root, InputType.TYPE_CLASS_NUMBER);
        }

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(CharacterDetailActivity.this, MapActivity.class);
            intent.putExtra("is_master", isMaster);
            startActivity(intent);
        });

        inventoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(CharacterDetailActivity.this, InventoryActivity.class);
            intent.putExtra("is_master", isMaster);
            startActivity(intent);
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
