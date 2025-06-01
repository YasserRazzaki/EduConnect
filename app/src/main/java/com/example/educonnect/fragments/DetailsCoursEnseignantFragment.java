package com.example.educonnect.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.educonnect.activities.PdfViewerActivity; // Import de l'activité PDF

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.adapters.DocumentAdapter;
import com.example.educonnect.models.Documents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DetailsCoursEnseignantFragment extends Fragment implements DocumentAdapter.OnDocumentActionListener {

    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private final List<Documents> listeDocuments = new ArrayList<>();
    private final List<DocumentSnapshot> listeSnapshots = new ArrayList<>();
    private ImageButton btnAdd;
    private TextView titreCoursHeader;
    private String coursId;
    private DocumentSnapshot coursSnapshot;
    private TextView tabTout, tabCours, tabTp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_cours_prof, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewRessources);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DocumentAdapter(listeDocuments, this); // Adapter avec listener
        recyclerView.setAdapter(adapter);

        titreCoursHeader = view.findViewById(R.id.titreCoursHeader);
        btnAdd = view.findViewById(R.id.btnAjouter);

        btnAdd.setVisibility(View.VISIBLE);
        coursId = getArguments() != null ? getArguments().getString("coursId") : null;

        chargerTitreCoursDepuisFirestore();
        chargerDocumentsDepuisFirestore();

        btnAdd.setOnClickListener(v -> ouvrirDialogAjoutDocument());

        tabTout = view.findViewById(R.id.tab_tout);
        tabCours = view.findViewById(R.id.tab_cours);
        tabTp = view.findViewById(R.id.tab_tp);

        tabTout.setOnClickListener(v -> {
            setActiveTab(tabTout);
            filtrerDocuments("TOUT");
        });
        tabCours.setOnClickListener(v -> {
            setActiveTab(tabCours);
            filtrerDocuments("Cours");
        });
        tabTp.setOnClickListener(v -> {
            setActiveTab(tabTp);
            filtrerDocuments("TP/TD");
        });

        setActiveTab(tabTout); // Par défaut

        return view;
    }

    private void setActiveTab(TextView selectedTab) {
        TextView[] allTabs = { tabTout, tabCours, tabTp };

        for (TextView tab : allTabs) {
            tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey));
            tab.setTypeface(null, Typeface.NORMAL);
        }

        selectedTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange));
        selectedTab.setTypeface(null, Typeface.BOLD);
    }

    private void filtrerDocuments(String type) {
        List<Documents> filtres = new ArrayList<>();
        for (Documents doc : listeDocuments) {
            if (type.equals("TOUT") || doc.getType().equalsIgnoreCase(type)) {
                filtres.add(doc);
            }
        }
        adapter.updateList(filtres);
    }

    private void chargerTitreCoursDepuisFirestore() {
        if (coursId != null) {
            FirebaseFirestore.getInstance().collection("cours")
                    .whereEqualTo("code", coursId)
                    .get()
                    .addOnSuccessListener(query -> {
                        if (!query.isEmpty()) {
                            coursSnapshot = query.getDocuments().get(0);
                            String titre = coursSnapshot.getString("titre");
                            titreCoursHeader.setText(titre);
                        }
                    });
        }
    }

    private void chargerDocumentsDepuisFirestore() {
        FirebaseFirestore.getInstance().collection("documents")
                .whereEqualTo("coursId", coursId)
                .get()
                .addOnSuccessListener(query -> {
                    listeDocuments.clear();
                    listeSnapshots.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        listeSnapshots.add(doc);
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
                    adapter.updateList(listeDocuments, listeSnapshots);
                });
    }

    private void showToast(String operation) {
        Toast.makeText(getContext(), "Document " + operation + " avec succès !", Toast.LENGTH_SHORT).show();
    }

    private void ouvrirDialogAjoutDocument() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_ajout_document, null);

        EditText editTitre = dialogView.findViewById(R.id.editTitre);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        EditText editType = dialogView.findViewById(R.id.editType);
        EditText editFormat = dialogView.findViewById(R.id.editFormat);
        EditText editUrl = dialogView.findViewById(R.id.editUrl);

        new AlertDialog.Builder(getContext())
                .setTitle("Ajouter un document")
                .setView(dialogView)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    Map<String, Object> docMap = new HashMap<>();
                    docMap.put("titre", editTitre.getText().toString());
                    docMap.put("description", editDescription.getText().toString());
                    docMap.put("type", editType.getText().toString());
                    docMap.put("format", editFormat.getText().toString());
                    docMap.put("url", editUrl.getText().toString());
                    docMap.put("coursId", coursId);
                    docMap.put("auteurId", FirebaseAuth.getInstance().getUid());
                    docMap.put("dateAjout", new Date());

                    FirebaseFirestore.getInstance().collection("documents")
                            .add(docMap)
                            .addOnSuccessListener(ref -> {
                                showToast("ajouté");
                                chargerDocumentsDepuisFirestore();
                            });
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onEditClicked(DocumentSnapshot doc) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_ajout_document, null);

        EditText editTitre = dialogView.findViewById(R.id.editTitre);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        EditText editType = dialogView.findViewById(R.id.editType);
        EditText editFormat = dialogView.findViewById(R.id.editFormat);
        EditText editUrl = dialogView.findViewById(R.id.editUrl);

        editTitre.setText(doc.getString("titre"));
        editDescription.setText(doc.getString("description"));
        editType.setText(doc.getString("type"));
        editFormat.setText(doc.getString("format"));
        editUrl.setText(doc.getString("url"));

        new AlertDialog.Builder(getContext())
                .setTitle("Modifier le document")
                .setView(dialogView)
                .setPositiveButton("Modifier", (dialog, which) -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("titre", editTitre.getText().toString());
                    updates.put("description", editDescription.getText().toString());
                    updates.put("type", editType.getText().toString());
                    updates.put("format", editFormat.getText().toString());
                    updates.put("url", editUrl.getText().toString());
                    updates.put("dateAjout", new Date());

                    doc.getReference().update(updates)
                            .addOnSuccessListener(x -> {
                                showToast("modifié");
                                chargerDocumentsDepuisFirestore();
                            });
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onDeleteClicked(DocumentSnapshot doc) {
        new AlertDialog.Builder(getContext())
                .setTitle("Supprimer le document")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce document ?")
                .setPositiveButton("Supprimer", (dialog, which) -> doc.getReference().delete()
                        .addOnSuccessListener(x -> {
                            showToast("supprimé");
                            chargerDocumentsDepuisFirestore();
                        }))
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onDocumentClicked(Documents document) {
        Intent intent = new Intent(requireContext(), PdfViewerActivity.class);
        intent.putExtra("url", document.getUrl());
        startActivity(intent);
    }

}

