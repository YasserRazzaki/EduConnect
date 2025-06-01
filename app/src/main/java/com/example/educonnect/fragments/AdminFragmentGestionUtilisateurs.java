package com.example.educonnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.adapters.UserAdapter;
import com.example.educonnect.models.Utilisateur;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminFragmentGestionUtilisateurs extends Fragment {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<Utilisateur> userList = new ArrayList<>();
    private Button buttonAddUser;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference utilisateursRef = db.collection("utilisateurs");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
        buttonAddUser = view.findViewById(R.id.buttonAddUser);

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(Utilisateur user) {
                ouvrirFormulaire(user);
            }

            @Override
            public void onDelete(Utilisateur user) {
                supprimerUtilisateur(user);
            }
        });
        recyclerViewUsers.setAdapter(userAdapter);

        buttonAddUser.setOnClickListener(v -> ouvrirFormulaire(null));

        chargerUtilisateurs();

        return view;
    }

    private void chargerUtilisateurs() {
        utilisateursRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    userList.clear();
                    for (var doc : querySnapshot) {
                        Utilisateur user = doc.toObject(Utilisateur.class);
                        user.setId(doc.getId());
                        userList.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                });
    }

    private void supprimerUtilisateur(Utilisateur user) {
        DocumentReference userDoc = utilisateursRef.document(user.getId());
        userDoc.delete().addOnSuccessListener(unused -> {
            userList.remove(user);
            userAdapter.notifyDataSetChanged();
        });
    }

    private void ouvrirFormulaire(Utilisateur user) {
        Bundle args = new Bundle();
        if (user != null) {
            args.putString("userId", user.getId());
        }

        Fragment formulaireFragment = new InterfaceGestionUtilisateurFragment();
        formulaireFragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, formulaireFragment)
                .addToBackStack(null)
                .commit();
    }
}
