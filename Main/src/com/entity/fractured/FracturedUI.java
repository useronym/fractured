package com.entity.fractured;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

    private float padding = 5f;

    // fractal options
    boolean optionsChanged = false;
    SelectBox fractalType;
    TextField parameterX, parameterY;
    Slider paramSliderX, paramSliderY;
    List colorSelector;

    private enum OptStatus {
        FRACTAL, COLOR, MORE, UNKNOWN
    }


    FracturedUI(Fractured owner) {
        app = owner;
        stage = new Stage();

        if (Gdx.graphics.getDensity() > 1f) {
            skin = new Skin(Gdx.files.internal("ui/uiskin_large.json"));
            padding = 15f;
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
        // type
        if (! app.getFractalRenderer().getFragmentPath().equals(app.settings.fractalTypes[app.settings.fractalType])) {
            app.getFractalRenderer().loadShader(app.settings.fractalTypes[app.settings.fractalType]);
        }

        // color
        if (! app.getFractalRenderer().getGradientPath().equals(app.settings.fractalColors[app.settings.fractalColor])) {
            app.getFractalRenderer().loadGradient((app.settings.fractalColors[app.settings.fractalColor]));
        }

        // parameter
        Vector2 paramc = new Vector2(Float.parseFloat(parameterX.getText().replaceAll("[^\\d.]", "")),
                Float.parseFloat(parameterY.getText().replaceAll("[^\\d.]", "")));
        // to remove unwanted characters like accidental letters in the text field
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
        //options.padTop(25f);
        options.top();

        // header
        Table header = new Table();
        header.pad(padding);
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
        header.add(headerFractal).pad(padding);

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
        header.add(headerColor).pad(padding);

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
        header.add(headerMore).pad(padding);
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

        typeTable.add(new Label("Type", skin)).pad(padding);
        String[] types = {"z^2 + c", "z^3 + c", "exp(z^2) - c", "exp(z^3) - c"};
        fractalType = new SelectBox(types, skin);
        fractalType.setSelection(app.settings.fractalType);
        fractalType.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                int newSelection = ((SelectBox)actor).getSelectionIndex();
                if (app.settings.fractalType != newSelection) {
                    app.settings.fractalType = newSelection;
                    optionsChanged = true;
                }
                optionsChanged = true;
            }
        });
        typeTable.add(fractalType).pad(padding);

        // parameter
        Table paramsTable = new Table();paramsTable.debug();
        fractal.add(paramsTable);

        // holds parameter controls
        Table paramXTable = new Table();
        parameterX = new TextField(Float.toString(app.getFractalRenderer().getParameter().x), skin);
        paramXTable.add(parameterX).pad(padding).width(100f);
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
        paramXTable.add(randomX).pad(padding);
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
        paramYTable.add(parameterY).pad(padding).width(100f);
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
        paramYTable.add(randomY).pad(padding);
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

        String[] colorNames = new String[app.settings.fractalColors.length];
        for(int i = 0; i < app.settings.fractalColors.length; i++) {
            colorNames[i] = app.settings.fractalColors[i].replace("gradients/", "");
        }
        colorSelector = new List(colorNames, skin);
        colorSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                int newSelection = ((List)actor).getSelectedIndex();
                if (app.settings.fractalColor != newSelection) {
                    app.settings.fractalColor = newSelection;
                    optionsChanged = true;
                }
            }
        });
        color.add(new ScrollPane(colorSelector, skin));

        return color;
    }

    private Table createOptionsMore() {
        Table more = new Table();

        more.add(new Label("Render quality", skin));
        String[] qualitySettings = {"200%", "100%", "50%", "25%"};
        SelectBox qualityBox = new SelectBox(qualitySettings, skin);
        more.add(qualityBox).pad(padding);
        more.row();

        more.add(new Label("Preview quality", skin));
        String[] previewQualitySettings = {"100%", "50%", "25%", "12.5%"};
        SelectBox previewBox = new SelectBox(previewQualitySettings, skin);
        more.add(previewBox).pad(padding);


        return more;
    }

    public void draw(float delta) {
        stage.act(delta);
        stage.draw();
        //Table.drawDebug(stage);

        if (optionsChanged) {
            app.requestPreviewRender();
        }
    }

    public void dispose() {
        destroyUI();
        stage.dispose();
    }

    public BitmapFont getFont() {
        return skin.getFont("default-font");
    }

    public Stage getStage() {
        return stage;
    }
}
