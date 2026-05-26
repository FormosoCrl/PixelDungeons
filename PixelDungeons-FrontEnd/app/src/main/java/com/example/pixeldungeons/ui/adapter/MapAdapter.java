package com.example.pixeldungeons.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.GameMap;

import java.util.List;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    public interface OnMapClickListener {
        void onMapClick(int position);
    }

    private final List<GameMap> maps;
    private final OnMapClickListener listener;

    public MapAdapter(List<GameMap> maps, OnMapClickListener listener) {
        this.maps = maps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_map, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameMap map = maps.get(position);
        holder.nameText.setText(map.getName());
        holder.visibleTag.setVisibility(map.isVisible() ? View.VISIBLE : View.GONE);

        String img = map.getImage();
        if (img != null && !img.isEmpty()) {
            byte[] bytes = Base64.decode(img, Base64.NO_WRAP);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.thumbnail.setImageBitmap(bmp);
        } else {
            holder.thumbnail.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> listener.onMapClick(position));
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        View visibleTag;
        ImageView thumbnail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.map_name);
            visibleTag = itemView.findViewById(R.id.map_visible_tag);
            thumbnail = itemView.findViewById(R.id.map_thumbnail);
        }
    }
}
