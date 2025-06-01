package com.example.educonnect.utils;

import android.util.Log;

import com.example.educonnect.models.Annonce;
import com.example.educonnect.models.Cours;
import com.example.educonnect.models.Documents;
import com.example.educonnect.models.Enseignant;
import com.example.educonnect.models.Etudiant;
import com.example.educonnect.models.Utilisateur;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDataUploader {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FirebaseDataUploader() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void ajouterUtilisateurEtudiantSiAbsent(Utilisateur utilisateur, Etudiant etudiant) {
        db.collection("utilisateurs")
                .whereEqualTo("email", utilisateur.getEmail())
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        ajouterUtilisateurEtudiant(utilisateur, etudiant);
                    } else {
                        Log.d("FirebaseUploader", "Étudiant déjà existant: " + utilisateur.getEmail());
                    }
                });
    }

    public void ajouterUtilisateurEtudiant(Utilisateur utilisateur, Etudiant etudiant) {
        auth.createUserWithEmailAndPassword(utilisateur.getEmail(), utilisateur.getMotDePasse())
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("nom", utilisateur.getNom());
                        userMap.put("prenom", utilisateur.getPrenom());
                        userMap.put("email", utilisateur.getEmail());
                        userMap.put("type", utilisateur.getType());

                        db.collection("utilisateurs").document(uid).set(userMap)
                                .addOnSuccessListener(unused -> {
                                    Map<String, Object> etuMap = new HashMap<>();
                                    etuMap.put("filiere", etudiant.getFiliere());
                                    etuMap.put("niveau", etudiant.getNiveau());
                                    etuMap.put("utilisateurId", uid);

                                    db.collection("etudiants").document(uid).set(etuMap)
                                            .addOnSuccessListener(x -> Log.d("FirebaseUploader", "Étudiant ajouté avec succès"))
                                            .addOnFailureListener(e -> Log.e("FirebaseUploader", "Erreur ajout étudiant", e));
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseUploader", "Erreur création compte étudiant", e));
    }

    public void ajouterUtilisateurEnseignantSiAbsent(Utilisateur utilisateur, Enseignant enseignant) {
        db.collection("utilisateurs")
                .whereEqualTo("email", utilisateur.getEmail())
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        ajouterUtilisateurEnseignant(utilisateur, enseignant);
                    } else {
                        String uid = query.getDocuments().get(0).getId();
                        Log.d("FirebaseUploader", "Enseignant déjà existant: " + utilisateur.getEmail());
                        ajouterInfosEnseignantFirestore(uid, enseignant);
                    }
                });
    }

    public void ajouterUtilisateurEnseignant(Utilisateur utilisateur, Enseignant enseignant) {
        auth.createUserWithEmailAndPassword(utilisateur.getEmail(), utilisateur.getMotDePasse())
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("nom", utilisateur.getNom());
                        userMap.put("prenom", utilisateur.getPrenom());
                        userMap.put("email", utilisateur.getEmail());
                        userMap.put("type", utilisateur.getType());

                        db.collection("utilisateurs").document(uid).set(userMap)
                                .addOnSuccessListener(unused -> ajouterInfosEnseignantFirestore(uid, enseignant));
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseUploader", "Erreur création compte enseignant", e));
    }

    private void ajouterInfosEnseignantFirestore(String uid, Enseignant enseignant) {
        db.collection("enseignants").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Map<String, Object> ensMap = new HashMap<>();
                        ensMap.put("departement", enseignant.getDepartement());
                        ensMap.put("specialite", enseignant.getSpecialite());
                        ensMap.put("utilisateurId", uid);

                        db.collection("enseignants").document(uid).set(ensMap)
                                .addOnSuccessListener(x -> Log.d("FirebaseUploader", "Enseignant ajouté avec succès"))
                                .addOnFailureListener(e -> Log.e("FirebaseUploader", "Erreur ajout enseignant", e));
                    } else {
                        Log.d("FirebaseUploader", "Infos enseignant déjà présentes pour UID: " + uid);
                    }
                });
    }

    public void ajouterCoursSiAbsent(Cours cours) {
        db.collection("cours")
                .whereEqualTo("code", cours.getCode())
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        Map<String, Object> coursMap = new HashMap<>();
                        coursMap.put("code", cours.getCode());
                        coursMap.put("titre", cours.getTitre());
                        coursMap.put("description", cours.getDescription());
                        coursMap.put("enseignantId", cours.getEnseignantId());
                        coursMap.put("credits", cours.getCredits());
                        coursMap.put("filiere", cours.getFiliere());
                        coursMap.put("niveau", cours.getNiveau());

                        db.collection("cours").add(coursMap);
                    }
                });
    }

    public void ajouterAnnonceSiAbsente(Annonce annonce) {
        db.collection("annonces")
                .whereEqualTo("titre", annonce.getTitre())
                .whereEqualTo("contenu", annonce.getContenu())
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("titre", annonce.getTitre());
                        map.put("contenu", annonce.getContenu());
                        map.put("datePublication", annonce.getDatePublication());
                        db.collection("annonces").add(map);
                    }
                });
    }

    public void ajouterDocumentSiAbsent(Documents doc) {
        db.collection("documents")
                .whereEqualTo("titre", doc.getTitre())
                .whereEqualTo("coursId", doc.getCoursId())
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("titre", doc.getTitre());
                        map.put("description", doc.getDescription());
                        map.put("format", doc.getFormat());
                        map.put("type", doc.getType());
                        map.put("url", doc.getUrl());
                        map.put("coursId", doc.getCoursId());
                        map.put("auteurId", doc.getAuteurId());
                        map.put("dateAjout", doc.getDateAjout());

                        db.collection("documents").add(map);
                    }
                });
    }

    public void ajouterUtilisateurAdminSiAbsent(Utilisateur utilisateur) {
        db.collection("utilisateurs")
                .whereEqualTo("email", utilisateur.getEmail())
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        auth.createUserWithEmailAndPassword(utilisateur.getEmail(), utilisateur.getMotDePasse())
                                .addOnSuccessListener(authResult -> {
                                    FirebaseUser firebaseUser = authResult.getUser();
                                    if (firebaseUser != null) {
                                        String uid = firebaseUser.getUid();

                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("nom", utilisateur.getNom());
                                        userMap.put("prenom", utilisateur.getPrenom());
                                        userMap.put("email", utilisateur.getEmail());
                                        userMap.put("type", "ADMIN");

                                        db.collection("utilisateurs").document(uid)
                                                .set(userMap)
                                                .addOnSuccessListener(x -> Log.d("FirebaseUploader", "Admin ajouté avec succès"))
                                                .addOnFailureListener(e -> Log.e("FirebaseUploader", "Erreur ajout admin", e));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("FirebaseUploader", "Erreur création compte admin", e));
                    } else {
                        Log.d("FirebaseUploader", "Admin déjà existant : " + utilisateur.getEmail());
                    }
                });
    }

}