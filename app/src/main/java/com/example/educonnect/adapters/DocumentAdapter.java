package com.example.educonnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educonnect.R;
import com.example.educonnect.models.Documents;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private List<Documents> documentsList;
    private List<DocumentSnapshot> snapshots;

    private final OnDocumentActionListener adminListener;
    private final OnDocumentClickListener clickListener;

    public interface OnDocumentActionListener {
        void onEditClicked(DocumentSnapshot documentSnapshot);
        void onDeleteClicked(DocumentSnapshot documentSnapshot);
        void onDocumentClicked(Documents document);
    }

    public interface OnDocumentClickListener {
        void onDocumentClicked(Documents document);
    }

    // Étudiant (lecture seule)
    public DocumentAdapter(List<Documents> documentsList, OnDocumentClickListener clickListener) {
        this.documentsList = documentsList;
        this.clickListener = clickListener;
        this.adminListener = null;
    }

    // Professeur (édition/suppression)
    public DocumentAdapter(List<Documents> documentsList, OnDocumentActionListener adminListener) {
        this.documentsList = documentsList;
        this.adminListener = adminListener;
        this.clickListener = null;
        this.snapshots = new ArrayList<>();
    }

    public void updateList(List<Documents> newList) {
        this.documentsList = newList;
        notifyDataSetChanged();
    }

    public void updateList(List<Documents> newList, List<DocumentSnapshot> newSnapshots) {
        this.documentsList = newList;
        this.snapshots = newSnapshots;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Documents document = documentsList.get(position);
        holder.titre.setText(document.getTitre());
        holder.description.setText(document.getDescription());
        holder.type.setText(document.getType());

        if (adminListener != null && snapshots != null && position < snapshots.size()) {
            DocumentSnapshot snapshot = snapshots.get(position);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> adminListener.onEditClicked(snapshot));
            holder.btnDelete.setOnClickListener(v -> adminListener.onDeleteClicked(snapshot));
            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onDocumentClicked(document);
            });
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onDocumentClicked(document);
            });
        }
    }

    @Override
    public int getItemCount() {
        return documentsList.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView titre, description, type;
        ImageButton btnEdit, btnDelete;
        ImageView icone;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.textTitreDocument);
            description = itemView.findViewById(R.id.textDescriptionDocument);
            type = itemView.findViewById(R.id.textTypeDocument);
            icone = itemView.findViewById(R.id.imageIconeDocument);
            btnEdit = itemView.findViewById(R.id.btnEditDocument);
            btnDelete = itemView.findViewById(R.id.btnDeleteDocument);
        }
    }
}
