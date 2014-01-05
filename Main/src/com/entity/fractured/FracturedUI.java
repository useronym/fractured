package com.entity.fractured;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class FracturedUI {
    private Fractured app;
    private Stage stage;
    //private Table table;
    private Skin skin;

    private Window options;
    private Table fractal, color, more;


    FracturedUI(Fractured owner) {
        app = owner;
        stage = new Stage();
        /*table = new Table();
        table.setFillParent(true);
        stage.addActor(table);*/

        skin = new Skin();
        BitmapFont defaultFont;
        if (Gdx.graphics.getDensity() > 1f) {
            defaultFont = new BitmapFont(Gdx.files.internal("ui/monospace-22.fnt"));
        } else {
            defaultFont = new BitmapFont(Gdx.files.internal("ui/monospace-15.fnt"));
        }
        skin.add("default-font", defaultFont);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
        skin.load(Gdx.files.internal("ui/uiskin.json"));

        createUI();
    }

    public void createUI() {
        createOptions();

        //table.add(options);

        /*TextButton dots = new TextButton("...", skin);
        dots.padLeft(8f).padRight(8f);
        table.right().bottom().add(dots).row();

        TextButton testBtn = new TextButton("Randomize", skin);
        testBtn.padLeft(10f).padRight(10f);
        testBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                app.getFractalRenderer().setParameter(new Vector2((float)Math.random(), (float)Math.random()));
                app.renderFractal();
            }
        });
        table.left().add(testBtn).pad(5f);
        table.row();

        TextButton saveBtn = new TextButton("Save", skin);
        saveBtn.padLeft(10f).padRight(10f);
        table.add(saveBtn).pad(5f);*/
    }

    public void destroyUI() {
        options.remove();
    }

    public void createOptions() {
        options = new Window("Options", skin);
        options.removeListener(options.getListeners().first());
        options.setHeight(Gdx.graphics.getHeight() + 20);
        options.setWidth(Gdx.graphics.getWidth() / 3);
        options.setTitle("");
        options.setModal(false);
        options.setKeepWithinStage(false);
        options.padTop(25f);
        options.top();

        Table header = new Table();
        header.pad(10f);
        options.add(header);
        TextButton headerFractal = new TextButton("Fractal", skin);
        headerFractal.padLeft(10f).padRight(10f);
        headerFractal.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                color.setVisible(false);
                more.setVisible(false);
                fractal.setVisible(true);
            }
        });
        header.add(headerFractal);
        TextButton headerColor = new TextButton("Color", skin);
        headerColor.padLeft(10f).padRight(10f);
        headerColor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                color.setVisible(true);
                more.setVisible(false);
                fractal.setVisible(false);
            }
        });
        header.add(headerColor);
        TextButton headerMore = new TextButton("More", skin);
        headerMore.padLeft(10f).padRight(10f);
        header.add(headerMore);
        options.row();

        // fractal options
        fractal = new Table();
        options.add(fractal).expand();

        TextField paramXText = new TextField(Float.toString(app.getFractalRenderer().getParameter().x), skin);
        fractal.add(paramXText);
        TextField paramYText = new TextField(Float.toString(app.getFractalRenderer().getParameter().y), skin);
        fractal.add(paramYText);

        fractal.row();
        fractal.add(new Slider(0f, 1f, 0.01f, false, skin));
        fractal.add(new Slider(0f, 1f, 0.01f, false, skin));
        fractal.row();
        fractal.add(new TextButton("random", skin).padLeft(10f).padRight(10f));
        fractal.add(new TextButton("random", skin).padLeft(10f).padRight(10f));
        fractal.row();

        // color options
        color = new Table();
        options.add(color).expand();
        color.setVisible(false);

        // more options
        more = new Table();
        options.add(more).expand();
        more.setVisible(false);

        stage.addActor(options);
        options.debug();
    }

    public void draw(float delta) {
        stage.act(delta);
        stage.draw();
        Window.drawDebug(stage);
    }

    public void dispose() {
        destroyUI();
    }

    public void invalidate() {
        stage.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        options.invalidateHierarchy();
    }

    public BitmapFont getFont() {
        return skin.getFont("default-font");
    }

    public Stage getStage() {
        return stage;
    }
}
