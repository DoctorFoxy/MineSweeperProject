package model;

import notifier.IGameStateNotifier;
import view.MinesweeperView;
import view.TileView;

import java.sql.Time;
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

    public Minesweeper() {
        flagCount = 0;
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

    }

    @Override
    public void startNewGame(int row, int col, int explosionCount) {
        System.out.println("Starting Game...");
        this.row = row;
        this.col = col;
        this.explosionCount = explosionCount;

        world = new Tile[row][col];

        for (int colIndex = 0 ; colIndex < col ; colIndex++) {
            for (int rowIndex = 0 ; rowIndex < row ; rowIndex++) {
                world[rowIndex][colIndex] = new Tile(false);
            }
        }

        Random random = new Random();
        random.setSeed(java.time.LocalTime.now().getNano());

        for (int bombIndex = 0 ; bombIndex < explosionCount ; bombIndex++) {
            boolean bombSet = false;
            while (!bombSet) {
                random.nextInt(row);
                random.nextInt(col);

                for (int x = 0 ; x < col ; x++) {
                    for (int y = 0; y < row; y++) {
                        if (!this.getTile(x, y).isExplosive()) {
                            bombSet = true;
                            this.getTile(x, y).setExplosive(true);
                            break;
                        }
                    }

                    if (bombSet) {
                        break;
                    }
                }
            }

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

        if (x < col && x > -1 && y < row && y > -1) {
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
        if (tempTile != null) {
            tempTile.open();
            this.viewNotifier.notifyOpened(x,y, explosiveNeighbourCount(x, y));
        }

        if (tempTile.isExplosive()) {
            this.viewNotifier.notifyExploded(x,y);
            this.viewNotifier.notifyGameLost();
        }

    }

    public int explosiveNeighbourCount(int x, int y) {
        int amount = 0;

        if (getTile(x-1,y-1) != null && getTile(x-1,y-1).isExplosive()) {
            amount++;
        }
        if (getTile(x-1,y) != null && getTile(x-1,y).isExplosive()) {
            amount++;
        }
        if (getTile(x-1,y+1) != null && getTile(x-1,y+1).isExplosive()) {
            amount++;
        }
        if (getTile(x,y-1) != null && getTile(x,y-1).isExplosive()) {
            amount++;
        }
        if (getTile(x,y+1) != null && getTile(x,y+1).isExplosive()) {
            amount++;
        }
        if (getTile(x+1,y-1) != null && getTile(x+1,y-1).isExplosive()) {
            amount++;
        }
        if (getTile(x+1,y) != null && getTile(x+1,y).isExplosive()) {
            amount++;
        }
        if (getTile(x+1,y+1) != null && getTile(x+1,y+1).isExplosive()) {
            amount++;
        }

        return amount;
    }

    @Override
    public void flag(int x, int y) {
        AbstractTile tempTile = getTile(x, y);
        if (tempTile != null) {
            getTile(x, y).flag();

            this.viewNotifier.notifyFlagged(x,y);
        }
    }

    @Override
    public void unflag(int x, int y) {
        AbstractTile tempTile = getTile(x, y);
        if (tempTile != null) {
            getTile(x, y).unflag();
            this.viewNotifier.notifyUnflagged(x,y);
        }
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
