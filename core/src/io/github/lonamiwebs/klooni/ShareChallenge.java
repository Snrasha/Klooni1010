package io.github.lonamiwebs.klooni;

import java.io.File;
import Color.BLACK;
import Color.GOLD.g;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import Color.GOLD.b;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import Gdx.gl;
import Color.GOLD.r;
import com.badlogic.gdx.graphics.Pixmap;
import Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import GL20.GL_COLOR_BUFFER_BIT;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.PixmapIO;
import Label.LabelStyle;
import Gdx.files;
import com.badlogic.gdx.math.Matrix4;

public abstract class ShareChallenge {
    abstract File getShareImageFilePath();

    public abstract void shareScreenshot(final boolean saveResult);

    public boolean saveChallengeImage(final int score, final boolean timeMode) {
        final File saveAt = getShareImageFilePath();
        if (!(saveAt.getParentFile().isDirectory()))
            if (!(saveAt.mkdirs()))
                return false;
            
        
        final FileHandle output = new FileHandle(saveAt);
        final Texture shareBase = new Texture(files.internal("share.png"));
        final int width = shareBase.getWidth();
        final int height = shareBase.getHeight();
        final FrameBuffer frameBuffer = new FrameBuffer(Format.RGB888, width, height, false);
        frameBuffer.begin();
        final SpriteBatch batch = new SpriteBatch();
        final Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, width, height);
        batch.setProjectionMatrix(matrix);
        gl.glClearColor(r, g, b, 1);
        gl.glClear(GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(shareBase, 0, 0);
        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = new com.badlogic.gdx.graphics.g2d.BitmapFont(files.internal("font/x1.0/geosans-light64.fnt"));
        Label label = new Label((("just scored " + score) + " on"), style);
        label.setColor(BLACK);
        label.setPosition(40, 500);
        label.draw(batch, 1);
        label.setText("try to beat me if you can");
        label.setPosition(40, 40);
        label.draw(batch, 1);
        if (timeMode) {
            Texture timeModeTexture = new Texture("ui/x1.5/stopwatch.png");
            batch.setColor(BLACK);
            batch.draw(timeModeTexture, 200, 340);
        }
        batch.end();
        final byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, width, height, true);
        final Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(output, pixmap);
        pixmap.dispose();
        shareBase.dispose();
        batch.dispose();
        frameBuffer.end();
        return true;
    }
}

