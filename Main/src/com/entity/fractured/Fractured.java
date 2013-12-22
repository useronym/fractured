package com.entity.fractured;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;


public class Fractured extends Game {
    private FracturedGestureListener gestureListener = null;
    private FractalRenderer renderer = null; // class which handles rendering of the fractal to a texture
    private boolean needsRender;
    private boolean justRendered;
    private long renderStart;

    private Sprite fractalSprite; // sprite which renders the screen quad with the rendered fractal texture
    private OrthographicCamera camera;
    private SpriteBatch Batch;

    private float aspectRatio = 1f;

    // settings
    private final float sZoomSpeed = 0.5f;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        aspectRatio = Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        gestureListener = new FracturedGestureListener();
        Gdx.input.setInputProcessor(new GestureDetector(gestureListener));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Batch = new SpriteBatch();

        renderer = new FractalRenderer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.loadShader("default.vert", "julia.frag");
        needsRender = true;
        justRendered = false;
        fractalSprite = new Sprite(renderer.getTexture());
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
        fractalSprite.draw(Batch);
        Batch.end();

        if (needsRender) {
            if (!Gdx.input.isTouched() && !Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
                renderFractal();
            }
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
        fractalSprite.getTexture().dispose();
    }


    private void renderFractal() {
        renderStart = TimeUtils.millis();

        Vector2 translationDelta = new Vector2(0f, 0f);
        translationDelta.x = -fractalSprite.getX() / Gdx.graphics.getWidth();
        translationDelta.y = -fractalSprite.getY() / Gdx.graphics.getHeight();
        translationDelta.y /= aspectRatio;
        renderer.addTranslation(translationDelta);

        float zoomDelta = (1f / fractalSprite.getScaleX()) - 1f;
        renderer.addZoom(zoomDelta);

        // reset the screen quad sprite
        fractalSprite.setPosition(0f, 0f);
        fractalSprite.setScale(1f);

        renderer.render();
        needsRender = false;
        justRendered = true;
    }

    private void handleInput() {
        // translation
        float inDeltaX = gestureListener.getDeltaPanX(),
                inDeltaY = gestureListener.getDeltaPanY();

        if (inDeltaX != 0f || inDeltaY != 0f) {
            fractalSprite.setPosition(fractalSprite.getX() + inDeltaX, fractalSprite.getY() - inDeltaY);
            needsRender = true;
        }


        // zoom
        float zoomDelta = (gestureListener.getZoom() - 1f);

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            zoomDelta -= 0.01f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            zoomDelta += 0.01f;
        }

        if (zoomDelta != 0f) {
            float step = renderer.getZoom() * sZoomSpeed;

            fractalSprite.setScale(fractalSprite.getScaleX() - fractalSprite.getScaleX() * zoomDelta * step);
            /*renderer.addZoom(zoomDelta * step);
            fractalSprite.setScale(1f / renderer.getZoom());*/

            needsRender = true;
        }
    }
}
