package com.entity.fractured;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "fractured! (Desktop)";
        cfg.useGL20 = true;
        cfg.width = 800;
        cfg.height = 480;
        //cfg.vSyncEnabled = true;
        new LwjglApplication(new Fractured(), cfg);
    }
}
