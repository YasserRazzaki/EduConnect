package com.example.educonnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.educonnect.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class FragmentAnnonceEnseignant extends Fragment {

    private EditText editTitre, editContenu;
    private Button btnPublier;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_annonce_prof, container, false);

        editTitre = view.findViewById(R.id.editTitreAnnonce);
        editContenu = view.findViewById(R.id.editContenuAnnonce);
        btnPublier = view.findViewById(R.id.btnPublierAnnonce);

        btnPublier.setOnClickListener(v -> publierAnnonce());

        return view;
    }

    private void publierAnnonce() {
        String titre = editTitre.getText().toString().trim();
        String contenu = editContenu.getText().toString().trim();

        if (titre.isEmpty() || contenu.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> annonceMap = new HashMap<>();
        annonceMap.put("titre", titre);
        annonceMap.put("contenu", contenu);
        annonceMap.put("datePublication", new Date());

        FirebaseFirestore.getInstance().collection("annonces")
                .add(annonceMap)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(getContext(), "Annonce publiée", Toast.LENGTH_SHORT).show();
                    // envoyerNotificationGlobale(titre, contenu);
                    editTitre.setText("");
                    editContenu.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

   /* private void envoyerNotificationGlobale(String titre, String message) {
        new Thread(() -> {
            try {
                String urlBackend = getString(R.string.backend_url_off);
                HttpURLConnection conn = (HttpURLConnection) new URL(urlBackend).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("title", titre);
                json.put("body", message);
                json.put("topic", "annonces");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes("UTF-8"));
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    System.out.println("✅ Notification envoyée !");
                } else {
                    System.out.println("❌ Erreur de notification : code " + conn.getResponseCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    } */
}