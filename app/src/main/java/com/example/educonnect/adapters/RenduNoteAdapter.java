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

import java.util.List;

public class RenduNoteAdapter extends RecyclerView.Adapter<RenduNoteAdapter.ViewHolder> {
    private final List<Rendu> rendus;

    public RenduNoteAdapter(List<Rendu> rendus) {
        this.rendus = rendus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rendu rendu = rendus.get(position);

        // Récupérer nom/prénom de l’étudiant
        FirebaseFirestore.getInstance().collection("utilisateurs")
                .document(rendu.getEtudiantId())
                .get()
                .addOnSuccessListener(doc -> {
                    String nom = doc.getString("nom");
                    String prenom = doc.getString("prenom");
                    holder.nom.setText(prenom + " " + nom);
                });

        // Récupérer le nom du devoir
        FirebaseFirestore.getInstance().collection("devoirs")
                .document(rendu.getDevoirId())
                .get()
                .addOnSuccessListener(doc -> {
                    String titre = doc.getString("titre");
                    holder.titreDevoir.setText("Devoir : " + titre);
                });

        holder.note.setText("Note : " + rendu.getNote() + "/20");
    }

    @Override
    public int getItemCount() {
        return rendus.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nom, note, titreDevoir;

        ViewHolder(View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.textNomEtudiant);
            note = itemView.findViewById(R.id.textNote);
            titreDevoir = itemView.findViewById(R.id.textTitreDevoir);
        }
    }
}
