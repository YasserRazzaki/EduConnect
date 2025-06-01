package com.example.educonnect.models;

import java.util.Date;

public class Rendu {
    private String id;
    private String devoirId;
    private String etudiantId;

    private String nomFichier;

    private String commentaire;
    private String fichierUrl;
    private Date dateSoumission;
    private Integer note; // peut être null si non encore corrigé
    private String commentaireEnseignant;

    public Rendu() {
        // Nécessaire pour Firestore
    }

    public Rendu(String devoirId, String etudiantId, String commentaire, String fichierUrl, Date dateSoumission) {
        this.devoirId = devoirId;
        this.etudiantId = etudiantId;
        this.commentaire = commentaire;
        this.fichierUrl = fichierUrl;
        this.dateSoumission = dateSoumission;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDevoirId() { return devoirId; }
    public void setDevoirId(String devoirId) { this.devoirId = devoirId; }

    public String getEtudiantId() { return etudiantId; }
    public void setEtudiantId(String etudiantId) { this.etudiantId = etudiantId; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public String getFichierUrl() { return fichierUrl; }
    public void setFichierUrl(String fichierUrl) { this.fichierUrl = fichierUrl; }

    public Date getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(Date dateSoumission) { this.dateSoumission = dateSoumission; }

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }

    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }
    public String getCommentaireEnseignant() { return commentaireEnseignant; }
    public void setCommentaireEnseignant(String commentaireEnseignant) { this.commentaireEnseignant = commentaireEnseignant; }
}
