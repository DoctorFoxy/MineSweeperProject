package model;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Minesweeper extends AbstractMineSweeper {
    private int row;
    private int col;
    private int explosionCount;
    private int flagCount;

    private AbstractTile[][] world;

    private boolean firstTileRule;
    private boolean firstOpen;

    private int elapsedTime;
    private boolean stopTimer;

    public Minesweeper() {
        flagCount = 0;
        firstOpen = true;
        firstTileRule = true;
        elapsedTime = 0;
        stopTimer = false;

    }

    @Override
    public int getWidth() {
        return this.col;
    }

    @Override
    public int getHeight() {
        return this.row;
    }

    @Override
    public void startNewGame(Difficulty level) {
        switch (level) {
            case EASY -> this.startNewGame(8,8,10);
            case MEDIUM -> this.startNewGame(16,16,40);
            case HARD -> this.startNewGame(16,30,99);
        }
    }

    @Override
    public void startNewGame(int row, int col, int explosionCount) {
        System.out.println("Starting Game...");
        this.firstOpen = true;
        this.row = row;
        this.col = col;
        this.explosionCount = explosionCount;
        this.viewNotifier.notifyBombCountChanged(explosionCount);
        this.flagCount = 0;
        this.viewNotifier.notifyFlagCountChanged(flagCount);

        //Time stuff
        elapsedTime = 0;
        stopTimer = false;

        try {
            Timer elapsed = new Timer();
            elapsed.scheduleAtFixedRate(updateElapsedTime,0,1000);
        }
        catch (Exception ignored) {

        }

        // Create new world
        world = new Tile[row][col];

        for (int colIndex = 0 ; colIndex < col ; colIndex++) {
            for (int rowIndex = 0 ; rowIndex < row ; rowIndex++) {
                world[rowIndex][colIndex] = new Tile();
            }
        }

        for (int bombIndex = 0 ; bombIndex < explosionCount ; bombIndex++) {
            newExplosive(-1,-1);

        }

        this.viewNotifier.notifyNewGame(row, col);
    }

    TimerTask updateElapsedTime = new TimerTask() {
        @Override
        public void run() {
            if (!stopTimer) {
                viewNotifier.notifyTimeElapsedChanged(Duration.of(elapsedTime, ChronoUnit.SECONDS));
                elapsedTime++;
            }
        }
    };


    @Override
    public void toggleFlag(int x, int y) {
        AbstractTile tempTile = getTile(x, y);
        if (tempTile.isFlagged()) {
            this.unflag(x,y);
        }
        else {
            if (flagCount != explosionCount) {
                this.flag(x,y);
            }
        }

    }

    @Override
    public AbstractTile getTile(int x, int y) {
        if (x < col && x >= 0 && y < row && y >= 0) {
            return world[y][x];
        }

        return null;
    }

    @Override
    public void setWorld(AbstractTile[][] world) {
        this.world = world;
        this.col = world[0].length;
        this.row = world.length;
    }

    @Override
    public void open(int x, int y) {
        AbstractTile toOpen = getTile(x, y);
        if (toOpen != null) {
            if (!toOpen.isFlagged()) {
                if (toOpen.isExplosive()) {  // EXPLOSIVE
                    if (firstOpen && firstTileRule) { // FIRST OPEN NEVER BOMB
                        firstOpen = false;
                        makeEmpty(x,y);
                        newExplosive(x,y);
                        open(x,y);
                    }
                    else {
                        firstOpen = false;
                        for (int tempX = 0; tempX < col; tempX++) {
                            for (int tempY = 0; tempY < row; tempY++) {
                                if (getTile(tempX, tempY).isExplosive()) {
                                    this.viewNotifier.notifyExploded(tempX, tempY);
                                }
                            }

                        }
                        
                        toOpen.open(this.viewNotifier);
                        stopTimer = true;
                    }
                } // NON EXPLOSIVE
                else {
                    firstOpen = false;

                    if (explosiveNeighbourCount(x,y) > 0) {
                        toOpen.open(this.viewNotifier);
                        this.viewNotifier.notifyOpened(x,y, explosiveNeighbourCount(x, y));
                    }
                    else { //If tile has no explosive neighbours, open all tiles around it
                        toOpen.open(this.viewNotifier);
                        this.viewNotifier.notifyOpened(x,y, explosiveNeighbourCount(x, y));

                        for(int i = -1; i <= 1; i++) {
                            for(int j = -1; j <= 1; j++) {
                                AbstractTile checkTile = getTile(x + i, y + j);
                                if(checkTile != null && !checkTile.isOpened()) {
                                    open(x + i, y + j);
                                }
                            }
                        }
                    }

                    checkWin();
                }
            }
        }

    }

    private boolean checkWin() {
        int unopened = col*row;
        for (AbstractTile[] column : world) {
            for (AbstractTile tile : column) {
                if (tile.isOpened()) {
                    unopened--;
                }
            }
        }

        if (unopened == explosionCount) {
            this.viewNotifier.notifyGameWon();
            this.stopTimer = true;
            return true;
        }
        return false;
    }

    private void newExplosive(int notX, int notY) {
        boolean bombSet = false;
        Random random = new Random();

        while (!bombSet) {
            int randomRow = random.nextInt(row);
            int randomCol = random.nextInt(col);

            if (!this.getTile(randomCol, randomRow).isExplosive() && randomCol != notX && randomRow != notY) {
                bombSet = true;
                makeExplosive(randomCol, randomRow);
            }
        }
    }

    public void makeExplosive(int x, int y) {
        if (x < col && x >= 0 && y < row && y >= 0) {
            world[y][x] = new ExplosiveTile();
        }
    }

    public void makeEmpty(int x, int y) {
        if (x < col && x >= 0 && y < row && y >= 0) {
            world[y][x] = new Tile();
        }
    }

    public int explosiveNeighbourCount(int x, int y) {
        int amount = 0;

        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(getTile(x + i, y + j) != null && getTile(x + i, y + j).isExplosive()) {
                    amount++;
                }
            }
        }

        return amount;
    }

    @Override
    public void flag(int x, int y) {
        AbstractTile tempTile = getTile(x, y);
        if (tempTile != null && !tempTile.isOpened()) {
            getTile(x, y).flag();

            this.flagCount++;
            this.viewNotifier.notifyFlagged(x,y);
        }
        this.viewNotifier.notifyFlagCountChanged(flagCount);
        this.viewNotifier.notifyBombCountChanged(explosionCount - flagCount);
    }

    @Override
    public void unflag(int x, int y) {
        AbstractTile tempTile = getTile(x, y);
        if (tempTile != null) {
            getTile(x, y).unflag();
            this.viewNotifier.notifyUnflagged(x,y);
            this.flagCount--;
        }

        this.viewNotifier.notifyFlagCountChanged(flagCount);
    }

    @Override
    public void deactivateFirstTileRule() {
        firstTileRule = false;
    }

    @Override
    public AbstractTile generateEmptyTile() {
        return new Tile();
    }

    @Override
    public AbstractTile generateExplosiveTile() {
        return new ExplosiveTile();
    }
}
