package com.example.educonnect.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.educonnect.R;
import com.example.educonnect.fragments.AdminFragmentGestionUtilisateurs;
import com.example.educonnect.fragments.AdminPlanningFragment;
import com.example.educonnect.fragments.AllCoursFragment;
import com.example.educonnect.fragments.MessagesFragment;
import com.example.educonnect.fragments.ProfilFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainAdminActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        bottomNav = findViewById(R.id.bottom_navigation_admin);

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
            bottomNav.setSelectedItemId(R.id.nav_messages_admin);
        } else if ("annonces".equals(target)) {
            bottomNav.setSelectedItemId(R.id.nav_cours_admin);
        } else {
            bottomNav.setSelectedItemId(R.id.nav_cours_admin);
        }

        listenForUnreadMessages();
    }

    private Fragment getFragmentById(int itemId) {
        if (itemId == R.id.nav_cours_admin) return new AllCoursFragment();
        if (itemId == R.id.nav_user_admin) return new AdminFragmentGestionUtilisateurs();
        if (itemId == R.id.nav_messages_admin) return new MessagesFragment();
        if (itemId == R.id.nav_planning_admin) return new AdminPlanningFragment();
        if (itemId == R.id.nav_profil_admin) return new ProfilFragment();
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