package com.example.educonnect.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.adapters.DevoirAdapter;
import com.example.educonnect.models.Devoir;
import com.example.educonnect.activities.CreateDevoirActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import com.example.educonnect.activities.ListeRendusActivity;

import java.util.ArrayList;
import java.util.List;

public class DevoirsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private DevoirAdapter devoirAdapter;
    private final List<Devoir> devoirs = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devoirs, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDevoirs);
        fab = view.findViewById(R.id.fabAjouterDevoir);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        devoirAdapter = new DevoirAdapter(devoirs, true,
                this::onModifierClicked, this::onSupprimerClicked,
                devoir -> {
                    Intent i = new Intent(getContext(), ListeRendusActivity.class);
                    i.putExtra("devoirId", devoir.getId());
                    startActivity(i);
                });
        recyclerView.setAdapter(devoirAdapter);

        fab.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CreateDevoirActivity.class));
        });

        Log.d("DEVOIRS", "profId = " + FirebaseAuth.getInstance().getUid());
        chargerDevoirs();
        return view;

    }

    private void chargerDevoirs() {
        String profId = FirebaseAuth.getInstance().getUid();
        db.collection("devoirs")
                .whereEqualTo("enseignantId", profId)
                .orderBy("dateEcheance", Query.Direction.ASCENDING) // ou DESCENDING selon préférence
                .get()
                .addOnSuccessListener(result -> {
                    devoirs.clear();
                    for (DocumentSnapshot doc : result.getDocuments()) {
                        Devoir d = doc.toObject(Devoir.class);
                        if (d != null) {
                            d.setId(doc.getId());
                            devoirs.add(d);
                        }
                    }
                    devoirAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur chargement devoirs", Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); // Ajoute ceci pour voir le lien vers l’index dans Logcat
                });
    }

    private void onModifierClicked(Devoir devoir) {
        Intent intent = new Intent(getContext(), CreateDevoirActivity.class);
        intent.putExtra("devoirId", devoir.getId());
        startActivity(intent);
    }

    private void onSupprimerClicked(Devoir devoir) {
        new AlertDialog.Builder(getContext())
                .setTitle("Supprimer le devoir")
                .setMessage("Voulez-vous vraiment supprimer ce devoir ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection("devoirs")
                            .document(devoir.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Devoir supprimé", Toast.LENGTH_SHORT).show();
                                chargerDevoirs(); // Recharge la liste
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        chargerDevoirs();
    }

}
