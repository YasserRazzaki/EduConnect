package com.example.educonnect.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.adapters.DevoirAdapter;
import com.example.educonnect.adapters.DocumentAdapter;
import com.example.educonnect.adapters.RenduNoteAdapter;
import com.example.educonnect.models.Devoir;
import com.example.educonnect.models.Documents;
import com.example.educonnect.models.Rendu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailsCoursActivity extends AppCompatActivity {

    private TextView nomCoursTitre;
    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private final List<Documents> listeDocuments = new ArrayList<>();
    private final List<Devoir> devoirsList = new ArrayList<>();
    private final List<Rendu> notesList = new ArrayList<>();

    private TextView tabTout, tabCours, tabTp, tabRendus, tabNotes;
    private String coursId;
    private String currentTab = "TOUT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_cours);

        nomCoursTitre = findViewById(R.id.nomCoursTitre);
        recyclerView = findViewById(R.id.recyclerViewRessources);
        tabTout = findViewById(R.id.tab_tout);
        tabCours = findViewById(R.id.tab_cours);
        tabTp = findViewById(R.id.tab_tp);
        tabRendus = findViewById(R.id.tab_rendus);
        tabNotes = findViewById(R.id.tab_notes);

        String nomCours = getIntent().getStringExtra("nomCours");
        coursId = getIntent().getStringExtra("coursId");

        Log.d("DetailsCours", "Cours ID reçu : " + coursId); // log important

        nomCoursTitre.setText(nomCours != null ? nomCours : "Cours");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DocumentAdapter(listeDocuments, document -> {
            Intent intent = new Intent(this, PdfViewerActivity.class);
            intent.putExtra("url", document.getUrl());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        tabTout.setOnClickListener(v -> {
            currentTab = "TOUT";
            setActiveTab(tabTout);
            chargerDocumentsDepuisFirestore();
        });

        tabCours.setOnClickListener(v -> {
            currentTab = "Cours";
            setActiveTab(tabCours);
            chargerDocumentsDepuisFirestore();
        });

        tabTp.setOnClickListener(v -> {
            currentTab = "TP/TD";
            setActiveTab(tabTp);
            chargerDocumentsDepuisFirestore();
        });

        tabRendus.setOnClickListener(v -> {
            currentTab = "Devoirs";
            setActiveTab(tabRendus);
            chargerDevoirsEtNotes();
        });

        tabNotes.setOnClickListener(v -> {
            currentTab = "Notes";
            setActiveTab(tabNotes);
            chargerDevoirsEtNotes();
        });

        // Par défaut
        currentTab = "TOUT";
        setActiveTab(tabTout);
        chargerDocumentsDepuisFirestore();
    }

    private void setActiveTab(TextView selectedTab) {
        TextView[] allTabs = { tabTout, tabCours, tabTp, tabRendus, tabNotes };

        for (TextView tab : allTabs) {
            tab.setTextColor(ContextCompat.getColor(this, R.color.grey));
            tab.setTypeface(null, Typeface.NORMAL);
        }

        selectedTab.setTextColor(ContextCompat.getColor(this, R.color.orange));
        selectedTab.setTypeface(null, Typeface.BOLD);

        // Vider l'affichage si on change de section
        listeDocuments.clear();
        devoirsList.clear();
        notesList.clear();
    }

    private void chargerDocumentsDepuisFirestore() {
        Log.d("DetailsCours", "Chargement documents pour coursId=" + coursId + ", type=" + currentTab);

        FirebaseFirestore.getInstance().collection("documents")
                .whereEqualTo("coursId", coursId)
                .get()
                .addOnSuccessListener(query -> {
                    listeDocuments.clear();
                    Log.d("DetailsCours", "Documents trouvés : " + query.size());

                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Documents document = new Documents(
                                doc.getString("titre"),
                                doc.getString("description"),
                                doc.getString("type"),
                                doc.getString("format"),
                                doc.getString("url"),
                                doc.getString("coursId"),
                                doc.getString("auteurId"),
                                doc.getDate("dateAjout") != null ? doc.getDate("dateAjout") : new Date()
                        );
                        listeDocuments.add(document);
                    }

                    filtrerDocuments(currentTab);
                })
                .addOnFailureListener(e -> Log.e("DetailsCours", "Erreur chargement documents", e));
    }

    private void filtrerDocuments(String type) {
        List<Documents> filtres = new ArrayList<>();
        for (Documents doc : listeDocuments) {
            if (type.equals("TOUT") || doc.getType().equalsIgnoreCase(type)) {
                filtres.add(doc);
            }
        }

        Log.d("DetailsCours", "Documents filtrés pour type " + type + " : " + filtres.size());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.updateList(filtres);
        recyclerView.setAdapter(adapter);
    }

    private void chargerDevoirsEtNotes() {
        Log.d("DetailsCours", "Chargement des devoirs pour coursId=" + coursId);

        FirebaseFirestore.getInstance().collection("devoirs")
                .whereEqualTo("coursId", coursId)
                .get()
                .addOnSuccessListener(devoirSnapshots -> {
                    devoirsList.clear();
                    notesList.clear();
                    List<String> devoirIds = new ArrayList<>();

                    Log.d("DetailsCours", "Nb de devoirs trouvés : " + devoirSnapshots.size());

                    for (DocumentSnapshot doc : devoirSnapshots.getDocuments()) {
                        Devoir d = doc.toObject(Devoir.class);
                        if (d != null) {
                            d.setId(doc.getId());
                            devoirsList.add(d);
                            devoirIds.add(d.getId());
                            Log.d("DetailsCours", "Devoir : " + d.getTitre() + ", ID: " + d.getId());
                        }
                    }

                    if (currentTab.equals("Devoirs")) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        recyclerView.setAdapter(new DevoirAdapter(devoirsList, false,
                                null, null, devoir -> {
                            // Callback on click
                            Intent intent = new Intent(DetailsCoursActivity.this, RenduActivity.class);
                            intent.putExtra("devoirId", devoir.getId());
                            intent.putExtra("titreDevoir", devoir.getTitre());
                            intent.putExtra("dateLimite", devoir.getDateEcheance() != null ? devoir.getDateEcheance().getTime() : -1);
                            startActivity(intent);
                        }
                        ));
                    }

                    if (currentTab.equals("Notes") && !devoirIds.isEmpty()) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        FirebaseFirestore.getInstance().collection("rendus")
                                .whereIn("devoirId", devoirIds)
                                .whereEqualTo("etudiantId", uid) // ✅ filtre par utilisateur connecté
                                .get()
                                .addOnSuccessListener(renduSnapshots -> {
                                    for (DocumentSnapshot doc : renduSnapshots.getDocuments()) {
                                        Rendu rendu = doc.toObject(Rendu.class);
                                        if (rendu != null && rendu.getNote() != null) {
                                            notesList.add(rendu);
                                            Log.d("DetailsCours", "Note récupérée pour rendu : " + rendu.getNote());
                                        }
                                    }
                                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                                    recyclerView.setAdapter(new RenduNoteAdapter(notesList));
                                    Log.d("DetailsCours", "Notes affichées : " + notesList.size());
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("DetailsCours", "Erreur chargement devoirs", e));
    }
}