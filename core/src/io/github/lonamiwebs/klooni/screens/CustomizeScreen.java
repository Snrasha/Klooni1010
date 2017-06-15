package io.github.lonamiwebs.klooni.screens;

import Input.Keys.BACK;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import io.github.lonamiwebs.klooni.Theme;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.lonamiwebs.klooni.actors.SoftButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import GL20.GL_COLOR_BUFFER_BIT;
import io.github.lonamiwebs.klooni.game.GameLayout;
import Gdx.net;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.Screen;
import Gdx.input;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import Gdx.gl;
import io.github.lonamiwebs.klooni.Klooni;
import Gdx.graphics;
import io.github.lonamiwebs.klooni.actors.ThemeCard;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.lonamiwebs.klooni.actors.MoneyBuyBand;

class CustomizeScreen implements Screen {
    private Klooni game;

    private Stage stage;

    private final Screen lastScreen;

    private float themeDragStartX;

    private float themeDragStartY;

    private static final float MIN_DELTA = 1 / 30.0F;

    private static final float DRAG_LIMIT_SQ = 20 * 20;

    CustomizeScreen(Klooni game, final Screen lastScreen) {
        final GameLayout layout = new GameLayout();
        this.game = game;
        this.lastScreen = lastScreen;
        stage = new Stage();
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        HorizontalGroup optionsGroup = new HorizontalGroup();
        optionsGroup.space(12);
        final SoftButton backButton = new SoftButton(1, "back_texture");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBack();
            }
        });
        optionsGroup.addActor(backButton);
        final SoftButton soundButton = new SoftButton(0, (Klooni.soundsEnabled() ? "sound_on_texture" : "sound_off_texture"));
        soundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final boolean enabled = Klooni.toggleSound();
                soundButton.image = CustomizeScreen.this.game.skin.getDrawable((enabled ? "sound_on_texture" : "sound_off_texture"));
            }
        });
        optionsGroup.addActor(soundButton);
        final SoftButton snapButton = new SoftButton(2, (Klooni.shouldSnapToGrid() ? "snap_on_texture" : "snap_off_texture"));
        snapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final boolean shouldSnap = Klooni.toggleSnapToGrid();
                snapButton.image = CustomizeScreen.this.game.skin.getDrawable((shouldSnap ? "snap_on_texture" : "snap_off_texture"));
            }
        });
        optionsGroup.addActor(snapButton);
        final SoftButton issuesButton = new SoftButton(3, "issues_texture");
        issuesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                net.openURI("https://github.com/LonamiWebs/Klooni1010/issues");
            }
        });
        optionsGroup.addActor(issuesButton);
        final SoftButton webButton = new SoftButton(2, "web_texture");
        webButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                net.openURI("https://lonamiwebs.github.io");
            }
        });
        optionsGroup.addActor(webButton);
        table.add(new ScrollPane(optionsGroup)).pad(20, 4, 12, 4).height(backButton.getHeight());
        final MoneyBuyBand buyBand = new MoneyBuyBand(game);
        table.row();
        final VerticalGroup themesGroup = new VerticalGroup();
        for (Theme theme : Theme.getThemes()) {
            final ThemeCard card = new ThemeCard(game, layout, theme);
            card.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    themeDragStartX = x;
                    themeDragStartY = y;
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    x -= themeDragStartX;
                    y -= themeDragStartY;
                    float distSq = (x * x) + (y * y);
                    if (distSq < (CustomizeScreen.DRAG_LIMIT_SQ)) {
                        if (Klooni.isThemeBought(card.theme))
                            card.use();
                        else
                            buyBand.askBuy(card);
                        
                        for (Actor a : themesGroup.getChildren()) {
                            ThemeCard c = ((ThemeCard) (a));
                            c.usedThemeUpdated();
                        }
                    }
                }
            });
            themesGroup.addActor(card);
        }
        final ScrollPane themesScroll = new ScrollPane(themesGroup);
        table.add(themesScroll).expand().fill();
        table.row();
        table.add(buyBand).expandX().fillX();
        table.layout();
        for (Actor a : themesGroup.getChildren()) {
            ThemeCard c = ((ThemeCard) (a));
            if (c.isUsed()) {
                themesScroll.scrollTo(c.getX(), ((c.getY()) + (c.getHeight())), c.getWidth(), c.getHeight());
                break;
            }
            c.usedThemeUpdated();
        }
    }

    private void goBack() {
        this.game.transitionTo(lastScreen);
    }

    @Override
    public void show() {
        input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Klooni.theme.glClearBackground();
        gl.glClear(GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(graphics.getDeltaTime(), CustomizeScreen.MIN_DELTA));
        stage.draw();
        if (input.isKeyJustPressed(BACK)) {
            goBack();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}

