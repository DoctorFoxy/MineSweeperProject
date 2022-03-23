//import model.Minesweeper;
import model.Difficulty;
import model.Minesweeper;
import model.PlayableMinesweeper;
import view.MinesweeperView;

public class App {
    public static void main(String[] args) throws Exception {
        PlayableMinesweeper model = new Minesweeper();
        MinesweeperView mainView = new MinesweeperView(model);

        model.startNewGame(Difficulty.EASY);


    }
}
