package com.example.educonnect.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.adapters.AnnonceAdapter;
import com.example.educonnect.models.Annonce;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllAnnoncesFragment extends Fragment {

    private RecyclerView recyclerViewAllAnnonces;
    private AnnonceAdapter annonceAdapter;
    private final List<Annonce> annonceList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_annonces, container, false);

        recyclerViewAllAnnonces = view.findViewById(R.id.recyclerViewAllAnnonces);
        recyclerViewAllAnnonces.setLayoutManager(new LinearLayoutManager(getContext()));
        annonceAdapter = new AnnonceAdapter(annonceList);
        recyclerViewAllAnnonces.setAdapter(annonceAdapter);

        chargerToutesLesAnnonces();

        return view;
    }

    private void chargerToutesLesAnnonces() {
        FirebaseFirestore.getInstance().collection("annonces")
                .orderBy("datePublication", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    annonceList.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Annonce annonce = new Annonce(
                                doc.getString("titre"),
                                doc.getString("contenu"),
                                doc.getDate("datePublication") != null ? doc.getDate("datePublication") : new Date()
                        );
                        annonce.setId(doc.getId());
                        annonceList.add(annonce);
                    }
                    annonceAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("AllAnnoncesFragment", "Erreur chargement annonces", e));
    }
}