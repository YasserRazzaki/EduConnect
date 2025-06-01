package com.example.educonnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.activities.AdminCreerCoursActivity;
import com.example.educonnect.R;
import com.example.educonnect.adapters.CoursAdapter;
import com.example.educonnect.models.Cours;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllCoursFragment extends Fragment {

    private RecyclerView recyclerViewAllCours;
    private CoursAdapter coursAdapter;
    private final List<Cours> coursList = new ArrayList<>();
    private boolean isProfesseur = false; // <-- Important pour afficher le menu ou non
    private boolean isAdmin = false; // <-- Important pour afficher le menu ou non

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_cours, container, false);

        recyclerViewAllCours = view.findViewById(R.id.recyclerViewAllCours);
        recyclerViewAllCours.setLayoutManager(new GridLayoutManager(getContext(), 2));
        coursAdapter = new CoursAdapter(coursList);
        recyclerViewAllCours.setAdapter(coursAdapter);

        setHasOptionsMenu(true); // <-- Très important pour activer le menu dans un Fragment
        chargerTousLesCours();

        return view;
    }

    private void chargerTousLesCours() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("utilisateurs")
                .document(uid)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String type = userDoc.getString("type");

                        if ("ENSEIGNANT".equals(type)) {
                            isProfesseur = true;
                            chargerCoursPourEnseignant(uid);
                        } else if ("ADMIN".equals(type)) {
                            isAdmin = true;
                            chargerTousLesCoursPourAdmin();
                        } else if ("ETUDIANT".equals(type)) {
                            chargerCoursPourEtudiant(uid);
                        }

                        if (isAdded()) requireActivity().invalidateOptionsMenu();
                    }
                })
                .addOnFailureListener(e -> Log.e("AllCoursFragment", "Erreur chargement utilisateur", e));
    }

    private void chargerCoursPourEnseignant(String uid) {
        FirebaseFirestore.getInstance().collection("cours")
                .whereEqualTo("enseignantId", uid)
                .get()
                .addOnSuccessListener(query -> {
                    coursList.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Cours cours = doc.toObject(Cours.class);
                        if (cours != null) {
                            cours.setId(doc.getId());
                            coursList.add(cours);
                        }
                    }
                    coursAdapter.notifyDataSetChanged();
                });
    }

    private void chargerTousLesCoursPourAdmin() {
        FirebaseFirestore.getInstance().collection("cours")
                .get()
                .addOnSuccessListener(query -> {
                    coursList.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Cours cours = doc.toObject(Cours.class);
                        if (cours != null) {
                            cours.setId(doc.getId());
                            coursList.add(cours);
                        }
                    }
                    coursAdapter.notifyDataSetChanged();
                });
    }


    private void chargerCoursPourEtudiant(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("etudiants").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String filiere = documentSnapshot.getString("filiere");
                        String niveau = documentSnapshot.getString("niveau");

                        db.collection("cours")
                                .whereEqualTo("filiere", filiere)
                                .whereEqualTo("niveau", niveau)
                                .get()
                                .addOnSuccessListener(query -> {
                                    coursList.clear();
                                    for (DocumentSnapshot doc : query.getDocuments()) {
                                        Cours cours = doc.toObject(Cours.class);
                                        if (cours != null) {
                                            cours.setId(doc.getId());
                                            coursList.add(cours);
                                        }
                                    }
                                    coursAdapter.notifyDataSetChanged();
                                });
                    }
                });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (isProfesseur) {
            inflater.inflate(R.menu.menu_teacher, menu);
        } else if (isAdmin) {
            inflater.inflate(R.menu.menu_admin, menu); // <-- À créer
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create_course) {
            // Créer un cours
            startActivity(new Intent(getContext(), AdminCreerCoursActivity.class));
            return true;

        } else if (id == R.id.action_events) {
            // Voir les annonces
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AllAnnoncesFragment())
                    .addToBackStack(null)
                    .commit();
            return true;

        } else if (id == R.id.action_create_event) {
            // Créer une annonce
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new FragmentAnnonceEnseignant())
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}