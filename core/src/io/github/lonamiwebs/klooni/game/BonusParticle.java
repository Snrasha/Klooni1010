package io.github.lonamiwebs.klooni.game;

import Label.LabelStyle;
import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.github.lonamiwebs.klooni.Theme;
import com.badlogic.gdx.math.Vector2;
import Interpolation.elasticOut;
import Gdx.graphics;
import Interpolation.linear;

class BonusParticle {
    private Label label;

    private float lifetime;

    private static final float SPEED = 1.0F;

    BonusParticle(final Vector2 pos, final int score, final Label.LabelStyle style) {
        label = new Label(("+" + score), style);
        label.setBounds(pos.x, pos.y, 0, 0);
    }

    void run(final Batch batch) {
        lifetime += (BonusParticle.SPEED) * (graphics.getDeltaTime());
        if ((lifetime) > 1.0F)
            lifetime = 1.0F;
        
        label.setColor(Klooni.theme.bonus);
        label.setFontScale(elasticOut.apply(0.0F, 1.0F, lifetime));
        float opacity = linear.apply(1.0F, 0.0F, lifetime);
        label.draw(batch, opacity);
    }

    boolean done() {
        return (lifetime) >= 1.0F;
    }
}

