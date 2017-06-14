package io.github.lonamiwebs.klooni.screens;

import com.badlogic.gdx.Screen;
import Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import GL20.GL_COLOR_BUFFER_BIT;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import Gdx.gl;

public class TransitionScreen implements Screen {
    private FrameBuffer frameBuffer;

    private TextureRegion bufferTexture;

    private final SpriteBatch spriteBatch;

    private float fadedElapsed;

    private boolean fadingOut;

    private int width;

    private int height;

    private final Screen fromScreen;

    private final Screen toScreen;

    private final Klooni game;

    private final boolean disposeAfter;

    private static final float FADE_INVERSE_DELAY = 1.0F / 0.15F;

    public TransitionScreen(Klooni game, Screen from, Screen to, boolean disposeAfter) {
        this.disposeAfter = disposeAfter;
        this.game = game;
        fromScreen = from;
        toScreen = to;
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {
        fadedElapsed = 0.0F;
        fadingOut = true;
    }

    @Override
    public void render(float delta) {
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL_COLOR_BUFFER_BIT);
        frameBuffer.begin();
        float opacity;
        if (fadingOut) {
            fromScreen.render(delta);
            opacity = 1 - (Math.min(((fadedElapsed) * (TransitionScreen.FADE_INVERSE_DELAY)), 1));
            if (opacity == 0) {
                fadedElapsed = 0;
                fadingOut = false;
            }
        }else {
            toScreen.render(delta);
            opacity = Math.min(((fadedElapsed) * (TransitionScreen.FADE_INVERSE_DELAY)), 1);
        }
        frameBuffer.end();
        spriteBatch.begin();
        spriteBatch.setColor(1, 1, 1, opacity);
        spriteBatch.draw(bufferTexture, 0, 0, width, height);
        spriteBatch.end();
        fadedElapsed += delta;
        if ((opacity == 1) && (!(fadingOut))) {
            game.setScreen(toScreen);
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        if ((frameBuffer) != null)
            frameBuffer.dispose();
        
        frameBuffer = new FrameBuffer(Format.RGB565, width, height, false);
        bufferTexture = new TextureRegion(frameBuffer.getColorBufferTexture());
        bufferTexture.flip(false, true);
    }

    @Override
    public void dispose() {
        frameBuffer.dispose();
        if (disposeAfter)
            fromScreen.dispose();
        
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}

