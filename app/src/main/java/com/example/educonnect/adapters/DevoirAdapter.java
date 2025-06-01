package com.example.educonnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.models.Devoir;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DevoirAdapter extends RecyclerView.Adapter<DevoirAdapter.DevoirViewHolder> {

    public interface OnModifierClickListener {
        void onModifier(Devoir devoir);
    }

    public interface OnSupprimerClickListener {
        void onSupprimer(Devoir devoir);
    }

    public interface OnItemClickListener {
        void onItemClick(Devoir devoir);
    }

    private final List<Devoir> devoirs;
    private final boolean isProf;
    private final OnModifierClickListener modifierListener;
    private final OnSupprimerClickListener supprimerListener;
    private final OnItemClickListener itemClickListener;

    public DevoirAdapter(List<Devoir> devoirs, boolean isProf,
                         OnModifierClickListener modifierListener,
                         OnSupprimerClickListener supprimerListener,
                         OnItemClickListener itemClickListener) {
        this.devoirs = devoirs;
        this.isProf = isProf;
        this.modifierListener = modifierListener;
        this.supprimerListener = supprimerListener;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public DevoirViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devoir, parent, false);
        return new DevoirViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevoirViewHolder holder, int position) {
        Devoir devoir = devoirs.get(position);

        holder.titre.setText(devoir.getTitre());
        holder.description.setText(devoir.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.dateEcheance.setText("À rendre avant : " + sdf.format(devoir.getDateEcheance()));

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(devoir));
        }

        if (isProf) {
            holder.btnModifier.setVisibility(View.VISIBLE);
            holder.btnSupprimer.setVisibility(View.VISIBLE);

            holder.btnModifier.setOnClickListener(v -> {
                if (modifierListener != null) modifierListener.onModifier(devoir);
            });

            holder.btnSupprimer.setOnClickListener(v -> {
                if (supprimerListener != null) supprimerListener.onSupprimer(devoir);
            });
        } else {
            holder.btnModifier.setVisibility(View.GONE);
            holder.btnSupprimer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return devoirs.size();
    }

    public static class DevoirViewHolder extends RecyclerView.ViewHolder {
        TextView titre, dateEcheance, description;
        ImageButton btnModifier, btnSupprimer;

        public DevoirViewHolder(@NonNull View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.textViewTitreDevoir);
            dateEcheance = itemView.findViewById(R.id.textViewDateEcheance);
            description = itemView.findViewById(R.id.textViewDescriptionDevoir);
            btnModifier = itemView.findViewById(R.id.btnModifierDevoir);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimerDevoir);
        }
    }
}
