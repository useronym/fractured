package com.entity.fractured;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created with IntelliJ IDEA.
 * User: entity
 * Date: 11/11/13
 * Time: 11:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class FracturedGestureListener implements GestureDetector.GestureListener {
    private float panX = 0f;
    private float panY = 0f;
    private float deltaPanX = 0f;
    private float deltaPanY = 0f;
    private float zoom = 1f;
    private Vector2 zoomCenter;

    public float getPanX() {
        return panX;
    }

    public float getPanY() {
        return panY;
    }

    public float getDeltaPanX() {
        float retVal = deltaPanX;
        deltaPanX = 0f;
        return retVal;
    }

    public float getDeltaPanY() {
        float retVal = deltaPanY;
        deltaPanY = 0f;
        return retVal;
    }

    public float getZoom() {
        float retVal = zoom;
        zoom = 1f;
        return retVal;
    }

    public Vector2 getZoomCenter() {
        return zoomCenter;
    }


    @Override
    public boolean touchDown(float v, float v2, int i, int i2) {
        return false;
    }

    @Override
    public boolean tap(float v, float v2, int i, int i2) {
        return false;
    }

    @Override
    public boolean longPress(float v, float v2) {
        return false;
    }

    @Override
    public boolean fling(float v, float v2, int i) {
        return false;
    }

    @Override
    public boolean pan(float v, float v2, float v3, float v4) {
        panX = v;
        panY = v2;
        deltaPanX = v3;
        deltaPanY = v4;

        return false;
    }

    @Override
    public boolean zoom(float v, float v2) {
        zoom = v2/v;
        return false;
    }

    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24) {

        return false;
    }
}
