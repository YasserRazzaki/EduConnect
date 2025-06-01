package com.example.educonnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.models.Rendu;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RenduAdapter extends RecyclerView.Adapter<RenduAdapter.RenduViewHolder> {

    public interface OnRenduClickListener {
        void onClick(Rendu rendu);
    }

    private final List<Rendu> rendus;
    private final OnRenduClickListener listener;

    public RenduAdapter(List<Rendu> rendus, OnRenduClickListener listener) {
        this.rendus = rendus;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RenduViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rendu_prof, parent, false);
        return new RenduViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RenduViewHolder holder, int position) {
        Rendu r = rendus.get(position);

        FirebaseFirestore.getInstance().collection("utilisateurs").document(r.getEtudiantId())
                .get()
                .addOnSuccessListener(doc -> {
                    String nom = doc.getString("nom");
                    String prenom = doc.getString("prenom");

                    holder.etudiant.setText(prenom + " " + nom);
                });

        FirebaseFirestore.getInstance().collection("etudiants").document(r.getEtudiantId())
                .get()
                .addOnSuccessListener(doc -> {
                    String filiere = doc.getString("filiere");
                    String niveau = doc.getString("niveau");

                    holder.details.setText(filiere + " - " + niveau);
                });

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault());
        holder.soumisLe.setText("Soumis le : " + sdf.format(r.getDateSoumission()));

        holder.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() {
        return rendus.size();
    }

    static class RenduViewHolder extends RecyclerView.ViewHolder {
        TextView etudiant, details, soumisLe;

        public RenduViewHolder(@NonNull View itemView) {
            super(itemView);
            etudiant = itemView.findViewById(R.id.textViewEtudiant);
            details = itemView.findViewById(R.id.textViewNomEtudiant);
            soumisLe = itemView.findViewById(R.id.textViewSoumisLe);
        }
    }
}