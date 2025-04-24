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

public class MachineFragment extends Fragment {

    private RecyclerView rvMachines;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_machine, container, false);

        rvMachines = view.findViewById(R.id.rvMachines);
        rvMachines.setLayoutManager(new LinearLayoutManager(getContext()));

        // Ici, vous pourriez configurer un adaptateur pour afficher la liste des machines
        // Exemple: rvMachines.setAdapter(new MachineAdapter(machines));

        return view;
    }
}