package com.example.educonnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.activities.RenduActivity;
import com.example.educonnect.R;
import com.example.educonnect.adapters.DevoirAdapter;
import com.example.educonnect.models.Devoir;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class DevoirsEtudiantFragment extends Fragment {

    private RecyclerView recyclerView;
    private DevoirAdapter devoirAdapter;
    private final List<Devoir> devoirs = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devoirs, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDevoirs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        devoirAdapter = new DevoirAdapter(devoirs, false, null, null,this::onDevoirClicked);
        recyclerView.setAdapter(devoirAdapter);
        FloatingActionButton fab = view.findViewById(R.id.fabAjouterDevoir);
        fab.setVisibility(View.GONE); // 👈 cacher par défaut (si c’est un étudiant)
        chargerDevoirs();
        return view;
    }

    private void chargerDevoirs() {
        String uid = FirebaseAuth.getInstance().getUid();
        assert uid != null;
        db.collection("etudiants").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String filiere = doc.getString("filiere");
                        String niveau = doc.getString("niveau");

                        Log.d("DEBUG_ETUDIANT", "Filière: " + filiere + " | Niveau: " + niveau);

                        db.collection("devoirs")
                                .whereEqualTo("filiere", filiere)
                                .whereEqualTo("niveau", niveau)
                                .orderBy("dateEcheance", Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener(result -> {
                                    devoirs.clear();
                                    for (DocumentSnapshot snapshot : result.getDocuments()) {
                                        Devoir d = snapshot.toObject(Devoir.class);
                                        if (d != null) {
                                            d.setId(snapshot.getId());
                                            devoirs.add(d);
                                            Log.d("DEBUG_DEVOIR", "Titre: " + d.getTitre() + " | Date: " + d.getDateEcheance());
                                        }
                                    }

                                    devoirAdapter.notifyDataSetChanged();

                                   // Toast.makeText(getContext(), "Devoirs récupérés : " + devoirs.size(), Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Log.e("DEBUG_DEVOIR", "Erreur récupération devoirs: " + e.getMessage()));
                    } else {
                        Log.w("DEBUG_ETUDIANT", "Document étudiant inexistant");
                    }
                })
                .addOnFailureListener(e -> Log.e("DEBUG_ETUDIANT", "Erreur récupération étudiant: " + e.getMessage()));
    }

    private void onDevoirClicked(Devoir devoir) {
        // Ouvre la page de dépôt de rendu
        Intent intent = new Intent(getContext(), RenduActivity.class);
        intent.putExtra("devoirId", devoir.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        chargerDevoirs();
    }

}

