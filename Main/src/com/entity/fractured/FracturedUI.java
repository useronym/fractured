package com.entity.fractured;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
        stage.addActor(table);
        font = new BitmapFont(Gdx.files.internal("arial-15.fnt"), Gdx.files.internal("arial-15.png"), false);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.ORANGE;
        TextButton testBtn = new TextButton("Touch me!", style);
        testBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Gdx.app.debug("fractured!", "something happened");
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
