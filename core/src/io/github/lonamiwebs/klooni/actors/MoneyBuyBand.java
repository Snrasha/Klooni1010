package io.github.lonamiwebs.klooni.actors;

import Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import Align.left;
import io.github.lonamiwebs.klooni.Theme;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MoneyBuyBand extends Table {
    private final Label infoLabel;

    private final SoftButton confirmButton;

    private final SoftButton cancelButton;

    private String infoText;

    private boolean showingTemp;

    private ThemeCard toBuy;

    private StringBuilder shownText;

    private long nextTextUpdate;

    private long nextTempRevertUpdate;

    private static final long SHOW_ONE_CHARACTER_EVERY = 30;

    private static final long TEMP_TEXT_DELAY = 2 * 1000;

    public MoneyBuyBand(final Klooni game) {
        infoText = "";
        shownText = new StringBuilder();
        final Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.skin.getFont("font_small");
        infoLabel = new Label(infoText, labelStyle);
        infoLabel.setAlignment(left);
        add(infoLabel).expandX().left().padLeft(20);
        confirmButton = new SoftButton(0, "ok_texture");
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ((toBuy) != null)
                    toBuy.performBuy();
                
                showCurrentMoney();
                hideBuyButtons();
            }
        });
        add(confirmButton).pad(8, 0, 8, 4);
        confirmButton.setVisible(false);
        cancelButton = new SoftButton(3, "cancel_texture");
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showCurrentMoney();
                hideBuyButtons();
            }
        });
        add(cancelButton).pad(8, 4, 8, 8);
        cancelButton.setVisible(false);
        setBackground(new TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(Theme.getBlankTexture())));
        showCurrentMoney();
    }

    private void showCurrentMoney() {
        setText(("money: " + (Klooni.getMoney())));
    }

    private void hideBuyButtons() {
        confirmButton.setVisible(false);
        cancelButton.setVisible(false);
        toBuy = null;
    }

    private void setText(String text) {
        infoText = text;
        showingTemp = false;
        nextTextUpdate = (TimeUtils.millis()) + (MoneyBuyBand.SHOW_ONE_CHARACTER_EVERY);
    }

    private void setTempText(String text) {
        setText(text);
        showingTemp = true;
        nextTempRevertUpdate = (TimeUtils.millis()) + (MoneyBuyBand.TEMP_TEXT_DELAY);
    }

    private void interpolateText() {
        if (!(shownText.toString().equals(infoText))) {
            int limit = Math.min(shownText.length(), infoText.length());
            for (int i = 0; i < limit; ++i) {
                if ((shownText.charAt(i)) != (infoText.charAt(i))) {
                    shownText.setCharAt(i, infoText.charAt(i));
                    infoLabel.setText(shownText);
                    return ;
                }
            }
            if ((shownText.length()) > (infoText.length())) {
                shownText.setLength(((shownText.length()) - 1));
            }else {
                shownText.append(infoText.charAt(shownText.length()));
            }
            infoLabel.setText(shownText);
        }
    }

    public void askBuy(final ThemeCard toBuy) {
        if ((toBuy.theme.getPrice()) > (Klooni.getMoney())) {
            setTempText("cannot buy!");
            confirmButton.setVisible(false);
            cancelButton.setVisible(false);
        }else {
            this.toBuy = toBuy;
            setText("confirm?");
            confirmButton.setVisible(true);
            cancelButton.setVisible(true);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        long now = TimeUtils.millis();
        if (now > (nextTextUpdate)) {
            interpolateText();
            nextTextUpdate = (TimeUtils.millis()) + (MoneyBuyBand.SHOW_ONE_CHARACTER_EVERY);
            if ((now > (nextTempRevertUpdate)) && (showingTemp)) {
                showCurrentMoney();
            }
        }
        setColor(Klooni.theme.bandColor);
        infoLabel.setColor(Klooni.theme.textColor);
        super.draw(batch, parentAlpha);
    }
}

