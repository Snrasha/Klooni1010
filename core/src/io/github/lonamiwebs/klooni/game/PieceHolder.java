package io.github.lonamiwebs.klooni.game;

import com.badlogic.gdx.utils.Array;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import Interpolation.linear;
import com.badlogic.gdx.math.MathUtils;
import Gdx.files;
import Gdx.audio;
import Gdx.input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.audio.Sound;
import Gdx.graphics;

public class PieceHolder implements BinSerializable {
    final Rectangle area;

    private final Piece[] pieces;

    private final Sound pieceDropSound;

    private final Sound invalidPieceDropSound;

    private final Sound takePiecesSound;

    private final int count;

    private int heldPiece;

    public boolean enabled;

    private final Rectangle[] originalPositions;

    private final float pickedCellSize;

    private final Board board;

    public static final float DRAG_SPEED = 0.5F;

    public PieceHolder(final GameLayout layout, final Board board, final int pieceCount, final float pickedCellSize) {
        this.board = board;
        enabled = true;
        count = pieceCount;
        pieces = new Piece[count];
        originalPositions = new Rectangle[count];
        pieceDropSound = audio.newSound(files.internal("sound/piece_drop.mp3"));
        invalidPieceDropSound = audio.newSound(files.internal("sound/invalid_drop.mp3"));
        takePiecesSound = audio.newSound(files.internal("sound/take_pieces.mp3"));
        heldPiece = -1;
        this.pickedCellSize = pickedCellSize;
        area = new Rectangle();
        layout.update(this);
        takeMore();
    }

    private boolean handFinished() {
        for (int i = 0; i < (count); ++i)
            if ((pieces[i]) != null)
                return false;
            
        
        return true;
    }

    private void takeMore() {
        for (int i = 0; i < (count); ++i)
            pieces[i] = Piece.random();
        
        updatePiecesStartLocation();
        if (Klooni.soundsEnabled()) {
            takePiecesSound.play(1, MathUtils.random(0.8F, 1.2F), 0);
        }
    }

    private void updatePiecesStartLocation() {
        float perPieceWidth = (area.width) / (count);
        Piece piece;
        for (int i = 0; i < (count); ++i) {
            piece = pieces[i];
            if (piece == null)
                continue;
            
            piece.pos.set(((area.x) + (i * perPieceWidth)), area.y);
            piece.cellSize = Math.min(Math.min((perPieceWidth / (piece.cellCols)), ((area.height) / (piece.cellRows))), pickedCellSize);
            Rectangle rectangle = piece.getRectangle();
            piece.pos.y += ((area.height) - (rectangle.height)) * 0.5F;
            piece.pos.x += (perPieceWidth - (rectangle.width)) * 0.5F;
            originalPositions[i] = new Rectangle(piece.pos.x, piece.pos.y, piece.cellSize, piece.cellSize);
            piece.cellSize = 0.0F;
        }
    }

    public boolean pickPiece() {
        Vector2 mouse = new Vector2(input.getX(), ((graphics.getHeight()) - (input.getY())));
        final float perPieceWidth = (area.width) / (count);
        for (int i = 0; i < (count); ++i) {
            if ((pieces[i]) != null) {
                Rectangle maxPieceArea = new Rectangle(((area.x) + (i * perPieceWidth)), area.y, perPieceWidth, area.height);
                if (maxPieceArea.contains(mouse)) {
                    heldPiece = i;
                    return true;
                }
            }
        }
        heldPiece = -1;
        return false;
    }

    public Array<Piece> getAvailablePieces() {
        Array<Piece> result = new Array<Piece>(count);
        for (int i = 0; i < (count); ++i)
            if ((pieces[i]) != null)
                result.add(pieces[i]);
            
        
        return result;
    }

    public int calculateHeldPieceArea() {
        return (heldPiece) > (-1) ? pieces[heldPiece].calculateArea() : 0;
    }

    public Vector2 calculateHeldPieceCenter() {
        return (heldPiece) > (-1) ? pieces[heldPiece].calculateGravityCenter() : null;
    }

    public PieceHolder.DropResult dropPiece() {
        PieceHolder.DropResult result;
        if ((heldPiece) > (-1)) {
            boolean put;
            put = (enabled) && (board.putScreenPiece(pieces[heldPiece]));
            if (put) {
                if (Klooni.soundsEnabled()) {
                    float pitch = 1.104F - ((pieces[heldPiece].calculateArea()) * 0.04F);
                    pieceDropSound.play(1, pitch, 0);
                }
                result = new PieceHolder.DropResult(calculateHeldPieceArea(), calculateHeldPieceCenter());
                pieces[heldPiece] = null;
            }else {
                if (Klooni.soundsEnabled())
                    invalidPieceDropSound.play();
                
                result = new PieceHolder.DropResult(true);
            }
            heldPiece = -1;
            if (handFinished())
                takeMore();
            
        }else
            result = new PieceHolder.DropResult(false);
        
        return result;
    }

    public void update() {
        Piece piece;
        if ((heldPiece) > (-1)) {
            piece = pieces[heldPiece];
            Vector2 mouse = new Vector2(input.getX(), ((graphics.getHeight()) - (input.getY())));
            if (Klooni.onDesktop) {
                mouse.sub(((piece.getRectangle().width) * 0.5F), ((piece.getRectangle().height) * 0.5F));
            }else {
                mouse.sub(((piece.getRectangle().width) * 0.5F), (-(pickedCellSize)));
            }
            if (Klooni.shouldSnapToGrid())
                mouse.set(board.snapToGrid(piece, mouse));
            
            piece.pos.lerp(mouse, PieceHolder.DRAG_SPEED);
            piece.cellSize = linear.apply(piece.cellSize, pickedCellSize, PieceHolder.DRAG_SPEED);
        }
        Rectangle original;
        for (int i = 0; i < (count); ++i) {
            if (i == (heldPiece))
                continue;
            
            piece = pieces[i];
            if (piece == null)
                continue;
            
            original = originalPositions[i];
            piece.pos.lerp(new Vector2(original.x, original.y), 0.3F);
            piece.cellSize = linear.apply(piece.cellSize, original.width, 0.3F);
        }
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < (count); ++i) {
            if ((pieces[i]) != null) {
                pieces[i].draw(batch);
            }
        }
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(count);
        for (int i = 0; i < (count); ++i) {
            if ((pieces[i]) == null) {
                out.writeBoolean(false);
            }else {
                out.writeBoolean(true);
                pieces[i].write(out);
            }
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        final int savedPieceCount = in.readInt();
        if (savedPieceCount != (count))
            throw new IOException("Invalid piece count saved.");
        
        for (int i = 0; i < (count); i++)
            pieces[i] = (in.readBoolean()) ? Piece.read(in) : null;
        
        updatePiecesStartLocation();
    }

    public class DropResult {
        public final boolean dropped;

        public final boolean onBoard;

        public final int area;

        public final Vector2 pieceCenter;

        DropResult(final boolean dropped) {
            this.dropped = dropped;
            onBoard = false;
            area = 0;
            pieceCenter = null;
        }

        DropResult(final int area, final Vector2 pieceCenter) {
            dropped = onBoard = true;
            this.area = area;
            this.pieceCenter = pieceCenter;
        }
    }
}

