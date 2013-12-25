package com.entity.fractured;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class FracturedUI {
    private Fractured app;
    private Stage stage;
    private Table table;
    private BitmapFont font;


    FracturedUI(Fractured owner) {
        app = owner;
        stage = new Stage();
        table = new Table();
        table.setFillParent(true);
        table.left();
        stage.addActor(table);
        font = new BitmapFont(Gdx.files.internal("arial-15.fnt"), Gdx.files.internal("arial-15.png"), false);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.ORANGE;
        TextButton testBtn = new TextButton("Randomize!", style);
        testBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                app.getFractalRenderer().setParameter(new Vector2((float)Math.random(), (float)Math.random()));
                app.renderFractal();
            }
        });
        table.add(testBtn);
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
