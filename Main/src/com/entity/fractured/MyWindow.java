package com.entity.fractured;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;


public class MyWindow extends Window {

    public MyWindow(String title, Skin skin) {
        super(title, skin);

        removeListener(getListeners().first());

        addListener(new InputListener() {
            private boolean dragging = false;
            private float lastX;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 0) {
                    dragging = true;
                    lastX = x;
                    return true;
                }
                return false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (dragging) {
                    if (getX() >= Gdx.graphics.getWidth() - getWidth()) {
                        setPosition(getX() + (x - lastX), 0f);
                    } else {
                        setPosition(Gdx.graphics.getWidth() - getWidth(), 0f);
                        dragging = false;
                    }

                }
                lastX = x;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dragging = false;
            }
        });
    }
}
