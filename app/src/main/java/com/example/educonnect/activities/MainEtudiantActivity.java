package com.example.educonnect.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.educonnect.R;
import com.example.educonnect.fragments.DevoirsEtudiantFragment;
import com.example.educonnect.fragments.EtudiantPlanningFragment;
import com.example.educonnect.fragments.HomeEtudiantFragment;
import com.example.educonnect.fragments.MessagesFragment;
import com.example.educonnect.fragments.ProfilFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainEtudiantActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentById(item.getItemId());
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        Intent intent = getIntent();
        String target = intent != null ? intent.getStringExtra("navigateTo") : null;

        if ("messages".equals(target)) {
            bottomNav.setSelectedItemId(R.id.nav_messages);
        } else if ("annonces".equals(target)) {
            bottomNav.setSelectedItemId(R.id.nav_cours); // Accueil étudiant (HomeEtudiantFragment)
        } else {
            bottomNav.setSelectedItemId(R.id.nav_cours); // Par défaut accueil
        }

        listenForUnreadMessages();
        FirebaseMessaging.getInstance().subscribeToTopic("annonces");
        FirebaseMessaging.getInstance().subscribeToTopic("devoirs");
        FirebaseMessaging.getInstance().subscribeToTopic("notes");
        FirebaseMessaging.getInstance().subscribeToTopic("messages");

    }

    private Fragment getFragmentById(int itemId) {
        if (itemId == R.id.nav_cours) return new HomeEtudiantFragment();
        if (itemId == R.id.nav_devoirs) return new DevoirsEtudiantFragment();
        if (itemId == R.id.nav_messages) return new MessagesFragment();
        if (itemId == R.id.nav_planning) return new EtudiantPlanningFragment();
        if (itemId == R.id.nav_profil) return new ProfilFragment();
        return null;
    }

    private void listenForUnreadMessages() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("messages")
                .whereEqualTo("destinataireId", currentUserId)
                .whereEqualTo("lu", false)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null || querySnapshot == null) return;

                    int count = querySnapshot.size();
                    BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_messages);
                    if (count > 0) {
                        badge.setVisible(true);
                        badge.setNumber(count);
                    } else {
                        badge.setVisible(false);
                    }
                });
    }
}