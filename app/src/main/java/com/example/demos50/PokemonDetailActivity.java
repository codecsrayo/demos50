package com.example.demos50;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PokemonDetailActivity extends AppCompatActivity {

    private ImageView ivSprite;
    private TextView  tvName, tvId, tvTypes, tvHeight, tvWeight, tvAbilities, tvStats;
    private ProgressBar progressBar;
    private LinearLayout layoutContent;
    private final ExecutorService executor    = Executors.newSingleThreadExecutor();
    private final Handler         mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detail);

        int    pokemonId   = getIntent().getIntExtra("pokemon_id", 1);
        String pokemonName = getIntent().getStringExtra("pokemon_name");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(capitalize(pokemonName));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ivSprite      = findViewById(R.id.ivDetailSprite);
        tvName        = findViewById(R.id.tvDetailName);
        tvId          = findViewById(R.id.tvDetailId);
        tvTypes       = findViewById(R.id.tvDetailTypes);
        tvHeight      = findViewById(R.id.tvDetailHeight);
        tvWeight      = findViewById(R.id.tvDetailWeight);
        tvAbilities   = findViewById(R.id.tvDetailAbilities);
        tvStats       = findViewById(R.id.tvDetailStats);
        progressBar   = findViewById(R.id.detailProgressBar);
        layoutContent = findViewById(R.id.layoutContent);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        fetchDetail(pokemonId);
    }

    private void fetchDetail(int id) {
        progressBar.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);

        executor.execute(() -> {
            try {
                String apiUrl = "https://pokeapi.co/api/v2/pokemon/" + id;
                HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONObject root = new JSONObject(sb.toString());

                // Sprite (intentar animated, fallback a estático)
                String imageUrl;
                try {
                    imageUrl = root.getJSONObject("sprites")
                            .getJSONObject("versions")
                            .getJSONObject("generation-v")
                            .getJSONObject("black-white")
                            .getJSONObject("animated")
                            .getString("front_default");
                    if (imageUrl == null || imageUrl.equals("null")) throw new Exception();
                } catch (Exception e) {
                    imageUrl = root.getJSONObject("sprites").getString("front_default");
                }

                // Tipos
                JSONArray typesArr = root.getJSONArray("types");
                StringBuilder types = new StringBuilder();
                for (int i = 0; i < typesArr.length(); i++) {
                    if (i > 0) types.append("  •  ");
                    types.append(capitalize(typesArr.getJSONObject(i)
                            .getJSONObject("type").getString("name")));
                }

                // Habilidades
                JSONArray abilitiesArr = root.getJSONArray("abilities");
                StringBuilder abilities = new StringBuilder();
                for (int i = 0; i < abilitiesArr.length(); i++) {
                    if (i > 0) abilities.append(",  ");
                    String aName = capitalize(abilitiesArr.getJSONObject(i)
                            .getJSONObject("ability").getString("name"));
                    boolean isHidden = abilitiesArr.getJSONObject(i).getBoolean("is_hidden");
                    abilities.append(aName).append(isHidden ? " (oculta)" : "");
                }

                // Stats
                JSONArray statsArr = root.getJSONArray("stats");
                StringBuilder stats = new StringBuilder();
                String[] statEmojis = {"❤️", "⚔️", "🛡️", "🌀", "✨", "⚡"};
                for (int i = 0; i < statsArr.length(); i++) {
                    JSONObject stat = statsArr.getJSONObject(i);
                    String statName = stat.getJSONObject("stat").getString("name");
                    int    baseStat = stat.getInt("base_stat");
                    String emoji    = i < statEmojis.length ? statEmojis[i] : "▪";
                    stats.append(emoji).append(" ")
                         .append(formatStatName(statName))
                         .append(": ").append(baseStat).append("\n");
                }

                double height = root.getInt("height") / 10.0;
                double weight = root.getInt("weight") / 10.0;
                int    pokeId = root.getInt("id");
                String name   = root.getString("name");
                String finalImageUrl = imageUrl;

                mainHandler.post(() -> {
                    tvName.setText(capitalize(name));
                    tvId.setText("#" + String.format("%03d", pokeId));
                    tvTypes.setText(types.toString());
                    tvHeight.setText(height + " m");
                    tvWeight.setText(weight + " kg");
                    tvAbilities.setText(abilities.toString());
                    tvStats.setText(stats.toString().trim());

                    Glide.with(this)
                            .load(finalImageUrl)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(ivSprite);

                    progressBar.setVisibility(View.GONE);
                    layoutContent.setVisibility(View.VISIBLE);
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error al cargar: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).replace("-", " ");
    }

    private String formatStatName(String raw) {
        switch (raw) {
            case "hp":              return "HP";
            case "attack":          return "Ataque";
            case "defense":         return "Defensa";
            case "special-attack":  return "At. Especial";
            case "special-defense": return "Def. Especial";
            case "speed":           return "Velocidad";
            default:                return capitalize(raw);
        }
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }

    @Override
    protected void onDestroy() { super.onDestroy(); executor.shutdown(); }
}
