package com.entity.fractured;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class FracturedUI {
    private Fractured app;
    private Stage stage;
    private Table table;
    private Skin skin;
    private BitmapFont font;


    FracturedUI(Fractured owner) {
        app = owner;
        stage = new Stage();
        table = new Table();
        table.debug();
        table.setFillParent(true);
        table.left();
        stage.addActor(table);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton testBtn = new TextButton("Randomize", skin);
        testBtn.padLeft(10f).padRight(10f);
        testBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                app.getFractalRenderer().setParameter(new Vector2((float)Math.random(), (float)Math.random()));
                app.renderFractal();
            }
        });
        table.add(testBtn).pad(5f);
        table.row();

        TextButton saveBtn = new TextButton("Save", skin);
        saveBtn.padLeft(10f).padRight(10f);
        table.add(saveBtn).pad(5f);
    }

    void draw(float delta) {
        stage.act(delta);
        stage.draw();
    }

    BitmapFont getFont() {
        return font;
    }

    Stage getStage() {
        return stage;
    }
}
