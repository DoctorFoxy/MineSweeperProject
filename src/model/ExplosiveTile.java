package model;

public class ExplosiveTile extends Tile {

    public ExplosiveTile() {

    }

    @Override
    public boolean open() {
        return super.open();
    }

    @Override
    public boolean isExplosive() {
        return true;
    }
}
