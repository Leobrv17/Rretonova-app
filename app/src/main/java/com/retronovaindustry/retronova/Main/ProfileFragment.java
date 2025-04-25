package com.retronovaindustry.retronova.Main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.retronovaindustry.retronova.Authentication.LoginActivity;
import com.retronovaindustry.retronova.R;
import com.retronovaindustry.retronova.api.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView tvProfileName, tvProfileEmail, tvPublicId, tvOfficialName;
    private Button btnEditProfile, btnLogout;
    private FirebaseAuth mAuth;
    private ApiService apiService;
    private String userId; // ID de l'utilisateur dans l'API
    private String publicId; // ID public de l'utilisateur
    private String firstName, lastName; // Nom et prénom officiels

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialiser Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialiser le service API
        apiService = new ApiService();

        // Initialiser les vues
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvPublicId = view.findViewById(R.id.tvPublicId);
        tvOfficialName = view.findViewById(R.id.tvOfficialName);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Configurer les listeners
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnLogout.setOnClickListener(v -> logout());

        // Charger les informations de l'utilisateur depuis Firebase
        updateProfileInfo();

        // Synchroniser avec l'API
        syncWithApi();

        return view;
    }

    private void syncWithApi() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Vérifier si l'utilisateur existe dans l'API
        apiService.getUserByFirebaseId(user.getUid(), new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                // L'utilisateur existe dans l'API
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    userId = jsonResponse.getString("id");
                    publicId = jsonResponse.getString("publique_id");

                    // Récupérer les informations officielles
                    if (!jsonResponse.isNull("first_name")) {
                        firstName = jsonResponse.getString("first_name");
                    } else {
                        firstName = "";
                    }

                    if (!jsonResponse.isNull("last_name")) {
                        lastName = jsonResponse.getString("last_name");
                    } else {
                        lastName = "";
                    }

                    // Mettre à jour l'interface utilisateur
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvPublicId.setText("ID Public: " + publicId);

                            // Afficher le nom officiel complet si disponible
                            if (!TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName)) {
                                tvOfficialName.setText("Nom officiel: " + firstName + " " + lastName);
                            } else {
                                tvOfficialName.setText("Nom officiel: Non défini");
                            }
                        });
                    }

                    Log.d(TAG, "Utilisateur trouvé dans l'API avec ID: " + userId + ", Public ID: " + publicId);
                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du traitement de la réponse JSON", e);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // L'utilisateur n'existe pas dans l'API
                Log.w(TAG, "Utilisateur non trouvé dans l'API: " + errorMessage);

                // Créer l'utilisateur dans l'API si nécessaire
                createUserInApi(user);
            }
        });
    }

    private void createUserInApi(FirebaseUser user) {
        String displayName = user.getDisplayName() != null ? user.getDisplayName() : "";
        final String userFirstName;
        final String userLastName;

        if (displayName.contains(" ")) {
            String[] nameParts = displayName.split(" ", 2);
            userFirstName = nameParts[0];
            userLastName = nameParts[1];
        } else {
            userFirstName = displayName;
            userLastName = "";
        }

        // Ces variables finales peuvent être référencées dans le callback
        final String finalFirstName = userFirstName;
        final String finalLastName = userLastName;

        apiService.createUserInApi(userFirstName, userLastName, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    userId = jsonResponse.getString("id");
                    publicId = jsonResponse.getString("publique_id");

                    // Mettre à jour les champs de classe directement
                    firstName = finalFirstName;
                    lastName = finalLastName;

                    // Mettre à jour l'interface utilisateur
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvPublicId.setText("ID Public: " + publicId);
                            tvOfficialName.setText("Nom officiel: " + firstName + " " + lastName);
                        });
                    }

                    Log.d(TAG, "Utilisateur créé dans l'API avec ID: " + userId + ", Public ID: " + publicId);
                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du traitement de la réponse JSON", e);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Échec de création dans l'API: " + errorMessage);
            }
        });
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

            // L'ID public sera mis à jour par syncWithApi()
            tvPublicId.setText("ID Public: Chargement...");
            tvOfficialName.setText("Nom officiel: Chargement...");
        }
    }

    private void showEditProfileDialog() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Créer la boîte de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Modifier le profil");

        // Inflater le layout custom
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(viewInflated);

        // Récupérer les vues du layout de la boîte de dialogue
        TextInputEditText etDisplayName = viewInflated.findViewById(R.id.etDisplayName);
        TextInputEditText etFirstName = viewInflated.findViewById(R.id.etFirstName);
        TextInputEditText etLastName = viewInflated.findViewById(R.id.etLastName);
        TextInputEditText etEmail = viewInflated.findViewById(R.id.etEmail);
        TextView tvResetPassword = viewInflated.findViewById(R.id.tvResetPassword);

        // Pré-remplir les champs
        etDisplayName.setText(user.getDisplayName());
        etFirstName.setText(firstName);
        etLastName.setText(lastName);
        etEmail.setText(user.getEmail());

        // Configurer le listener pour la réinitialisation du mot de passe
        tvResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                sendPasswordResetEmail(email);
            } else {
                Toast.makeText(getContext(), "Veuillez entrer votre email", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurer les boutons de la boîte de dialogue
        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            String newDisplayName = etDisplayName.getText().toString().trim();
            String newFirstName = etFirstName.getText().toString().trim();
            String newLastName = etLastName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();

            // Vérifier que les champs ne sont pas vides
            if (TextUtils.isEmpty(newDisplayName)) {
                Toast.makeText(getContext(), "Le nom d'utilisateur ne peut pas être vide", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(newEmail)) {
                Toast.makeText(getContext(), "L'email ne peut pas être vide", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mettre à jour le profil
            updateProfile(newDisplayName, newEmail, newFirstName, newLastName);
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

        // Afficher la boîte de dialogue
        builder.show();
    }

    private void updateProfile(String newDisplayName, String newEmail, String newFirstName, String newLastName) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Vérifier si l'email a changé
        boolean emailChanged = !user.getEmail().equals(newEmail);

        // Mettre à jour le displayName dans Firebase (nom d'utilisateur public)
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Mettre à jour l'UI avec le nouveau nom
                        tvProfileName.setText(newDisplayName);

                        // Mettre à jour les informations dans l'API (informations officielles)
                        if (userId != null) {
                            firstName = newFirstName;
                            lastName = newLastName;
                            updateUserInApi(newFirstName, newLastName);
                        }

                        // Si l'email a changé, mettre à jour l'email
                        if (emailChanged) {
                            updateEmail(newEmail);
                        } else {
                            Toast.makeText(getContext(), "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Erreur lors de la mise à jour du profil", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInApi(String newFirstName, String newLastName) {
        if (userId == null) return;

        apiService.updateUserInApi(userId, newFirstName, newLastName, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Profil mis à jour dans l'API: " + response);

                // Mettre à jour l'UI avec les nouveaux noms officiels
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvOfficialName.setText("Nom officiel: " + newFirstName + " " + newLastName);
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Erreur lors de la mise à jour du profil dans l'API: " + errorMessage);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Erreur lors de la mise à jour du profil dans l'API", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void updateEmail(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Demander une reauthentification avant de changer l'email
        showReauthDialog(user, () -> {
            user.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            tvProfileEmail.setText(newEmail);
                            Toast.makeText(getContext(), "Email mis à jour avec succès", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Erreur lors de la mise à jour de l'email: " +
                                            (task.getException() != null ? task.getException().getMessage() : "Erreur inconnue"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Email de réinitialisation envoyé à " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Erreur lors de l'envoi de l'email de réinitialisation: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Erreur inconnue"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showReauthDialog(FirebaseUser user, Runnable onSuccess) {
        // Créer la boîte de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Reauthentification");
        builder.setMessage("Pour des raisons de sécurité, veuillez entrer votre mot de passe actuel");

        // Inflater le layout custom pour le mot de passe
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_reauth, null);
        TextInputEditText etPassword = viewInflated.findViewById(R.id.etPassword);
        builder.setView(viewInflated);

        // Configurer les boutons
        builder.setPositiveButton("Confirmer", null); // On le configure plus tard pour éviter la fermeture automatique
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());

        // Créer et afficher la boîte de dialogue
        AlertDialog dialog = builder.create();
        dialog.show();

        // Remplacer le listener du bouton positif pour éviter la fermeture automatique en cas d'échec
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Le mot de passe est requis");
                return;
            }

            // Reauthentifier l'utilisateur
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            onSuccess.run();
                        } else {
                            etPassword.setError("Mot de passe incorrect");
                        }
                    });
        });
    }

    private void logout() {
        // Déconnecter l'utilisateur
        mAuth.signOut();

        // Rediriger vers l'écran de connexion
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish(); // Fermer MainActivity pour éviter le retour en arrière
        }
    }
}