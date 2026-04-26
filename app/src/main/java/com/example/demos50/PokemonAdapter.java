package com.example.demos50;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import java.util.List;

public class PokemonAdapter extends ArrayAdapter<Pokemon> {

    public PokemonAdapter(Context context, List<Pokemon> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_pokemon, parent, false);
            holder           = new ViewHolder();
            holder.ivSprite  = convertView.findViewById(R.id.ivSprite);
            holder.tvName    = convertView.findViewById(R.id.tvPokemonName);
            holder.tvId      = convertView.findViewById(R.id.tvPokemonId);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Pokemon p = getItem(position);
        if (p != null) {
            holder.tvName.setText(capitalize(p.getName()));
            holder.tvId.setText("#" + String.format("%03d", p.getId()));
            Glide.with(getContext())
                    .load(p.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivSprite);
        }
        return convertView;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    static class ViewHolder {
        ImageView ivSprite;
        TextView  tvName;
        TextView  tvId;
    }
}
