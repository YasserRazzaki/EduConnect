package com.example.educonnect.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Enseignant.class,
        parentColumns = "id",
        childColumns = "enseignantId",
        onDelete = ForeignKey.CASCADE
), indices = {@Index("enseignantId")})
public class Cours {
    private String id;
    private String code;
    private String titre;
    private String description;
    private String enseignantId;
    private int credits;
    private String filiere;
    private String niveau;

    public Cours() {}

    public Cours(String code, String titre, String description, String enseignantId, int credits, String filiere, String niveau) {
        this.code = code;
        this.titre = titre;
        this.description = description;
        this.enseignantId = enseignantId;
        this.credits = credits;
        this.filiere = filiere;
        this.niveau = niveau;
    }

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

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(String enseignantId) {
        this.enseignantId = enseignantId;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }
}