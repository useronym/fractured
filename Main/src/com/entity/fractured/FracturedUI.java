package com.entity.fractured;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class FracturedUI {
    private Fractured app;
    private Stage stage;
    private Skin skin;

    private SlideWindow options;
    private OptStatus optStatus;
    private Table optWrapper, optCurrent;

    private float padding = 5f;

    private Label userMessage = null;
    private float currentMessageTime;

    // fractal options
    boolean optionsChanged = false;
    SelectBox fractalType;
    TextField parameterX, parameterY;
    Slider paramSliderX, paramSliderY;
    List colorSelector;

    private final float timeForMessages = 10f;

    private enum OptStatus {
        FRACTAL, COLOR, MORE, UNKNOWN
    }


    FracturedUI(Fractured owner) {
        app = owner;
        stage = new Stage();

        Gdx.app.debug("fractured!", "display density ratio: " + String.valueOf(Gdx.graphics.getDensity()));
        if (Gdx.graphics.getDensity() > 1f) {
            skin = new Skin(Gdx.files.internal("ui/uiskin_large.json"));
            padding = 10f;
            Gdx.app.debug("fractured!", "using large ui skin");
        } else {
            skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
            Gdx.app.debug("fractured!", "using standard ui skin");
        }

        createUI();
    }

    public void postMessage(String msg) {
        if (userMessage != null) {
            userMessage.remove();
        }

        userMessage = new Label(msg, skin);
        userMessage.setPosition(10f, 15f);
        stage.addActor(userMessage);
        currentMessageTime = timeForMessages;
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

        options = new SlideWindow("Options", skin);
        options.setHeight(Gdx.graphics.getHeight());
        options.setWidth(Gdx.graphics.getWidth() / 3f);
        options.setPosition((Gdx.graphics.getWidth() / 3f) * 2f, 0f);
        options.setModal(false);
        options.setKeepWithinStage(false);
        //options.padLeft(30f);
        options.top();

        // header
        Table header = new Table();
        header.pad(padding);
        options.add(header);
        ButtonGroup headerGroup = new ButtonGroup();
        headerGroup.setMinCheckCount(1);
        headerGroup.setMaxCheckCount(1);

        TextButton headerFractal = new TextButton("Fractal", skin, "toggle");
        headerFractal.padLeft(10f).padRight(10f);
        headerFractal.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (optStatus != OptStatus.UNKNOWN) {
                    optCurrent.remove();
                    optCurrent = createOptionsFractal();
                    optWrapper.add(optCurrent).expand();
                    optStatus = OptStatus.FRACTAL;
                }
            }
        });
        header.add(headerFractal).pad(padding);
        headerGroup.add(headerFractal);

        TextButton headerColor = new TextButton("Color", skin, "toggle");
        headerColor.padLeft(10f).padRight(10f);
        headerColor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (optStatus != OptStatus.UNKNOWN) {
                    optCurrent.remove();
                    optCurrent = createOptionsColor();
                    optWrapper.add(optCurrent).expand();
                    optStatus = OptStatus.COLOR;
                }
            }
        });
        header.add(headerColor).pad(padding);
        headerGroup.add(headerColor);

        TextButton headerMore = new TextButton("More", skin, "toggle");
        headerMore.padLeft(10f).padRight(10f);
        headerMore.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (optStatus != OptStatus.UNKNOWN) {
                    optCurrent.remove();
                    optCurrent = createOptionsMore();
                    optWrapper.add(optCurrent);
                    optStatus = OptStatus.MORE;
                }
            }
        });
        header.add(headerMore).pad(padding);
        headerGroup.add(headerMore);
        options.row();

        // options wrapper table
        optWrapper = new Table();
        options.add(optWrapper).expand();

        // default to fractal options
        optCurrent = createOptionsFractal();
        optWrapper.add(optCurrent).expand();
        optStatus = OptStatus.FRACTAL;
        headerGroup.setChecked("Fractal");

        stage.addActor(options);


        if (app.settings.debugGUI) {
            options.debug();
        }
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
        sliderXTable.add(paramSliderX).pad(padding);
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
        sliderYTable.add(paramSliderY).pad(padding);
        sliderYTable.add(paramYTable);
        paramsTable.add(sliderYTable);

        return fractal;
    }

    private Table createOptionsColor() {
        Table color = new Table();

        String[] colorNames = new String[app.settings.fractalColors.length];
        for(int i = 0; i < app.settings.fractalColors.length; i++) {
            colorNames[i] = app.settings.fractalColors[i].replace("gradients/", "").replace(".png", "");
        }
        colorSelector = new List(colorNames, skin);
        colorSelector.setSelectedIndex(app.settings.fractalColor);
        colorSelector.setFillParent(true);
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
        color.add(new ScrollPane(colorSelector, skin)).expandX();

        return color;
    }

    private Table createOptionsMore() {
        Table more = new Table();

        Table moreQuality = new Table();
        moreQuality.add(new Label("Render quality", skin));
        String[] qualitySettings = Arrays.copyOfRange(app.settings.renderQualityNames, 2, 8);
        SelectBox qualityBox = new SelectBox(qualitySettings, skin);
        qualityBox.setSelection(app.settings.renderSetting - 2);
        qualityBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                int selection = ((SelectBox)actor).getSelectionIndex();
                app.settings.renderSetting = selection + 2;
                app.createRenderer();
                app.requestRender();
            }
        });
        moreQuality.add(qualityBox).pad(padding);
        moreQuality.row();

        moreQuality.add(new Label("Preview quality", skin));
        String[] previewQualitySettings = Arrays.copyOfRange(app.settings.renderQualityNames, 0, 5);
        SelectBox previewBox = new SelectBox(previewQualitySettings, skin);
        previewBox.setSelection(app.settings.previewSetting);
        previewBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                int selection = ((SelectBox)actor).getSelectionIndex();
                app.settings.previewSetting = selection;
                app.createPreviewRenderer();
                optionsChanged = true;
            }
        });
        moreQuality.add(previewBox).pad(padding);
        moreQuality.row();
        more.add(moreQuality);
        more.row();

        TextButton makeScreenshot = new TextButton("Save screenshot", skin);
        makeScreenshot.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                String screenName = "Pictures/fractured-" + TimeUtils.millis() + ".png";
                Pixmap screenMap = app.getFractalRenderer().createScreenshot();

                if (screenMap != null) {
                    PixmapIO.writePNG(Gdx.files.external(screenName), app.getFractalRenderer().createScreenshot());
                    postMessage("Screenshot saved as " + screenName);
                }
            }
        });
        more.add(makeScreenshot).pad(padding);


        return more;
    }

    public void draw(float delta) {
        stage.act(delta);
        stage.draw();

        if (optionsChanged) {
            app.requestPreviewRender();
        }

        if (userMessage != null) {
            currentMessageTime -= delta;

            if (currentMessageTime < 0f) {
                userMessage.remove();
                userMessage = null;
            }
        }

        if (app.settings.debugGUI) {
            Table.drawDebug(stage);
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
