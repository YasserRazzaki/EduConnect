package com.example.educonnect.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.educonnect.activities.EditSeanceActivity;
import com.example.educonnect.utils.PlanningView;
import com.example.educonnect.R;
import com.example.educonnect.models.Seance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminPlanningFragment extends Fragment {

    private PlanningView planningView;
    private LinearLayout headerDays;
    private ProgressBar progressBar;
    private List<Date> jours = new ArrayList<>();
    private List<Seance> seances = new ArrayList<>();

    public AdminPlanningFragment() {}

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

        planningView.setOnSeanceClickListener(this::onSeanceClicked);

        initJoursSemaine();
        chargerToutesLesSeances();

        view.findViewById(R.id.buttonPreviousWeek).setOnClickListener(v -> decalerSemaine(-1));
        view.findViewById(R.id.buttonNextWeek).setOnClickListener(v -> decalerSemaine(1));

        FloatingActionButton fabAdminMenu = view.findViewById(R.id.fabAdminMenu);
        checkIfUserIsAdmin(fabAdminMenu);

    }

    private void checkIfUserIsAdmin(FloatingActionButton fabAdminMenu) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("utilisateurs")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists() && "ADMIN".equals(document.getString("type"))) {
                        fabAdminMenu.setVisibility(View.VISIBLE);
                        fabAdminMenu.setOnClickListener(v -> showAdminMenu());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur lors de la vérification du rôle", Toast.LENGTH_SHORT).show();
                });
    }

    private void showAdminMenu() {
        String[] options = {"Créer un cours", "Modifier un cours", "Supprimer un cours"};
        new AlertDialog.Builder(getContext())
                .setTitle("Actions Admin")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Créer
                            Intent createIntent = new Intent(getContext(), EditSeanceActivity.class);
                            startActivity(createIntent);
                            break;
                        case 1: // Modifier : attend une séance sélectionnée ?
                            Toast.makeText(getContext(), "Cliquez sur un cours à modifier", Toast.LENGTH_SHORT).show();
                            break;
                        case 2: // Supprimer : idem
                            Toast.makeText(getContext(), "Cliquez sur un cours à supprimer", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }).show();
    }


    private void initJoursSemaine() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int diff = day == Calendar.SUNDAY ? -6 : Calendar.MONDAY - day;
        cal.add(Calendar.DAY_OF_MONTH, diff);

        jours.clear();
        for (int i = 0; i < 5; i++) {
            jours.add(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        updateHeaderDays();
    }

    private void decalerSemaine(int nbSemaines) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(jours.get(0));
        cal.add(Calendar.DAY_OF_MONTH, 7 * nbSemaines);

        jours.clear();
        for (int i = 0; i < 5; i++) {
            jours.add(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        updateHeaderDays();
        chargerToutesLesSeances();
    }

    private void updateHeaderDays() {
        headerDays.removeAllViews();
        SimpleDateFormat sdfJour = new SimpleDateFormat("E", Locale.FRANCE);
        SimpleDateFormat sdfNum = new SimpleDateFormat("d", Locale.FRANCE);

        for (Date jour : jours) {
            View jourView = getLayoutInflater().inflate(R.layout.item_jour_entete, headerDays, false);
            ((TextView) jourView.findViewById(R.id.textViewJour)).setText(sdfJour.format(jour).substring(0, 3));
            ((TextView) jourView.findViewById(R.id.textViewNumeroJour)).setText(sdfNum.format(jour));
            headerDays.addView(jourView);
        }
    }

    private void chargerToutesLesSeances() {
        progressBar.setVisibility(View.VISIBLE);

        Calendar calDebut = Calendar.getInstance();
        calDebut.setTime(jours.get(0));
        calDebut.set(Calendar.HOUR_OF_DAY, 0);
        calDebut.set(Calendar.MINUTE, 0);

        Calendar calFin = Calendar.getInstance();
        calFin.setTime(jours.get(jours.size() - 1));
        calFin.set(Calendar.HOUR_OF_DAY, 23);
        calFin.set(Calendar.MINUTE, 59);

        FirebaseFirestore.getInstance().collection("seances")
                .whereGreaterThanOrEqualTo("dateDebut", calDebut.getTime())
                .whereLessThanOrEqualTo("dateDebut", calFin.getTime())
                .get()
                .addOnSuccessListener(query -> {
                    seances.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Seance seance = doc.toObject(Seance.class);
                        seances.add(seance);
                    }

                    planningView.setJours(jours);
                    planningView.setSeances(seances);

                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void onSeanceClicked(Seance seance) {
        new AlertDialog.Builder(getContext())
                .setTitle("Séance")
                .setMessage("Que souhaitez-vous faire ?")
                .setPositiveButton("Modifier", (dialog, which) -> {
                    Intent intent = new Intent(getContext(), EditSeanceActivity.class);
                    intent.putExtra("seanceId", seance.getId());
                    startActivity(intent);
                })
                .setNegativeButton("Supprimer", (dialog, which) -> supprimerSeance(seance))
                .setNeutralButton("Annuler", null)
                .show();
    }

    private void supprimerSeance(Seance seance) {
        FirebaseFirestore.getInstance()
                .collection("seances")
                .document(seance.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Séance supprimée", Toast.LENGTH_SHORT).show();
                    chargerToutesLesSeances();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
