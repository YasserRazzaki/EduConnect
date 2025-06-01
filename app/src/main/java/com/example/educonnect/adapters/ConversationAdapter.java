package com.example.educonnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.models.Conversation;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClicked(Conversation conversation);
    }

    private final List<Conversation> conversations;
    private final OnConversationClickListener listener;
    private final String currentUserId = FirebaseAuth.getInstance().getUid();

    public ConversationAdapter(List<Conversation> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);

        Context context = holder.itemView.getContext();
        String fullName = conversation.getPrenomDestinataire() + " " + conversation.getNomDestinataire();
        holder.nomDestinataire.setText(fullName.trim());

        if (conversation.isNonLu()) {
            holder.nomDestinataire.setTextColor(context.getColor(R.color.orange));
        } else {
            holder.nomDestinataire.setTextColor(context.getColor(R.color.black));
        }

        String prefixe = conversation.getExpediteurId().equals(currentUserId) ? "Vous" : conversation.getPrenomDestinataire();
        holder.dernierMessage.setText(prefixe + " : " + conversation.getDernierMessage());

        // Date/heure formatée
        Date date = conversation.getDateDernierMessage();
        if (date != null) {
            Calendar calMsg = Calendar.getInstance();
            calMsg.setTime(date);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long diff = calMsg.getTimeInMillis() - today.getTimeInMillis();

            SimpleDateFormat heureFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            holder.heure.setText(heureFormat.format(date));

            if (diff >= 0 && diff < 86400000) {
                holder.date.setText("Aujourd'hui");
            } else if (diff >= -86400000 && diff < 0) {
                holder.date.setText("Hier");
            } else {
                holder.date.setText(dateFormat.format(date));
            }
        }

        // Badge vert
        int nonLus = conversation.getNonLus();
        if (nonLus > 0) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(String.valueOf(nonLus));
        } else {
            holder.badge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onConversationClicked(conversation));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView nomDestinataire, dernierMessage, date, heure, badge;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            nomDestinataire = itemView.findViewById(R.id.textViewNomDestinataire);
            dernierMessage = itemView.findViewById(R.id.textViewDernierMessage);
            date = itemView.findViewById(R.id.textViewDate);
            heure = itemView.findViewById(R.id.textViewHeure);
            badge = itemView.findViewById(R.id.textViewBadge); // À ajouter dans ton layout XML
        }
    }
}