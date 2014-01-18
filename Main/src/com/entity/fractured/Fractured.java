package com.entity.fractured;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;


public class Fractured extends Game {
    FracturedUI ui = null;
    InputMultiplexer inputMultiplexer;
    private FracturedGestureListener gestureListener = null;
    private FractalRenderer renderer = null; // class which handles rendering of the fractal to a texture
    private FractalRenderer previewRenderer = null;
    private boolean needsRender = false;
    private boolean justRendered = false;
    private int renderRequestFrames = -1;
    private long renderStart;

    private Sprite fractalSprite; // sprite which renders the screen quad with the rendered fractal texture
    private OrthographicCamera camera;
    private SpriteBatch Batch;

    // settings
    FracturedSettings settings;


    @Override
    public void create() {
        settings = new FracturedSettings();

        if (settings.debugLogging) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        }

        inputMultiplexer = new InputMultiplexer();
        gestureListener = new FracturedGestureListener();
        Gdx.input.setInputProcessor(inputMultiplexer);

        camera = new OrthographicCamera();

        Batch = new SpriteBatch();

        fractalSprite = new Sprite();
    }

    @Override
    public void resize(int width, int height) {
        if (width == settings.width && height == settings.height) return;

        settings.updateDisplaySettings();

        camera.setToOrtho(false, width, height);

        if (ui != null) {
            ui.dispose();
        }

        createRenderer();

        createPreviewRenderer();

        ui = new FracturedUI(this);
        ui.setBusy(true);
        inputMultiplexer.clear();
        inputMultiplexer.addProcessor(ui.getStage());
        inputMultiplexer.addProcessor(new GestureDetector(gestureListener));

        fractalSprite.setTexture(renderer.getTexture(true));
        fractalSprite.setRegion(0f, 0f, 1f, 1f);
        fractalSprite.setSize(width, height);
        fractalSprite.setOrigin(width / 2f, height / 2f);

        needsRender = false;
        justRendered = false;

        // request render in 10 frames
        requestRender(10);
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

        if (renderRequestFrames >= 0) {
            if (renderRequestFrames == 0) {
                needsRender = true;
            }

            renderRequestFrames--;
        }

        if (needsRender)
            ui.setBusy(true);

        ui.draw(Gdx.graphics.getDeltaTime());

        if (needsRender) {
            if (! Gdx.input.isTouched() && !Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
                renderFractal();
                needsRender = false;
                justRendered = true;
                ui.setBusy(false);
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

    // request render in n frames
    public void requestRender(int n) {
        renderRequestFrames = n;
    }

    public void requestRender() {
        requestRender(0);
    }

    public void requestPreviewRender() {
        ui.requestFractalOptionsUpdate();
        previewRenderer.copyFrom(renderer);
        previewRenderer.render();
        fractalSprite.setTexture(previewRenderer.getTexture());
    }

    public void createRenderer() {
        float quality = settings.renderQualities[settings.renderSetting];
        FractalRenderer newRenderer = new FractalRenderer((int) (settings.width/quality),
                (int) (settings.height/quality));

        if (renderer != null) {
            newRenderer.copyFrom(renderer);
            renderer.dispose();
            renderer = newRenderer;
        } else {
            renderer = newRenderer;
            renderer.loadShader(settings.fractalTypes[settings.fractalType]);
            renderer.loadGradient(settings.fractalColors[settings.fractalColor]);
        }
    }

    public void createPreviewRenderer() {
        if(previewRenderer != null) {
            previewRenderer.dispose();
        }

        float quality = settings.renderQualities[settings.previewSetting];
        previewRenderer = new FractalRenderer((int) (settings.width/quality),
                (int) (settings.height/quality));
    }

    public FractalRenderer getFractalRenderer() {
        return renderer;
    }

    private void renderFractal() {
        renderStart = TimeUtils.millis();

        Vector2 translationDelta = new Vector2(0f, 0f);
        translationDelta.x = -fractalSprite.getX() / Gdx.graphics.getWidth();
        translationDelta.y = -fractalSprite.getY() / Gdx.graphics.getHeight();
        translationDelta.y /= settings.aspectRatio;
        renderer.addTranslation(translationDelta);

        float zoomDelta = (1f / fractalSprite.getScaleX()) - 1f;
        renderer.addZoom(zoomDelta * renderer.getZoom());

        renderer.render();

        // reset the screen quad sprite
        fractalSprite.setTexture(renderer.getTexture());
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
            fractalSprite.setScale(spriteScale - zoomDelta * settings.zoomSpeed * spriteScale);

            requestRender();
            return;
        }

        // translation
        float inDeltaX = gestureListener.getDeltaPanX(),
                inDeltaY = -gestureListener.getDeltaPanY();

        if (inDeltaX != 0f || inDeltaY != 0f) {
            fractalSprite.setPosition(fractalSprite.getX() + inDeltaX, fractalSprite.getY() + inDeltaY);

            requestRender();
        }
    }
}
