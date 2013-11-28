package com.entity.fractured;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created with IntelliJ IDEA.
 * User: entity
 * Date: 11/2/13
 * Time: 10:33 PM
 * To change this template use File | Settings | File Templates.
 */

public class Fractured extends Game {
    private FracturedGestureListener gestureListener = null;
    private FractalRenderer renderer = null;
    private boolean needsRender;
    private boolean justRendered;
    private long renderStart;

    private Sprite fractal;
    private OrthographicCamera camera;
    private Sprite logoSprite;
    private SpriteBatch Batch;

    private float aspectRatio = 1f;

    // settings
    private final float sZoomSpeed = 25f;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        aspectRatio = Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        gestureListener = new FracturedGestureListener();
        Gdx.input.setInputProcessor(new GestureDetector(gestureListener));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Texture logoTex = new Texture(Gdx.files.internal("gdxlogo.png"));
        logoSprite = new Sprite(logoTex);
        logoSprite.setRotation(90f);
        logoSprite.setPosition(-100f, 150f);
        Batch = new SpriteBatch();

        renderer = new FractalRenderer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.loadShader("default.vert", "julia.frag");
        needsRender = true;
        justRendered = false;
        fractal = new Sprite(renderer.getTexture());
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        if (justRendered) {
            Gdx.app.debug("fractured!", "rebound in " + String.valueOf(TimeUtils.millis() - renderStart) + "ms");
            justRendered = false;
        }

        handleInput();

        Gdx.gl.glClearColor(0.f, 0.f, 0.f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Batch.setProjectionMatrix(camera.combined);
        Batch.begin();
        fractal.draw(Batch);
        logoSprite.draw(Batch);
        Batch.end();

        if (needsRender && !Gdx.input.isTouched()) {
            renderFractal();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        renderer.dispose();
        Batch.dispose();
        logoSprite.getTexture().dispose();
        fractal.getTexture().dispose();
    }


    private void renderFractal() {
        renderStart = TimeUtils.millis();

        Vector2 translationToAdd = new Vector2(0f, 0f);
        translationToAdd.x = -fractal.getX() / Gdx.graphics.getWidth();
        translationToAdd.y = -fractal.getY() / Gdx.graphics.getHeight();
        translationToAdd.y /= aspectRatio;
        renderer.addTranslation(translationToAdd);

        fractal.setPosition(0f, 0f);
        renderer.render();
        needsRender = false;
        justRendered = true;
    }

    private void handleInput() {
        float inDeltaX = gestureListener.getDeltaPanX(),
                inDeltaY = gestureListener.getDeltaPanY();

        if (inDeltaX != 0f && inDeltaY != 0f) {
            fractal.setPosition(fractal.getX() + inDeltaX, fractal.getY() - inDeltaY);
            needsRender = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            float step = renderer.getZoom() / sZoomSpeed;
            renderer.setZoom(renderer.getZoom() - step);
            needsRender = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            float step = renderer.getZoom() / sZoomSpeed;
            renderer.setZoom(renderer.getZoom() + step);
            needsRender = true;
        }

        float zoom = gestureListener.getZoom();
        if (zoom != 1f) {
            float step = renderer.getZoom() / sZoomSpeed;

            if (zoom > 1f) {
                zoom = -1f / zoom;
            } else {
                // do nothing
            }
            renderer.setZoom(renderer.getZoom() + zoom * step);
            needsRender = true;
        }
    }
}
