package io.github.lonamiwebs.klooni;

import codesmells.annotations.Lm;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Texture;
import Gdx.app;
import Gdx.files;
import Gdx.graphics;

public class SkinLoader {
    private static float[] multipliers = new float[]{ 0.75F , 1.0F , 1.25F , 1.5F , 2.0F , 4.0F };

    private static String[] ids = new String[]{ "play" , "play_saved" , "star" , "stopwatch" , "palette" , "home" , "replay" , "share" , "sound_on" , "sound_off" , "snap_on" , "snap_off" , "issues" , "credits" , "web" , "back" , "ok" , "cancel" };

    private static float bestMultiplier;

    static {
        int i;
        float desired = ((float) (graphics.getHeight())) / ((float) (Klooni.GAME_HEIGHT));
        for (i = (SkinLoader.multipliers.length) - 1; i > 0; --i) {
            if ((SkinLoader.multipliers[i]) < desired)
                break;
            
        }
        app.log("SkinLoader", ("Using assets multiplier x" + (SkinLoader.multipliers[i])));
        SkinLoader.bestMultiplier = SkinLoader.multipliers[i];
    }

    @Lm
    static Skin loadSkin() {
        String folder = ("ui/x" + (SkinLoader.bestMultiplier)) + "/";
        Skin skin = new Skin(files.internal("skin/uiskin.json"));
        final int border = ((int) (28 * (SkinLoader.bestMultiplier)));
        skin.add("button_up", new NinePatch(new Texture(files.internal((folder + "button_up.png"))), border, border, border, border));
        skin.add("button_down", new NinePatch(new Texture(files.internal((folder + "button_down.png"))), border, border, border, border));
        for (String id : SkinLoader.ids) {
            skin.add((id + "_texture"), new Texture(files.internal(((folder + id) + ".png"))));
        }
        folder = ("font/x" + (SkinLoader.bestMultiplier)) + "/";
        skin.add("font", new com.badlogic.gdx.graphics.g2d.BitmapFont(files.internal((folder + "geosans-light64.fnt"))));
        skin.add("font_small", new com.badlogic.gdx.graphics.g2d.BitmapFont(files.internal((folder + "geosans-light32.fnt"))));
        skin.add("font_bonus", new com.badlogic.gdx.graphics.g2d.BitmapFont(files.internal((folder + "the-next-font.fnt"))));
        return skin;
    }

    public static Texture loadPng(String name) {
        final String filename = (("ui/x" + (SkinLoader.bestMultiplier)) + "/") + name;
        return new Texture(files.internal(filename));
    }
}

