package com.entity.fractured;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;


public class Fractured extends Game {
    FracturedUI ui;
    InputMultiplexer inputMultiplexer;
    private FracturedGestureListener gestureListener = null;
    private FractalRenderer renderer = null; // class which handles rendering of the fractal to a texture
    private boolean needsRender = false;
    private boolean justRendered = false;
    private long renderStart;

    private Sprite fractalSprite; // sprite which renders the screen quad with the rendered fractal texture
    private OrthographicCamera camera;
    private SpriteBatch Batch;

    private float aspectRatio = 1f;

    // settings
    private final boolean debugMode = true;
    private final float sQuality = 1f; // 0.5 is 200%, 1 is 100%; 2 is 50% etc...
    private final float sZoomSpeed = 0.5f;


    @Override
    public void create() {
        if (debugMode) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        }

        aspectRatio = Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();

        renderer = new FractalRenderer((int) (Gdx.graphics.getWidth()/sQuality), (int) (Gdx.graphics.getHeight()/sQuality));

        ui = new FracturedUI(this);

        inputMultiplexer = new InputMultiplexer();
        gestureListener = new FracturedGestureListener();
        inputMultiplexer.addProcessor(ui.getStage());
        inputMultiplexer.addProcessor(new GestureDetector(gestureListener));
        Gdx.input.setInputProcessor(inputMultiplexer);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Batch = new SpriteBatch();

        fractalSprite = new Sprite();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        aspectRatio = width / (float) height;
        ui.invalidate();

        if (renderer != null) {
            renderer.dispose();
        }

        renderer = new FractalRenderer((int) (width/sQuality), (int) (height/sQuality));
        renderer.loadShader("fractals/julia_z3.frag");
        renderer.setGradient(new Texture(Gdx.files.internal("gradients/full_spectrum.png")));

        fractalSprite.setTexture(renderer.getTexture());
        fractalSprite.setRegion(0f, 0f, 1f, 1f);
        fractalSprite.setSize(width, height);
        fractalSprite.setOrigin(width / 2f, height / 2f);

        needsRender = true;
        justRendered = false;
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

        ui.draw(Gdx.graphics.getDeltaTime());

        if (needsRender) {
            if (!Gdx.input.isTouched() && !Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
                renderFractal();
                needsRender = false;
                justRendered = true;
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
        ui.dispose();
    }

    public FractalRenderer getFractalRenderer() {
        return renderer;
    }

    public void renderFractal() {
        renderStart = TimeUtils.millis();

        ui.requestFractalOptionsUpdate();

        Vector2 translationDelta = new Vector2(0f, 0f);
        translationDelta.x = -fractalSprite.getX() / Gdx.graphics.getWidth();
        translationDelta.y = -fractalSprite.getY() / Gdx.graphics.getHeight();
        translationDelta.y /= aspectRatio;
        renderer.addTranslation(translationDelta);

        float zoomDelta = (1f / fractalSprite.getScaleX()) - 1f;
        renderer.addZoom(zoomDelta * renderer.getZoom());

        renderer.render();

        // reset the screen quad sprite
        fractalSprite.setPosition(0f, 0f);
        fractalSprite.setScale(1f);
    }


    private void handleInput() {
        // zoom
        float zoomDelta = (gestureListener.getZoom() - 1f) / 10f;

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            zoomDelta -= 0.02f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            zoomDelta += 0.02f;
        }

        if (zoomDelta != 0f) {
            float spriteScale = fractalSprite.getScaleX();
            fractalSprite.setScale(spriteScale - zoomDelta * sZoomSpeed * spriteScale);

            needsRender = true;
            return;
        }

        // translation
        float inDeltaX = gestureListener.getDeltaPanX(),
                inDeltaY = gestureListener.getDeltaPanY();

        if (inDeltaX != 0f || inDeltaY != 0f) {
            fractalSprite.setPosition(fractalSprite.getX() + inDeltaX, fractalSprite.getY() - inDeltaY);
            needsRender = true;
        }
    }
}
