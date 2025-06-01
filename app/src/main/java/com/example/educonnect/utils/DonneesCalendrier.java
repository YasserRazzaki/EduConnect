package com.example.educonnect.utils;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class DonneesCalendrier {

    public static void ajouterSeancesTest(String enseignantId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final int DUREE_SEANCE_MINUTES = 90;
        final int PAUSE_MINUTES = 15;

        // List of colors to use, ensuring uniqueness for different courses
        // Reusing colors for the same course code across different dates.
        final String COULEUR_HAB13 = "#FF5252"; // Habitat
        final String COULEUR_HAB10 = "#4CAF50"; // Matériaux
        final String COULEUR_HAB08 = "#FFC107"; // Conception
        final String COULEUR_HAB21 = "#03A9F4"; // Urbanisme
        final String COULEUR_HAB02 = "#9C27B0"; // Dessin
        final String COULEUR_HAB01 = "#673AB7"; // Histoire
        final String COULEUR_HAB18 = "#E91E63"; // Projet

        List<Map<String, Object>> allSeances = new ArrayList<>();

        // --- WEEK 1: Starting Monday, May 12th, 2025 ---
        Calendar calWeek1 = Calendar.getInstance();
        calWeek1.set(2025, Calendar.MAY, 12); // May is 4 (0-indexed)
        calWeek1.set(Calendar.HOUR_OF_DAY, 0); calWeek1.set(Calendar.MINUTE, 0); calWeek1.set(Calendar.SECOND, 0); calWeek1.set(Calendar.MILLISECOND, 0);

        // LUNDI (May 12, 2025)
        Calendar mondayCalW1 = (Calendar) calWeek1.clone();
        setHeureFixe(mondayCalW1, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW1, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW1, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW1, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (May 13, 2025)
        Calendar tuesdayCalW1 = (Calendar) calWeek1.clone();
        tuesdayCalW1.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW1, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW1, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (May 14, 2025)
        Calendar wednesdayCalW1 = (Calendar) calWeek1.clone();
        wednesdayCalW1.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW1, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW1, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW1, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW1, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (May 15, 2025)
        Calendar thursdayCalW1 = (Calendar) calWeek1.clone();
        thursdayCalW1.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW1, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW1, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW1, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW1, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (May 16, 2025)
        Calendar fridayCalW1 = (Calendar) calWeek1.clone();
        fridayCalW1.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW1, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW1, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));

        // --- WEEK 2: Starting Monday, May 19th, 2025 ---
        Calendar calWeek2 = (Calendar) calWeek1.clone();
        calWeek2.add(Calendar.WEEK_OF_YEAR, 1);

        // LUNDI (May 19, 2025)
        Calendar mondayCalW2 = (Calendar) calWeek2.clone();
        setHeureFixe(mondayCalW2, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW2, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW2, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW2, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (May 20, 2025)
        Calendar tuesdayCalW2 = (Calendar) calWeek2.clone();
        tuesdayCalW2.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW2, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW2, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (May 21, 2025)
        Calendar wednesdayCalW2 = (Calendar) calWeek2.clone();
        wednesdayCalW2.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW2, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW2, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW2, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW2, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (May 22, 2025)
        Calendar thursdayCalW2 = (Calendar) calWeek2.clone();
        thursdayCalW2.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW2, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW2, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW2, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW2, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (May 23, 2025)
        Calendar fridayCalW2 = (Calendar) calWeek2.clone();
        fridayCalW2.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW2, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW2, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));

        // --- WEEK 3: Starting Monday, May 26th, 2025 ---
        Calendar calWeek3 = (Calendar) calWeek1.clone();
        calWeek3.add(Calendar.WEEK_OF_YEAR, 2);

        // LUNDI (May 26, 2025)
        Calendar mondayCalW3 = (Calendar) calWeek3.clone();
        setHeureFixe(mondayCalW3, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW3, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW3, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW3, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (May 27, 2025)
        Calendar tuesdayCalW3 = (Calendar) calWeek3.clone();
        tuesdayCalW3.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW3, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW3, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (May 28, 2025)
        Calendar wednesdayCalW3 = (Calendar) calWeek3.clone();
        wednesdayCalW3.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW3, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW3, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW3, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW3, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (May 29, 2025)
        Calendar thursdayCalW3 = (Calendar) calWeek3.clone();
        thursdayCalW3.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW3, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW3, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW3, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW3, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (May 30, 2025)
        Calendar fridayCalW3 = (Calendar) calWeek3.clone();
        fridayCalW3.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW3, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW3, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));

        // --- WEEK 4: Starting Monday, June 2nd, 2025 ---
        Calendar calWeek4 = (Calendar) calWeek1.clone();
        calWeek4.add(Calendar.WEEK_OF_YEAR, 3);

        // LUNDI (June 2, 2025)
        Calendar mondayCalW4 = (Calendar) calWeek4.clone();
        setHeureFixe(mondayCalW4, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW4, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW4, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW4, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (June 3, 2025)
        Calendar tuesdayCalW4 = (Calendar) calWeek4.clone();
        tuesdayCalW4.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW4, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW4, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (June 4, 2025)
        Calendar wednesdayCalW4 = (Calendar) calWeek4.clone();
        wednesdayCalW4.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW4, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW4, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW4, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW4, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (June 5, 2025)
        Calendar thursdayCalW4 = (Calendar) calWeek4.clone();
        thursdayCalW4.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW4, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW4, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW4, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW4, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (June 6, 2025)
        Calendar fridayCalW4 = (Calendar) calWeek4.clone();
        fridayCalW4.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW4, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW4, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));

        // --- WEEK 5: Starting Monday, June 9th, 2025 ---
        Calendar calWeek5 = (Calendar) calWeek1.clone();
        calWeek5.add(Calendar.WEEK_OF_YEAR, 4);

        // LUNDI (June 9, 2025)
        Calendar mondayCalW5 = (Calendar) calWeek5.clone();
        setHeureFixe(mondayCalW5, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW5, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW5, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW5, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (June 10, 2025)
        Calendar tuesdayCalW5 = (Calendar) calWeek5.clone();
        tuesdayCalW5.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW5, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW5, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (June 11, 2025)
        Calendar wednesdayCalW5 = (Calendar) calWeek5.clone();
        wednesdayCalW5.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW5, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW5, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW5, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW5, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (June 12, 2025)
        Calendar thursdayCalW5 = (Calendar) calWeek5.clone();
        thursdayCalW5.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW5, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW5, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW5, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW5, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (June 13, 2025)
        Calendar fridayCalW5 = (Calendar) calWeek5.clone();
        fridayCalW5.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW5, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW5, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));

        // --- WEEK 6: Starting Monday, June 16th, 2025 ---
        Calendar calWeek6 = (Calendar) calWeek1.clone();
        calWeek6.add(Calendar.WEEK_OF_YEAR, 5);

        // LUNDI (June 16, 2025)
        Calendar mondayCalW6 = (Calendar) calWeek6.clone();
        setHeureFixe(mondayCalW6, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW6, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW6, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW6, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (June 17, 2025)
        Calendar tuesdayCalW6 = (Calendar) calWeek6.clone();
        tuesdayCalW6.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW6, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW6, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (June 18, 2025)
        Calendar wednesdayCalW6 = (Calendar) calWeek6.clone();
        wednesdayCalW6.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW6, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW6, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW6, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW6, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (June 19, 2025)
        Calendar thursdayCalW6 = (Calendar) calWeek6.clone();
        thursdayCalW6.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW6, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW6, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW6, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW6, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (June 20, 2025)
        Calendar fridayCalW6 = (Calendar) calWeek6.clone();
        fridayCalW6.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW6, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW6, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));

        // --- WEEK 7: Starting Monday, June 23rd, 2025 ---
        Calendar calWeek7 = (Calendar) calWeek1.clone();
        calWeek7.add(Calendar.WEEK_OF_YEAR, 6);

        // LUNDI (June 23, 2025)
        Calendar mondayCalW7 = (Calendar) calWeek7.clone();
        setHeureFixe(mondayCalW7, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW7, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW7, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW7, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (June 24, 2025)
        Calendar tuesdayCalW7 = (Calendar) calWeek7.clone();
        tuesdayCalW7.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW7, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW7, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (June 25, 2025)
        Calendar wednesdayCalW7 = (Calendar) calWeek7.clone();
        wednesdayCalW7.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW7, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW7, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW7, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW7, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (June 26, 2025)
        Calendar thursdayCalW7 = (Calendar) calWeek7.clone();
        thursdayCalW7.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW7, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW7, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW7, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW7, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (June 27, 2025)
        Calendar fridayCalW7 = (Calendar) calWeek7.clone();
        fridayCalW7.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW7, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW7, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));

        // --- WEEK 8: Starting Monday, June 30th, 2025 ---
        Calendar calWeek8 = (Calendar) calWeek1.clone();
        calWeek8.add(Calendar.WEEK_OF_YEAR, 7);

        // LUNDI (June 30, 2025)
        Calendar mondayCalW8 = (Calendar) calWeek8.clone();
        setHeureFixe(mondayCalW8, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW8, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW8, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW8, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (July 1, 2025)
        Calendar tuesdayCalW8 = (Calendar) calWeek8.clone();
        tuesdayCalW8.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW8, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW8, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (July 2, 2025)
        Calendar wednesdayCalW8 = (Calendar) calWeek8.clone();
        wednesdayCalW8.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW8, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW8, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW8, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW8, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (July 3, 2025)
        Calendar thursdayCalW8 = (Calendar) calWeek8.clone();
        thursdayCalW8.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW8, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW8, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW8, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW8, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (July 4, 2025)
        Calendar fridayCalW8 = (Calendar) calWeek8.clone();
        fridayCalW8.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW8, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW8, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));

        // --- WEEK 9: Starting Monday, July 7th, 2025 ---
        Calendar calWeek9 = (Calendar) calWeek1.clone();
        calWeek9.add(Calendar.WEEK_OF_YEAR, 8);

        // LUNDI (July 7, 2025)
        Calendar mondayCalW9 = (Calendar) calWeek9.clone();
        setHeureFixe(mondayCalW9, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW9, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW9, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW9, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (July 8, 2025)
        Calendar tuesdayCalW9 = (Calendar) calWeek9.clone();
        tuesdayCalW9.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW9, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW9, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (July 9, 2025)
        Calendar wednesdayCalW9 = (Calendar) calWeek9.clone();
        wednesdayCalW9.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW9, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW9, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW9, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW9, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (July 10, 2025)
        Calendar thursdayCalW9 = (Calendar) calWeek9.clone();
        thursdayCalW9.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW9, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW9, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW9, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW9, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (July 11, 2025)
        Calendar fridayCalW9 = (Calendar) calWeek9.clone();
        fridayCalW9.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW9, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW9, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));

        // --- WEEK 10: Starting Monday, July 14th, 2025 ---
        Calendar calWeek10 = (Calendar) calWeek1.clone();
        calWeek10.add(Calendar.WEEK_OF_YEAR, 9);

        // LUNDI (July 14, 2025)
        Calendar mondayCalW10 = (Calendar) calWeek10.clone();
        setHeureFixe(mondayCalW10, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW10, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW10, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW10, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (July 15, 2025)
        Calendar tuesdayCalW10 = (Calendar) calWeek10.clone();
        tuesdayCalW10.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW10, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW10, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (July 16, 2025)
        Calendar wednesdayCalW10 = (Calendar) calWeek10.clone();
        wednesdayCalW10.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW10, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW10, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW10, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW10, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (July 17, 2025)
        Calendar thursdayCalW10 = (Calendar) calWeek10.clone();
        thursdayCalW10.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW10, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW10, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW10, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW10, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (July 18, 2025)
        Calendar fridayCalW10 = (Calendar) calWeek10.clone();
        fridayCalW10.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW10, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW10, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));

        // --- WEEK 11: Starting Monday, July 21st, 2025 ---
        Calendar calWeek11 = (Calendar) calWeek1.clone();
        calWeek11.add(Calendar.WEEK_OF_YEAR, 10);

        // LUNDI (July 21, 2025)
        Calendar mondayCalW11 = (Calendar) calWeek11.clone();
        setHeureFixe(mondayCalW11, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", mondayCalW11, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(mondayCalW11, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB10", "Matériaux", "A47", mondayCalW11, DUREE_SEANCE_MINUTES, COULEUR_HAB10, enseignantId, "M1", "Architecture"));

        // MARDI (July 22, 2025)
        Calendar tuesdayCalW11 = (Calendar) calWeek11.clone();
        tuesdayCalW11.add(Calendar.DAY_OF_MONTH, 1);
        setHeureFixe(tuesdayCalW11, 8, 0);
        allSeances.add(creerSeance("HAB08", "Conception", "C12", tuesdayCalW11, DUREE_SEANCE_MINUTES, COULEUR_HAB08, enseignantId, "L3", "Informatique"));

        // MERCREDI (July 23, 2025)
        Calendar wednesdayCalW11 = (Calendar) calWeek11.clone();
        wednesdayCalW11.add(Calendar.DAY_OF_MONTH, 2);
        setHeureFixe(wednesdayCalW11, 9, 0);
        allSeances.add(creerSeance("HAB21", "Urbanisme", "B08", wednesdayCalW11, DUREE_SEANCE_MINUTES, COULEUR_HAB21, enseignantId, "M2", "Architecture"));
        avancer(wednesdayCalW11, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB02", "Dessin", "A15", wednesdayCalW11, DUREE_SEANCE_MINUTES, COULEUR_HAB02, enseignantId, "L2", "Informatique"));

        // JEUDI (July 24, 2025)
        Calendar thursdayCalW11 = (Calendar) calWeek11.clone();
        thursdayCalW11.add(Calendar.DAY_OF_MONTH, 3);
        setHeureFixe(thursdayCalW11, 8, 0);
        allSeances.add(creerSeance("HAB13", "Habitat", "B23", thursdayCalW11, DUREE_SEANCE_MINUTES, COULEUR_HAB13, enseignantId, "L3", "Informatique"));
        avancer(thursdayCalW11, DUREE_SEANCE_MINUTES, PAUSE_MINUTES);
        allSeances.add(creerSeance("HAB01", "Histoire", "C05", thursdayCalW11, DUREE_SEANCE_MINUTES, COULEUR_HAB01, enseignantId, "M1", "Architecture"));

        // VENDREDI (July 25, 2025)
        Calendar fridayCalW11 = (Calendar) calWeek11.clone();
        fridayCalW11.add(Calendar.DAY_OF_MONTH, 4);
        setHeureFixe(fridayCalW11, 9, 0);
        allSeances.add(creerSeance("HAB18", "Projet", "A32", fridayCalW11, DUREE_SEANCE_MINUTES, COULEUR_HAB18, enseignantId, "M2", "Architecture"));


        // --- Add all sessions to Firestore ---
        for (Map<String, Object> seance : allSeances) {
            String code = (String) seance.get("code");
            Date dateDebut = (Date) seance.get("dateDebut");
            String enseignant = (String) seance.get("enseignantId");

            db.collection("seances")
                    .whereEqualTo("code", code)
                    .whereEqualTo("dateDebut", dateDebut)
                    .whereEqualTo("enseignantId", enseignant)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            db.collection("seances").add(seance);
                        } else {
                            System.out.println("⛔ Déjà existante : " + code + " | " + dateDebut);
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Erreur de vérification : " + e.getMessage());
                    });
        }
    }

    private static void avancer(Calendar cal, int dureeMinutes, int pauseMinutes) {
        cal.add(Calendar.MINUTE, dureeMinutes + pauseMinutes);
        // Reset seconds and milliseconds to zero after advancing time
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    private static void setHeureFixe(Calendar cal, int heure, int minute) {
        cal.set(Calendar.HOUR_OF_DAY, heure);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    private static Map<String, Object> creerSeance(String code, String nom, String salle, Calendar startCal,
                                                   int dureeMinutes, String couleur, String enseignantId,
                                                   String niveau, String filiere) {

        // Date normalization is already done via setHeureFixe
        Date dateDebut = startCal.getTime();

        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.MINUTE, dureeMinutes);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);
        Date dateFin = endCal.getTime();

        Map<String, Object> seance = new HashMap<>();
        seance.put("code", code);
        seance.put("nom", nom);
        seance.put("salle", salle);
        seance.put("dateDebut", dateDebut);
        seance.put("dateFin", dateFin);
        seance.put("couleur", couleur);
        seance.put("enseignantId", enseignantId);
        seance.put("niveau", niveau);
        seance.put("filiere", filiere);
        return seance;
    }
}