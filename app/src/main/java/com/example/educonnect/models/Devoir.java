package com.example.educonnect.models;

import java.util.Date;

public class Devoir {
    private String id; // ID Firestore généré automatiquement
    private String enseignantId;
    private String titre;
    private String description;
    private String coursId;
    private Date dateCreation;
    private Date dateEcheance;

    public Devoir() {
        // Nécessaire pour Firestore
    }

    public Devoir(String titre, String description, String coursId, Date dateCreation, Date dateEcheance, String enseignantId) {
        this.titre = titre;
        this.description = description;
        this.coursId = coursId;
        this.dateCreation = dateCreation;
        this.dateEcheance = dateEcheance;
        this.enseignantId = enseignantId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoursId() { return coursId; }
    public void setCoursId(String coursId) { this.coursId = coursId; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Date getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(Date dateEcheance) { this.dateEcheance = dateEcheance; }

    public String getEnseignantId() { return enseignantId; }
    public void setEnseignantId(String enseignantId) { this.enseignantId = enseignantId; }
}
