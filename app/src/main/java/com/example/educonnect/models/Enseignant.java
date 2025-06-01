package com.example.educonnect.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Utilisateur.class,
        parentColumns = "id",
        childColumns = "id",
        onDelete = ForeignKey.CASCADE
), indices = {@Index("id")})
public class Enseignant {
    @PrimaryKey
    private String id;
    private String departement;
    private String specialite;

    public Enseignant(String id, String departement, String specialite) {
        this.id = id;
        this.departement = departement;
        this.specialite = specialite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
}