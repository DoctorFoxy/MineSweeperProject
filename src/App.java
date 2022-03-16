//import model.Minesweeper;
import model.Difficulty;
import model.Minesweeper;
import model.PlayableMinesweeper;
import view.MinesweeperView;
import view.TileView;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class App {
    public static void main(String[] args) throws Exception {
        PlayableMinesweeper model = new Minesweeper();
        MinesweeperView mainView = new MinesweeperView(model);

        model.startNewGame(Difficulty.EASY);


    }
}
