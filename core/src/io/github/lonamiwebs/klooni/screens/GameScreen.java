package io.github.lonamiwebs.klooni.screens;

import io.github.lonamiwebs.klooni.game.BonusParticleHandler;
import io.github.lonamiwebs.klooni.game.BaseScorer;
import io.github.lonamiwebs.klooni.serializer.BinSerializable;
import io.github.lonamiwebs.klooni.serializer.BinSerializer;
import io.github.lonamiwebs.klooni.game.Board;
import codesmells.annotations.CC;
import io.github.lonamiwebs.klooni.game.Piece;
import com.badlogic.gdx.audio.Sound;
import Input.Keys;
import java.io.DataInputStream;
import io.github.lonamiwebs.klooni.game.Scorer;
import java.io.IOException;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import Gdx.files;
import java.io.DataOutputStream;
import io.github.lonamiwebs.klooni.Klooni;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import GL20.GL_COLOR_BUFFER_BIT;
import io.github.lonamiwebs.klooni.game.TimeScorer;
import com.badlogic.gdx.InputProcessor;
import io.github.lonamiwebs.klooni.game.GameLayout;
import Gdx.audio;
import Gdx.gl;
import codesmells.annotations.LM;
import io.github.lonamiwebs.klooni.game.PieceHolder;

@CC
class GameScreen implements InputProcessor , Screen , BinSerializable {
    private final BaseScorer scorer;

    private final BonusParticleHandler bonusParticleHandler;

    private final Board board;

    private final PieceHolder holder;

    private final SpriteBatch batch;

    private final Sound gameOverSound;

    private final PauseMenuStage pauseMenu;

    private final int gameMode;

    private boolean gameOverDone;

    private int savedMoneyScore;

    private static final int BOARD_SIZE = 10;

    private static final int HOLDER_PIECE_COUNT = 3;

    static final int GAME_MODE_SCORE = 0;

    static final int GAME_MODE_TIME = 1;

    private static final String SAVE_DAT_FILENAME = ".klooni.sav";

    @LM
    GameScreen(final Klooni game, final int gameMode) {
        this(game, gameMode, true);
    }

    @LM
    GameScreen(final Klooni game, final int gameMode, final boolean loadSave) {
        batch = new SpriteBatch();
        this.gameMode = gameMode;
        final GameLayout layout = new GameLayout();
        switch (gameMode) {
            case GameScreen.GAME_MODE_SCORE :
                scorer = new Scorer(game, layout);
                break;
            case GameScreen.GAME_MODE_TIME :
                scorer = new TimeScorer(game, layout);
                break;
            default :
                throw new RuntimeException(("Unknown game mode given: " + gameMode));
        }
        board = new Board(layout, GameScreen.BOARD_SIZE);
        holder = new PieceHolder(layout, board, GameScreen.HOLDER_PIECE_COUNT, board.cellSize);
        pauseMenu = new PauseMenuStage(layout, game, scorer, gameMode);
        bonusParticleHandler = new BonusParticleHandler(game);
        gameOverSound = audio.newSound(files.internal("sound/game_over.mp3"));
        if (gameMode == (GameScreen.GAME_MODE_SCORE)) {
            if (loadSave) {
                tryLoad();
            }else {
                deleteSave();
            }
        }
    }

    private boolean isGameOver() {
        for (Piece piece : holder.getAvailablePieces())
            if (board.canPutPiece(piece))
                return false;
            
        
        return true;
    }

    private void doGameOver(final String gameOverReason) {
        if (!(gameOverDone)) {
            gameOverDone = true;
            saveMoney();
            holder.enabled = false;
            pauseMenu.showGameOver(gameOverReason, ((scorer) instanceof TimeScorer));
            if (Klooni.soundsEnabled())
                gameOverSound.play();
            
            if ((gameMode) == (GameScreen.GAME_MODE_SCORE))
                deleteSave();
            
        }
    }

    @Override
    public void show() {
        if (pauseMenu.isShown())
            Gdx.input.setInputProcessor(pauseMenu);
        else
            Gdx.input.setInputProcessor(this);
        
    }

    private void showPauseMenu() {
        saveMoney();
        pauseMenu.show();
        save();
    }

    @Override
    public void pause() {
        save();
    }

    @Override
    @LM
    public void render(float delta) {
        Klooni.theme.glClearBackground();
        gl.glClear(GL_COLOR_BUFFER_BIT);
        if ((scorer.isGameOver()) && (!(pauseMenu.isShown()))) {
            doGameOver(scorer.gameOverReason());
        }
        batch.begin();
        scorer.draw(batch);
        board.draw(batch);
        holder.update();
        holder.draw(batch);
        bonusParticleHandler.run(batch);
        batch.end();
        if ((pauseMenu.isShown()) || (pauseMenu.isHiding())) {
            pauseMenu.act(delta);
            pauseMenu.draw();
        }
    }

    @Override
    public void dispose() {
        pauseMenu.dispose();
    }

    @Override
    public boolean keyUp(int keycode) {
        if ((keycode == (Keys.P)) || (keycode == (Keys.BACK)))
            showPauseMenu();
        
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return holder.pickPiece();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        PieceHolder.DropResult result = holder.dropPiece();
        if (!(result.dropped))
            return false;
        
        if (result.onBoard) {
            scorer.addPieceScore(result.area);
            int bonus = scorer.addBoardScore(board.clearComplete(), board.cellCount);
            if (bonus > 0)
                bonusParticleHandler.addBonus(result.pieceCenter, bonus);
            
            if (isGameOver()) {
                doGameOver("no moves left");
            }
        }
        return true;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private void saveMoney() {
        int nowScore = scorer.getCurrentScore();
        int newMoneyScore = nowScore - (savedMoneyScore);
        savedMoneyScore = nowScore;
        Klooni.addMoneyFromScore(newMoneyScore);
    }

    private void save() {
        if (((gameOverDone) || ((gameMode) != (GameScreen.GAME_MODE_SCORE))) || ((scorer.getCurrentScore()) == 0))
            return ;
        
        final FileHandle handle = files.local(GameScreen.SAVE_DAT_FILENAME);
        try {
            BinSerializer.serialize(this, handle.write(false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteSave() {
        final FileHandle handle = files.local(GameScreen.SAVE_DAT_FILENAME);
        if (handle.exists())
            handle.delete();
        
    }

    static boolean hasSavedData() {
        return files.local(GameScreen.SAVE_DAT_FILENAME).exists();
    }

    private boolean tryLoad() {
        final FileHandle handle = files.local(GameScreen.SAVE_DAT_FILENAME);
        if (handle.exists()) {
            try {
                BinSerializer.deserialize(this, handle.read());
                savedMoneyScore = scorer.getCurrentScore();
                deleteSave();
                return true;
            } catch (IOException ignored) {
            }
        }
        return false;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(gameMode);
        board.write(out);
        holder.write(out);
        scorer.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        int savedGameMode = in.readInt();
        if (savedGameMode != (gameMode))
            throw new IOException("A different game mode was saved. Cannot load the save data.");
        
        board.read(in);
        holder.read(in);
        scorer.read(in);
    }
}

