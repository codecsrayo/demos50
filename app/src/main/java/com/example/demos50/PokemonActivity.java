package com.example.demos50;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PokemonActivity extends AppCompatActivity {

    private static final String API_URL = "https://pokeapi.co/api/v2/pokemon?limit=40&offset=0";

    private ListView lvPokemon;
    private ProgressBar progressBar;
    private final List<Pokemon> pokemonList = new ArrayList<>();
    private PokemonAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pokédex");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lvPokemon   = findViewById(R.id.lvPokemon);
        progressBar = findViewById(R.id.progressBar);

        adapter = new PokemonAdapter(this, pokemonList);
        lvPokemon.setAdapter(adapter);

        lvPokemon.setOnItemClickListener((parent, view, position, id) -> {
            Pokemon p = pokemonList.get(position);
            Intent intent = new Intent(this, PokemonDetailActivity.class);
            intent.putExtra("pokemon_id",   p.getId());
            intent.putExtra("pokemon_name", p.getName());
            startActivity(intent);
        });

        fetchPokemon();
    }

    private void fetchPokemon() {
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONObject root    = new JSONObject(sb.toString());
                JSONArray  results = root.getJSONArray("results");

                List<Pokemon> temp = new ArrayList<>();
                for (int i = 0; i < results.length(); i++) {
                    JSONObject obj  = results.getJSONObject(i);
                    String name     = obj.getString("name");
                    String url      = obj.getString("url");

                    // extraer id del url: .../pokemon/25/
                    String[] parts  = url.split("/");
                    String id       = parts[parts.length - 1];
                    String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";

                    temp.add(new Pokemon(Integer.parseInt(id), name, imageUrl));
                }

                mainHandler.post(() -> {
                    pokemonList.addAll(temp);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
