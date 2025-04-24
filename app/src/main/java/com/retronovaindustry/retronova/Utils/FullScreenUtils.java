package com.retronovaindustry.retronova.Utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

/**
 * Utilitaire pour gérer le mode plein écran immersif
 */
public class FullScreenUtils {

    /**
     * Active le mode plein écran immersif
     * @param activity L'activité à mettre en plein écran
     */
    public static void enableFullScreenMode(Activity activity) {
        if (activity == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Pour Android 11+ (API 30+)
            activity.getWindow().setDecorFitsSystemWindows(false);
            View decorView = activity.getWindow().getDecorView();
            WindowInsetsController controller = decorView.getWindowInsetsController();
            if (controller != null) {
                // Cache les barres système et entre en mode immersif sticky
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // Pour Android 4.4 à Android 10
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    /**
     * Désactive le mode plein écran et montre les barres système
     * @param activity L'activité pour laquelle désactiver le mode plein écran
     */
    public static void disableFullScreenMode(Activity activity) {
        if (activity == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Pour Android 11+ (API 30+)
            activity.getWindow().setDecorFitsSystemWindows(true);
            View decorView = activity.getWindow().getDecorView();
            WindowInsetsController controller = decorView.getWindowInsetsController();
            if (controller != null) {
                controller.show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            }
        } else {
            // Pour Android 4.4 à Android 10
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    /**
     * Active le mode plein écran immersif pendant un court moment (pour attirer l'attention de l'utilisateur)
     * puis revient automatiquement en mode normal après une interaction utilisateur
     * @param activity L'activité à mettre en plein écran temporairement
     */
    public static void toggleFullScreenMode(Activity activity) {
        if (activity == null) return;

        // Obtenir l'état actuel
        boolean isFullScreen = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            View decorView = activity.getWindow().getDecorView();
            WindowInsetsController controller = decorView.getWindowInsetsController();
            if (controller != null) {
                // Vérifier si les barres sont visibles
                isFullScreen = !decorView.getRootWindowInsets().isVisible(WindowInsets.Type.statusBars());
            }
        } else {
            int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
            isFullScreen = (uiOptions & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0;
        }

        // Basculer l'état
        if (isFullScreen) {
            disableFullScreenMode(activity);
        } else {
            enableFullScreenMode(activity);
        }
    }
}