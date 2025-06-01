package com.example.educonnect.utils;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class CalendrierSync {

    public static long getDefaultCalendarId(Context context) {
        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                new String[]{CalendarContract.Calendars._ID},
                CalendarContract.Calendars.VISIBLE + " = 1 AND " +
                        CalendarContract.Calendars.IS_PRIMARY + " = 1",
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }

        // Fallback : premier calendrier visible
        cursor = context.getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                new String[]{CalendarContract.Calendars._ID},
                CalendarContract.Calendars.VISIBLE + " = 1",
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }

        return -1;
    }

    public static Map<String, Long> getCalendriersDisponibles(Context context) {
        Map<String, Long> calendriers = new LinkedHashMap<>();

        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME},
                CalendarContract.Calendars.VISIBLE + " = 1",
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                calendriers.put(name, id);
            }
            cursor.close();
        }

        return calendriers;
    }

    private static boolean evenementExiste(Context context, String title, long startMillis, String location) {
        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                new String[]{CalendarContract.Events._ID},
                CalendarContract.Events.TITLE + " = ? AND " +
                        CalendarContract.Events.DTSTART + " = ? AND " +
                        CalendarContract.Events.EVENT_LOCATION + " = ?",
                new String[]{title, String.valueOf(startMillis), location},
                null
        );

        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        return exists;
    }

    public static void exporterSeancesPourUtilisateur(Context context, boolean estProf, String niveau, String filiere) {
        long calendarId = getDefaultCalendarId(context);
        if (calendarId == -1) {
            Toast.makeText(context, "Aucun calendrier disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        exporterSeancesPourUtilisateurAvecId(context, estProf, niveau, filiere, calendarId);
    }

    public static void exporterSeancesPourUtilisateurAvecId(Context context, boolean estProf, String niveau, String filiere, long calendarId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = estProf ?
                db.collection("seances").whereEqualTo("enseignantId", userId) :
                db.collection("seances").whereEqualTo("niveau", niveau).whereEqualTo("filiere", filiere);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Permission WRITE_CALENDAR refusée", Toast.LENGTH_LONG).show();
                        return;
                    }

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Map<String, Object> seance = doc.getData();
                        if (seance == null) continue;

                        String titre = (String) seance.get("nom");
                        String location = (String) seance.get("salle");
                        String code = (String) seance.get("code");
                        String niveauSeance = (String) seance.get("niveau");
                        String filiereSeance = (String) seance.get("filiere");

                        Timestamp tsDebut = (Timestamp) seance.get("dateDebut");
                        Timestamp tsFin = (Timestamp) seance.get("dateFin");

                        long startMillis = tsDebut.toDate().getTime();
                        long endMillis = tsFin.toDate().getTime();

                        if (evenementExiste(context, titre, startMillis, location)) continue;

                        ContentValues event = new ContentValues();
                        event.put(CalendarContract.Events.CALENDAR_ID, calendarId);
                        event.put(CalendarContract.Events.TITLE, code + " - " + titre + "(" + niveauSeance +")" );
                        event.put(CalendarContract.Events.DESCRIPTION, "Code : " + code + ", Niveau : " + niveauSeance + ", Filière : " + filiereSeance + " #EduConnect");
                        event.put(CalendarContract.Events.EVENT_LOCATION, location);
                        event.put(CalendarContract.Events.DTSTART, startMillis);
                        event.put(CalendarContract.Events.DTEND, endMillis);
                        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

                        Uri uri = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);
                        if (uri != null) {
                            Log.d("CalendrierSync", "Événement inséré : " + uri.toString());
                        }
                    }

                    Toast.makeText(context, "Séances synchronisées avec le calendrier", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Erreur de récupération des séances : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static void supprimerEvenementsEduConnect(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission WRITE_CALENDAR refusée", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                new String[]{CalendarContract.Events._ID},
                CalendarContract.Events.DESCRIPTION + " LIKE ?",
                new String[]{"%#EduConnect%"},
                null
        );

        if (cursor != null) {
            int count = 0;
            while (cursor.moveToNext()) {
                long eventId = cursor.getLong(0);
                context.getContentResolver().delete(
                        CalendarContract.Events.CONTENT_URI,
                        CalendarContract.Events._ID + " = ?",
                        new String[]{String.valueOf(eventId)}
                );
                count++;
            }
            cursor.close();
            Toast.makeText(context, count + " événement(s) supprimé(s)", Toast.LENGTH_SHORT).show();
        }
    }
}