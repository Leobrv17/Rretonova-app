package com.retronovaindustry.retronova.api;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {
    private static final String TAG = "ApiService";
    private static final String BASE_URL = "http://10.31.38.184:8000"; // Remplacez par l'URL de votre API
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private FirebaseAuth mAuth;

    public ApiService() {
        // Configurer le client HTTP avec des timeouts
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        mAuth = FirebaseAuth.getInstance();
    }

    // Méthode pour créer un utilisateur dans l'API après inscription Firebase
    public void createUserInApi(String firstName, String lastName, final ApiCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("Aucun utilisateur connecté");
            return;
        }

        // Obtenir le token d'authentification Firebase
        currentUser.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                try {
                    // Créer les données à envoyer
                    JSONObject jsonData = new JSONObject();
                    jsonData.put("first_name", firstName);
                    jsonData.put("last_name", lastName);
                    jsonData.put("nb_ticket", 0);
                    jsonData.put("bar", false);
                    jsonData.put("firebase_id", currentUser.getUid());

                    // Créer la requête POST
                    RequestBody body = RequestBody.create(jsonData.toString(), JSON);
                    Request request = new Request.Builder()
                            .url(BASE_URL + "/users/")
                            .addHeader("Authorization", "Bearer " + token)
                            .post(body)
                            .build();

                    // Exécuter la requête de façon asynchrone
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "API request failed", e);
                            callback.onFailure("Erreur réseau: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                callback.onSuccess(response.body().string());
                            } else {
                                callback.onFailure("Erreur API: " + response.code() + " " + response.message());
                            }
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "JSON error", e);
                    callback.onFailure("Erreur de formatage JSON: " + e.getMessage());
                }
            } else {
                callback.onFailure("Impossible d'obtenir le token Firebase: " + task.getException().getMessage());
            }
        });
    }

    // Méthode pour récupérer un utilisateur par son Firebase ID
    public void getUserByFirebaseId(String firebaseId, final ApiCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("Aucun utilisateur connecté");
            return;
        }

        // Obtenir le token d'authentification Firebase
        currentUser.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                // Créer la requête GET
                Request request = new Request.Builder()
                        .url(BASE_URL + "/users/firebase/" + firebaseId)
                        .addHeader("Authorization", "Bearer " + token)
                        .get()
                        .build();

                // Exécuter la requête de façon asynchrone
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "API request failed", e);
                        callback.onFailure("Erreur réseau: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            callback.onSuccess(response.body().string());
                        } else if (response.code() == 404) {
                            callback.onFailure("Utilisateur non trouvé dans l'API");
                        } else {
                            callback.onFailure("Erreur API: " + response.code() + " " + response.message());
                        }
                    }
                });
            } else {
                callback.onFailure("Impossible d'obtenir le token Firebase: " + task.getException().getMessage());
            }
        });
    }

    // Méthode pour mettre à jour un utilisateur existant
    // Dans ApiService.java, modifiez la méthode updateUserInApi pour simplifier l'appel
    // Dans ApiService.java
    public void updateUserInApi(String userId, String firstName, String lastName, final ApiCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("Aucun utilisateur connecté");
            return;
        }

        // Obtenir le token d'authentification Firebase
        currentUser.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                try {
                    // Créer les données à envoyer (uniquement les infos officielles)
                    JSONObject jsonData = new JSONObject();
                    jsonData.put("first_name", firstName);
                    jsonData.put("last_name", lastName);
                    jsonData.put("firebase_id", currentUser.getUid());

                    // Créer la requête PUT
                    RequestBody body = RequestBody.create(jsonData.toString(), JSON);
                    Request request = new Request.Builder()
                            .url(BASE_URL + "/users/" + userId)
                            .addHeader("Authorization", "Bearer " + token)
                            .put(body)
                            .build();

                    // Exécuter la requête de façon asynchrone
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "API request failed", e);
                            callback.onFailure("Erreur réseau: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                callback.onSuccess(response.body().string());
                            } else {
                                callback.onFailure("Erreur API: " + response.code() + " " + response.message());
                            }
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "JSON error", e);
                    callback.onFailure("Erreur de formatage JSON: " + e.getMessage());
                }
            } else {
                callback.onFailure("Impossible d'obtenir le token Firebase: " + task.getException().getMessage());
            }
        });
    }

    // Interface pour les callbacks
    public interface ApiCallback {
        void onSuccess(String response);

        void onFailure(String errorMessage);
    }
}