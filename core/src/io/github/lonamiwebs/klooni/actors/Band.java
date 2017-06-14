package io.github.lonamiwebs.klooni.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.lonamiwebs.klooni.game.BaseScorer;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.lonamiwebs.klooni.game.GameLayout;
import com.badlogic.gdx.graphics.Texture;
import io.github.lonamiwebs.klooni.Klooni;
import Label.LabelStyle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import io.github.lonamiwebs.klooni.Theme;
import Align.center;

public class Band extends Actor {
    private final BaseScorer scorer;

    private final Texture bandTexture;

    public final Rectangle scoreBounds;

    public final Rectangle infoBounds;

    private final Label infoLabel;

    private final Label scoreLabel;

    public Band(final Klooni game, final GameLayout layout, final BaseScorer scorer) {
        this.scorer = scorer;
        bandTexture = Theme.getBlankTexture();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font");
        scoreLabel = new Label("", labelStyle);
        scoreLabel.setAlignment(center);
        infoLabel = new Label("pause menu", labelStyle);
        infoLabel.setAlignment(center);
        scoreBounds = new Rectangle();
        infoBounds = new Rectangle();
        layout.update(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getParent().getX();
        float y = getParent().getY();
        Vector2 pos = localToStageCoordinates(new Vector2(x, y));
        batch.setColor(Klooni.theme.bandColor);
        batch.draw(bandTexture, pos.x, pos.y, getWidth(), getHeight());
        scoreLabel.setBounds((x + (scoreBounds.x)), (y + (scoreBounds.y)), scoreBounds.width, scoreBounds.height);
        scoreLabel.setText(Integer.toString(scorer.getCurrentScore()));
        scoreLabel.setColor(Klooni.theme.textColor);
        scoreLabel.draw(batch, parentAlpha);
        infoLabel.setBounds((x + (infoBounds.x)), (y + (infoBounds.y)), infoBounds.width, infoBounds.height);
        infoLabel.setColor(Klooni.theme.textColor);
        infoLabel.draw(batch, parentAlpha);
    }

    public void setMessage(final String message) {
        if (!(message.equals("")))
            infoLabel.setText(message);
        
    }
}

