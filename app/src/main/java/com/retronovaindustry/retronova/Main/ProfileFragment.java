package com.retronovaindustry.retronova.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.retronovaindustry.retronova.Authentication.LoginActivity;
import com.retronovaindustry.retronova.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail;
    private Button btnEditProfile, btnLogout;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialiser Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialiser les vues
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Configurer les listeners
        btnEditProfile.setOnClickListener(v -> editProfile());
        btnLogout.setOnClickListener(v -> logout());

        // Afficher les informations de l'utilisateur
        updateProfileInfo();

        return view;
    }

    private void updateProfileInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            String email = user.getEmail();

            if (displayName != null && !displayName.isEmpty()) {
                tvProfileName.setText(displayName);
            } else {
                tvProfileName.setText("Utilisateur");
            }

            tvProfileEmail.setText(email);
        }
    }

    private void editProfile() {
        // Implémentation future de la modification du profil
        // Par exemple : ouvrir une activité d'édition de profil
    }

    private void logout() {
        // Déconnecter l'utilisateur
        mAuth.signOut();

        // Rediriger vers l'écran de connexion
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish(); // Fermer MainActivity pour éviter le retour en arrière
    }
}