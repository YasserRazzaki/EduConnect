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
public class Etudiant {
    @PrimaryKey
    private String id;
    private String filiere;
    private String niveau;

    public Etudiant(String id, String filiere, String niveau) {
        this.id = id;
        this.filiere = filiere;
        this.niveau = niveau;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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