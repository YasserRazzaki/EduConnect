package com.example.educonnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.educonnect.R;
import com.example.educonnect.models.Devoir;
import com.example.educonnect.models.Rendu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class RenduActivity extends AppCompatActivity {

    private static final int FILE_PICKER_REQUEST = 1001;

    private TextView statut, tempsRestant, fichier;
    private Button btnDeposer, btnSupprimer;

    private String devoirId;
    private String etudiantId;
    private Date dateEcheance;

    private TextView noteView, commentaireView;
    private View rowNote, rowCommentaire;

    private String formatTempsRestant(long millis) {
        long secondes = millis / 1000;
        long minutes = secondes / 60;
        long heures = minutes / 60;
        long jours = heures / 24;

        minutes %= 60;
        heures %= 24;

        StringBuilder sb = new StringBuilder();
        if (jours > 0) sb.append(jours).append(" jour").append(jours > 1 ? "s " : " ");
        if (heures > 0) sb.append(heures).append(" heure").append(heures > 1 ? "s " : " ");
        if (minutes > 0 || sb.length() == 0) sb.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");

        return sb.toString().trim();
    }


    private Uri selectedFileUri = null;
    private String renduDocId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rendu);

        devoirId = getIntent().getStringExtra("devoirId");
        etudiantId = FirebaseAuth.getInstance().getUid();

        statut = findViewById(R.id.textViewStatut);
        tempsRestant = findViewById(R.id.textViewTempsRestant);
        fichier = findViewById(R.id.textViewNomFichier);
        btnDeposer = findViewById(R.id.btnDeposer);
        btnSupprimer = findViewById(R.id.btnSupprimer);

        btnDeposer.setOnClickListener(v -> ouvrirExplorateur());
        btnSupprimer.setOnClickListener(v -> supprimerRendu());
        noteView = findViewById(R.id.textViewNote);
        commentaireView = findViewById(R.id.textViewCommentaire);
        rowNote = findViewById(R.id.rowNote);
        rowCommentaire = findViewById(R.id.rowCommentaire);

        chargerDevoirEtRendu();
    }

    private void chargerDevoirEtRendu() {
        FirebaseFirestore.getInstance().collection("devoirs").document(devoirId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Devoir devoir = doc.toObject(Devoir.class);
                        dateEcheance = devoir.getDateEcheance();
                        chargerEtatRendu();
                    } else {
                        Toast.makeText(this, "Devoir introuvable", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void chargerEtatRendu() {

        FirebaseFirestore.getInstance().collection("rendus")
                .whereEqualTo("devoirId", devoirId)
                .whereEqualTo("etudiantId", etudiantId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    boolean renduExiste = !snapshots.isEmpty();
                    boolean enRetard = new Date().after(dateEcheance);

                    if (renduExiste) {
                        DocumentSnapshot doc = snapshots.getDocuments().get(0);
                        renduDocId = doc.getId();
                        Rendu rendu = doc.toObject(Rendu.class);
                        statut.setText("Travail rendu");

                        String nomFichier = rendu.getNomFichier(); // ✅ Utilise le nom enregistré
                        fichier.setText(nomFichier != null ? nomFichier : "Fichier inconnu");

                        long diff = dateEcheance.getTime() - rendu.getDateSoumission().getTime();
                        tempsRestant.setText(diff > 0
                                ? "Rendu avec " + formatTempsRestant(diff) + " d'avance"
                                : "Rendu en retard");

                        btnSupprimer.setVisibility(!enRetard ? View.VISIBLE : View.GONE);
                        btnDeposer.setText("Modifier le rendu");
                        btnDeposer.setVisibility(!enRetard ? View.VISIBLE : View.GONE);
                        if (rendu.getNote() != null) {
                            rowNote.setVisibility(View.VISIBLE);
                            noteView.setText(rendu.getNote() + "/20");
                        }

                        if (rendu.getCommentaireEnseignant() != null && !rendu.getCommentaireEnseignant().isEmpty()) {
                            rowCommentaire.setVisibility(View.VISIBLE);
                            commentaireView.setText(rendu.getCommentaireEnseignant());
                        }

                    } else {
                        statut.setText("Pas de rendu pour l’instant");
                        fichier.setText("Aucun fichier");
                        long diff = dateEcheance.getTime() - new Date().getTime();

                        tempsRestant.setText(diff > 0
                                ? "Temps restant : " + formatTempsRestant(diff)
                                : "Dépassement de l’échéance");

                        btnDeposer.setVisibility(diff > 0 ? View.VISIBLE : View.GONE);
                        btnSupprimer.setVisibility(View.GONE);
                    }
                });
    }

    private void ouvrirExplorateur() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Sélectionner un fichier"), FILE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            deposerOuModifierRendu();
        }
    }

    private void deposerOuModifierRendu() {
        if (selectedFileUri == null) {
            Toast.makeText(this, "Aucun fichier valide sélectionné", Toast.LENGTH_SHORT).show();
            return;
        }

        // Afficher un indicateur de progression
        Toast.makeText(this, "Téléchargement en cours...", Toast.LENGTH_SHORT).show();

        try {
            // Obtenir le nom du fichier original
            String originalFileName = getFileNameFromUri(selectedFileUri);
            if (originalFileName == null) originalFileName = "document";

            // Créer un chemin unique pour le fichier
            String fileName = "rendus/" + etudiantId + "/" + devoirId + "/" + System.currentTimeMillis() + "_" + originalFileName;

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileName);

            // Télécharger directement depuis l'URI sans créer de fichier temporaire
            String finalOriginalFileName = originalFileName;
            ref.putFile(selectedFileUri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return ref.getDownloadUrl();
                    })
                    .addOnSuccessListener(uri -> {
                        Rendu rendu = new Rendu(devoirId, etudiantId, "", uri.toString(), new Date());
                        rendu.setCommentaire(""); // si nécessaire
                        rendu.setNote(null);
                        rendu.setCommentaireEnseignant(null);
                        rendu.setNomFichier(finalOriginalFileName); // ✅ Ajout du nom réel
                        if (renduDocId != null) {
                            FirebaseFirestore.getInstance().collection("rendus")
                                    .document(renduDocId)
                                    .set(rendu)
                                    .addOnSuccessListener(v -> {
                                        Toast.makeText(this, "Rendu modifié", Toast.LENGTH_SHORT).show();
                                        chargerEtatRendu();
                                    });
                        } else {
                            FirebaseFirestore.getInstance().collection("rendus")
                                    .add(rendu)
                                    .addOnSuccessListener(v -> {
                                        Toast.makeText(this, "Rendu déposé", Toast.LENGTH_SHORT).show();
                                        chargerEtatRendu();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Méthode pour obtenir le nom du fichier à partir de l'URI
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void supprimerRendu() {
        if (renduDocId != null) {
            FirebaseFirestore.getInstance().collection("rendus")
                    .document(renduDocId)
                    .delete()
                    .addOnSuccessListener(v -> {
                        Toast.makeText(this, "Rendu supprimé", Toast.LENGTH_SHORT).show();
                        chargerEtatRendu();
                    });
        }
    }

}
