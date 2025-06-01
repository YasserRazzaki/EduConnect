package com.example.educonnect.models;

import java.util.Date;

public class Message {
    private String id; // facultatif, utilisé uniquement côté app
    private String contenu;
    private String expediteurId;
    private String destinataireId;
    private Date dateEnvoi;
    private boolean lu;

    public Message() {
        // Obligatoire pour Firestore (constructeur vide)
    }

    public Message(String contenu, String expediteurId, String destinataireId, Date dateEnvoi, boolean lu) {
        this.contenu = contenu;
        this.expediteurId = expediteurId;
        this.destinataireId = destinataireId;
        this.dateEnvoi = dateEnvoi;
        this.lu = lu;
    }

    // GETTERS / SETTERS
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getExpediteurId() { return expediteurId; }
    public void setExpediteurId(String expediteurId) { this.expediteurId = expediteurId; }

    public String getDestinataireId() { return destinataireId; }
    public void setDestinataireId(String destinataireId) { this.destinataireId = destinataireId; }

    public Date getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(Date dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }
}