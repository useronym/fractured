package com.entity.fractured;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created with IntelliJ IDEA.
 * User: entity
 * Date: 11/2/13
 * Time: 10:33 PM
 * To change this template use File | Settings | File Templates.
 */

public class Fractured extends Game {
    private OrthographicCamera camera;
    private Sprite logoSprite;
    private SpriteBatch Batch;
    private ShaderProgram testShader;
    private Mesh testMesh;
    private float timeMs = 0;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800f, 480f);

        Texture logoTex = new Texture(Gdx.files.internal("gdxlogo.png"));
        logoSprite = new Sprite(logoTex);
        logoSprite.setPosition(250, 215);
        Batch = new SpriteBatch();

        String vSource, fSource;
        vSource = Gdx.files.internal("shaders/default.vert").readString();
        fSource = Gdx.files.internal("shaders/default.frag").readString();
        testShader = new ShaderProgram(vSource, fSource);
        if (!testShader.isCompiled()) {
            System.out.print(testShader.getLog());
            Gdx.app.exit();
        }

        testMesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
        testMesh.setVertices(new float[]
                {-1, -1, 0, 0, 1,
                 1, -1, 0, 1, 1,
                 1, 1, 0, 1, 0,
                 -1, 1, 0, 0, 0});
        testMesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        timeMs += Gdx.graphics.getDeltaTime();

        logoSprite.setRotation((float) (logoSprite.getRotation()+0.25));
        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        testShader.begin();
        testShader.setUniformf("time", timeMs);
        testMesh.render(testShader, GL20.GL_TRIANGLES);
        testShader.end();

        Batch.setProjectionMatrix(camera.combined);
        Batch.begin();
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
