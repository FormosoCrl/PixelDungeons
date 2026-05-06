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

public class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.HeroViewHolder> {

    public interface OnHeroClickListener {
        void onHeroClick(Hero hero);
    }

    private final List<Hero> heroes;
    private final OnHeroClickListener listener;

    public HeroAdapter(List<Hero> heroes, OnHeroClickListener listener) {
        this.heroes = heroes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player, parent, false);
        return new HeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        Hero hero = heroes.get(position);
        holder.name.setText(hero.getName());
        holder.raceClass.setText(hero.getRace() + " · " + hero.getHeroClass());
        holder.hp.setText(hero.getHp() + " / " + hero.getMaxHp());
        holder.itemView.setOnClickListener(v -> listener.onHeroClick(hero));
    }

    @Override
    public int getItemCount() {
        return heroes.size();
    }

    static class HeroViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView raceClass;
        TextView hp;

        HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.player_name);
            raceClass = itemView.findViewById(R.id.player_race_class);
            hp = itemView.findViewById(R.id.player_hp);
        }
    }
}
