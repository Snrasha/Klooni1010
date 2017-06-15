package io.github.lonamiwebs.klooni.desktop;

import Files.FileType.Internal;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Klooni 1010!";
        config.width = Klooni.GAME_WIDTH;
        config.height = Klooni.GAME_HEIGHT;
        config.addIcon("ic_launcher/icon128.png", Internal);
        config.addIcon("ic_launcher/icon32.png", Internal);
        config.addIcon("ic_launcher/icon16.png", Internal);
        new com.badlogic.gdx.backends.lwjgl.LwjglApplication(new Klooni(null), config);
    }
}

