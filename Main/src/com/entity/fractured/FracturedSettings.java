package com.entity.fractured;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

import java.util.Arrays;

public class FracturedSettings {
    String version = "1.0";
    String preferencesName = "fractured-preferences";
    String aboutText = "fractured! v." + version + "\n" +
            "Faculty of Informatics, Masaryk University\n" +
            "PV097, fall 2013\n" +
            "Adam Krupicka";
    int screenCounter = 0;
    boolean debugLogging = true;
    boolean debugGUI = false;
    float width = 0f, height = 0f;
    float aspectRatio;

    // 0 = auto; 1 = force standard; 2 = force large
    int guiMode = 0;
    float zoomSpeed = 0.5f;
    float uiPadding = 5f, uiPaddingLarge = 10f;
    float uiWidth = 80f, uiWidthLarge = 100f;
    float uiWindowBorder = 35f, uiWindowBorderLarge = 50f;
    float uiWindowMin = 280f, uiWindowMinLarge = 450f;


    int fractalType = 0, fractalColor = 0;
    int previewSetting = 2, renderSetting = 3;

    FracturedSettings() {
        loadFromShared();
        loadGradients();
    }

    public void updateDisplaySettings() {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        aspectRatio = width / height;
    }

    public void saveToShared() {
        Preferences prefs = Gdx.app.getPreferences(preferencesName);
        prefs.clear();

        prefs.putString("version", version);
        prefs.putInteger("screenCounter", screenCounter);
        prefs.putInteger("guiMode", guiMode);
        prefs.putInteger("previewSetting", previewSetting);
        prefs.putInteger("renderSetting", renderSetting);
        prefs.flush();

        Gdx.app.debug("fractured!", "saved shared settings");
    }

    public void loadFromShared() {
        Preferences prefs = Gdx.app.getPreferences(preferencesName);

        if (prefs.contains("version")) {
            if (prefs.getString("version").equals(version)) {
                screenCounter = prefs.getInteger("screenCounter", screenCounter);
                guiMode = prefs.getInteger("guiMode", guiMode);
                previewSetting = prefs.getInteger("previewSetting", previewSetting);
                renderSetting = prefs.getInteger("renderSetting", renderSetting);
            } else {
                Gdx.app.debug("fractured!", "ignoring an outdated version of shared settings");
            }
        }
    }

    // does not work on desktop - lol ( ffs )
    public void loadGradients() {
        /*FileHandle gradsDir = Gdx.files.internal("gradients");
        FileHandle[] grads = gradsDir.list();
        fractalColors = new String[grads.length];

        for (int i = 0; i < grads.length; i++) {
            fractalColors[i] = "gradients/" + grads[i].name();
        }*/

        Arrays.sort(fractalColors);
        //Gdx.app.debug("fractured!", "parsed " + grads.length + " gradients");
    }

    String[] fractalColors = {"gradients/aneurism.png",
            "gradients/full_spectrum.png",
            "gradients/incandescent.png",
            "gradients/metallic_something.png",
            "gradients/purples.png",
            "gradients/shadows1.png",
            "gradients/shadows3.png",
            "gradients/skyline.png",
            "gradients/three_bars_sin.png",
            "gradients/tropical_colors.png",
            "gradients/yellow_contrast.png",
            "gradients/afterdusk.png",
            "gradients/alien_green_planet.png",
            "gradients/blue-HSVCCW.png",
            "gradients/blue-HSVCW.png",
            "gradients/blue-magenta-white.png",
            "gradients/burning_paper.png",
            "gradients/cetonia.png",
            "gradients/cold_steel.png",
            "gradients/cyan-HSVCCW.png",
            "gradients/cyan-HSVCW.png",
            "gradients/cyan.png",
            "gradients/dark.png",
            "gradients/full_spectrum-stripes.png",
            "gradients/golden.png",
            "gradients/green-cyan.png",
            "gradients/magenta.png",
            "gradients/magenta-red.png",
            "gradients/metallic_something.png",
            "gradients/neutral.png",
            "gradients/neutral_vermillion.png",
            "gradients/old_leaf.png",
            "gradients/red-HSVCCW.png",
            "gradients/red-HSVCW.png",
            "gradients/red-magenta-yellow.png",
            "gradients/red.png",
            "gradients/red-yellow.png",
            "gradients/red-yellow-zigzig.png",
            "gradients/turanj.png",
    };

    float[] renderQualities = {8f, 4f, 2f, 1.25f, 1f, 0.8f, 0.66f, 0.5f};
    String[] renderQualityNames = {"12.5%", "25%", "50%", "80%", "100%", "125%", "150%", "200%"};

    String[] fractalTypes = {"fractals/julia_z2.frag",
            "fractals/julia_z3.frag",
            "fractals/julia_expz2.frag",
            "fractals/julia_expz3.frag",
    };
    String[] fractalNames = {"z^2 + c",
            "z^3 + c",
            "exp(z^2) - c",
            "exp(z^3) - c"};
}
