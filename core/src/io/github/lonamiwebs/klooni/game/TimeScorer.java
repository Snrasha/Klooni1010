package io.github.lonamiwebs.klooni.game;

import java.io.IOException;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;
import java.io.DataInputStream;
import Align.center;
import java.io.DataOutputStream;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.github.lonamiwebs.klooni.Theme;
import com.badlogic.gdx.utils.TimeUtils;
import Label.LabelStyle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class TimeScorer extends BaseScorer implements BinSerializable {
    private final Label timeLeftLabel;

    private long startTime;

    private int highScore;

    private long deadTime;

    private long pauseTime;

    private int pausedTimeLeft;

    private static final long START_TIME = 30 * 1000000000L;

    private static final double SCORE_TO_NANOS = 2.0E8;

    private static final double NANOS_TO_SECONDS = 1.0E-9;

    public TimeScorer(final Klooni game, GameLayout layout) {
        super(game, layout, Klooni.getMaxTimeScore());
        highScore = Klooni.getMaxTimeScore();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font");
        timeLeftLabel = new Label("", labelStyle);
        timeLeftLabel.setAlignment(center);
        layout.updateTimeLeftLabel(timeLeftLabel);
        startTime = TimeUtils.nanoTime();
        deadTime = (startTime) + (TimeScorer.START_TIME);
        pausedTimeLeft = -1;
    }

    private int nanosToSeconds(long nano) {
        return MathUtils.ceil(((float) (nano * (TimeScorer.NANOS_TO_SECONDS))));
    }

    private long scoreToNanos(int score) {
        return ((long) (score * (TimeScorer.SCORE_TO_NANOS)));
    }

    private int getTimeLeft() {
        return Math.max(nanosToSeconds(((deadTime) - (TimeUtils.nanoTime()))), 0);
    }

    @Override
    public int addBoardScore(int stripsCleared, int boardSize) {
        long extraTime = scoreToNanos(calculateClearScore(stripsCleared, boardSize));
        deadTime += extraTime;
        super.addBoardScore(stripsCleared, boardSize);
        return nanosToSeconds(extraTime);
    }

    @Override
    public boolean isGameOver() {
        return (TimeUtils.nanoTime()) > (deadTime);
    }

    @Override
    public String gameOverReason() {
        return "time is up";
    }

    @Override
    public void saveScore() {
        if (isNewRecord()) {
            Klooni.setMaxTimeScore(getCurrentScore());
        }
    }

    @Override
    protected boolean isNewRecord() {
        return (getCurrentScore()) > (highScore);
    }

    @Override
    public void pause() {
        pauseTime = TimeUtils.nanoTime();
        pausedTimeLeft = getTimeLeft();
    }

    @Override
    public void resume() {
        if ((pauseTime) != 0L) {
            long difference = (TimeUtils.nanoTime()) - (pauseTime);
            startTime += difference;
            deadTime += difference;
            pauseTime = 0L;
            pausedTimeLeft = -1;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        int timeLeft = ((pausedTimeLeft) < 0) ? getTimeLeft() : pausedTimeLeft;
        timeLeftLabel.setText(Integer.toString(timeLeft));
        timeLeftLabel.setColor(Klooni.theme.currentScore);
        timeLeftLabel.draw(batch, 1.0F);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeLong(((TimeUtils.nanoTime()) - (startTime)));
        out.writeInt(highScore);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        long deadOffset = in.readLong();
        deadTime = (startTime) + deadOffset;
        highScore = in.readInt();
    }
}

