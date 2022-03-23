package view;

import model.Difficulty;
import model.PlayableMinesweeper;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import notifier.IGameStateNotifier;

public class MinesweeperView implements IGameStateNotifier {
    public static final int MAX_TIME = 1;//in minutes
    public static final int TILE_SIZE = 53;
    public static final class AssetPath {
        public static final String CLOCK_ICON = "src/assets/clock_small.png";
        public static final String FLAG_ICON = "src/assets/flag_small.png";
        public static final String BOMB_ICON = "src/assets/bomb_small.png";
    }
    private PlayableMinesweeper gameModel;
    private JFrame window;
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenuItem easyGame, mediumGame, hardGame;
    private TileView[][] tiles;
    private JPanel world = new JPanel();
    private JPanel infoPanel = new JPanel();
    private JLabel timerView = new JLabel();
    private JLabel flagCountView = new JLabel();
    private JLabel bombCountView = new JLabel();
    private JOptionPane winLosePane = new JOptionPane();

    public MinesweeperView() {
        this.window = new JFrame("Minesweeper");
        this.menuBar = new JMenuBar();
        this.gameMenu = new JMenu("New Game");
        this.menuBar.add(gameMenu);
        
        this.easyGame = new JMenuItem("Easy");
        this.gameMenu.add(this.easyGame);
        this.easyGame.addActionListener((ActionEvent e) -> {
            if (gameModel != null) 
                gameModel.startNewGame(Difficulty.EASY);
        });
        this.mediumGame = new JMenuItem("Medium");
        this.gameMenu.add(this.mediumGame);
        this.mediumGame.addActionListener((ActionEvent e) -> {
            if (gameModel != null)
                gameModel.startNewGame(Difficulty.MEDIUM);
        });
        this.hardGame = new JMenuItem("Hard");
        this.gameMenu.add(this.hardGame);
        this.hardGame.addActionListener((ActionEvent e) -> {
            if (gameModel != null)
                gameModel.startNewGame(Difficulty.HARD);
        });
        
        this.window.setJMenuBar(this.menuBar);

        infoPanel.setLayout(new FlowLayout());
        try {
            JLabel clockIcon = new JLabel(new ImageIcon(ImageIO.read(new File(AssetPath.CLOCK_ICON))));
            clockIcon.setSize(new DimensionUIResource(10, 10));
            infoPanel.add(clockIcon);
            infoPanel.add(new JLabel("TIME ELAPSED: "));
            infoPanel.add(this.timerView);
            JLabel bombIcon = new JLabel(new ImageIcon(ImageIO.read(new File(AssetPath.BOMB_ICON))));
            infoPanel.add(bombIcon);
            infoPanel.add(new JLabel("BOMBS:"));
            infoPanel.add(bombCountView);
            JLabel flagIcon = new JLabel(new ImageIcon(ImageIO.read(new File(AssetPath.FLAG_ICON))));
            flagIcon.setSize(new DimensionUIResource(10, 10));
            infoPanel.add(flagIcon);
            infoPanel.add(new JLabel("FLAG: "));
            infoPanel.add(this.flagCountView);
        }
        catch (IOException e) {
            System.out.println("Unable to locate resources");
            System.out.println(e);
        }

        this.window.setLayout(new GridBagLayout());
        GridBagConstraints layoutConstraints = new GridBagConstraints();
        layoutConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 0;
        this.window.add(infoPanel, layoutConstraints);
        layoutConstraints.fill = GridBagConstraints.BOTH;
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 1;
        layoutConstraints.gridwidth = 2;
        layoutConstraints.weightx = 1.0;
        layoutConstraints.weighty = 1.0;
        this.window.add(world, layoutConstraints);
        this.window.setSize(1000, 10);
        this.window.setVisible(true);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.world.setVisible(true);
    }

    public MinesweeperView(PlayableMinesweeper gameModel) {
        this();
        this.setGameModel(gameModel);
    }

    public void setGameModel(PlayableMinesweeper newGameModel) {
        this.gameModel = newGameModel;
        this.gameModel.setGameStateNotifier(this);
    }

    @Override
    public void notifyNewGame(int row, int col) {
        this.flagCountView.setText("0");
        this.window.setSize(col * TILE_SIZE, row * TILE_SIZE + 30);
        this.world.removeAll();
        
        this.tiles = new TileView[row][col];
        for (int i=0; i<row; ++i) {
            for (int j=0; j<col; ++j) {
                TileView temp = new TileView(j, i); 
                temp.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent arg0) {
                        if (arg0.getButton() == MouseEvent.BUTTON1){
                            if (gameModel!=null)
                                gameModel.open(temp.getPositionX(), temp.getPositionY());
                        } 
                        else if (arg0.getButton() == MouseEvent.BUTTON3) {
                            if (gameModel!=null)
                                gameModel.toggleFlag(temp.getPositionX(), temp.getPositionY());
                        } 
                    }
                });
                this.tiles[i][j] = temp;
                this.world.add(temp);
            }
        }
        this.world.setLayout(new GridLayout(row, col));
        this.world.setVisible(false);
        this.world.setVisible(true);
        this.world.repaint();
    }
    @Override
    public void notifyGameLost() {
        winLosePane.showMessageDialog(infoPanel, "You LOST :(");
        this.removeAllTileEvents();
        //throw new UnsupportedOperationException();
    }
    @Override
    public void notifyGameWon() {
        winLosePane.showMessageDialog(infoPanel, "You WON!");
        this.removeAllTileEvents();
        //throw new UnsupportedOperationException();
    }

    private void removeAllTileEvents() {
        for (int i=0; i<this.tiles.length; ++i)
            for (int j=0; j<this.tiles[i].length; ++j) 
                this.tiles[i][j].removalAllMouseListeners();
    }

    @Override
    public void notifyFlagCountChanged(int newFlagCount) {
        this.flagCountView.setText(Integer.toString(newFlagCount));
    }

    @Override
    public void notifyBombCountChanged(int newBombCount) {
        this.bombCountView.setText(Integer.toString(newBombCount));
    }

    @Override
    public void notifyTimeElapsedChanged(Duration newTimeElapsed) {
        timerView.setText(
                    String.format("%d:%02d", newTimeElapsed.toMinutesPart(), newTimeElapsed.toSecondsPart()));  
        
    }

    @Override
    public void notifyOpened(int x, int y, int explosiveNeighbourCount) {
        this.tiles[y][x].notifyOpened(explosiveNeighbourCount);
    }

    @Override
    public void notifyFlagged(int x, int y) {
        this.tiles[y][x].notifyFlagged();
    }

    @Override
    public void notifyUnflagged(int x, int y) {
        this.tiles[y][x].notifyUnflagged();
    }

    @Override
    public void notifyExploded(int x, int y) {
        this.tiles[y][x].notifyExplode();
    }

}