package com.retronovaindustry.retronova.Main;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.retronovaindustry.retronova.Authentication.LoginActivity;
import com.retronovaindustry.retronova.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.retronovaindustry.retronova.Utils.FullScreenUtils;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Forcer l'orientation portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialiser Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialiser les vues
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        rootView = findViewById(android.R.id.content);

        // Configurer la toolbar
        setSupportActionBar(toolbar);

        // Configurer la barre de navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Charger le fragment par défaut (Home)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Activer le mode plein écran
        FullScreenUtils.enableFullScreenMode(this);

        // Configurer un listener pour sortir temporairement du mode plein écran lors d'un tap
        rootView.setOnClickListener(v -> {
            // Afficher temporairement les barres système
            FullScreenUtils.disableFullScreenMode(MainActivity.this);

            // Réactiver le mode plein écran après un délai
            rootView.postDelayed(() -> FullScreenUtils.enableFullScreenMode(MainActivity.this), 3000);
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Réactiver le mode plein écran quand l'activité regagne le focus
            FullScreenUtils.enableFullScreenMode(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Réactiver le mode plein écran quand l'activité reprend
        FullScreenUtils.enableFullScreenMode(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Vérifier si l'utilisateur est connecté
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Rediriger vers LoginActivity si non connecté
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Fermer MainActivity pour éviter le retour en arrière
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        if (item.getItemId() == R.id.navigation_home) {
            toolbar.setTitle("Accueil");
            fragment = new HomeFragment();
        } else if (item.getItemId() == R.id.navigation_machine) {
            toolbar.setTitle("Machines");
            fragment = new MachineFragment();
        } else if (item.getItemId() == R.id.navigation_score) {
            toolbar.setTitle("Scores");
            fragment = new ScoreFragment();
        } else if (item.getItemId() == R.id.navigation_achat) {
            toolbar.setTitle("Boutique");
            fragment = new AchatFragment();
        } else if (item.getItemId() == R.id.navigation_profile) {
            toolbar.setTitle("Profil");
            fragment = new ProfileFragment();
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}