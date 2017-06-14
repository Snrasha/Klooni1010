package io.github.lonamiwebs.klooni.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import io.github.lonamiwebs.klooni.Klooni;
import io.github.lonamiwebs.klooni.Theme;

public class SoftButton extends ImageButton {
    private int styleIndex;

    public Drawable image;

    public SoftButton(final int styleIndex, final String imageName) {
        super(Klooni.theme.getStyle(styleIndex));
        this.styleIndex = styleIndex;
        updateImage(imageName);
    }

    public void updateImage(final String imageName) {
        image = Theme.skin.getDrawable(imageName);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ImageButtonStyle style = getStyle();
        Klooni.theme.updateStyle(style, styleIndex);
        style.imageUp = image;
        getImage().setColor(Klooni.theme.foreground);
        super.draw(batch, parentAlpha);
    }
}

