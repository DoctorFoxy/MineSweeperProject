package model;

public class Tile extends AbstractTile {
    private boolean opened;
    private boolean flagged;

    public Tile() {
        this.opened = false;
        this.flagged = false;
    }

    @Override
    public boolean open() {
        if (!this.isOpened() && !this.isFlagged()) {
            opened = true;
            return true;
        }

        return false;
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
        return false;
    }

    @Override
    public boolean isOpened() {
        return opened;
    }

}
