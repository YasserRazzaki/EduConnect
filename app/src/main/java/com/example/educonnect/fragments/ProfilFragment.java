package com.example.educonnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.educonnect.activities.LoginActivity;
import com.example.educonnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilFragment extends Fragment {

    private FirebaseAuth auth;
    private TextView nomUtilisateur, emailUtilisateur;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        auth = FirebaseAuth.getInstance();

        nomUtilisateur = view.findViewById(R.id.nomUtilisateur);
        emailUtilisateur = view.findViewById(R.id.emailUtilisateur);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            emailUtilisateur.setText(user.getEmail());
            FirebaseFirestore.getInstance().collection("utilisateurs")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String nom = snapshot.getString("nom");
                            String prenom = snapshot.getString("prenom");
                            nomUtilisateur.setText(prenom + " " + nom);
                        }
                    });
        }

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}