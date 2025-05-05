package mines;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

public class RobotPlayer {
    private Board board;
    private Timer timer;
    private Random random;
    private boolean isActive = false;
    private int moveDelay = 500;
    
    public RobotPlayer(Board board) {
        this.board = board;
        this.random = new Random();
        
        // Create timer for automated moves
        this.timer = new Timer(moveDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (board.isGameActive()) {
                    makeMove();
                } else {
                    stop(); // Stop when game is over
                }
            }
        });
    }
    
    public void start() {
        if (!isActive) {
            isActive = true;
            timer.start();
        }
    }
    
    public void stop() {
        if (isActive) {
            isActive = false;
            timer.stop();
        }
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setMoveDelay(int delay) {
        this.moveDelay = delay;
        timer.setDelay(delay);
    }
    
    private void makeMove() {
        int[] move = board.getNextRobotMove();
        if (move != null) {
            board.makeRobotMove(move[0], move[1], move[2] == 1);
        }
    }
}