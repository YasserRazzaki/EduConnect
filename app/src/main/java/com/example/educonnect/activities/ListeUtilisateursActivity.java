package com.example.educonnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.adapters.UtilisateurAdapter;
import com.example.educonnect.models.Utilisateur;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListeUtilisateursActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUtilisateurs;
    private UtilisateurAdapter utilisateurAdapter;
    private final List<Utilisateur> utilisateurs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_utilisateurs);

        recyclerViewUtilisateurs = findViewById(R.id.recyclerViewUtilisateurs);
        recyclerViewUtilisateurs.setLayoutManager(new LinearLayoutManager(this));
        utilisateurAdapter = new UtilisateurAdapter(utilisateurs, utilisateur -> {
            // Quand on clique sur un utilisateur -> Ouvrir Chat
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("destinataireId", utilisateur.getId());
            startActivity(intent);
            finish();
        });
        recyclerViewUtilisateurs.setAdapter(utilisateurAdapter);

        chargerUtilisateurs();
    }

    private void chargerUtilisateurs() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("utilisateurs")
                .get()
                .addOnSuccessListener(query -> {
                    utilisateurs.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Utilisateur utilisateur = doc.toObject(Utilisateur.class);
                        utilisateur.setId(doc.getId());
                        // Exclure soi-même
                        if (!utilisateur.getId().equals(currentUserId)) {
                            utilisateurs.add(utilisateur);
                        }
                    }
                    utilisateurAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("ListeUtilisateurs", "Erreur chargement", e));
    }
}