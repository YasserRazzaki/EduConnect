package com.example.educonnect.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.educonnect.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminCreerCoursActivity extends AppCompatActivity {

    private EditText editCode, editTitre, editDescription, editCredits, editFiliere, editNiveau;
    private Spinner spinnerProfs;
    private Button btnValider;
    private List<DocumentSnapshot> profDocs = new ArrayList<>();
    private String coursId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_creer_cours);

        editCode = findViewById(R.id.editCode);
        editTitre = findViewById(R.id.editTitre);
        editDescription = findViewById(R.id.editDescription);
        editCredits = findViewById(R.id.editCredits);
        editFiliere = findViewById(R.id.editFiliere);
        editNiveau = findViewById(R.id.editNiveau);
        spinnerProfs = findViewById(R.id.spinnerProfs);
        btnValider = findViewById(R.id.btnValider);

        chargerListeProfs();

        coursId = getIntent().getStringExtra("coursId");
        if (coursId != null) {
            chargerCoursExistants(coursId);
        }

        btnValider.setOnClickListener(v -> validerCours());
    }

    private void chargerListeProfs() {
        FirebaseFirestore.getInstance().collection("utilisateurs")
                .whereEqualTo("type", "ENSEIGNANT")
                .get()
                .addOnSuccessListener(query -> {
                    profDocs = query.getDocuments();
                    List<String> noms = new ArrayList<>();
                    for (DocumentSnapshot doc : profDocs) {
                        String nom = doc.getString("prenom") + " " + doc.getString("nom");
                        noms.add(nom);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, noms);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProfs.setAdapter(adapter);
                });
    }

    private void chargerCoursExistants(String coursId) {
        FirebaseFirestore.getInstance().collection("cours")
                .document(coursId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        editCode.setText(doc.getString("code"));
                        editTitre.setText(doc.getString("titre"));
                        editDescription.setText(doc.getString("description"));
                        editCredits.setText(String.valueOf(doc.getLong("credits")));
                        editFiliere.setText(doc.getString("filiere"));
                        editNiveau.setText(doc.getString("niveau"));

                        String enseignantId = doc.getString("enseignantId");
                        for (int i = 0; i < profDocs.size(); i++) {
                            if (profDocs.get(i).getId().equals(enseignantId)) {
                                spinnerProfs.setSelection(i);
                                break;
                            }
                        }

                        btnValider.setText("Modifier");
                    }
                });
    }

    private void validerCours() {
        String code = editCode.getText().toString();
        String titre = editTitre.getText().toString();
        String description = editDescription.getText().toString();
        int credits = Integer.parseInt(editCredits.getText().toString());
        String filiere = editFiliere.getText().toString();
        String niveau = editNiveau.getText().toString();
        String enseignantId = profDocs.get(spinnerProfs.getSelectedItemPosition()).getId();

        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("titre", titre);
        map.put("description", description);
        map.put("credits", credits);
        map.put("filiere", filiere);
        map.put("niveau", niveau);
        map.put("enseignantId", enseignantId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (coursId != null) {
            db.collection("cours").document(coursId).set(map)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Cours modifié", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            db.collection("cours").add(map)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Cours créé", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}