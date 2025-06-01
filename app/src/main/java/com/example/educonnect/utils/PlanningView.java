package com.example.educonnect.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.educonnect.R;
import com.example.educonnect.models.Seance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlanningView extends LinearLayout {
    private static final int CELL_HEIGHT_DP = 60; // Hauteur d'une cellule d'une heure
    private static final int COLUMN_WIDTH_DP = 65; // Largeur d'une colonne (jour)
    private static final int HOUR_COLUMN_WIDTH_DP = 48; // Largeur de la colonne des heures
    private static final int START_HOUR = 8; // Heure de début (8h)
    private static final int END_HOUR = 18; // Heure de fin (18h)

    private OnSeanceClickListener seanceClickListener;
    private LinearLayout containerLayout;
    private List<Date> jours = new ArrayList<>();
    private List<Seance> seances = new ArrayList<>();

    private float density;

    public PlanningView(Context context) {
        super(context);
        init(context);
    }

    public PlanningView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlanningView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public interface OnSeanceClickListener {
        void onClick(Seance seance);
    }

    public void setOnSeanceClickListener(OnSeanceClickListener listener) {
        this.seanceClickListener = listener;
    }
    private void init(Context context) {
        setOrientation(VERTICAL);
        density = context.getResources().getDisplayMetrics().density;
        containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(HORIZONTAL);
        addView(containerLayout);
        setBackgroundColor(Color.parseColor("#F0F0F0")); // ✅ fond gris global
    }

    public void setJours(List<Date> jours) {
        this.jours = jours;
        refreshPlanning();
    }

    public void setSeances(List<Seance> seances) {
        this.seances = seances;
        refreshPlanning();
    }

    private void refreshPlanning() {
        if (jours.isEmpty()) return;

        containerLayout.removeAllViews();

        // Ajouter la colonne des heures
        addHeuresColumn();

        // Ajouter une colonne pour chaque jour
        for (Date jour : jours) {
            addJourColumn(jour);
        }
    }

    private void addHeuresColumn() {
        LinearLayout heuresColumn = new LinearLayout(getContext());
        heuresColumn.setOrientation(VERTICAL);
        heuresColumn.setLayoutParams(new LayoutParams(
                dpToPx(HOUR_COLUMN_WIDTH_DP),
                LayoutParams.WRAP_CONTENT));
        heuresColumn.setBackgroundColor(Color.WHITE);

        // Ajouter les heures
        for (int heure = START_HOUR; heure <= END_HOUR; heure++) {
            View heureView = LayoutInflater.from(getContext()).inflate(R.layout.item_heure, heuresColumn, false);
            TextView textViewHeure = heureView.findViewById(R.id.textViewHeure);
            textViewHeure.setText(String.format(Locale.getDefault(), "%02d", heure));

            // Définir la hauteur de la cellule
            heureView.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    dpToPx(CELL_HEIGHT_DP)));

            heuresColumn.addView(heureView);
        }

        containerLayout.addView(heuresColumn);
    }

    private void addJourColumn(Date jour) {
        FrameLayout jourColumn = new FrameLayout(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(COLUMN_WIDTH_DP),
                LayoutParams.MATCH_PARENT);

        if (containerLayout.getChildCount() > 0) {
            params.setMarginStart(dpToPx(8));
        }

        jourColumn.setLayoutParams(params);

        // Créer le fond de la colonne avec les lignes des heures
        LinearLayout backgroundColumn = new LinearLayout(getContext());
        backgroundColumn.setOrientation(VERTICAL);
        backgroundColumn.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));

        // Ajouter les lignes d'heures
        for (int heure = START_HOUR; heure <= END_HOUR; heure++) {
            View ligneHeure = new View(getContext());
            ligneHeure.setBackgroundColor(Color.parseColor("#F0F0F0"));
            ligneHeure.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    dpToPx(CELL_HEIGHT_DP)));
            backgroundColumn.addView(ligneHeure);
        }

        jourColumn.addView(backgroundColumn);

        // Ajouter les séances pour ce jour
        List<Seance> seancesDuJour = getSeancesPourJour(jour);
        for (Seance seance : seancesDuJour) {
            addSeanceView(jourColumn, seance);
        }

        containerLayout.addView(jourColumn);
    }

    private void addSeanceView(FrameLayout jourColumn, Seance seance) {
        View seanceView = LayoutInflater.from(getContext()).inflate(R.layout.item_seance, jourColumn, false);
        CardView cardView = seanceView.findViewById(R.id.cardViewSeance);
        TextView textViewCode = seanceView.findViewById(R.id.textViewCodeCours);
        TextView textViewHoraire = seanceView.findViewById(R.id.textViewHoraire);
        TextView textViewSalle = seanceView.findViewById(R.id.textViewSalle);

        textViewCode.setText(seance.getCode());
        textViewHoraire.setText(seance.getHeureDebut());

        if (seance.getSalle() != null && !seance.getSalle().isEmpty()) {
            textViewSalle.setText(seance.getSalle());
            textViewSalle.setVisibility(View.VISIBLE);
        } else {
            textViewSalle.setVisibility(View.GONE);
        }

        cardView.setCardBackgroundColor(Color.parseColor(seance.getCouleur()));

        // ✅ Conversion UTC → local avec Calendar
        Calendar cal = Calendar.getInstance();
        cal.setTime(seance.getDateDebut());
        int startHour = cal.get(Calendar.HOUR_OF_DAY);
        int startMinute = cal.get(Calendar.MINUTE);

        int durationMinutes = seance.getDureeMinutes();
        int topMargin = dpToPx((startHour - START_HOUR) * CELL_HEIGHT_DP + (startMinute * CELL_HEIGHT_DP / 60)) + dpToPx(34);
        int height = dpToPx(durationMinutes * CELL_HEIGHT_DP / 60);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                height);
        params.topMargin = topMargin;
        seanceView.setLayoutParams(params);

        jourColumn.addView(seanceView);

        seanceView.setOnClickListener(v -> {
            if (seanceClickListener != null) {
                seanceClickListener.onClick(seance);
            }
        });
    }

    private List<Seance> getSeancesPourJour(Date jour) {
        List<Seance> seancesDuJour = new ArrayList<>();
        Calendar calJour = Calendar.getInstance();
        calJour.setTime(jour);

        int jourAnnee = calJour.get(Calendar.DAY_OF_YEAR);
        int annee = calJour.get(Calendar.YEAR);

        for (Seance seance : seances) {
            Calendar calSeance = Calendar.getInstance();
            calSeance.setTime(seance.getDateDebut());

            if (calSeance.get(Calendar.DAY_OF_YEAR) == jourAnnee &&
                    calSeance.get(Calendar.YEAR) == annee) {
                seancesDuJour.add(seance);
            }
        }

        return seancesDuJour;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * density);
    }
}