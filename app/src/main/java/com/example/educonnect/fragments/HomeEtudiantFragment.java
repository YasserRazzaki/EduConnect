package com.example.educonnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.adapters.AnnonceAdapter;
import com.example.educonnect.adapters.CoursAdapter;
import com.example.educonnect.models.Annonce;
import com.example.educonnect.models.Cours;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomeEtudiantFragment extends Fragment {

    private RecyclerView recyclerViewAnnonces;
    private RecyclerView recyclerViewCours;
    private TextView textAucuneAnnonce, textAucunCours;
    private TextView voirToutCours, voirToutAnnonces;
    private final List<Annonce> annonceList = new ArrayList<>();
    private final List<Cours> coursList = new ArrayList<>();
    private AnnonceAdapter annonceAdapter;
    private CoursAdapter coursAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_etudiant, container, false);

        recyclerViewAnnonces = view.findViewById(R.id.recyclerViewAnnonces);
        recyclerViewCours = view.findViewById(R.id.recyclerViewCours);
        textAucuneAnnonce = view.findViewById(R.id.textAucuneAnnonce);
        textAucunCours = view.findViewById(R.id.textAucunCours);
        voirToutCours = view.findViewById(R.id.voirToutCours);
        voirToutAnnonces = view.findViewById(R.id.voirToutAnnonces);

        recyclerViewAnnonces.setLayoutManager(new LinearLayoutManager(getContext()));
        annonceAdapter = new AnnonceAdapter(annonceList);
        recyclerViewAnnonces.setAdapter(annonceAdapter);

        recyclerViewCours.setLayoutManager(new GridLayoutManager(getContext(), 2));
        coursAdapter = new CoursAdapter(coursList);
        recyclerViewCours.setAdapter(coursAdapter);

        chargerAnnoncesDepuisFirestore();
        chargerCoursDepuisFirestore();

        voirToutCours.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AllCoursFragment())
                    .addToBackStack(null)
                    .commit();
        });

        voirToutAnnonces.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AllAnnoncesFragment()) // à créer ensuite
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void chargerAnnoncesDepuisFirestore() {
        FirebaseFirestore.getInstance().collection("annonces")
                .orderBy("datePublication", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    annonceList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Annonce annonce = new Annonce(
                                doc.getString("titre"),
                                doc.getString("contenu"),
                                doc.getDate("datePublication") != null ? doc.getDate("datePublication") : new Date()
                        );
                        annonceList.add(annonce);
                    }
                    annonceAdapter.notifyDataSetChanged();
                    textAucuneAnnonce.setVisibility(annonceList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> Log.e("HomeEtudiantFragment", "Erreur chargement annonces", e));
    }

    private void chargerCoursDepuisFirestore() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("etudiants")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String filiere = documentSnapshot.getString("filiere");
                        String niveau = documentSnapshot.getString("niveau");

                        FirebaseFirestore.getInstance().collection("cours")
                                .whereEqualTo("filiere", filiere)
                                .whereEqualTo("niveau", niveau)
                                .get()
                                .addOnSuccessListener(query -> {
                                    List<Cours> tempCoursList = new ArrayList<>();
                                    for (DocumentSnapshot doc : query.getDocuments()) {
                                        Cours cours = new Cours(
                                                doc.getString("code"),
                                                doc.getString("titre"),
                                                doc.getString("description"),
                                                doc.getString("enseignantId"),
                                                doc.getLong("credits") != null ? doc.getLong("credits").intValue() : 0,
                                                doc.getString("filiere"),
                                                doc.getString("niveau")
                                        );
                                        cours.setId(doc.getId());
                                        tempCoursList.add(cours);
                                    }
                                    Collections.shuffle(tempCoursList); // Mélange
                                    coursList.clear();
                                    coursList.addAll(tempCoursList.subList(0, Math.min(4, tempCoursList.size())));
                                    coursAdapter.notifyDataSetChanged();
                                    textAucunCours.setVisibility(coursList.isEmpty() ? View.VISIBLE : View.GONE);
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("HomeEtudiantFragment", "Erreur chargement cours", e));
    }
}
