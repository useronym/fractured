package com.entity.fractured;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;


public class FractalRenderer {
    private FrameBuffer fbo;
    private Mesh planeMesh;
    private ShaderProgram shader = null;
    private boolean ready = false;
    private boolean hasRendered = false;

    private String vertexPath = "default.vert";
    private String fragmentPath;
    private String gradientPath;

    // fractal define :)
    private int iterations = 100;
    // fractal uniforms
    private Vector2 translation = new Vector2(0f, 0f);
    private Vector2 parameter = new Vector2(0.33f, 0.4f);
    private float zoom = 4f;
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

    public void copyFrom(FractalRenderer other) {
        boolean needsReload = false;
        if (! other.fragmentPath.equals(fragmentPath) || iterations != other.iterations) {
            needsReload = true;
        }

        vertexPath = other.vertexPath;
        fragmentPath = other.fragmentPath;
        iterations = other.iterations;
        translation = other.getTranslation();
        parameter = other.getParameter();
        zoom = other.getZoom();

        if (needsReload) {
            loadShader(fragmentPath);
        }

        if (! other.gradientPath.equals(gradientPath)) {
            loadGradient(other.getGradientPath());
        }
    }

    public boolean loadShader(String fragment) {
        return loadShader(vertexPath, fragment, iterations);
    }

    public boolean loadShader(String vertex, String fragment, int iter) {
        Gdx.app.debug("fractured!", "loading shader " + fragment);

        unloadShader();

        vertexPath = vertex;
        fragmentPath = fragment;

        String vSource, fSource;
        vSource = Gdx.files.internal(vertex).readString();
        fSource = Gdx.files.internal(fragment).readString();
        fSource = fSource.replace("#define ITER", "#define ITER " + Integer.toString(iter) + "//");
        shader = new ShaderProgram(vSource, fSource);

        if (! shader.isCompiled()) {
            Gdx.app.error("fractured!", shader.getLog());
            ready = false;
            return false;
        } else {
            ready = true;
            return true;
        }
    }

    public void unloadShader() {
        if (shader != null) {
            shader.dispose();
            shader = null;
        }
    }

    public void setIterations(int n) {
        if (iterations != n) {
            iterations = n;
            loadShader(fragmentPath);
        }
    }

    public int getIterations() {
        return iterations;
    }

    private void setGradient(Texture newgradient) {
        gradient = newgradient;
    }

    public void loadGradient(String filename) {
        Gdx.app.debug("fractured!", "loading gradient " + filename);
        unloadGradient();

        gradientPath = filename;
        setGradient(new Texture(Gdx.files.internal(filename)));
    }

    public void unloadGradient() {
        if (gradient != null) {
            gradient.dispose();
            gradient = null;
        }
    }

    public void dispose() {
        unloadShader();
        fbo.dispose();
    }

    public void render() {
        long startTime = TimeUtils.millis();

        fbo.begin();
        shader.begin();

        setUniforms();

        planeMesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
        fbo.end();

        Gdx.app.debug("fractured!", "rendered fractal in " + String.valueOf(TimeUtils.millis() - startTime) + "ms");
        hasRendered = true;
    }

    public Pixmap createScreenshot() {
        if (! isReady()) {
            return null;
        }

        Pixmap screenMap = new Pixmap(fbo.getWidth(), fbo.getHeight(), Pixmap.Format.RGBA8888);

        fbo.begin();
        shader.begin();

        setUniforms();

        planeMesh.render(shader, GL20.GL_TRIANGLES);

        Gdx.gl.glReadPixels(0, 0, fbo.getWidth(), fbo.getHeight(), GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, screenMap.getPixels());

        shader.end();
        fbo.end();

        return screenMap;
    }

    private void setUniforms() {
        shader.setUniformf("u_c", parameter);
        shader.setUniformf("u_translation", translation);
        shader.setUniformf("u_zoom", zoom);
        shader.setUniformf("u_aspectratio", aspectRatio);
        if (gradient != null) {
            shader.setUniformi("gradient", 0);
            gradient.bind(0);
        }
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
        Vector2 addT = new Vector2(getTranslation()).scl((zoom - z) * (1f/z));
        addTranslation(addT);

        zoom = z;
    }

    public void addZoom(float delta) {
        setZoom(getZoom() + delta);
    }

    public boolean isReady() {
        return ready;
    }

    public String toString() {
        String str = "Param: " + parameter.toString();
        str += " Trans: " + translation.toString();
        str += " Zoom: " + zoom;

        return str;
    }

    // passing true results in obtaining the texture even if it's all black(hasn't been rendered yet)
    public Texture getTexture(boolean force) {
        if (ready && (hasRendered || force)) {
            return fbo.getColorBufferTexture();
        } else {
            return null;
        }
    }

    public Texture getTexture() {
        return getTexture(false);
    }

    public String getVertexPath() {
        return vertexPath;
    }

    public String getFragmentPath() {
        return fragmentPath;
    }

    public String getGradientPath() {
        return gradientPath;
    }

    public Vector2 getParameter() {
        return parameter;
    }

    public Vector2 getTranslation() {
        return translation;
    }

    public float getZoom() {
        return zoom;
    }
}
