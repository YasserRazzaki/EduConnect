package com.example.educonnect.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.activities.DetailsCoursActivity;
import com.example.educonnect.R;
import com.example.educonnect.activities.AdminCreerCoursActivity;
import com.example.educonnect.fragments.DetailsCoursEnseignantFragment;
import com.example.educonnect.models.Cours;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CoursAdapter extends RecyclerView.Adapter<CoursAdapter.CoursViewHolder> {

    private final List<Cours> coursList;
    private boolean isAdmin = false;

    public CoursAdapter(List<Cours> coursList) {
        this.coursList = coursList;
        checkIfAdmin(); // dès l'initialisation
    }

    private void checkIfAdmin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("utilisateurs")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if ("ADMIN".equals(snapshot.getString("type"))) {
                            isAdmin = true;
                            notifyDataSetChanged(); // mettre à jour l'affichage pour montrer les boutons
                        }
                    });
        }
    }

    @NonNull
    @Override
    public CoursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cours, parent, false);
        return new CoursViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursViewHolder holder, int position) {
        Cours cours = coursList.get(position);
        holder.txtTitre.setText(cours.getTitre());
        holder.txtCode.setText("Code : " + cours.getCode());
        holder.txtFiliere.setText("Filière : " + cours.getFiliere());
        holder.txtNiveau.setText("Niveau : " + cours.getNiveau());

        // Afficher le bouton si admin
        holder.btnEdit.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        // Action du bouton "modifier"
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AdminCreerCoursActivity.class);
            intent.putExtra("coursId", cours.getId());
            v.getContext().startActivity(intent);
        });

        holder.btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Suppression")
                    .setMessage("Supprimer ce cours ? Cette action est irréversible.")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("cours")
                                .document(cours.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(v.getContext(), "Cours supprimé", Toast.LENGTH_SHORT).show();
                                    coursList.remove(holder.getAdapterPosition());
                                    notifyItemRemoved(holder.getAdapterPosition());
                                })
                                .addOnFailureListener(e -> Toast.makeText(v.getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });


        // Clic sur l'item principal
        holder.itemView.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore.getInstance().collection("utilisateurs")
                        .document(user.getUid())
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            String type = snapshot.getString("type");
                            if ("ENSEIGNANT".equals(type)) {
                                // Rediriger vers DetailsCoursProfFragment
                                DetailsCoursEnseignantFragment fragment = new DetailsCoursEnseignantFragment();
                                androidx.fragment.app.FragmentManager fm = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                                androidx.fragment.app.FragmentTransaction ft = fm.beginTransaction();

                                Bundle bundle = new Bundle();
                                bundle.putString("coursId", cours.getId());
                                fragment.setArguments(bundle);

                                ft.replace(R.id.fragment_container, fragment);
                                ft.addToBackStack(null);
                                ft.commit();
                            } else {
                                // Rediriger vers activité étudiante
                                Intent intent = new Intent(v.getContext(), DetailsCoursActivity.class);
                                intent.putExtra("nomCours", cours.getTitre());
                                intent.putExtra("coursId", cours.getId());
                                v.getContext().startActivity(intent);
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return coursList.size();
    }

    public static class CoursViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitre, txtFiliere, txtNiveau, txtCode;
        View btnEdit;
        View btnDelete;

        public CoursViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCode = itemView.findViewById(R.id.textViewCode);
            txtTitre = itemView.findViewById(R.id.textTitreCours);
            txtFiliere = itemView.findViewById(R.id.textViewFiliere);
            txtNiveau = itemView.findViewById(R.id.textViewNiveau);
            btnEdit = itemView.findViewById(R.id.btnEditCours);
            btnDelete = itemView.findViewById(R.id.btnSupprimerCours);

        }
    }
}
