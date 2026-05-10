package com.example.pixeldungeons.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onItemAction(int position);
    }

    private final List<Item> items;
    @Nullable private final OnItemActionListener listener;
    private final boolean masterMode;

    public ItemAdapter(List<Item> items, boolean masterMode, @Nullable OnItemActionListener listener) {
        this.items = items;
        this.masterMode = masterMode;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.nameText.setText(item.getName());
        String bonusInfo = (!item.getBonusStat().equals("none") && item.getBonusValue() > 0)
                ? " · +" + item.getBonusValue() + " " + item.getBonusStat().toUpperCase()
                : "";
        holder.typeText.setText(item.getType() + " · " + item.getDescription() + bonusInfo);
        holder.quantityText.setText("x" + item.getQuantity());

        if (listener == null) {
            holder.actionButton.setVisibility(View.GONE);
            holder.quantityText.setVisibility(View.GONE);
        } else {
            holder.quantityText.setVisibility(View.VISIBLE);
            holder.actionButton.setVisibility(View.VISIBLE);
            holder.actionButton.setAlpha(1.0f);

            if (masterMode) {
                holder.actionButton.setText("Quitar");
                holder.actionButton.setEnabled(item.getQuantity() > 0);
            } else if (item.isConsumable()) {
                holder.actionButton.setText("Usar");
                holder.actionButton.setEnabled(item.getQuantity() > 0);
            } else {
                if (item.isEquipped()) {
                    holder.actionButton.setText("Equipado");
                    holder.actionButton.setEnabled(false);
                    holder.actionButton.setAlpha(0.4f);
                } else {
                    holder.actionButton.setText("Equipar");
                    holder.actionButton.setEnabled(item.getQuantity() > 0);
                }
            }

            holder.actionButton.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) listener.onItemAction(pos);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView typeText;
        TextView quantityText;
        Button actionButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.item_name);
            typeText = itemView.findViewById(R.id.item_type);
            quantityText = itemView.findViewById(R.id.item_quantity);
            actionButton = itemView.findViewById(R.id.item_use_button);
        }
    }
}
