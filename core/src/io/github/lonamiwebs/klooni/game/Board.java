package io.github.lonamiwebs.klooni.game;

import java.io.DataInputStream;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;
import java.io.DataOutputStream;
import java.io.IOException;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.audio.Sound;
import Gdx.audio;
import Gdx.files;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Board implements BinSerializable {
    public final int cellCount;

    public float cellSize;

    private Cell[][] cells;

    final Vector2 pos;

    private final Sound stripClearSound;

    private final Vector2 lastPutPiecePos;

    public Board(final GameLayout layout, int cellCount) {
        this.cellCount = cellCount;
        stripClearSound = audio.newSound(files.internal("sound/strip_clear.mp3"));
        lastPutPiecePos = new Vector2();
        pos = new Vector2();
        layout.update(this);
        cells = new Cell[this.cellCount][this.cellCount];
        for (int i = 0; i < (this.cellCount); ++i) {
            for (int j = 0; j < (this.cellCount); ++j) {
                cells[i][j] = new Cell(((pos.x) + (j * (cellSize))), ((pos.y) + (i * (cellSize))), cellSize);
            }
        }
    }

    private boolean inBounds(int x, int y) {
        return (((x >= 0) && (x < (cellCount))) && (y >= 0)) && (y < (cellCount));
    }

    private boolean inBounds(Piece piece, int x, int y) {
        return (inBounds(x, y)) && (inBounds(((x + (piece.cellCols)) - 1), ((y + (piece.cellRows)) - 1)));
    }

    private boolean canPutPiece(Piece piece, int x, int y) {
        if (!(inBounds(piece, x, y)))
            return false;
        
        for (int i = 0; i < (piece.cellRows); ++i)
            for (int j = 0; j < (piece.cellCols); ++j)
                if ((!(cells[(y + i)][(x + j)].isEmpty())) && (piece.filled(i, j)))
                    return false;
                
            
        
        return true;
    }

    private boolean putPiece(Piece piece, int x, int y) {
        if (!(canPutPiece(piece, x, y)))
            return false;
        
        lastPutPiecePos.set(piece.calculateGravityCenter());
        for (int i = 0; i < (piece.cellRows); ++i)
            for (int j = 0; j < (piece.cellCols); ++j)
                if (piece.filled(i, j))
                    cells[(y + i)][(x + j)].set(piece.colorIndex);
                
            
        
        return true;
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < (cellCount); ++i)
            for (int j = 0; j < (cellCount); ++j)
                cells[i][j].draw(batch);
            
        
    }

    public boolean canPutPiece(Piece piece) {
        for (int i = 0; i < (cellCount); ++i)
            for (int j = 0; j < (cellCount); ++j)
                if (canPutPiece(piece, j, i))
                    return true;
                
            
        
        return false;
    }

    boolean putScreenPiece(Piece piece) {
        Vector2 local = piece.pos.cpy().sub(pos);
        int x = MathUtils.round(((local.x) / (piece.cellSize)));
        int y = MathUtils.round(((local.y) / (piece.cellSize)));
        return putPiece(piece, x, y);
    }

    Vector2 snapToGrid(final Piece piece, final Vector2 position) {
        final Vector2 local = position.cpy().sub(pos);
        int x = MathUtils.round(((local.x) / (piece.cellSize)));
        int y = MathUtils.round(((local.y) / (piece.cellSize)));
        if (canPutPiece(piece, x, y))
            return new Vector2(((pos.x) + (x * (piece.cellSize))), ((pos.y) + (y * (piece.cellSize))));
        else
            return position;
        
    }

    public int clearComplete() {
        int clearCount = 0;
        boolean[] clearedRows = new boolean[cellCount];
        boolean[] clearedCols = new boolean[cellCount];
        for (int i = 0; i < (cellCount); ++i) {
            clearedRows[i] = true;
            for (int j = 0; j < (cellCount); ++j) {
                if (cells[i][j].isEmpty()) {
                    clearedRows[i] = false;
                    break;
                }
            }
            if (clearedRows[i])
                clearCount++;
            
        }
        for (int j = 0; j < (cellCount); ++j) {
            clearedCols[j] = true;
            for (int i = 0; i < (cellCount); ++i) {
                if (cells[i][j].isEmpty()) {
                    clearedCols[j] = false;
                    break;
                }
            }
            if (clearedCols[j])
                clearCount++;
            
        }
        if (clearCount > 0) {
            float pan = 0;
            for (int i = 0; i < (cellCount); ++i)
                if (clearedRows[i])
                    for (int j = 0; j < (cellCount); ++j)
                        cells[i][j].vanish(lastPutPiecePos);
                    
                
            
            for (int j = 0; j < (cellCount); ++j) {
                if (clearedCols[j]) {
                    pan += (2.0F * (j - ((cellCount) / 2))) / ((float) (cellCount));
                    for (int i = 0; i < (cellCount); ++i) {
                        cells[i][j].vanish(lastPutPiecePos);
                    }
                }
            }
            if (Klooni.soundsEnabled()) {
                pan = MathUtils.clamp(pan, (-1), 1);
                stripClearSound.play(MathUtils.random(0.7F, 1.0F), MathUtils.random(0.8F, 1.2F), pan);
            }
        }
        return clearCount;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(cellCount);
        for (int i = 0; i < (cellCount); ++i)
            for (int j = 0; j < (cellCount); ++j)
                cells[i][j].write(out);
            
        
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        final int savedCellCount = in.readInt();
        if (savedCellCount != (cellCount))
            throw new IOException("Invalid cellCount saved.");
        
        for (int i = 0; i < (cellCount); ++i)
            for (int j = 0; j < (cellCount); ++j)
                cells[i][j].read(in);
            
        
    }
}

