package com.retronovaindustry.retronova.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.retronovaindustry.retronova.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AchatFragment extends Fragment {

    private RecyclerView rvAchats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achat, container, false);

        rvAchats = view.findViewById(R.id.rvAchats);
        rvAchats.setLayoutManager(new LinearLayoutManager(getContext()));

        // Ici, vous pourriez configurer un adaptateur pour afficher les produits à acheter
        // Exemple: rvAchats.setAdapter(new AchatAdapter(products));

        return view;
    }
}