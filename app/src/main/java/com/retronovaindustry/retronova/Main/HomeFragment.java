package com.retronovaindustry.retronova.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.retronovaindustry.retronova.R;

public class HomeFragment extends Fragment {

    private TextView tvUserInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvUserInfo = view.findViewById(R.id.tvUserInfo);

        // Afficher les informations de l'utilisateur connecté
        updateUserInfo();

        return view;
    }

    private void updateUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            String email = user.getEmail();

            if (displayName != null && !displayName.isEmpty()) {
                tvUserInfo.setText("Connecté en tant que: " + displayName + " (" + email + ")");
            } else {
                tvUserInfo.setText("Connecté en tant que: " + email);
            }
        }
    }
}