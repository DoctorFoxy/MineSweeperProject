package model;

import notifier.IGameStateNotifier;

public class ExplosiveTile extends Tile {

    public ExplosiveTile() {

    }

    @Override
    public boolean open(IGameStateNotifier notifier) {
        notifier.notifyGameLost();
        return super.open(notifier);
    }

    @Override
    public boolean isExplosive() {
        return true;
    }
}
