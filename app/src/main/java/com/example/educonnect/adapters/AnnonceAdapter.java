package com.example.educonnect.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.models.Annonce;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceAdapter.AnnonceViewHolder> {

    private final List<Annonce> annonces;

    public AnnonceAdapter(List<Annonce> annonces) {
        this.annonces = annonces;
    }

    @NonNull
    @Override
    public AnnonceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_annonce, parent, false);
        return new AnnonceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnonceViewHolder holder, int position) {
        Annonce annonce = annonces.get(position);

        holder.txtTitre.setText(annonce.getTitre());
        holder.txtContenu.setText(annonce.getContenu());

        if (annonce.getDatePublication() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.txtDate.setText(sdf.format(annonce.getDatePublication()));
        } else {
            holder.txtDate.setText("");
        }

        // 🔴 Bouton Supprimer
        holder.btnSupprimer.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirmation")
                    .setMessage("Supprimer cette annonce ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("annonces")
                                .document(annonce.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    annonces.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, annonces.size());
                                    Toast.makeText(v.getContext(), "Annonce supprimée", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        // 🟡 Bouton Modifier
        holder.btnEdit.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_ajout_annonce, null);
            EditText editTitre = dialogView.findViewById(R.id.editTitreAnnonce);
            EditText editContenu = dialogView.findViewById(R.id.editContenuAnnonce);

            editTitre.setText(annonce.getTitre());
            editContenu.setText(annonce.getContenu());

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Modifier l'annonce")
                    .setView(dialogView)
                    .setPositiveButton("Enregistrer", (dialog, which) -> {
                        String nouveauTitre = editTitre.getText().toString().trim();
                        String nouveauContenu = editContenu.getText().toString().trim();

                        if (nouveauTitre.isEmpty() || nouveauContenu.isEmpty()) {
                            Toast.makeText(v.getContext(), "Champs vides", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> update = new HashMap<>();
                        update.put("titre", nouveauTitre);
                        update.put("contenu", nouveauContenu);

                        FirebaseFirestore.getInstance()
                                .collection("annonces")
                                .document(annonce.getId())
                                .update(update)
                                .addOnSuccessListener(aVoid -> {
                                    annonce.setTitre(nouveauTitre);
                                    annonce.setContenu(nouveauContenu);
                                    notifyItemChanged(position);
                                    Toast.makeText(v.getContext(), "Annonce modifiée", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return annonces.size();
    }

    public static class AnnonceViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitre, txtContenu, txtDate;
        ImageButton btnEdit, btnSupprimer;

        public AnnonceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitre = itemView.findViewById(R.id.textViewTitreAnnonce);
            txtContenu = itemView.findViewById(R.id.textViewContenuAnnonce);
            txtDate = itemView.findViewById(R.id.textViewDateAnnonce);
            btnEdit = itemView.findViewById(R.id.btnEditAnnonce);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimerAnnonce);
        }
    }
}
