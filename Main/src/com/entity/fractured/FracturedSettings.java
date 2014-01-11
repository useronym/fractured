package com.entity.fractured;

import com.badlogic.gdx.Gdx;

import java.util.Arrays;

public class FracturedSettings {
    boolean debugMode = true;
    float aspectRatio;
    float previewQuality = 4f, highQuality = 1.25f;
    float zoomSpeed = 0.5f;

    int fractalType = 0, fractalColor = 1;


    FracturedSettings() {
        aspectRatio = Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        Arrays.sort(fractalColors);
    }

    String[] fractalTypes = {"fractals/julia_z2.frag",
            "fractals/julia_z3.frag",
            "fractals/julia_expz2.frag",
            "fractals/julia_expz3.frag",
    };

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
    };
}
