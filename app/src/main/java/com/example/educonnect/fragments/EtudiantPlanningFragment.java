package com.example.educonnect.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.educonnect.utils.CalendrierSync;
import com.example.educonnect.utils.PlanningView;
import com.example.educonnect.R;
import com.example.educonnect.models.Seance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.*;

public class EtudiantPlanningFragment extends Fragment {

    private PlanningView planningView;
    private LinearLayout headerDays;
    private ProgressBar progressBar;
    private List<Date> jours = new ArrayList<>();
    private List<Seance> seances = new ArrayList<>();
    private String userId;
    private String niveau;
    private String filiere;

    public EtudiantPlanningFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        initJoursSemaine();
        chargerInformationsEtudiant();

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                    1001
            );
        }

        TextView buttonPreviousWeek = view.findViewById(R.id.buttonPreviousWeek);
        TextView buttonNextWeek = view.findViewById(R.id.buttonNextWeek);

        buttonPreviousWeek.setOnClickListener(v -> {
            decalerSemaine(-1);
        });

        buttonNextWeek.setOnClickListener(v -> {
            decalerSemaine(1);
        });

    }

    private void decalerSemaine(int nbSemaines) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(jours.get(0)); // début de la semaine actuelle
        calendar.add(Calendar.DAY_OF_MONTH, 7 * nbSemaines);

        jours.clear();
        for (int i = 0; i < 5; i++) {
            jours.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        updateHeaderDays();
        chargerSeancesEtudiant(); // recharge les séances pour la nouvelle semaine
    }

    private void initJoursSemaine() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysToSubtract = dayOfWeek - Calendar.MONDAY;
        if (daysToSubtract < 0) daysToSubtract += 7;
        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);

        jours.clear();
        for (int i = 0; i < 5; i++) {
            jours.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

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

    private void chargerInformationsEtudiant() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("etudiants")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        niveau = documentSnapshot.getString("niveau");
                        filiere = documentSnapshot.getString("filiere");

                        if (niveau != null && filiere != null) {
                            chargerSeancesEtudiant();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Niveau ou filière non définis", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Étudiant non trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("DEBUG", "Niveau: " + niveau + ", Filière: " + filiere);
                });
    }

    private void chargerSeancesEtudiant() {
        Calendar calDebut = Calendar.getInstance();
        calDebut.setTime(jours.get(0));
        calDebut.set(Calendar.HOUR_OF_DAY, 0);
        calDebut.set(Calendar.MINUTE, 0);

        Calendar calFin = Calendar.getInstance();
        calFin.setTime(jours.get(jours.size() - 1));
        calFin.set(Calendar.HOUR_OF_DAY, 23);
        calFin.set(Calendar.MINUTE, 59);

        FirebaseFirestore.getInstance().collection("seances")
                .whereEqualTo("niveau", niveau)
                .whereEqualTo("filiere", filiere)
                .whereGreaterThanOrEqualTo("dateDebut", calDebut.getTime())
                .whereLessThanOrEqualTo("dateDebut", calFin.getTime())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    seances.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Seance seance = document.toObject(Seance.class);
                        seances.add(seance);
                    }

                    planningView.setJours(jours);
                    planningView.setSeances(seances);

                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_calendrier, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sync_calendar) {
            if (niveau != null && filiere != null) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{
                            Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR
                    }, 1001);
                    return true;
                }

                CalendrierSync.exporterSeancesPourUtilisateur(
                        requireContext(),
                        false,
                        niveau,
                        filiere
                );
            } else {
                Toast.makeText(getContext(), "Données de l'étudiant non disponibles", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.action_delete_calendar) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{
                        Manifest.permission.WRITE_CALENDAR,
                        Manifest.permission.READ_CALENDAR
                }, 1001);
                return true;
            }

            CalendrierSync.supprimerEvenementsEduConnect(requireContext());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission calendrier accordée", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission refusée. Action annulée.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
