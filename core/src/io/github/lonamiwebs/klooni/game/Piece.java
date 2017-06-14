package io.github.lonamiwebs.klooni.game;

import java.io.DataInputStream;
import com.badlogic.gdx.graphics.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Piece {
    final Vector2 pos;

    final int colorIndex;

    private final int rotation;

    final int cellCols;

    final int cellRows;

    private boolean[][] shape;

    float cellSize = 10.0F;

    private Piece(int cols, int rows, int rotateSizeBy, int colorIndex) {
        this.colorIndex = colorIndex;
        pos = new Vector2();
        rotation = rotateSizeBy % 2;
        cellCols = ((rotation) == 1) ? rows : cols;
        cellRows = ((rotation) == 1) ? cols : rows;
        shape = new boolean[cellRows][cellCols];
        for (int i = 0; i < (cellRows); ++i) {
            for (int j = 0; j < (cellCols); ++j) {
                shape[i][j] = true;
            }
        }
    }

    private Piece(int lSize, int rotateCount, int colorIndex) {
        this.colorIndex = colorIndex;
        pos = new Vector2();
        cellCols = cellRows = lSize;
        shape = new boolean[lSize][lSize];
        rotation = rotateCount % 4;
        switch (rotation) {
            case 0 :
                for (int j = 0; j < lSize; ++j)
                    shape[0][j] = true;
                
                for (int i = 0; i < lSize; ++i)
                    shape[i][0] = true;
                
                break;
            case 1 :
                for (int j = 0; j < lSize; ++j)
                    shape[0][j] = true;
                
                for (int i = 0; i < lSize; ++i)
                    shape[i][(lSize - 1)] = true;
                
                break;
            case 2 :
                for (int j = 0; j < lSize; ++j)
                    shape[(lSize - 1)][j] = true;
                
                for (int i = 0; i < lSize; ++i)
                    shape[i][(lSize - 1)] = true;
                
                break;
            case 3 :
                for (int j = 0; j < lSize; ++j)
                    shape[(lSize - 1)][j] = true;
                
                for (int i = 0; i < lSize; ++i)
                    shape[i][0] = true;
                
                break;
        }
    }

    static Piece random() {
        return Piece.fromIndex(MathUtils.random(8), MathUtils.random(4));
    }

    private static Piece fromIndex(int colorIndex, int rotateCount) {
        switch (colorIndex) {
            case 0 :
                return new Piece(1, 1, 0, colorIndex);
            case 1 :
                return new Piece(2, 2, 0, colorIndex);
            case 2 :
                return new Piece(3, 3, 0, colorIndex);
            case 3 :
                return new Piece(1, 2, rotateCount, colorIndex);
            case 4 :
                return new Piece(1, 3, rotateCount, colorIndex);
            case 5 :
                return new Piece(1, 4, rotateCount, colorIndex);
            case 6 :
                return new Piece(1, 5, rotateCount, colorIndex);
            case 7 :
                return new Piece(2, rotateCount, colorIndex);
            case 8 :
                return new Piece(3, rotateCount, colorIndex);
        }
        throw new RuntimeException("Random function is broken.");
    }

    void draw(SpriteBatch batch) {
        final Color c = Klooni.theme.getCellColor(colorIndex);
        for (int i = 0; i < (cellRows); ++i)
            for (int j = 0; j < (cellCols); ++j)
                if (shape[i][j])
                    Cell.draw(c, batch, ((pos.x) + (j * (cellSize))), ((pos.y) + (i * (cellSize))), cellSize);
                
            
        
    }

    Rectangle getRectangle() {
        return new Rectangle(pos.x, pos.y, ((cellCols) * (cellSize)), ((cellRows) * (cellSize)));
    }

    boolean filled(int i, int j) {
        return shape[i][j];
    }

    int calculateArea() {
        int area = 0;
        for (int i = 0; i < (cellRows); ++i) {
            for (int j = 0; j < (cellCols); ++j) {
                if (shape[i][j]) {
                    area++;
                }
            }
        }
        return area;
    }

    Vector2 calculateGravityCenter() {
        int filledCount = 0;
        Vector2 result = new Vector2();
        for (int i = 0; i < (cellRows); ++i) {
            for (int j = 0; j < (cellCols); ++j) {
                if (shape[i][j]) {
                    filledCount++;
                    result.add((((pos.x) + (j * (cellSize))) - ((cellSize) * 0.5F)), (((pos.y) + (i * (cellSize))) - ((cellSize) * 0.5F)));
                }
            }
        }
        return result.scl((1.0F / filledCount));
    }

    void write(DataOutputStream out) throws IOException {
        out.writeInt(colorIndex);
        out.writeInt(rotation);
    }

    static Piece read(DataInputStream in) throws IOException {
        return Piece.fromIndex(in.readInt(), in.readInt());
    }
}

