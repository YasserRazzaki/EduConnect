package com.example.educonnect.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.educonnect.utils.DataInserter;
import com.example.educonnect.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private MaterialButton buttonLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DataInserter.forcerSeancesSeulement();

        SharedPreferences prefs = getSharedPreferences("educonnect", MODE_PRIVATE);
        boolean dejaInsere = prefs.getBoolean("donnees_inserees", false);
        prefs.edit().clear().apply();
        if (!dejaInsere) {
            DataInserter.insererDonneesTest();
            prefs.edit().putBoolean("donnees_inserees", true).apply();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        String navigateTo = getIntent().getStringExtra("navigateTo");

        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                db.collection("utilisateurs").document(uid)
                                        .get()
                                        .addOnSuccessListener(document -> {
                                            if (document.exists()) {
                                                String type = document.getString("type");

                                                // 🔥 Abonnement au topic personnel
                                                FirebaseMessaging.getInstance().subscribeToTopic(uid)
                                                        .addOnCompleteListener(t1 -> Log.d("Login", "Abonné au topic : " + uid));

                                                // ✅ Enregistrement token FCM
                                                FirebaseMessaging.getInstance().getToken()
                                                        .addOnSuccessListener(token -> {
                                                            db.collection("utilisateurs").document(uid)
                                                                    .update("fcmToken", token)
                                                                    .addOnSuccessListener(unused -> Log.d("Login", "Token enregistré"))
                                                                    .addOnFailureListener(e -> Log.e("Login", "Erreur enregistrement token", e));
                                                        });

                                                Intent intent;

                                                if ("ETUDIANT".equals(type)) {
                                                    prefs.edit().putString("type_utilisateur", "ETUDIANT").apply();
                                                    FirebaseMessaging.getInstance().subscribeToTopic("annonces");

                                                    intent = new Intent(this, MainEtudiantActivity.class);
                                                } else if ("ENSEIGNANT".equals(type)) {
                                                    prefs.edit().putString("type_utilisateur", "ENSEIGNANT").apply();
                                                    intent = new Intent(this, MainEnseignantActivity.class);
                                                } else if ("ADMIN".equals(type)) {
                                                    prefs.edit()
                                                            .putString("type_utilisateur", "ADMIN")
                                                            .putString("admin_email", email)
                                                            .putString("admin_password", password)
                                                            .apply();
                                                    intent = new Intent(this, MainAdminActivity.class); }
                                                else {
                                                    Toast.makeText(this, "Type d'utilisateur inconnu", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                // 🔄 On passe navigateTo si présent
                                                if (navigateTo != null) {
                                                    intent.putExtra("navigateTo", navigateTo);
                                                }

                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(this, "Utilisateur introuvable", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Échec de connexion", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
