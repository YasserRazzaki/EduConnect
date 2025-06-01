package com.example.educonnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.activities.ChatActivity;
import com.example.educonnect.activities.ListeUtilisateursActivity;
import com.example.educonnect.R;
import com.example.educonnect.adapters.ConversationAdapter;
import com.example.educonnect.models.Conversation;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerViewConversations;

    private BottomNavigationView bottomNavigationView;
    private ConversationAdapter conversationAdapter;
    private final List<Conversation> conversationList = new ArrayList<>();
    private ImageButton btnNouvelleConversation;
    private final Map<String, Conversation> conversationMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerViewConversations = view.findViewById(R.id.recyclerViewConversations);
        btnNouvelleConversation = view.findViewById(R.id.btnNouvelleConversation);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation); // ton ID réel ici
        recyclerViewConversations.setLayoutManager(new LinearLayoutManager(getContext()));
        conversationAdapter = new ConversationAdapter(conversationList, conversation -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("destinataireId", conversation.getIdDestinataire());
            startActivity(intent);
        });
        recyclerViewConversations.setAdapter(conversationAdapter);

        btnNouvelleConversation.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ListeUtilisateursActivity.class));
        });

        chargerConversations();

        return view;
    }

    private void chargerConversations() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("messages")
                .orderBy("dateEnvoi", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) return;
                    if (querySnapshot == null) return;

                    conversationMap.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String expediteurId = doc.getString("expediteurId");
                        String destinataireId = doc.getString("destinataireId");
                        String contenu = doc.getString("contenu");
                        Boolean lu = doc.getBoolean("lu");
                        Date dateEnvoi = doc.getDate("dateEnvoi");

                        if (currentUserId.equals(expediteurId) || currentUserId.equals(destinataireId)) {
                            String contactId = currentUserId.equals(expediteurId) ? destinataireId : expediteurId;

                            Conversation conv = conversationMap.get(contactId);
                            if (conv == null) {
                                conv = new Conversation();
                                conv.setIdDestinataire(contactId);
                                conv.setDernierMessage(contenu);
                                conv.setDateDernierMessage(dateEnvoi != null ? dateEnvoi : new Date());
                                conv.setExpediteurId(expediteurId);
                                conv.setNonLus(0); // init
                                conversationMap.put(contactId, conv);
                            }

                            // mettre à jour dernier message si plus récent
                            if (dateEnvoi != null && (conv.getDateDernierMessage() == null ||
                                    dateEnvoi.after(conv.getDateDernierMessage()))) {
                                conv.setDernierMessage(contenu);
                                conv.setDateDernierMessage(dateEnvoi);
                                conv.setExpediteurId(expediteurId);
                            }

                            // compter messages non lus si destinataire == moi
                            if (!lu && destinataireId.equals(currentUserId)) {
                                conv.setNonLus(conv.getNonLus() + 1);
                            }
                        }
                    }

                    List<String> contactIds = new ArrayList<>(conversationMap.keySet());

                    if (!contactIds.isEmpty()) {
                        FirebaseFirestore.getInstance().collection("utilisateurs")
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    conversationList.clear();
                                    for (DocumentSnapshot userDoc : snapshot.getDocuments()) {
                                        String id = userDoc.getId();
                                        if (contactIds.contains(id)) {
                                            String nom = userDoc.getString("nom");
                                            String prenom = userDoc.getString("prenom");

                                            Conversation conv = conversationMap.get(id);
                                            if (conv != null) {
                                                conv.setPrenomDestinataire(prenom != null ? prenom : "");
                                                conv.setNomDestinataire(nom != null ? nom : "");
                                                conversationList.add(conv);
                                            }
                                        }
                                    }
                                    conversationAdapter.notifyDataSetChanged();
                                    // 🔢 Calculer total des messages non lus
                                    int totalMessagesNonLus = 0;
                                    for (Conversation conv : conversationMap.values()) {
                                        totalMessagesNonLus += conv.getNonLus();
                                    }

                                    if (bottomNavigationView != null) {
                                        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_messages); // nav_messages = ID du menu
                                        if (totalMessagesNonLus > 0) {
                                            badge.setVisible(true);
                                            badge.setNumber(totalMessagesNonLus);
                                        } else {
                                            badge.setVisible(false);
                                        }
                                    }

                                });
                    }
                });
                    }
    }