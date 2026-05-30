package com.example.pixeldungeons.ui.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;
import com.example.pixeldungeons.model.StatItem;

import java.util.List;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.ViewHolder> {

    private final List<StatItem> stats;
    private final boolean editable;
    private final SparseArray<EditText> editRefs = new SparseArray<>();

    public StatAdapter(List<StatItem> stats, boolean editable) {
        this.stats = stats;
        this.editable = editable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatItem item = stats.get(position);
        holder.label.setText(item.label);
        holder.value.setText(String.valueOf(item.value));
        holder.value.setEnabled(editable);
        holder.value.setFocusable(editable);
        holder.value.setFocusableInTouchMode(editable);
        holder.value.setCursorVisible(editable);
        editRefs.put(position, holder.value);
    }

    public int getValue(int position) {
        EditText et = editRefs.get(position);
        if (et == null) return stats.get(position).value;
        try {
            return Integer.parseInt(et.getText().toString().trim());
        } catch (NumberFormatException e) {
            return stats.get(position).value;
        }
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        EditText value;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.stat_label);
            value = itemView.findViewById(R.id.stat_value);
        }
    }
}
