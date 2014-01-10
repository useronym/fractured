package com.entity.fractured;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class FracturedUI {
    private Fractured app;
    private Stage stage;
    private Skin skin;

    private SlideWindow options;
    private OptStatus optStatus;
    private Table optWrapper, optCurrent;

    // fractal options
    boolean optionsChanged = false;
    TextField parameterX, parameterY;
    Slider paramSliderX, paramSliderY;

    private enum OptStatus {
        FRACTAL, COLOR, MORE, UNKNOWN
    }


    FracturedUI(Fractured owner) {
        app = owner;
        stage = new Stage();

        if (Gdx.graphics.getDensity() > 1f) {
            skin = new Skin(Gdx.files.internal("ui/uiskin_large.json"));
            Gdx.app.log("fractured!", "using large ui skin");
        } else {
            skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
            Gdx.app.log("fractured!", "using standard ui skin");
        }

        createUI();
    }

    public void createUI() {
        createOptions();
    }

    public void destroyUI() {
        options.remove();
    }

    public void requestFractalOptionsUpdate() {
        Vector2 paramc = new Vector2(Float.parseFloat(parameterX.getText().replaceAll("[^\\d.]", "")),
                Float.parseFloat(parameterY.getText().replaceAll("[^\\d.]", "")));
        // to remove unwanted characters lite accidental letters in the text field
        parameterX.setText(Float.toString(paramc.x));
        parameterY.setText(Float.toString(paramc.y));
        app.getFractalRenderer().setParameter(paramc);

        optionsChanged = false;
    }

    private void createOptions() {
        optStatus = OptStatus.UNKNOWN;
        options = new SlideWindow("fractured! Options", skin);
        options.setHeight(Gdx.graphics.getHeight());
        options.setWidth(Gdx.graphics.getWidth() / 3f);
        options.setPosition((Gdx.graphics.getWidth() / 3f) * 2f, 0f);
        options.setModal(false);
        options.setKeepWithinStage(false);
        options.padTop(25f);
        options.top();

        // header
        Table header = new Table();
        header.pad(10f);
        options.add(header);

        TextButton headerFractal = new TextButton("Fractal", skin);
        headerFractal.padLeft(10f).padRight(10f);
        headerFractal.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (optStatus != OptStatus.FRACTAL) {
                    optCurrent.remove();
                    optCurrent = createOptionsFractal();
                    optWrapper.add(optCurrent).expand();
                    optStatus = OptStatus.FRACTAL;
                }
            }
        });
        header.add(headerFractal);

        TextButton headerColor = new TextButton("Color", skin);
        headerColor.padLeft(10f).padRight(10f);
        headerColor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (optStatus != OptStatus.COLOR) {
                    optCurrent.remove();
                    optCurrent = createOptionsColor();
                    optWrapper.add(optCurrent);
                    optStatus = OptStatus.COLOR;
                }
            }
        });
        header.add(headerColor);

        TextButton headerMore = new TextButton("More", skin);
        headerMore.padLeft(10f).padRight(10f);
        headerMore.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (optStatus != OptStatus.MORE) {
                    optCurrent.remove();
                    optCurrent = createOptionsMore();
                    optWrapper.add(optCurrent);
                    optStatus = OptStatus.MORE;
                }
            }
        });
        header.add(headerMore);
        options.row();

        // options wrapper table
        optWrapper = new Table();
        options.add(optWrapper).expand();

        // default to fractal options
        optCurrent = createOptionsFractal();
        optWrapper.add(optCurrent).expand();
        optStatus = OptStatus.FRACTAL;

        stage.addActor(options);
        options.debug();
    }

    private Table createOptionsFractal() {
        Table fractal = new Table();

        // type
        Table typeTable = new Table();
        fractal.add(typeTable);
        fractal.row();

        typeTable.add(new Label("Type", skin)).pad(5f);
        String[] types = {"z^2 + c", "z^3 + c", "exp(z^2) - c", "exp(z^3) - c"};
        SelectBox fractalType = new SelectBox(types, skin);
        typeTable.add(fractalType).pad(5f);

        // parameter
        Table paramsTable = new Table();paramsTable.debug();
        fractal.add(paramsTable);

        // holds parameter controls
        Table paramXTable = new Table();
        parameterX = new TextField(Float.toString(app.getFractalRenderer().getParameter().x), skin);
        paramXTable.add(parameterX).pad(5f).width(100f);
        paramXTable.row();
        TextButton randomX = new TextButton("random", skin);
        randomX.padLeft(10f).padRight(10f);
        randomX.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                float newVal = (float)Math.random();
                parameterX.setText(Float.toString(newVal));
                paramSliderX.setValue(newVal);
                optionsChanged = true;
            }
        });
        paramXTable.add(randomX).pad(5f);
        // holds slider and parameter controls, resides in paramsTable
        Table sliderXTable = new Table();
        paramSliderX = new Slider(0f, 1f, 0.01f, true, skin);
        paramSliderX.setValue(app.getFractalRenderer().getParameter().x);
        paramSliderX.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                float val = ((Slider)actor).getValue();
                parameterX.setText(Float.toString(val));
                optionsChanged = true;
            }
        });
        sliderXTable.add(paramSliderX);
        sliderXTable.add(paramXTable);
        paramsTable.add(sliderXTable);

        // holds parameter controls
        Table paramYTable = new Table();
        parameterY = new TextField(Float.toString(app.getFractalRenderer().getParameter().y), skin);
        paramYTable.add(parameterY).pad(5f).width(100f);
        paramYTable.row();
        TextButton randomY = new TextButton("random", skin);
        randomY.padLeft(10f).padRight(10f);
        randomY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                float newVal = (float)Math.random();
                parameterY.setText(Float.toString(newVal));
                paramSliderY.setValue(newVal);
                optionsChanged = true;
            }
        });
        paramYTable.add(randomY).pad(5f);
        // holds slider and parameter controls, resides in paramsTable
        Table sliderYTable = new Table();
        paramSliderY = new Slider(0f, 1f, 0.01f, true, skin);
        paramSliderY.setValue(app.getFractalRenderer().getParameter().y);
        paramSliderY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                float val = ((Slider)actor).getValue();
                parameterY.setText(Float.toString(val));
                optionsChanged = true;
            }
        });
        sliderYTable.add(paramSliderY);
        sliderYTable.add(paramYTable);
        paramsTable.add(sliderYTable);

        return fractal;
    }

    private Table createOptionsColor() {
        Table color = new Table();

        color.add(new Label("Color options here!", skin));

        return color;
    }

    private Table createOptionsMore() {
        Table more = new Table();

        more.add(new Label("Render quality", skin));
        String[] qualitySettings = {"200%", "100%", "50%", "25%"};
        more.add(new SelectBox(qualitySettings, skin)).pad(5f);

        return more;
    }

    public void draw(float delta) {
        if (optionsChanged && false) {
            app.renderFractal();
        }
        stage.act(delta);
        stage.draw();
        //Table.drawDebug(stage);
    }

    public void dispose() {
        destroyUI();
    }

    public void invalidate() {
        destroyUI();
        stage.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        createUI();
    }

    public BitmapFont getFont() {
        return skin.getFont("default-font");
    }

    public Stage getStage() {
        return stage;
    }
}
