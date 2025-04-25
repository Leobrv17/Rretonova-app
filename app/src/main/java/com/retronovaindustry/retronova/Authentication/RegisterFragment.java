package com.retronovaindustry.retronova.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.retronovaindustry.retronova.Main.MainActivity;
import com.retronovaindustry.retronova.Utils.NetworkUtils;
import com.retronovaindustry.retronova.R;
import com.retronovaindustry.retronova.api.ApiService;

public class RegisterFragment extends Fragment {

    private TextInputEditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialiser Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialiser le service API
        apiService = new ApiService();

        // Initialiser les vues
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnRegister = view.findViewById(R.id.btnRegister);
        progressBar = view.findViewById(R.id.progressBar);

        // Configurer le listener du bouton d'inscription
        btnRegister.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Valider les entrées
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("Le prénom est requis");
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Le nom est requis");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("L'email est requis");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Le mot de passe est requis");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Les mots de passe ne correspondent pas");
            return;
        }

        // Vérifier la connexion Internet
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "Pas de connexion Internet disponible. Veuillez vérifier votre connexion.", Toast.LENGTH_LONG).show();
            return;
        }

        // Afficher la barre de progression
        progressBar.setVisibility(View.VISIBLE);

        // Créer un utilisateur avec Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Ajouter le nom complet de l'utilisateur à son profil
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Combiner prénom et nom pour le displayName de Firebase
                            String displayName = firstName + " " + lastName;

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            // Firebase profile mis à jour, maintenant créer l'utilisateur dans notre API
                                            // Utiliser les champs séparés pour l'API
                                            apiService.createUserInApi(firstName, lastName, new ApiService.ApiCallback() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    // Succès de l'API, méthode appelée sur un thread secondaire
                                                    if (getActivity() != null) {
                                                        getActivity().runOnUiThread(() -> {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(getContext(), "Inscription réussie!", Toast.LENGTH_SHORT).show();
                                                            startMainActivity();
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onFailure(String errorMessage) {
                                                    // Échec de l'API, méthode appelée sur un thread secondaire
                                                    if (getActivity() != null) {
                                                        getActivity().runOnUiThread(() -> {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(getContext(), "Connexion à l'API échouée: " + errorMessage, Toast.LENGTH_LONG).show();
                                                            // On peut continuer vers MainActivity malgré l'échec, car la connexion Firebase a réussi
                                                            startMainActivity();
                                                        });
                                                    }
                                                }
                                            });
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getContext(), "Erreur de mise à jour du profil", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String errorMessage = "Échec de l'inscription";
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();

                            // Afficher plus de détails dans les logs
                            task.getException().printStackTrace();
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish(); // Fermer l'activité actuelle pour éviter le retour en arrière
    }
}