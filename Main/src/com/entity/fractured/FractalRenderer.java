package com.entity.fractured;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;


public class FractalRenderer {
    private FrameBuffer fbo;
    private Mesh planeMesh;
    private ShaderProgram shader = null;
    private boolean ready = false;

    // fractal uniforms
    private Vector2 translation = new Vector2(0f, 0f);
    private Vector2 parameter = new Vector2(0.33f, 0.4f);
    private float zoom = 1f;
    private float aspectRatio = 1f;
    private Texture gradient = null;


    FractalRenderer(int sx, int sy) {
        fbo = new FrameBuffer(Pixmap.Format.RGB888, sx, sy, false);

        aspectRatio = sy / (float) sx;
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
            Gdx.app.error("fractured!", shader.getLog());
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

    public void setGradient(Texture newgradient) {
        gradient = newgradient;
    }

    public void dispose() {
        unloadShader();
        fbo.dispose();
    }

    public void render() {
        long startTime = TimeUtils.millis();

        fbo.begin();
        shader.begin();

        shader.setUniformf("u_c", parameter);
        shader.setUniformf("u_translation", translation);
        shader.setUniformf("u_zoom", zoom);
        shader.setUniformf("u_aspectratio", aspectRatio);
        if (gradient != null) {
            shader.setUniformi("gradient", 0);
            gradient.bind(0);
        }

        planeMesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
        fbo.end();

        Gdx.app.debug("fractured!", "rendered fractal in " + String.valueOf(TimeUtils.millis() - startTime) + "ms");
    }

    public void setTranslation(Vector2 nt) {
        translation = nt;
    }

    public void addTranslation(Vector2 at) {
        Vector2 newT = new Vector2(translation).add(at);
        setTranslation(newT);
    }

    public void setParameter(Vector2 np) {
        parameter = np;
    }

    public void setZoom(float z) {
        Vector2 addT = new Vector2(getTranslation()).mul((zoom - z) * (1f/z));
        addTranslation(addT);

        zoom = z;
    }

    public void addZoom(float delta) {
        setZoom(getZoom() + delta);
    }

    public String toString() {
        String str = "Param: " + parameter.toString();
        str += " Trans: " + translation.toString();
        str += " Zoom: " + zoom;

        return str;
    }

    public Texture getTexture() {
        if (ready) {
            return fbo.getColorBufferTexture();
        } else {
            return null;
        }
    }

    public Vector2 getTranslation() {
        return translation;
    }

    public float getZoom() {
        return zoom;
    }
}
