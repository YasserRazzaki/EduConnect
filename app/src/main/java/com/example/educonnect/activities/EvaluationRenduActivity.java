package com.example.educonnect.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.educonnect.R;
import com.example.educonnect.models.Rendu;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EvaluationRenduActivity extends AppCompatActivity {

    private TextView nomFichier, dateSoumission, textTelecharger;
    private EditText inputNote, inputCommentaire;
    private Button btnEnregistrer;
    private String renduId;
    private Rendu rendu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_rendu);

        renduId = getIntent().getStringExtra("renduId");

        nomFichier = findViewById(R.id.textNomFichier);
        dateSoumission = findViewById(R.id.textDateSoumission);
        textTelecharger = findViewById(R.id.textTelecharger);
        inputNote = findViewById(R.id.inputNote);
        inputCommentaire = findViewById(R.id.inputCommentaire);
        btnEnregistrer = findViewById(R.id.btnEvaluer);

        chargerRendu();

        btnEnregistrer.setOnClickListener(v -> enregistrerEvaluation());
    }

    private void chargerRendu() {
        FirebaseFirestore.getInstance().collection("rendus").document(renduId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        rendu = doc.toObject(Rendu.class);
                        String nom = getNomFichierDepuisUrl(rendu.getFichierUrl());
                        nomFichier.setText("Fichier : " + nom);

                        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM yyyy HH:mm", Locale.getDefault());
                        dateSoumission.setText("Soumis le : " + sdf.format(rendu.getDateSoumission()));

                        textTelecharger.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rendu.getFichierUrl()));
                            startActivity(intent);
                        });

                        if (rendu.getNote() != null) inputNote.setText(String.valueOf(rendu.getNote()));
                        if (rendu.getCommentaireEnseignant() != null)
                            inputCommentaire.setText(rendu.getCommentaireEnseignant());
                    }
                });
    }

    private String getNomFichierDepuisUrl(String url) {
        try {
            return Uri.decode(Uri.parse(url).getLastPathSegment().split("\\?")[0].replaceAll(".+/", ""));
        } catch (Exception e) {
            return "Fichier inconnu";
        }
    }

    private void enregistrerEvaluation() {
        try {
            int note = Integer.parseInt(inputNote.getText().toString());
            String commentaire = inputCommentaire.getText().toString();

            rendu.setNote(note);
            rendu.setCommentaireEnseignant(commentaire);

            FirebaseFirestore.getInstance().collection("rendus")
                    .document(renduId)
                    .set(rendu)
                    .addOnSuccessListener(v -> {
                        Toast.makeText(this, "Évaluation enregistrée", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Note invalide", Toast.LENGTH_SHORT).show();
        }
    }
}
