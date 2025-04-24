package com.retronovaindustry.retronova.Authentication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.retronovaindustry.retronova.Main.MainActivity;
import com.retronovaindustry.retronova.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.retronovaindustry.retronova.Utils.FullScreenUtils;

public class LoginActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private AuthPagerAdapter pagerAdapter;
    private FirebaseAuth mAuth;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Forcer l'orientation portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialiser Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialiser les vues
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        rootView = findViewById(android.R.id.content);

        // Configurer l'adaptateur pour le ViewPager
        pagerAdapter = new AuthPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connecter le TabLayout avec ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Connexion");
                    } else {
                        tab.setText("Inscription");
                    }
                }).attach();

        // Activer le mode plein écran
        FullScreenUtils.enableFullScreenMode(this);

        // Configurer un listener pour sortir temporairement du mode plein écran lors d'un tap
        rootView.setOnClickListener(v -> {
            // Afficher temporairement les barres système
            FullScreenUtils.disableFullScreenMode(LoginActivity.this);

            // Réactiver le mode plein écran après un délai
            rootView.postDelayed(() -> FullScreenUtils.enableFullScreenMode(LoginActivity.this), 3000);
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
        // Vérifier si l'utilisateur est déjà connecté
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Rediriger vers MainActivity si déjà connecté
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Fermer LoginActivity pour éviter le retour en arrière
    }
}