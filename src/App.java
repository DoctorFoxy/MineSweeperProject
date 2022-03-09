//import model.Minesweeper;
import model.Minesweeper;
import model.PlayableMinesweeper;
import view.MinesweeperView;
import view.TileView;

import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        PlayableMinesweeper model = new Minesweeper();
        MinesweeperView mainView = new MinesweeperView(model);

        model.startNewGame(10,8,5);

        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        System.out.println("You entered string " + s);


        /**
            Your code to bind your game model to the game user interface
        */

        //model.startNewGame(Difficulty.EASY);
    }
}
