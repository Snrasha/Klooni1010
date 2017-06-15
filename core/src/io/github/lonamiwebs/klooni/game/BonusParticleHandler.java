package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Batch;
import java.util.Iterator;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import Label.LabelStyle;
import com.badlogic.gdx.math.Vector2;

public class BonusParticleHandler {
    private final Array<BonusParticle> particles;

    private final LabelStyle labelStyle;

    public BonusParticleHandler(final Klooni game) {
        labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font_bonus");
        particles = new Array<BonusParticle>();
    }

    public void addBonus(final Vector2 pos, final int score) {
        particles.add(new BonusParticle(pos, score, labelStyle));
    }

    public void run(final Batch batch) {
        BonusParticle particle;
        Iterator<BonusParticle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            particle = iterator.next();
            particle.run(batch);
            if (particle.done())
                iterator.remove();
            
        } 
    }
}

