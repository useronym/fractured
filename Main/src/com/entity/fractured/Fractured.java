package com.entity.fractured;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.input.GestureDetector;
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

    private Sprite fractal;
    private OrthographicCamera camera;
    private Sprite logoSprite;
    private SpriteBatch Batch;
    private float timeMs = 0;

    @Override
    public void create() {
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
        renderer.loadShader("default.vert", "julia_time.frag");
        renderer.render();
        fractal = new Sprite(renderer.getTexture());
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        timeMs = timeMs + Gdx.graphics.getDeltaTime();

        /*renderer.setTranslation(renderer.getTranslationX() - gestureListener.getDeltaTapX()/800f,
                renderer.getTranslationY() + gestureListener.getDeltaTapY()/480f);
        renderer.render();*/
        fractal.setPosition(fractal.getX() + gestureListener.getDeltaTapX(),
                fractal.getY() - gestureListener.getDeltaTapY());

        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Batch.setProjectionMatrix(camera.combined);
        Batch.begin();
        fractal.draw(Batch);
        logoSprite.draw(Batch);
        Batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
