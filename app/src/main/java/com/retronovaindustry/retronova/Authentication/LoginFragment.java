package com.retronovaindustry.retronova.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.retronovaindustry.retronova.Main.MainActivity;
import com.retronovaindustry.retronova.Utils.NetworkUtils;
import com.retronovaindustry.retronova.R;

public class LoginFragment extends Fragment {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialiser Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialiser les vues
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        progressBar = view.findViewById(R.id.progressBar);

        // Configurer le listener du bouton de connexion
        btnLogin.setOnClickListener(v -> loginUser());

        // Configurer le listener du texte "Mot de passe oublié"
        tvForgotPassword.setOnClickListener(v -> forgotPassword());

        return view;
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Valider les entrées
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("L'email est requis");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Le mot de passe est requis");
            return;
        }

        // Vérifier la connexion Internet
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "Pas de connexion Internet disponible. Veuillez vérifier votre connexion.", Toast.LENGTH_LONG).show();
            return;
        }

        // Afficher la barre de progression
        progressBar.setVisibility(View.VISIBLE);

        // Connexion avec Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Connexion réussie
                        Toast.makeText(getContext(), "Connexion réussie", Toast.LENGTH_SHORT).show();
                        startMainActivity();
                    } else {
                        // Échec de la connexion
                        String errorMessage = "Échec de la connexion";
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();

                            // Afficher plus de détails dans les logs
                            task.getException().printStackTrace();
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void forgotPassword() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Entrez votre email pour réinitialiser le mot de passe");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Email de réinitialisation envoyé à " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Erreur: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish(); // Fermer l'activité actuelle pour éviter le retour en arrière
    }
}