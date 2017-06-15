package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;
import com.badlogic.gdx.graphics.Color;
import io.github.lonamiwebs.klooni.Theme;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import Interpolation.elasticIn;
import Gdx.graphics;
import java.io.IOException;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Cell implements BinSerializable {
    private int colorIndex;

    private Vector2 pos;

    private float size;

    private Color vanishColor;

    private float vanishSize;

    private float vanishElapsed;

    private float vanishLifetime;

    Cell(float x, float y, float cellSize) {
        pos = new Vector2(x, y);
        size = cellSize;
        colorIndex = -1;
        vanishElapsed = Float.POSITIVE_INFINITY;
    }

    void set(int ci) {
        colorIndex = ci;
    }

    void draw(SpriteBatch batch) {
        Cell.draw(Klooni.theme.getCellColor(colorIndex), batch, pos.x, pos.y, size);
        if ((vanishElapsed) <= (vanishLifetime)) {
            vanishElapsed += graphics.getDeltaTime();
            float progress = Math.min(1.0F, ((Math.max(vanishElapsed, 0.0F)) / (vanishLifetime)));
            vanishSize = elasticIn.apply(size, 0, progress);
            float centerOffset = ((size) * 0.5F) - ((vanishSize) * 0.5F);
            Cell.draw(vanishColor, batch, ((pos.x) + centerOffset), ((pos.y) + centerOffset), vanishSize);
        }
    }

    void vanish(Vector2 vanishFrom) {
        if (isEmpty())
            return ;
        
        vanishSize = size;
        vanishColor = Klooni.theme.getCellColor(colorIndex).cpy();
        vanishLifetime = 1.0F;
        colorIndex = -1;
        Vector2 center = new Vector2(((pos.x) + ((size) * 0.5F)), ((pos.y) + 0.5F));
        float vanishDist = (Vector2.dst2(vanishFrom.x, vanishFrom.y, center.x, center.y)) / (((((size) * (size)) * (size)) * (size)) * 0.2F);
        vanishElapsed = ((vanishLifetime) * 0.4F) - vanishDist;
    }

    boolean isEmpty() {
        return (colorIndex) < 0;
    }

    public static void draw(final Color color, final Batch batch, final float x, final float y, final float size) {
        batch.setColor(color);
        batch.draw(Klooni.theme.cellTexture, x, y, size, size);
    }

    public static void draw(final Texture texture, final Color color, final Batch batch, final float x, final float y, final float size) {
        batch.setColor(color);
        batch.draw(texture, x, y, size, size);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(colorIndex);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        colorIndex = in.readInt();
    }
}

