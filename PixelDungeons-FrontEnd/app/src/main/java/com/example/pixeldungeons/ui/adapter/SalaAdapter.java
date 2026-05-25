package com.example.pixeldungeons.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pixeldungeons.R;

import java.util.List;
import java.util.Map;

public class SalaAdapter extends RecyclerView.Adapter<SalaAdapter.ViewHolder> {

    public interface OnSalaClickListener {
        void onContinuar(Map<String, Object> sala);
    }

    private final List<Map<String, Object>> salas;
    private final OnSalaClickListener listener;

    public SalaAdapter(List<Map<String, Object>> salas, OnSalaClickListener listener) {
        this.salas = salas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sala, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> sala = salas.get(position);
        holder.nombre.setText((String) sala.get("nombre"));
        holder.continuar.setOnClickListener(v -> listener.onContinuar(sala));
    }

    @Override
    public int getItemCount() { return salas.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        Button continuar;
        ViewHolder(@NonNull View v) {
            super(v);
            nombre   = v.findViewById(R.id.sala_nombre);
            continuar = v.findViewById(R.id.sala_continuar);
        }
    }
}
