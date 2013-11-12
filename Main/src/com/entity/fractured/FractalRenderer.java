package com.entity.fractured;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created with IntelliJ IDEA.
 * User: entity
 * Date: 11/11/13
 * Time: 10:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class FractalRenderer {
    private FrameBuffer fbo;
    private Mesh planeMesh;
    private ShaderProgram shader = null;
    private boolean ready = false;

    // fractal uniforms
    private float fractalTransX = 0f, fractalTransY = 0f;
    private float fractalCX = 0.33f, fractalCY = 0.4f;


    FractalRenderer(int sx, int sy) {
        fbo = new FrameBuffer(Pixmap.Format.RGB888, sx, sy, false);

        float aspectRatio = sy / (float) sx;
        planeMesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
        planeMesh.setVertices(new float[]
                        {-1, -1, 0, 0, 1*aspectRatio,
                        1, -1, 0, 1, 1*aspectRatio,
                        1, 1, 0, 1, 0,
                        -1, 1, 0, 0, 0});
        planeMesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});
    }

    public void loadShader(String vertex, String fragment) {
        unloadShader();

        String vSource, fSource;
        vSource = Gdx.files.internal(vertex).readString();
        fSource = Gdx.files.internal(fragment).readString();
        shader = new ShaderProgram(vSource, fSource);

        if (!shader.isCompiled()) {
            Gdx.app.log("Shader compile error", shader.getLog());
            ready = false;
        } else {
            ready = true;
        }
    }

    public void unloadShader() {
        if (shader != null) {
            shader.dispose();
            shader = null;
        }
    }

    public void render() {
        fbo.begin();
        shader.begin();

        shader.setUniformf("u_c", fractalCX, fractalCY);
        shader.setUniformf("u_translation", fractalTransX, fractalTransY);

        planeMesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
        fbo.end();
    }

    public void setTranslation(float tx, float ty) {
        fractalTransX = tx;
        fractalTransY = ty;
    }

    public void setC(float cx, float cy) {
        fractalCX = cx;
        fractalCY = cy;
    }

    public Texture getTexture() {
        if (ready) {
            return fbo.getColorBufferTexture();
        } else {
            return null;
        }
    }

    public float getTranslationX() {
        return fractalTransX;
    }

    public float getTranslationY() {
        return fractalTransY;
    }
}
