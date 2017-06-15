package io.github.lonamiwebs.klooni.game;

import io.github.lonamiwebs.klooni.serializer.BinSerializable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import io.github.lonamiwebs.klooni.Klooni;

public class Scorer extends BaseScorer implements BinSerializable {
    private int highScore;

    public Scorer(final Klooni game, GameLayout layout) {
        super(game, layout, Klooni.getMaxScore());
        highScore = Klooni.getMaxScore();
    }

    public void saveScore() {
        if (isNewRecord()) {
            Klooni.setMaxScore(currentScore);
        }
    }

    @Override
    protected boolean isNewRecord() {
        return (currentScore) > (highScore);
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(currentScore);
        out.writeInt(highScore);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        currentScore = in.readInt();
        highScore = in.readInt();
    }
}

