package com.entity.fractured;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: entity
 * Date: 11/2/13
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
*/
public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "fractured! (Desktop)";
        cfg.useGL20 = true;
        cfg.width = 800;
        cfg.height = 480;
        cfg.vSyncEnabled = true;
        new LwjglApplication(new Fractured(), cfg);
    }
}
