package com.example.educonnect.models;

import java.util.Date;

public class Conversation {
    private String idDestinataire;
    private String nomDestinataire;
    private String prenomDestinataire;
    private String dernierMessage;
    private boolean nonLu;
    private String expediteurId;
    private Date dateDernierMessage;

    private int nonLus;

    public Conversation() {}

    public Conversation(String idDestinataire, String nomDestinataire, String dernierMessage, boolean nonLu) {
        this.idDestinataire = idDestinataire;
        this.nomDestinataire = nomDestinataire;
        this.dernierMessage = dernierMessage;
        this.nonLu = nonLu;
    }

    public String getIdDestinataire() {
        return idDestinataire;
    }

    public void setIdDestinataire(String idDestinataire) {
        this.idDestinataire = idDestinataire;
    }

    public String getNomDestinataire() {
        return nomDestinataire;
    }

    public void setNomDestinataire(String nomDestinataire) {
        this.nomDestinataire = nomDestinataire;
    }

    public String getPrenomDestinataire() {
        return prenomDestinataire;
    }

    public void setPrenomDestinataire(String prenomDestinataire) {
        this.prenomDestinataire = prenomDestinataire;
    }

    public String getDernierMessage() {
        return dernierMessage;
    }

    public void setDernierMessage(String dernierMessage) {
        this.dernierMessage = dernierMessage;
    }

    public boolean isNonLu() {
        return nonLu;
    }

    public void setNonLu(boolean nonLu) {
        this.nonLu = nonLu;
    }

    public int getNonLus() {
        return nonLus;
    }

    public void setNonLus(int nonLus) {
        this.nonLus = nonLus;
    }

    public String getExpediteurId() {
        return expediteurId;
    }

    public void setExpediteurId(String expediteurId) {
        this.expediteurId = expediteurId;
    }

    public Date getDateDernierMessage() {
        return dateDernierMessage;
    }

    public void setDateDernierMessage(Date dateDernierMessage) {
        this.dateDernierMessage = dateDernierMessage;
    }
}