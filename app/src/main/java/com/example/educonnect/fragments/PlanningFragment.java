package com.example.educonnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.educonnect.R;
import com.example.educonnect.models.Seance;
import com.example.educonnect.utils.PlanningView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlanningFragment extends Fragment {

    private PlanningView planningView;
    private LinearLayout headerDays;
    private ProgressBar progressBar;
    private List<Date> jours = new ArrayList<>();
    private List<Seance> seances = new ArrayList<>();
    private String userId;
    private String userRole; // "etudiant" ou "enseignant"
    private String classeId; // Pour les étudiants

    public PlanningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        planningView = view.findViewById(R.id.planningView);
        headerDays = view.findViewById(R.id.headerDays);
        progressBar = view.findViewById(R.id.progressBar);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialiser les jours de la semaine (du lundi au vendredi)
        initJoursSemaine();

        // Charger le rôle de l'utilisateur puis les séances
        chargerRoleUtilisateur();
    }

    private void initJoursSemaine() {
        // Obtenir la date du lundi de la semaine courante
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Créer la liste des 5 jours (lundi à vendredi)
        jours.clear();
        for (int i = 0; i < 5; i++) {
            jours.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Mettre à jour l'en-tête des jours
        updateHeaderDays();
    }

    private void updateHeaderDays() {
        headerDays.removeAllViews();

        SimpleDateFormat sdfJour = new SimpleDateFormat("E", Locale.FRANCE);
        SimpleDateFormat sdfNumero = new SimpleDateFormat("d", Locale.FRANCE);

        for (Date jour : jours) {
            View jourView = getLayoutInflater().inflate(R.layout.item_jour_entete, headerDays, false);
            TextView textViewNumeroJour = jourView.findViewById(R.id.textViewNumeroJour);
            TextView textViewJour = jourView.findViewById(R.id.textViewJour);

            textViewNumeroJour.setText(sdfNumero.format(jour));
            textViewJour.setText(sdfJour.format(jour).substring(0, 3));

            headerDays.addView(jourView);
        }
    }

    private void chargerRoleUtilisateur() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("utilisateurs")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userRole = documentSnapshot.getString("role");

                        if ("etudiant".equals(userRole)) {
                            classeId = documentSnapshot.getString("classeId");
                            chargerSeancesEtudiant();
                        } else if ("enseignant".equals(userRole)) {
                            chargerSeancesEnseignant();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Rôle utilisateur non reconnu", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void chargerSeancesEtudiant() {
        if (classeId == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Aucune classe associée à cet étudiant", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculer les dates de début et fin de la semaine
        Calendar calDebut = Calendar.getInstance();
        calDebut.setTime(jours.get(0));
        calDebut.set(Calendar.HOUR_OF_DAY, 0);
        calDebut.set(Calendar.MINUTE, 0);

        Calendar calFin = Calendar.getInstance();
        calFin.setTime(jours.get(jours.size() - 1));
        calFin.set(Calendar.HOUR_OF_DAY, 23);
        calFin.set(Calendar.MINUTE, 59);

        FirebaseFirestore.getInstance().collection("seances")
                .whereEqualTo("classeId", classeId)
                .whereGreaterThanOrEqualTo("dateDebut", calDebut.getTime())
                .whereLessThanOrEqualTo("dateDebut", calFin.getTime())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    seances.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Seance seance = document.toObject(Seance.class);
                        seances.add(seance);
                    }

                    // Mettre à jour la vue du planning
                    planningView.setJours(jours);
                    planningView.setSeances(seances);

                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void chargerSeancesEnseignant() {
        // Calculer les dates de début et fin de la semaine
        Calendar calDebut = Calendar.getInstance();
        calDebut.setTime(jours.get(0));
        calDebut.set(Calendar.HOUR_OF_DAY, 0);
        calDebut.set(Calendar.MINUTE, 0);

        Calendar calFin = Calendar.getInstance();
        calFin.setTime(jours.get(jours.size() - 1));
        calFin.set(Calendar.HOUR_OF_DAY, 23);
        calFin.set(Calendar.MINUTE, 59);

        FirebaseFirestore.getInstance().collection("seances")
                .whereEqualTo("enseignantId", userId)
                .whereGreaterThanOrEqualTo("dateDebut", calDebut.getTime())
                .whereLessThanOrEqualTo("dateDebut", calFin.getTime())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    seances.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Seance seance = document.toObject(Seance.class);
                        seances.add(seance);
                    }

                    // Mettre à jour la vue du planning
                    planningView.setJours(jours);
                    planningView.setSeances(seances);

                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}