package com.example.educonnect.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.educonnect.R;
import com.example.educonnect.models.Etudiant;
import com.example.educonnect.models.Enseignant;
import com.example.educonnect.models.Utilisateur;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class InterfaceGestionUtilisateurFragment extends Fragment {

    private EditText editNom, editPrenom, editEmail, editMotDePasse;
    private Spinner spinnerType;
    private LinearLayout layoutEtudiant, layoutEnseignant;
    private EditText editFiliere, editNiveau;
    private EditText editDepartement, editSpecialite;
    private Button buttonEnregistrer;

    private Utilisateur utilisateurExistant = null;

    public static InterfaceGestionUtilisateurFragment newInstance(Utilisateur utilisateur) {
        InterfaceGestionUtilisateurFragment fragment = new InterfaceGestionUtilisateurFragment();
        Bundle args = new Bundle();
        args.putSerializable("utilisateur", utilisateur);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        editNom = view.findViewById(R.id.editTextNom);
        editPrenom = view.findViewById(R.id.editTextPrenom);
        editEmail = view.findViewById(R.id.editTextEmail);
        editMotDePasse = view.findViewById(R.id.editTextMotDePasse);
        spinnerType = view.findViewById(R.id.spinnerType);
        layoutEtudiant = view.findViewById(R.id.layoutEtudiant);
        layoutEnseignant = view.findViewById(R.id.layoutEnseignant);
        editFiliere = view.findViewById(R.id.editTextFiliere);
        editNiveau = view.findViewById(R.id.editTextNiveau);
        editDepartement = view.findViewById(R.id.editTextDepartement);
        editSpecialite = view.findViewById(R.id.editTextSpecialite);
        buttonEnregistrer = view.findViewById(R.id.buttonEnregistrer);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.type_utilisateur, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                adapterChampsSelonType(parent.getItemAtPosition(pos).toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (getArguments() != null && getArguments().getSerializable("utilisateur") != null) {
            utilisateurExistant = (Utilisateur) getArguments().getSerializable("utilisateur");
            remplirFormulaire(utilisateurExistant);
        }

        buttonEnregistrer.setOnClickListener(v -> enregistrerUtilisateur());

        return view;
    }

    private void adapterChampsSelonType(String type) {
        if ("ETUDIANT".equalsIgnoreCase(type)) {
            layoutEtudiant.setVisibility(View.VISIBLE);
            layoutEnseignant.setVisibility(View.GONE);
        } else if ("ENSEIGNANT".equalsIgnoreCase(type)) {
            layoutEtudiant.setVisibility(View.GONE);
            layoutEnseignant.setVisibility(View.VISIBLE);
        } else {
            layoutEtudiant.setVisibility(View.GONE);
            layoutEnseignant.setVisibility(View.GONE);
        }
    }

    private void remplirFormulaire(Utilisateur utilisateur) {
        editNom.setText(utilisateur.getNom());
        editPrenom.setText(utilisateur.getPrenom());
        editEmail.setText(utilisateur.getEmail());
        editMotDePasse.setText(utilisateur.getMotDePasse());
        if (utilisateur.getType() != null) {
            int position = utilisateur.getType().equals("ETUDIANT") ? 0 : 1;
            spinnerType.setSelection(position);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if ("ETUDIANT".equals(utilisateur.getType())) {
                db.collection("etudiants").document(utilisateur.getId()).get().addOnSuccessListener(doc -> {
                    editFiliere.setText(doc.getString("filiere"));
                    editNiveau.setText(doc.getString("niveau"));
                });
            } else if ("ENSEIGNANT".equals(utilisateur.getType())) {
                db.collection("enseignants").document(utilisateur.getId()).get().addOnSuccessListener(doc -> {
                    editDepartement.setText(doc.getString("departement"));
                    editSpecialite.setText(doc.getString("specialite"));
                });
            }
        }
    }

    private void enregistrerUtilisateur() {
        String nom = editNom.getText().toString().trim();
        String prenom = editPrenom.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String motDePasse = editMotDePasse.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();

        if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(prenom) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(motDePasse)) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (utilisateurExistant == null) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, motDePasse)
                    .addOnSuccessListener(authResult -> {
                        String uid = authResult.getUser().getUid();

                        enregistrerDonneesFirestore(uid, nom, prenom, email, motDePasse, type, db, () -> {
                            // 🔁 Reconnexion à l'admin APRES enregistrement
                            SharedPreferences prefs = requireContext().getSharedPreferences("educonnect", getContext().MODE_PRIVATE);
                            String adminEmail = prefs.getString("admin_email", null);
                            String adminPassword = prefs.getString("admin_password", null);

                            FirebaseAuth.getInstance().signOut();
                            if (adminEmail != null && adminPassword != null) {
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(adminEmail, adminPassword)
                                        .addOnSuccessListener(a -> {
                                            Toast.makeText(getContext(), "Utilisateur créé. Reconnecté en tant qu’admin.", Toast.LENGTH_SHORT).show();
                                            revenirAUserList();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Créé, mais reconnexion admin échouée", Toast.LENGTH_SHORT).show());
                            } else {
                                Toast.makeText(getContext(), "Utilisateur créé, reconnectez l’admin manuellement.", Toast.LENGTH_SHORT).show();
                                revenirAUserList();
                            }
                        });

                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Erreur Auth: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            String uid = utilisateurExistant.getId();
            enregistrerDonneesFirestore(uid, nom, prenom, email, motDePasse, type, db, this::revenirAUserList);
        }
    }

    private void enregistrerDonneesFirestore(String uid, String nom, String prenom, String email, String motDePasse, String type, FirebaseFirestore db, Runnable onComplete) {
        Utilisateur utilisateur = new Utilisateur(nom, prenom, email, motDePasse, type);
        utilisateur.setId(uid);

        db.collection("utilisateurs").document(uid).set(utilisateur)
                .addOnSuccessListener(unused -> {
                    if ("ETUDIANT".equalsIgnoreCase(type)) {
                        Etudiant etu = new Etudiant(uid,
                                editFiliere.getText().toString().trim(),
                                editNiveau.getText().toString().trim());
                        db.collection("etudiants").document(uid).set(etu);
                    } else if ("ENSEIGNANT".equalsIgnoreCase(type)) {
                        Enseignant ens = new Enseignant(uid,
                                editDepartement.getText().toString().trim(),
                                editSpecialite.getText().toString().trim());
                        db.collection("enseignants").document(uid).set(ens);
                    }

                    Toast.makeText(getContext(), "Utilisateur enregistré avec succès", Toast.LENGTH_SHORT).show();

                    if (onComplete != null) onComplete.run();

                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Erreur Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void revenirAUserList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AdminFragmentGestionUtilisateurs()) // ← ton fragment listant les users
                .commit();
    }

}