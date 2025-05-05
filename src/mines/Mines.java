package mines;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Mines extends JFrame {
    private static final long serialVersionUID = 4772165125287256837L;
    
    private int WIDTH = 250;
    private int HEIGHT = 290;

    private JLabel statusbar;
    private Board board;
    
    public Mines() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setTitle("Minesweeper");

        // Create menu
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        
        // Add difficulty options
        JMenuItem beginnerItem = new JMenuItem("Beginner");
        JMenuItem intermediateItem = new JMenuItem("Intermediate");
        JMenuItem expertItem = new JMenuItem("Expert");
        
        // Add menu items to menu
        gameMenu.add(beginnerItem);
        gameMenu.add(intermediateItem);
        gameMenu.add(expertItem);
        menuBar.add(gameMenu);
        
        // Set menu bar
        setJMenuBar(menuBar);

        statusbar = new JLabel("");
        add(statusbar, BorderLayout.SOUTH);

        board = new Board(statusbar);
        add(board);
        
        // Add action listeners for difficulty levels
        beginnerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDifficulty(Difficulty.BEGINNER);
            }
        });
        
        intermediateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDifficulty(Difficulty.INTERMEDIATE);
            }
        });
        
        expertItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDifficulty(Difficulty.EXPERT);
            }
        });

        setResizable(false);
        setVisible(true);
    }
    
    private void setDifficulty(Difficulty difficulty) {
        // Update board with new difficulty
        board.setDifficulty(difficulty);
        
        // Adjust window size based on difficulty
        int cellSize = 15; // This should match CELL_SIZE in Board.java
        WIDTH = difficulty.getCols() * cellSize + 20;
        HEIGHT = difficulty.getRows() * cellSize + 70; // Extra space for menu and status bar
        
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Re-center window
        repaint();
    }
    
    public static void main(String[] args) {
        new Mines();
    }
}