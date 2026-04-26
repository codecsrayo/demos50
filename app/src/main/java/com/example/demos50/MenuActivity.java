package com.example.demos50;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        String username = getIntent().getStringExtra("username");
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Hola, " + username + " 👋");

        MaterialCardView cardUsers   = findViewById(R.id.cardUsers);
        MaterialCardView cardPokemon = findViewById(R.id.cardPokemon);

        cardUsers.setOnClickListener(v -> {
            Intent i = new Intent(this, UserMenuActivity.class);
            i.putExtra("username", username);
            startActivity(i);
        });

        cardPokemon.setOnClickListener(v ->
                startActivity(new Intent(this, PokemonActivity.class)));
    }
}
