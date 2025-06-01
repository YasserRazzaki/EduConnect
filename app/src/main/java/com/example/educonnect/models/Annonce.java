package com.example.educonnect.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Cours.class,
                parentColumns = "id",
                childColumns = "coursId",
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Utilisateur.class,
                parentColumns = "id",
                childColumns = "auteurId",
                onDelete = ForeignKey.CASCADE
        )
}, indices = {
        @Index("coursId"),
        @Index("auteurId")
})
public class Annonce {

    private String id;
    private String titre;
    private String contenu;
    private Date datePublication;

    public Annonce(String titre, String contenu, Date datePublication) {
        this.titre = titre;
        this.contenu = contenu;
        this.datePublication = datePublication;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }
}