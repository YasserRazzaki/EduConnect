package com.example.educonnect.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.adapters.MessageAdapter;
import com.example.educonnect.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editMessage;
    private ImageButton btnSend;

    private TextView textViewNomContact;

    private MessageAdapter messageAdapter;
    private final List<Message> messageList = new ArrayList<>();

    private String currentUserId;
    private String destinataireId;

    private FirebaseFirestore db;
    private CollectionReference messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        destinataireId = getIntent().getStringExtra("destinataireId");

        db = FirebaseFirestore.getInstance();
        messagesRef = db.collection("messages");

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        btnSend.setOnClickListener(v -> envoyerMessage());

        ecouterMessages();

        textViewNomContact = findViewById(R.id.textViewNomContact);

        FirebaseFirestore.getInstance().collection("utilisateurs")
                .document(destinataireId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nom = documentSnapshot.getString("nom");
                        String prenom = documentSnapshot.getString("prenom");
                        textViewNomContact.setText((prenom != null ? prenom : "") + " " + (nom != null ? nom : ""));
                    }
                });

    }

    private void envoyerMessage() {
        String contenu = editMessage.getText().toString().trim();
        if (TextUtils.isEmpty(contenu)) return;

        Message message = new Message(
                contenu,
                currentUserId,
                destinataireId,
                new Date(),
                false
        );

        messagesRef.add(message)
                .addOnSuccessListener(ref -> {
                    editMessage.setText("");
                   // envoyerNotificationMessageViaBackend(destinataireId, contenu);
                });
    }

    private void ecouterMessages() {
        messagesRef
                .orderBy("dateEnvoi", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    messageList.clear();
                    final List<DocumentSnapshot> unreadSnapshots = new ArrayList<>();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Message msg = doc.toObject(Message.class);

                        if ((msg.getExpediteurId().equals(currentUserId) && msg.getDestinataireId().equals(destinataireId)) ||
                                (msg.getExpediteurId().equals(destinataireId) && msg.getDestinataireId().equals(currentUserId))) {

                            messageList.add(msg);

                            // Ne pas marquer comme lu tout de suite, mais stocker ceux à lire
                            if (msg.getDestinataireId().equals(currentUserId) && !msg.isLu()) {
                                unreadSnapshots.add(doc);
                            }
                        }
                    }

                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);

                    // ✅ Marquer comme lu après un léger délai (simule lecture réelle)
                    recyclerView.postDelayed(() -> {
                        for (DocumentSnapshot doc : unreadSnapshots) {
                            messagesRef.document(doc.getId()).update("lu", true);
                        }
                    }, 1000); // attend 1 seconde par exemple
                });
    }

    /* private void envoyerNotificationMessageViaBackend(String destinataireId, String contenu) {
        FirebaseFirestore.getInstance().collection("utilisateurs")
                .document(destinataireId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String token = doc.getString("fcmToken");
                        if (token != null && !token.isEmpty()) {
                            // Appelle le backend avec ce token
                            new Thread(() -> {
                                try {
                                    String urlBackend = getString(R.string.backend_url_msgs);
                                    URL url = new URL(urlBackend);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestMethod("POST");
                                    conn.setRequestProperty("Content-Type", "application/json");
                                    conn.setDoOutput(true);

                                    JSONObject json = new JSONObject();
                                    json.put("title", "Nouveau message");
                                    json.put("body", contenu);
                                    json.put("token", token);
                                    json.put("senderId", currentUserId);// ✅ le vrai token FCM ici

                                    OutputStream os = conn.getOutputStream();
                                    os.write(json.toString().getBytes("UTF-8"));
                                    os.flush();
                                    os.close();

                                    int responseCode = conn.getResponseCode();
                                    System.out.println("Backend Message Réponse code : " + responseCode);

                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        System.out.println("Notification envoyée avec succès !");
                                    } else {
                                        System.out.println("Erreur d'envoi au backend : code " + responseCode);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                    }
                });
    }*/

    }