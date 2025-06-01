package com.example.educonnect.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.educonnect.R;
import com.example.educonnect.models.Seance;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class EditSeanceActivity extends AppCompatActivity {

    private EditText editNom, editCode, editSalle, editFiliere, editNiveau;
    private Button btnDate, btnHeureDebut, btnHeureFin, btnSave;
    private Spinner spinnerCouleur;

    private String seanceId;
    private Date dateDebut, dateFin;
    private final Calendar calendar = Calendar.getInstance();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
    private final SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seance);

        editNom = findViewById(R.id.editNom);
        editCode = findViewById(R.id.editCode);
        editSalle = findViewById(R.id.editSalle);
        editFiliere = findViewById(R.id.editFiliere);
        editNiveau = findViewById(R.id.editNiveau);
        btnDate = findViewById(R.id.btnDate);
        btnHeureDebut = findViewById(R.id.btnHeureDebut);
        btnHeureFin = findViewById(R.id.btnHeureFin);
        btnSave = findViewById(R.id.btnSave);
        spinnerCouleur = findViewById(R.id.spinnerCouleur);

        // Initialiser les couleurs disponibles
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Rouge", "Bleu", "Vert", "Jaune"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCouleur.setAdapter(adapter);

        seanceId = getIntent().getStringExtra("seanceId");
        if (seanceId != null) {
            chargerSeance();
        }

        btnDate.setOnClickListener(v -> showDatePicker());
        btnHeureDebut.setOnClickListener(v -> showTimePicker(true));
        btnHeureFin.setOnClickListener(v -> showTimePicker(false));
        btnSave.setOnClickListener(v -> sauvegarderSeance());
    }

    private void chargerSeance() {
        FirebaseFirestore.getInstance()
                .collection("seances")
                .document(seanceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Seance seance = doc.toObject(Seance.class);
                        editNom.setText(seance.getNom());
                        editCode.setText(seance.getCode());
                        editSalle.setText(seance.getSalle());
                        editFiliere.setText(seance.getFiliere());
                        editNiveau.setText(seance.getNiveau());

                        dateDebut = seance.getDateDebut();
                        dateFin = seance.getDateFin();

                        btnDate.setText(dateFormat.format(dateDebut));
                        btnHeureDebut.setText(heureFormat.format(dateDebut));
                        btnHeureFin.setText(heureFormat.format(dateFin));
                    }
                });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(boolean isStartTime) {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            if (isStartTime) {
                if (dateDebut == null) dateDebut = calendar.getTime();
                Calendar start = Calendar.getInstance();
                start.setTime(dateDebut);
                start.set(Calendar.HOUR_OF_DAY, hourOfDay);
                start.set(Calendar.MINUTE, minute);
                dateDebut = start.getTime();
                btnHeureDebut.setText(heureFormat.format(dateDebut));
            } else {
                if (dateFin == null) dateFin = calendar.getTime();
                Calendar end = Calendar.getInstance();
                end.setTime(dateFin);
                end.set(Calendar.HOUR_OF_DAY, hourOfDay);
                end.set(Calendar.MINUTE, minute);
                dateFin = end.getTime();
                btnHeureFin.setText(heureFormat.format(dateFin));
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
    }

    private void updateDate() {
        Calendar selectedDate = (Calendar) calendar.clone();

        if (dateDebut != null) {
            Calendar start = Calendar.getInstance();
            start.setTime(dateDebut);
            selectedDate.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
            selectedDate.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
        } else {
            selectedDate.set(Calendar.HOUR_OF_DAY, 8);
            selectedDate.set(Calendar.MINUTE, 0);
        }
        dateDebut = selectedDate.getTime();

        if (dateFin != null) {
            Calendar end = Calendar.getInstance();
            end.setTime(dateFin);
            selectedDate.set(Calendar.HOUR_OF_DAY, end.get(Calendar.HOUR_OF_DAY));
            selectedDate.set(Calendar.MINUTE, end.get(Calendar.MINUTE));
        } else {
            Calendar end = (Calendar) selectedDate.clone();
            end.add(Calendar.HOUR_OF_DAY, 1);
            dateFin = end.getTime();
        }

        btnDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void sauvegarderSeance() {
        String nom = editNom.getText().toString().trim();
        String code = editCode.getText().toString().trim();
        String salle = editSalle.getText().toString().trim();
        String filiere = editFiliere.getText().toString().trim();
        String niveau = editNiveau.getText().toString().trim();

        if (TextUtils.isEmpty(nom) || dateDebut == null || dateFin == null) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // Déterminer la couleur selon le Spinner
        String selectedColorName = spinnerCouleur.getSelectedItem().toString();
        String couleur;
        switch (selectedColorName) {
            case "Bleu": couleur = "#2196F3"; break;
            case "Vert": couleur = "#4CAF50"; break;
            case "Jaune": couleur = "#FFEB3B"; break;
            default: couleur = "#FF5252"; // Rouge par défaut
        }

        Map<String, Object> data = new HashMap<>();
        data.put("nom", nom);
        data.put("code", code);
        data.put("salle", salle);
        data.put("filiere", filiere);
        data.put("niveau", niveau);
        data.put("dateDebut", dateDebut);
        data.put("dateFin", dateFin);
        data.put("couleur", couleur);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (seanceId != null) {
            db.collection("seances").document(seanceId).update(data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Séance modifiée", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            db.collection("seances").add(data)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Séance ajoutée", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}