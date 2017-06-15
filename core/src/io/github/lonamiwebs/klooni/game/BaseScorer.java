package io.github.lonamiwebs.klooni.game;

import io.github.lonamiwebs.klooni.serializer.BinSerializable;
import com.badlogic.gdx.graphics.Color;
import Interpolation.linear;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import Label.LabelStyle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import Align.right;
import codesmells.annotations.MIM;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Texture;
import io.github.lonamiwebs.klooni.Theme;
import com.badlogic.gdx.math.Rectangle;
import io.github.lonamiwebs.klooni.SkinLoader;

public abstract class BaseScorer implements BinSerializable {
    int currentScore;

    final Label currentScoreLabel;

    final Label highScoreLabel;

    final Texture cupTexture;

    final Rectangle cupArea;

    private final Color cupColor;

    private float shownScore;

    BaseScorer(final Klooni game, GameLayout layout, int highScore) {
        cupTexture = SkinLoader.loadPng("cup.png");
        cupColor = Klooni.theme.currentScore.cpy();
        cupArea = new Rectangle();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font");
        currentScoreLabel = new Label("0", labelStyle);
        currentScoreLabel.setAlignment(right);
        highScoreLabel = new Label(Integer.toString(highScore), labelStyle);
        layout.update(this);
    }

    final int calculateClearScore(int stripsCleared, int boardSize) {
        if (stripsCleared < 1)
            return 0;
        
        if (stripsCleared == 1)
            return boardSize;
        else
            return (boardSize * stripsCleared) + (calculateClearScore((stripsCleared - 1), boardSize));
        
    }

    public int addPieceScore(int areaPut) {
        currentScore += areaPut;
        return areaPut;
    }

    public int addBoardScore(int stripsCleared, int boardSize) {
        int score = calculateClearScore(stripsCleared, boardSize);
        currentScore += score;
        return score;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    @MIM
    public void pause() {
    }

    @MIM
    public void resume() {
    }

    @MIM
    public abstract boolean isGameOver();

    @MIM
    protected abstract boolean isNewRecord();

    @MIM
    public String gameOverReason() {
        return "";
    }

    @MIM
    public abstract void saveScore();

    public void draw(SpriteBatch batch) {
        cupColor.lerp((isNewRecord() ? Klooni.theme.highScore : Klooni.theme.currentScore), 0.05F);
        batch.setColor(cupColor);
        batch.draw(cupTexture, cupArea.x, cupArea.y, cupArea.width, cupArea.height);
        int roundShown = MathUtils.round(shownScore);
        if (roundShown != (currentScore)) {
            shownScore = linear.apply(shownScore, currentScore, 0.1F);
            currentScoreLabel.setText(Integer.toString(MathUtils.round(shownScore)));
        }
        currentScoreLabel.setColor(Klooni.theme.currentScore);
        currentScoreLabel.draw(batch, 1.0F);
        highScoreLabel.setColor(Klooni.theme.highScore);
        highScoreLabel.draw(batch, 1.0F);
    }
}

