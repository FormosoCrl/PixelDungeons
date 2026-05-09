package com.example.pixeldungeons.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pixeldungeons.R;

public class CreateRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText nameInput = findViewById(R.id.name_input);
        Button createButton = findViewById(R.id.create_button);

        createButton.setOnClickListener(v -> {
            String roomName = nameInput.getText().toString().trim();

            if (roomName.isEmpty()) {
                Toast.makeText(this, "Introduce el nombre de la sala", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CreateRoomActivity.this, MasterDashboardActivity.class);
            intent.putExtra("room_name", roomName);
            startActivity(intent);
        });
    }
}
