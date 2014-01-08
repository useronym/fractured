package com.entity.fractured;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class FracturedUI {
    private Fractured app;
    private Stage stage;
    //private Table table;
    private Skin skin;

    private SlideWindow options;
    private OptStatus optStatus;
    private Table optWrapper, optCurrent;

    private enum OptStatus {
        FRACTAL, COLOR, MORE, UNKNOWN
    }


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
    }

    public void destroyUI() {
        options.remove();
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

        /*fractal.add(new Label("Type", skin));
        String[] types = {"z^2 + c", "z^3 + c", "exp(z^2) - c", "exp(z^3) - c"};
        SelectBox fractalType = new SelectBox(types, skin);
        fractal.add(fractalType).expandX().fill();
        fractal.row();*/

        // parameter
        Table paramsTable = new Table();paramsTable.debug();
        fractal.add(paramsTable);

        // holds parameter controls
        Table paramXTable = new Table();
        TextField paramXText = new TextField(Float.toString(app.getFractalRenderer().getParameter().x), skin);
        paramXTable.add(paramXText).pad(5f).width(100f);
        paramXTable.row();
        paramXTable.add(new TextButton("random", skin).padLeft(10f).padRight(10f)).pad(5f);

        // holds slider and parameter controls, resides in paramsTable
        Table sliderXTable = new Table();
        sliderXTable.add(new Slider(0f, 1f, 0.01f, true, skin));
        sliderXTable.add(paramXTable);
        paramsTable.add(sliderXTable);

        // holds parameter controls
        Table paramYTable = new Table();
        TextField paramYText = new TextField(Float.toString(app.getFractalRenderer().getParameter().y), skin);
        paramYTable.add(paramYText).pad(5f).width(100f);
        paramYTable.row();
        paramYTable.add(new TextButton("random", skin).padLeft(10f).padRight(10f)).pad(5f);

        // holds slider and parameter controls, resides in paramsTable
        Table sliderYTable = new Table();
        sliderYTable.add(new Slider(0f, 1f, 0.01f, true, skin));
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

        more.add(new Label("More options here!", skin));

        return more;
    }

    public void draw(float delta) {
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
