package com.entity.fractured;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;


public class SlideWindow extends Window {
    private float borderSize;

    private boolean dragging = false, sliding = false;
    private float speedX = 0f;

    private Sprite dragImage = null;

    public SlideWindow(String title, Skin skin, float border, String dragger) {
        super(title, skin);
        borderSize = border;

        getButtonTable().remove();

        dragImage = new Sprite(new Texture(Gdx.files.internal(dragger)));

        removeListener(getListeners().first());

        addListener(new InputListener() {
            private float touchDownX;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 0 && SlideWindow.this.hit(x, y, true) == SlideWindow.this) {
                    dragging = true;
                    touchDownX = x;

                    return true;
                }
                return false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (dragging) {
                    speedX = x - touchDownX;
                    setPosition(getX() + speedX, 0f);

                    checkWindowPosition();
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (dragging) {
                    dragging = false;

                    if (Math.abs(speedX) > 1.1f) {
                        speedX *= 2.5f;
                        sliding = true;
                    }
                }
            }
        });
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        if (sliding) {
            speedX *= 0.96f;

            setPosition(getX() + speedX, 0f);
            checkWindowPosition();

            if (Math.abs(speedX) < 0.1f) {
                sliding = false;
            }
        }

        super.draw(batch, parentAlpha);

        dragImage.setPosition(getX(), dragImage.getY());
        dragImage.draw(batch);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);

        if (dragImage != null) {
            dragImage.setPosition(getX(), getHeight() / 2f - dragImage.getHeight() / 2f);
        }
    }

    public void dispose() {
        dragImage.getTexture().dispose();

        remove();
    }

    private void checkWindowPosition() {
        if (getX() < Gdx.graphics.getWidth() - getWidth()) {
            setPosition(Gdx.graphics.getWidth() - getWidth(), 0f);
            dragging = false;
            sliding = false;
        }
        else if (getX() >= Gdx.graphics.getWidth() - borderSize) {
            setPosition(Gdx.graphics.getWidth() - borderSize, 0f);
            dragging = false;
            sliding = false;
        }
    }
}
