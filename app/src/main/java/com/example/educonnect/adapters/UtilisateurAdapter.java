package com.example.educonnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.models.Utilisateur;

import java.util.List;

public class UtilisateurAdapter extends RecyclerView.Adapter<UtilisateurAdapter.UtilisateurViewHolder> {

    public interface OnUtilisateurClickListener {
        void onUtilisateurClicked(Utilisateur utilisateur);
    }

    private final List<Utilisateur> utilisateurs;
    private final OnUtilisateurClickListener listener;

    public UtilisateurAdapter(List<Utilisateur> utilisateurs, OnUtilisateurClickListener listener) {
        this.utilisateurs = utilisateurs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UtilisateurViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_utilisateur, parent, false);
        return new UtilisateurViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UtilisateurViewHolder holder, int position) {
        Utilisateur utilisateur = utilisateurs.get(position);
        holder.nomUtilisateur.setText(utilisateur.getPrenom() + " " + utilisateur.getNom());
        holder.itemView.setOnClickListener(v -> listener.onUtilisateurClicked(utilisateur));
    }

    @Override
    public int getItemCount() {
        return utilisateurs.size();
    }

    public static class UtilisateurViewHolder extends RecyclerView.ViewHolder {
        TextView nomUtilisateur;

        public UtilisateurViewHolder(@NonNull View itemView) {
            super(itemView);
            nomUtilisateur = itemView.findViewById(R.id.textViewNomUtilisateur);
        }
    }
}