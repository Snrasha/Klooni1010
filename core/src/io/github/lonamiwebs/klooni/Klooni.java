package io.github.lonamiwebs.klooni;

import io.github.lonamiwebs.klooni.screens.MainMenuScreen;
import codesmells.annotations.CC;
import Application.ApplicationType.Desktop;
import Gdx.input;
import com.badlogic.gdx.Game;
import codesmells.annotations.MIM;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.lonamiwebs.klooni.screens.TransitionScreen;
import Gdx.app;

@CC
public class Klooni extends Game {
    public static Theme theme;

    public Skin skin;

    public ShareChallenge shareChallenge;

    public static boolean onDesktop;

    private static final float SCORE_TO_MONEY = 1.0F / 100.0F;

    public static final int GAME_HEIGHT = 680;

    public static final int GAME_WIDTH = 408;

    public Klooni(final ShareChallenge shareChallenge) {
        this.shareChallenge = shareChallenge;
    }

    @Override
    public void create() {
        Klooni.onDesktop = app.getType().equals(Desktop);
        Klooni.prefs = app.getPreferences("io.github.lonamiwebs.klooni.game");
        skin = SkinLoader.loadSkin();
        Theme.skin = skin;
        final String themeName = Klooni.prefs.getString("themeName", "default");
        if (Theme.exists(themeName))
            Klooni.theme = Theme.getTheme(themeName);
        else
            Klooni.theme = Theme.getTheme("default");
        
        input.setCatchBackKey(true);
        setScreen(new MainMenuScreen(this));
    }

    @MIM
    public void transitionTo(Screen screen) {
        transitionTo(screen, true);
    }

    @MIM
    public void transitionTo(Screen screen, boolean disposeAfter) {
        setScreen(new TransitionScreen(this, getScreen(), screen, disposeAfter));
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
        Klooni.theme.dispose();
    }

    private static Preferences prefs;

    public static int getMaxScore() {
        return Klooni.prefs.getInteger("maxScore", 0);
    }

    public static int getMaxTimeScore() {
        return Klooni.prefs.getInteger("maxTimeScore", 0);
    }

    public static void setMaxScore(int score) {
        Klooni.prefs.putInteger("maxScore", score).flush();
    }

    public static void setMaxTimeScore(int maxTimeScore) {
        Klooni.prefs.putInteger("maxTimeScore", maxTimeScore).flush();
    }

    public static boolean soundsEnabled() {
        return !(Klooni.prefs.getBoolean("muteSound", false));
    }

    public static boolean toggleSound() {
        final boolean result = Klooni.soundsEnabled();
        Klooni.prefs.putBoolean("muteSound", result).flush();
        return !result;
    }

    public static boolean shouldSnapToGrid() {
        return Klooni.prefs.getBoolean("snapToGrid", false);
    }

    public static boolean toggleSnapToGrid() {
        final boolean result = !(Klooni.shouldSnapToGrid());
        Klooni.prefs.putBoolean("snapToGrid", result).flush();
        return result;
    }

    public static boolean isThemeBought(Theme theme) {
        if ((theme.getPrice()) == 0)
            return true;
        
        String[] themes = Klooni.prefs.getString("boughtThemes", "").split(":");
        for (String t : themes)
            if (t.equals(theme.getName()))
                return true;
            
        
        return false;
    }

    public static boolean buyTheme(Theme theme) {
        final float money = Klooni.getRealMoney();
        if ((theme.getPrice()) > money)
            return false;
        
        Klooni.setMoney((money - (theme.getPrice())));
        String bought = Klooni.prefs.getString("boughtThemes", "");
        if (bought.equals(""))
            bought = theme.getName();
        else
            bought += ":" + (theme.getName());
        
        Klooni.prefs.putString("boughtThemes", bought);
        return true;
    }

    public static void updateTheme(Theme newTheme) {
        Klooni.prefs.putString("themeName", newTheme.getName()).flush();
        Klooni.theme.update(newTheme.getName());
    }

    public static void addMoneyFromScore(int score) {
        Klooni.setMoney(((Klooni.getRealMoney()) + (score * (Klooni.SCORE_TO_MONEY))));
    }

    private static void setMoney(float money) {
        Klooni.prefs.putFloat("money", money).flush();
    }

    public static int getMoney() {
        return ((int) (Klooni.getRealMoney()));
    }

    private static float getRealMoney() {
        return Klooni.prefs.getFloat("money");
    }
}

