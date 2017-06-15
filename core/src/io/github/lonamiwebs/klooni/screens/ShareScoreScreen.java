package io.github.lonamiwebs.klooni.screens;

import GL20.GL_COLOR_BUFFER_BIT;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import Label.LabelStyle;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import Gdx.gl;
import Gdx.graphics;
import io.github.lonamiwebs.klooni.Theme;
import Align.center;

class ShareScoreScreen implements Screen {
    private Klooni game;

    private final Label infoLabel;

    private final SpriteBatch spriteBatch;

    private final int score;

    private final boolean timeMode;

    private final Screen lastScreen;

    ShareScoreScreen(final Klooni game, final Screen lastScreen, final int score, final boolean timeMode) {
        this.game = game;
        this.lastScreen = lastScreen;
        this.score = score;
        this.timeMode = timeMode;
        final Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font_small");
        infoLabel = new Label("Generating image...", labelStyle);
        infoLabel.setColor(Klooni.theme.textColor);
        infoLabel.setAlignment(center);
        infoLabel.layout();
        infoLabel.setPosition((((graphics.getWidth()) - (infoLabel.getWidth())) * 0.5F), (((graphics.getHeight()) - (infoLabel.getHeight())) * 0.5F));
        spriteBatch = new SpriteBatch();
    }

    private void goBack() {
        game.transitionTo(lastScreen);
    }

    @Override
    public void show() {
        final boolean ok = game.shareChallenge.saveChallengeImage(score, timeMode);
        game.shareChallenge.shareScreenshot(ok);
        goBack();
    }

    @Override
    public void render(float delta) {
        Klooni.theme.glClearBackground();
        gl.glClear(GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        infoLabel.draw(spriteBatch, 1);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
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

