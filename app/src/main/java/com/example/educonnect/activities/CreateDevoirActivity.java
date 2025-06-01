package com.example.educonnect.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.educonnect.R;
import com.example.educonnect.models.Cours;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class CreateDevoirActivity extends AppCompatActivity {

    private EditText editTitre, editDescription, editDateEcheance;
    private Spinner spinnerCours;
    private Button btnValider;
    private Calendar dateEcheanceCalendar;
    private List<Cours> coursList = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private String selectedCoursId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.educonnect.R.layout.activity_create_devoir);
        Log.d("UID", Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        editTitre = findViewById(com.example.educonnect.R.id.editTitreDevoir);
        editDescription = findViewById(com.example.educonnect.R.id.editDescriptionDevoir);
        editDateEcheance = findViewById(com.example.educonnect.R.id.editDateEcheance);
        spinnerCours = findViewById(com.example.educonnect.R.id.spinnerCours);
        btnValider = findViewById(R.id.btnValiderDevoir);
        dateEcheanceCalendar = Calendar.getInstance();

        editDateEcheance.setOnClickListener(v -> openDateTimePicker());

        btnValider.setOnClickListener(v -> creerDevoir());

        chargerCoursDuProf();
        String devoirId = getIntent().getStringExtra("devoirId");
        if (devoirId != null) {
            chargerDevoirPourModification(devoirId);
        }

    }

    private void chargerDevoirPourModification(String devoirId) {
        FirebaseFirestore.getInstance().collection("devoirs")
                .document(devoirId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        editTitre.setText(doc.getString("titre"));
                        editDescription.setText(doc.getString("description"));
                        Date dateEcheance = doc.getDate("dateEcheance");
                        if (dateEcheance != null) {
                            dateEcheanceCalendar.setTime(dateEcheance);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            editDateEcheance.setText(sdf.format(dateEcheance));
                        }

                        selectedCoursId = doc.getString("coursId");

                        // Attendre que les cours soient chargés pour sélectionner celui du devoir
                        spinnerCours.post(() -> {
                            for (int i = 0; i < coursList.size(); i++) {
                                if (coursList.get(i).getId().equals(selectedCoursId)) {
                                    spinnerCours.setSelection(i);
                                    break;
                                }
                            }
                        });

                        btnValider.setText("Modifier");
                    }
                });
    }

    private void openDateTimePicker() {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            dateEcheanceCalendar.set(Calendar.YEAR, year);
            dateEcheanceCalendar.set(Calendar.MONTH, month);
            dateEcheanceCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(this, (view1, hour, minute) -> {
                dateEcheanceCalendar.set(Calendar.HOUR_OF_DAY, hour);
                dateEcheanceCalendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                editDateEcheance.setText(sdf.format(dateEcheanceCalendar.getTime()));
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();

        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void chargerCoursDuProf() {
        String profId = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("cours")
                .whereEqualTo("enseignantId", profId)
                .get()
                .addOnSuccessListener(query -> {
                    List<String> titres = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Cours c = doc.toObject(Cours.class);
                        if (c != null) {
                            c.setId(doc.getId()); // utile pour retrouver plus tard
                            coursList.add(c);
                            titres.add(c.getTitre());
                        }
                    }

                    spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, titres);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCours.setAdapter(spinnerAdapter);
                    spinnerCours.setPrompt("Choisir un cours");
                    spinnerCours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCoursId = coursList.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedCoursId = null;
                        }
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur chargement des cours", Toast.LENGTH_SHORT).show());
    }

    private void creerDevoir() {
        String profId = FirebaseAuth.getInstance().getUid();
        String titre = editTitre.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (TextUtils.isEmpty(titre) || TextUtils.isEmpty(editDateEcheance.getText().toString()) || selectedCoursId == null) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Cours selectedCours = null;
        for (Cours c : coursList) {
            if (c.getId().equals(selectedCoursId)) {
                selectedCours = c;
                break;
            }
        }

        if (selectedCours == null) {
            Toast.makeText(this, "Cours introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> devoirMap = new HashMap<>();
        devoirMap.put("titre", titre);
        devoirMap.put("description", description);
        devoirMap.put("coursId", selectedCoursId);
        devoirMap.put("dateEcheance", dateEcheanceCalendar.getTime());
        devoirMap.put("enseignantId", profId);
        devoirMap.put("filiere", selectedCours.getFiliere());
        devoirMap.put("niveau", selectedCours.getNiveau());

        String devoirId = getIntent().getStringExtra("devoirId");
        if (devoirId != null) {
            // 🛠 MODIFICATION
            FirebaseFirestore.getInstance().collection("devoirs")
                    .document(devoirId)
                    .update(devoirMap)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Devoir modifié avec succès", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // 🆕 CRÉATION
            devoirMap.put("dateCreation", new Date());
            FirebaseFirestore.getInstance().collection("devoirs")
                    .add(devoirMap)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Devoir créé avec succès", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}