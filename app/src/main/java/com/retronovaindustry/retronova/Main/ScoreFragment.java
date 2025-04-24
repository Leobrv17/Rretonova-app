package com.retronovaindustry.retronova.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.retronovaindustry.retronova.R;
public class ScoreFragment extends Fragment {

    private RecyclerView rvScores;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);

        rvScores = view.findViewById(R.id.rvScores);
        rvScores.setLayoutManager(new LinearLayoutManager(getContext()));

        // Ici, vous pourriez configurer un adaptateur pour afficher les scores
        // Exemple: rvScores.setAdapter(new ScoreAdapter(scores));

        return view;
    }
}