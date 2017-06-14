package io.github.lonamiwebs.klooni.game;

import io.github.lonamiwebs.klooni.actors.Band;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.math.Rectangle;
import io.github.lonamiwebs.klooni.actors.ThemeCard;
import Gdx.graphics;

public class GameLayout {
    private float screenWidth;

    private float marginWidth;

    private float availableWidth;

    private float screenHeight;

    private float logoHeight;

    private float scoreHeight;

    private float boardHeight;

    private float pieceHolderHeight;

    private float themeCardHeight;

    public GameLayout() {
        calculate();
    }

    private void calculate() {
        screenWidth = graphics.getWidth();
        screenHeight = graphics.getHeight();
        marginWidth = (screenWidth) * 0.05F;
        availableWidth = (screenWidth) - ((marginWidth) * 2.0F);
        logoHeight = (screenHeight) * 0.1F;
        scoreHeight = (screenHeight) * 0.15F;
        boardHeight = (screenHeight) * 0.5F;
        pieceHolderHeight = (screenHeight) * 0.25F;
        themeCardHeight = (screenHeight) * 0.15F;
    }

    void update(BaseScorer scorer) {
        float cupSize = Math.min(scoreHeight, scorer.cupTexture.getHeight());
        final Rectangle area = new Rectangle(marginWidth, ((pieceHolderHeight) + (boardHeight)), availableWidth, scoreHeight);
        scorer.cupArea.set((((area.x) + ((area.width) * 0.5F)) - (cupSize * 0.5F)), area.y, cupSize, cupSize);
        scorer.currentScoreLabel.setBounds(area.x, area.y, (((area.width) * 0.5F) - (cupSize * 0.5F)), area.height);
        scorer.highScoreLabel.setBounds((((area.x) + ((area.width) * 0.5F)) + (cupSize * 0.5F)), area.y, (((area.width) * 0.5F) - (cupSize * 0.5F)), area.height);
    }

    void updateTimeLeftLabel(Label timeLeftLabel) {
        timeLeftLabel.setBounds(0, ((screenHeight) - (logoHeight)), screenWidth, logoHeight);
    }

    void update(Board board) {
        float boardSize = Math.min(availableWidth, boardHeight);
        board.cellSize = boardSize / (board.cellCount);
        board.pos.set((((screenWidth) * 0.5F) - (boardSize * 0.5F)), pieceHolderHeight);
    }

    void update(PieceHolder holder) {
        holder.area.set(marginWidth, 0.0F, availableWidth, pieceHolderHeight);
    }

    public void update(Band band) {
        final Rectangle area = new Rectangle(0, ((pieceHolderHeight) + (boardHeight)), screenWidth, scoreHeight);
        band.setBounds(area.x, area.y, area.width, area.height);
        band.scoreBounds.set(area.x, ((area.y) + ((area.height) * 0.55F)), area.width, ((area.height) * 0.35F));
        band.infoBounds.set(area.x, ((area.y) + ((area.height) * 0.1F)), area.width, ((area.height) * 0.35F));
    }

    public void update(ThemeCard card) {
        card.setSize(((availableWidth) - (marginWidth)), themeCardHeight);
        card.cellSize = (themeCardHeight) * 0.2F;
        card.nameBounds.set(themeCardHeight, card.cellSize, ((availableWidth) - (themeCardHeight)), themeCardHeight);
        card.priceBounds.set(themeCardHeight, (-(card.cellSize)), ((availableWidth) - (themeCardHeight)), themeCardHeight);
    }
}

