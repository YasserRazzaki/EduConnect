package com.example.educonnect.utils;

import android.util.Log;

import com.example.educonnect.models.Annonce;
import com.example.educonnect.models.Cours;
import com.example.educonnect.models.Documents;
import com.example.educonnect.models.Enseignant;
import com.example.educonnect.models.Etudiant;
import com.example.educonnect.models.Utilisateur;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class DataInserter {

    public static void insererDonneesTest() {
        FirebaseDataUploader uploader = new FirebaseDataUploader();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Administrateur
        Utilisateur adminUser = new Utilisateur("Admin", "Super", "admin@admin.com", "admin123", "ADMIN");
        uploader.ajouterUtilisateurAdminSiAbsent(adminUser);

        // Étudiant
        Utilisateur etuUser = new Utilisateur("Durand", "Alice", "alice@etu.com", "test123", "ETUDIANT");
        Etudiant etudiant = new Etudiant("etu1", "Informatique", "L3");
        uploader.ajouterUtilisateurEtudiantSiAbsent(etuUser, etudiant);

        // Enseignant
        Utilisateur ensUser = new Utilisateur("Martin", "Paul", "paul@prof.com", "prof123", "ENSEIGNANT");
        Enseignant enseignant = new Enseignant("ens1", "Informatique", "Réseaux");
        uploader.ajouterUtilisateurEnseignantSiAbsent(ensUser, enseignant);

        // Cours
        uploader.ajouterCoursSiAbsent(new Cours("INFO101", "Java", "Intro à Java", "ens1", 3, "Informatique", "L3"));
        uploader.ajouterCoursSiAbsent(new Cours("HAI825", "Prog Mobile", "Intro à la prog Mobile", "ens1", 3, "Informatique", "L3"));

        // Annonce
        uploader.ajouterAnnonceSiAbsente(
                new Annonce("Cours de Compilation annulé",
                        "Le cours de compilation est annulé vu que M. David Delhaye a une inconvénience personnelle.",
                        new Date())
        );

        // Documents
        uploader.ajouterDocumentSiAbsent(new Documents(
                "Cours 1 - Introduction", "PDF d'introduction au module", "Cours", "pdf",
                "https://www.africau.edu/images/default/sample.pdf", "INFO101", "ens1", new Date()));

        uploader.ajouterDocumentSiAbsent(new Documents(
                "TP1 - Structures de données", "TP à rendre la semaine prochaine", "TP/TD", "pdf",
                "https://www.orimi.com/pdf-test.pdf", "INFO101", "ens1", new Date()));

        // ➤ Ajout des séances SI elles n'existent pas déjà
        db.collection("enseignants")
                .whereEqualTo("specialite", "Réseaux")
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        DocumentSnapshot enseignantDoc = query.getDocuments().get(0);
                        String enseignantId = enseignantDoc.getId();

                        db.collection("seances")
                                .whereEqualTo("enseignantId", enseignantId)
                                .get()
                                .addOnSuccessListener(seancesQuery -> {
                                    if (seancesQuery.isEmpty()) {
                                        System.out.println("🆕 Séances inexistantes, création...");
                                        DonneesCalendrier.ajouterSeancesTest(enseignantId);
                                    } else {
                                        System.out.println("✅ Les séances existent déjà. Pas de duplication.");
                                    }
                                })
                                .addOnFailureListener(e ->
                                        System.err.println("❌ Erreur lors de la vérification des séances : " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e ->
                        System.err.println("❌ Erreur lors de la récupération de l'enseignant : " + e.getMessage()));
    }

    public static void forcerSeancesSeulement() {
        FirebaseFirestore.getInstance().collection("enseignants")
                .whereEqualTo("specialite", "Réseaux")
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        String enseignantId = query.getDocuments().get(0).getId();
                        DonneesCalendrier.ajouterSeancesTest(enseignantId);
                    } else {
                        Log.e("DataInserter", "Aucun enseignant trouvé pour insérer les séances");
                    }
                })
                .addOnFailureListener(e -> Log.e("DataInserter", "Erreur récupération enseignant", e));
    }
}
