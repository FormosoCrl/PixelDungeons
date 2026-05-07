package com.example.pixeldungeons.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.Hero;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    public interface OnPlayerClickListener {
        void onPlayerClick(Hero hero);
    }

    private final List<Hero> players;
    private final OnPlayerClickListener listener;

    public PlayerAdapter(List<Hero> players, OnPlayerClickListener listener) {
        this.players = players;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hero hero = players.get(position);
        holder.nameText.setText(hero.getName());
        holder.raceClassText.setText(hero.getRace() + " · " + hero.getHeroClass());
        holder.hpText.setText(hero.getHp() + " / " + hero.getMaxHp());
        holder.itemView.setOnClickListener(v -> listener.onPlayerClick(hero));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView raceClassText;
        TextView hpText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.player_name);
            raceClassText = itemView.findViewById(R.id.player_race_class);
            hpText = itemView.findViewById(R.id.player_hp);
        }
    }
}
