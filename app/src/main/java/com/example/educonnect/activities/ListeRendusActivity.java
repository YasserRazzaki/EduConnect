package com.example.educonnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.adapters.RenduAdapter;
import com.example.educonnect.models.Rendu;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListeRendusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RenduAdapter adapter;
    private final List<Rendu> rendus = new ArrayList<>();
    private String devoirId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_rendus);

        devoirId = getIntent().getStringExtra("devoirId");
        recyclerView = findViewById(R.id.recyclerViewRendus);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RenduAdapter(rendus, rendu -> {
            Intent i = new Intent(this, EvaluationRenduActivity.class);
            i.putExtra("renduId", rendu.getId());
            startActivity(i);
        });

        recyclerView.setAdapter(adapter);
        chargerRendus();
    }

    private void chargerRendus() {
        FirebaseFirestore.getInstance().collection("rendus")
                .whereEqualTo("devoirId", devoirId)
                .get()
                .addOnSuccessListener(query -> {
                    rendus.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Rendu r = doc.toObject(Rendu.class);
                        r.setId(doc.getId());
                        rendus.add(r);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur chargement rendus", Toast.LENGTH_SHORT).show());
    }
}
