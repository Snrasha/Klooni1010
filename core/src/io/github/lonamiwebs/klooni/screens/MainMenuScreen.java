package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import Input.Keys.BACK;
import GL20.GL_COLOR_BUFFER_BIT;
import Gdx.app;
import io.github.lonamiwebs.klooni.Klooni;
import Gdx.input;
import Gdx.net;
import Gdx.graphics;
import Gdx.gl;
import com.badlogic.gdx.Screen;
import io.github.lonamiwebs.klooni.actors.SoftButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MainMenuScreen extends InputListener implements Screen {
    private final Klooni game;

    private final Stage stage;

    private static final float minDelta = 1 / 30.0F;

    public MainMenuScreen(Klooni game) {
        this.game = game;
        stage = new Stage();
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        final SoftButton playButton = new SoftButton(0, (GameScreen.hasSavedData() ? "play_saved_texture" : "play_texture"));
        playButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuScreen.this.game.transitionTo(new GameScreen(MainMenuScreen.this.game, GameScreen.GAME_MODE_SCORE));
            }
        });
        table.add(playButton).colspan(3).fill().space(16);
        table.row();
        final SoftButton starButton = new SoftButton(1, "star_texture");
        starButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                net.openURI("https://github.com/LonamiWebs/Klooni1010/stargazers");
            }
        });
        table.add(starButton).space(16);
        final SoftButton statsButton = new SoftButton(2, "stopwatch_texture");
        statsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuScreen.this.game.transitionTo(new GameScreen(MainMenuScreen.this.game, GameScreen.GAME_MODE_TIME));
            }
        });
        table.add(statsButton).space(16);
        final SoftButton paletteButton = new SoftButton(3, "palette_texture");
        paletteButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                MainMenuScreen.this.game.transitionTo(new CustomizeScreen(MainMenuScreen.this.game, MainMenuScreen.this.game.getScreen()), false);
            }
        });
        table.add(paletteButton).space(16);
    }

    @Override
    public void show() {
        input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Klooni.theme.glClearBackground();
        gl.glClear(GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(graphics.getDeltaTime(), MainMenuScreen.minDelta));
        stage.draw();
        if (input.isKeyJustPressed(BACK)) {
            app.exit();
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

