package com.example.educonnect.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class Seance {
    @DocumentId
    private String id;
    private String code;        // Ex: HAB13
    private String nom;         // Nom du cours
    private String salle;       // Salle de cours (optionnel)
    private Date dateDebut;     // Date et heure de début
    private Date dateFin;       // Date et heure de fin
    private String couleur;     // Code couleur pour l'affichage (format #RRGGBB)
    private String enseignantId; // ID de l'enseignant
    private String niveau;    // ID de la classe
    private String filiere;    // ID de la classe

    // Constructeur vide requis pour Firestore
    public Seance() {
    }

    public Seance(String code, String nom, String salle, Date dateDebut, Date dateFin,
                  String couleur, String enseignantId, String niveau, String filiere) {
        this.code = code;
        this.nom = nom;
        this.salle = salle;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.couleur = couleur;
        this.enseignantId = enseignantId;
        this.niveau = niveau;
        this.filiere = filiere;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public String getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(String enseignantId) {
        this.enseignantId = enseignantId;
    }

    // Méthodes utilitaires
    public int getDureeMinutes() {
        if (dateDebut == null || dateFin == null) return 0;
        return (int) ((dateFin.getTime() - dateDebut.getTime()) / (60 * 1000));
    }

    public String getHeureDebut() {
        return String.format("%02d:%02d", dateDebut.getHours(), dateDebut.getMinutes());
    }

    public String getHeureFin() {
        return String.format("%02d:%02d", dateFin.getHours(), dateFin.getMinutes());
    }
}