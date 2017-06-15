package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import io.github.lonamiwebs.klooni.actors.Band;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.lonamiwebs.klooni.actors.SoftButton;
import Interpolation.swingOut;
import io.github.lonamiwebs.klooni.game.BaseScorer;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.lonamiwebs.klooni.Theme;
import Gdx.gl;
import com.badlogic.gdx.graphics.Color;
import Interpolation.swingIn;
import codesmells.annotations.LIC;
import Gdx.graphics;
import ShapeRenderer.ShapeType.Filled;
import GL20.GL_BLEND;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import io.github.lonamiwebs.klooni.game.GameLayout;
import com.badlogic.gdx.InputProcessor;
import Input.Keys;
import Gdx.input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import codesmells.annotations.LM;

class PauseMenuStage extends Stage {
    private InputProcessor lastInputProcessor;

    private boolean shown;

    private boolean hiding;

    private final ShapeRenderer shapeRenderer;

    private final Klooni game;

    private final Band band;

    private final BaseScorer scorer;

    private final SoftButton playButton;

    @LM
    PauseMenuStage(final GameLayout layout, final Klooni game, final BaseScorer scorer, final int gameMode) {
        this.game = game;
        this.scorer = scorer;
        shapeRenderer = new ShapeRenderer(20);
        Table table = new Table();
        table.setFillParent(true);
        addActor(table);
        band = new Band(game, layout, this.scorer);
        addActor(band);
        final SoftButton homeButton = new SoftButton(3, "home_texture");
        table.add(homeButton).space(16);
        homeButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.transitionTo(new MainMenuScreen(game));
            }
        });
        final SoftButton replayButton = new SoftButton(0, "replay_texture");
        table.add(replayButton).space(16);
        replayButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.transitionTo(new GameScreen(game, gameMode, false));
            }
        });
        table.row();
        final SoftButton paletteButton = new SoftButton(1, "palette_texture");
        table.add(paletteButton).space(16);
        paletteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.transitionTo(new CustomizeScreen(game, game.getScreen()), false);
            }
        });
        playButton = new SoftButton(2, "play_texture");
        table.add(playButton).space(16);
        playButton.addListener(playChangeListener);
    }

    private void hide() {
        shown = false;
        hiding = true;
        input.setInputProcessor(lastInputProcessor);
        addAction(Actions.sequence(Actions.moveTo(0, graphics.getHeight(), 0.5F, swingIn), new RunnableAction() {
            @Override
            public void run() {
                hiding = false;
            }
        }));
        scorer.resume();
    }

    private final ChangeListener playChangeListener = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            hide();
        }
    };

    void show() {
        scorer.pause();
        scorer.saveScore();
        lastInputProcessor = input.getInputProcessor();
        input.setInputProcessor(this);
        shown = true;
        hiding = false;
        addAction(Actions.moveTo(0, graphics.getHeight()));
        addAction(Actions.moveTo(0, 0, 0.75F, swingOut));
    }

    void showGameOver(final String gameOverReason, final boolean timeMode) {
        if ((game.shareChallenge) != null) {
            playButton.removeListener(playChangeListener);
            playButton.updateImage("share_texture");
            playButton.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    game.transitionTo(new ShareScoreScreen(game, game.getScreen(), scorer.getCurrentScore(), timeMode), false);
                }
            });
        }
        band.setMessage(gameOverReason);
        show();
    }

    boolean isShown() {
        return shown;
    }

    boolean isHiding() {
        return hiding;
    }

    @Override
    public void draw() {
        if (shown) {
            gl.glEnable(GL_BLEND);
            shapeRenderer.begin(Filled);
            Color color = new Color(Klooni.theme.bandColor);
            color.a = 0.1F;
            shapeRenderer.setColor(color);
            shapeRenderer.rect(0, 0, graphics.getWidth(), graphics.getHeight());
            shapeRenderer.end();
        }
        super.draw();
    }

    @Override
    public boolean keyUp(int keyCode) {
        if ((keyCode == (Keys.P)) || (keyCode == (Keys.BACK)))
            hide();
        
        return super.keyUp(keyCode);
    }
}

