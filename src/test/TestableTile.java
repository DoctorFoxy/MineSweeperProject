package test;

import notifier.IGameStateNotifier;
import notifier.ITileStateNotifier;

public interface TestableTile {
    boolean open(IGameStateNotifier notifier);
    void flag();
    void unflag();
    boolean isFlagged();
    boolean isExplosive();
    void setTileNotifier(ITileStateNotifier notifier);
}
