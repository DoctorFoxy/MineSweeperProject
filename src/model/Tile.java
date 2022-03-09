package model;

public class Tile extends AbstractTile {
    private boolean opened;
    private boolean flagged;
    private boolean explosive;

    public Tile(boolean explosive) {
        this.explosive = explosive;
        this.opened = false;
        this.flagged = false;
    }

    @Override
    public boolean open() {
        opened = true;
        return true;
    }

    @Override
    public void flag() {
        flagged = true;
    }

    @Override
    public void unflag() {
        flagged = false;
    }

    @Override
    public boolean isFlagged() {
        return flagged;
    }

    @Override
    public boolean isExplosive() {
        return explosive;
    }

    @Override
    public boolean isOpened() {
        return opened;
    }

    public void setExplosive(boolean toSet) {
        this.explosive = toSet;
    }
}
