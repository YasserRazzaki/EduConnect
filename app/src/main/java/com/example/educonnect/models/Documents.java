package com.example.educonnect.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = {
        @ForeignKey(entity = Cours.class, parentColumns = "id", childColumns = "coursId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Utilisateur.class, parentColumns = "id", childColumns = "auteurId", onDelete = ForeignKey.CASCADE)
}, indices = {
        @Index("coursId"),
        @Index("auteurId")
})
public class Documents {
    @PrimaryKey(autoGenerate = true)
    private String id;
    private String titre;
    private String description;
    private String format;
    private String type;
    private String url;
    private String coursId;
    private String auteurId;
    private Date dateAjout;

    public Documents(String titre, String description, String type, String format, String url, String coursId, String auteurId,
                    Date dateAjout) {
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.format = format;
        this.url = url;
        this.coursId = coursId;
        this.auteurId = auteurId;
        this.dateAjout = dateAjout;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoursId() {
        return coursId;
    }

    public void setCoursId(String coursId) {
        this.coursId = coursId;
    }

    public String getAuteurId() {
        return auteurId;
    }

    public void setAuteurId(String auteurId) {
        this.auteurId = auteurId;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }
}