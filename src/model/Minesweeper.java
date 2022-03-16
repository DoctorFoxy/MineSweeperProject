package model;

import notifier.IGameStateNotifier;
import view.MinesweeperView;
import view.TileView;

import javax.xml.datatype.Duration;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;
import java.time.LocalDateTime;

public class Minesweeper extends AbstractMineSweeper {
    private int row;
    private int col;
    private int explosionCount;
    private AbstractTile[][] world;
    private int flagCount;
    private TileView tileView;

    private boolean firstOpen;

    public Minesweeper() {
        flagCount = 0;
        firstOpen = true;
    }

    @Override
    public int getWidth() {
        return col;
    }

    @Override
    public int getHeight() {
        return row;
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

        world = new Tile[row][col];

        for (int colIndex = 0 ; colIndex < col ; colIndex++) {
            for (int rowIndex = 0 ; rowIndex < row ; rowIndex++) {
                world[rowIndex][colIndex] = new Tile(false);
            }
        }

        for (int bombIndex = 0 ; bombIndex < explosionCount ; bombIndex++) {
            newExplosive(-1,-1);

        }

        this.viewNotifier.notifyNewGame(row, col);
    }

    @Override
    public void toggleFlag(int x, int y) {
        AbstractTile tempTile = getTile(x, y);
        if (tempTile.isFlagged()) {
            this.unflag(x,y);
        }
        else {
            this.flag(x,y);
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
        AbstractTile tempTile = getTile(x, y);

        if (tempTile != null && tempTile.isFlagged() == false) {
            if (tempTile.isExplosive() && firstOpen) {
                firstOpen = false;
                tempTile.setExplosive(false);
                newExplosive(x, y);
                open(x, y);
            }
            else if (tempTile.isExplosive()) { //If tile is explosive, notify the view
                firstOpen = false;

                for (int tempX = 0; tempX < col; tempX++) {
                    for (int tempY = 0; tempY < row; tempY++) {
                        if (getTile(tempX, tempY).isExplosive()) {
                            this.viewNotifier.notifyExploded(tempX, tempY);
                        }
                    }

                }

                this.viewNotifier.notifyGameLost();
            }

            else { //If tile has explosive neighbours just open and nothing else
                firstOpen = false;

                if (explosiveNeighbourCount(x,y) > 0) {
                    tempTile.open();
                    this.viewNotifier.notifyOpened(x,y, explosiveNeighbourCount(x, y));
                }
                else { //If tile has no explosive neighbours, open all tiles around it
                    tempTile.open();
                    this.viewNotifier.notifyOpened(x,y, explosiveNeighbourCount(x, y));

                    for(int i = -1; i <= 1; i++) {
                        for(int j = -1; j <= 1; j++) {
                            AbstractTile checkTile = getTile(x + i, y + j);
                            if(checkTile != null && checkTile.isOpened() == false) {
                                open(x + i, y + j);
                            }
                        }
                    }
                }
            }
        }

    }

    private void newExplosive(int notX, int notY) {
        boolean bombSet = false;
        Random random = new Random();

        while (!bombSet) {
            int randomRow = random.nextInt(row);
            int randomCol = random.nextInt(col);

            if (!this.getTile(randomCol, randomRow).isExplosive() && randomCol != notX && randomRow != notY) {
                bombSet = true;
                this.getTile(randomCol, randomRow).setExplosive(true);
                break;
            }
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
        if (tempTile != null && tempTile.isOpened() == false) {
            getTile(x, y).flag();

            this.flagCount++;
            this.viewNotifier.notifyFlagged(x,y);
        }
        this.viewNotifier.notifyFlagCountChanged(flagCount);
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

    }

    @Override
    public AbstractTile generateEmptyTile() {
        return new Tile(false);
    }

    @Override
    public AbstractTile generateExplosiveTile() {
        return new Tile(true);
    }
}
