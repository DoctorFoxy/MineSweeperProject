package model;

public class EmptyTile extends Tile {

    public EmptyTile() {

    }

    @Override
    public boolean open() {
        return super.open();
    }

    @Override
    public boolean isExplosive() {
        return false;
    }
}
