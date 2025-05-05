package mines;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Board extends JPanel {
    private static final long serialVersionUID = 6195235521361212179L;

    private final int NUM_IMAGES = 13;
    private final int CELL_SIZE = 15;

    private final int COVER_FOR_CELL = 10;
    private final int MARK_FOR_CELL = 10;
    private final int EMPTY_CELL = 0;
    private final int MINE_CELL = 9;
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private final int DRAW_MINE = 9;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;

    private int[] field;
    private boolean inGame;
    private int mines_left;
    private Image[] img;
    private int mines;
    private int rows;
    private int cols;
    private int all_cells;
    private JLabel statusbar;
    private Difficulty difficulty;

    // Add these fields for competitive play
    private boolean competitiveMode = false;
    private boolean isPlayerTurn = true;
    private int playerScore = 0;
    private int robotScore = 0;
    private Timer robotTurnTimer;

    public Board(JLabel statusbar) {
        this(statusbar, Difficulty.INTERMEDIATE); // Default to intermediate
    }

    // Update constructor to initialize robot turn timer
    public Board(JLabel statusbar, Difficulty difficulty) {
        this.statusbar = statusbar;
        this.difficulty = difficulty;
        this.cols = difficulty.getCols();
        this.rows = difficulty.getRows();
        this.mines = difficulty.getMines();

        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {
            img[i] = (new ImageIcon(getClass().getClassLoader().getResource((i) + ".gif"))).getImage();
        }

        setDoubleBuffered(true);

        addMouseListener(new MinesAdapter());

        // Create timer for robot's turn in competitive mode
        robotTurnTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inGame && competitiveMode && !isPlayerTurn) {
                    makeRobotTurn();
                } else {
                    robotTurnTimer.stop();
                }
            }
        });

        newGame();
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.cols = difficulty.getCols();
        this.rows = difficulty.getRows();
        this.mines = difficulty.getMines();
        newGame();
    }

    // Method to toggle competitive mode
    public void setCompetitiveMode(boolean enabled) {
        this.competitiveMode = enabled;
        newGame();
        playerScore = 0;
        robotScore = 0;
        isPlayerTurn = true;
        updateStatusBar();
    }

    public boolean isCompetitiveMode() {
        return competitiveMode;
    }

    // Override existing newGame method
    public void newGame() {

        Random random;
        int current_col;

        int i = 0;
        int position = 0;
        int cell = 0;

        random = new Random();
        inGame = true;
        mines_left = mines;

        all_cells = rows * cols;
        field = new int[all_cells];

        for (i = 0; i < all_cells; i++)
            field[i] = COVER_FOR_CELL;

        if (competitiveMode) {
            isPlayerTurn = true;
            updateStatusBar();
        } else {
            statusbar.setText(Integer.toString(mines_left));
        }

        i = 0;
        while (i < mines) {

            position = (int) (all_cells * random.nextDouble());

            if ((position < all_cells) &&
                (field[position] != COVERED_MINE_CELL)) {

                current_col = position % cols;
                field[position] = COVERED_MINE_CELL;
                i++;

                if (current_col > 0) {
                    cell = position - 1 - cols;
                    if (cell >= 0)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;
                    cell = position - 1;
                    if (cell >= 0)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;

                    cell = position + cols - 1;
                    if (cell < all_cells)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;
                }

                cell = position - cols;
                if (cell >= 0)
                    if (field[cell] != COVERED_MINE_CELL)
                        field[cell] += 1;
                cell = position + cols;
                if (cell < all_cells)
                    if (field[cell] != COVERED_MINE_CELL)
                        field[cell] += 1;

                if (current_col < (cols - 1)) {
                    cell = position - cols + 1;
                    if (cell >= 0)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;
                    cell = position + cols + 1;
                    if (cell < all_cells)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;
                    cell = position + 1;
                    if (cell < all_cells)
                        if (field[cell] != COVERED_MINE_CELL)
                            field[cell] += 1;
                }
            }
        }
    }

    public void find_empty_cells(int j) {

        int current_col = j % cols;
        int cell;

        if (current_col > 0) {
            cell = j - cols - 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j - 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + cols - 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }
        }

        cell = j - cols;
        if (cell >= 0)
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

        cell = j + cols;
        if (cell < all_cells)
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL)
                    find_empty_cells(cell);
            }

        if (current_col < (cols - 1)) {
            cell = j - cols + 1;
            if (cell >= 0)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + cols + 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }

            cell = j + 1;
            if (cell < all_cells)
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL)
                        find_empty_cells(cell);
                }
        }

    }

    public void paint(Graphics g) {
        int cell = 0;
        int uncover = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cell = field[(i * cols) + j];

                // Only end the game for mines in non-competitive mode
                if (inGame && cell == MINE_CELL && !competitiveMode) {
                    inGame = false;
                }

                if (!inGame) {
                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                    }
                } else {
                    if (cell > COVERED_MINE_CELL)
                        cell = DRAW_MARK;
                    else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE), (i * CELL_SIZE), this);
            }
        }

        // Rest of the method stays the same
        if (competitiveMode) {
            updateStatusBar();
        } else {
            if (uncover == 0 && inGame) {
                inGame = false;
                statusbar.setText("Game won");
            } else if (!inGame)
                statusbar.setText("Game lost");
        }
    }

    // Add method to update status bar with current game state
    private void updateStatusBar() {
        if (competitiveMode) {
            if (!inGame) {
                if (playerScore > robotScore) {
                    statusbar.setText("Game over - You won! " + playerScore + " vs " + robotScore);
                } else if (robotScore > playerScore) {
                    statusbar.setText("Game over - Robot won! " + robotScore + " vs " + playerScore);
                } else {
                    statusbar.setText("Game over - It's a tie! " + playerScore + " vs " + robotScore);
                }
            } else {
                String turn = isPlayerTurn ? "Your turn" : "Robot's turn";
                statusbar.setText("Score: You " + playerScore + " - Robot " + robotScore + " | " + turn);
            }
        } else {
            statusbar.setText(Integer.toString(mines_left));
        }
    }

    // Method for robot to make its turn
    private void makeRobotTurn() {
        int[] move = getNextRobotMove();
        if (move != null) {
            makeCompetitiveMove(move[0], move[1], move[2] == 1, false);
        } else {
            // No valid moves, end turn
            switchTurn();
        }
    }

    // Method to handle competitive moves
    private void makeCompetitiveMove(int row, int col, boolean isMarkAction, boolean isPlayerMove) {
        int index = row * cols + col;
        boolean validMove = false;

        if (isMarkAction) {
            // Mark cell
            if (field[index] <= COVERED_MINE_CELL && mines_left > 0) {
                field[index] += MARK_FOR_CELL;
                mines_left--;
                validMove = true;
            }
        } else {
            // Uncover cell
            if (field[index] > MINE_CELL && field[index] <= COVERED_MINE_CELL) {
                field[index] -= COVER_FOR_CELL;
                validMove = true;

                // Award points for valid move
                if (isPlayerMove) {
                    playerScore++;
                } else {
                    robotScore++;
                }

                // Handle hitting a mine
                if (field[index] == MINE_CELL) {
                    // In competitive mode, hitting a mine just costs points
                    if (isPlayerMove) {
                        playerScore -= 5;
                    } else {
                        robotScore -= 5;
                    }
                    switchTurn();
                } else if (field[index] == EMPTY_CELL) {
                    find_empty_cells(index);
                }
            }
        }

        if (validMove) {
            repaint();
            updateStatusBar();

            // Check if game is won
            int uncover = 0;
            for (int i = 0; i < all_cells; i++) {
                if (field[i] > MINE_CELL) {
                    uncover++;
                }
            }

            if (uncover == 0) {
                inGame = false;
                updateStatusBar();
                return;
            }

            // After a valid move, always switch turns unless a mine was hit
            // (since hitting a mine already switches turns)
            if (field[index] != MINE_CELL) {
                switchTurn();
            }
        }
    }

    // Method to switch turns
    private void switchTurn() {
        isPlayerTurn = !isPlayerTurn;
        updateStatusBar();

        if (!isPlayerTurn) {
            // Start robot's turn
            robotTurnTimer.start();
        } else {
            robotTurnTimer.stop();
        }
    }

    // For robot player to check if game is still active
    public boolean isGameActive() {
        return inGame;
    }

    // Get coordinates for robot's next move: [row, col, action]
    // action: 0 = uncover, 1 = mark
    public int[] getNextRobotMove() {
        // Simple strategy: find uncovered cells with numbers and look at their neighbors
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = i * cols + j;
                int cellValue = field[index];

                // If uncovered cell with number 1-8
                if (cellValue > 0 && cellValue < MINE_CELL) {
                    List<Integer> coveredNeighbors = new ArrayList<Integer>();
                    List<Integer> markedNeighbors = new ArrayList<Integer>();

                    // Check all neighbors
                    for (int ni = Math.max(0, i - 1); ni <= Math.min(rows - 1, i + 1); ni++) {
                        for (int nj = Math.max(0, j - 1); nj <= Math.min(cols - 1, j + 1); nj++) {
                            if (ni == i && nj == j) continue; // Skip self

                            int nindex = ni * cols + nj;
                            if (field[nindex] > COVERED_MINE_CELL) {
                                markedNeighbors.add(nindex);
                            } else if (field[nindex] > MINE_CELL) {
                                coveredNeighbors.add(nindex);
                            }
                        }
                    }

                    // If unmarked mines matches cell value, mark all covered neighbors
                    if (cellValue == coveredNeighbors.size() && !coveredNeighbors.isEmpty()) {
                        int randomIdx = new Random().nextInt(coveredNeighbors.size());
                        int moveIdx = coveredNeighbors.get(randomIdx);
                        return new int[]{moveIdx / cols, moveIdx % cols, 1}; // Mark
                    }

                    // If marked mines equals cell value, uncover remaining neighbors
                    if (cellValue == markedNeighbors.size() && !coveredNeighbors.isEmpty()) {
                        int randomIdx = new Random().nextInt(coveredNeighbors.size());
                        int moveIdx = coveredNeighbors.get(randomIdx);
                        return new int[]{moveIdx / cols, moveIdx % cols, 0}; // Uncover
                    }
                }
            }
        }

        // If no logical move found, make random move on uncovered cell
        List<Integer> validMoves = new ArrayList<Integer>();
        for (int i = 0; i < all_cells; i++) {
            if (field[i] > MINE_CELL && field[i] <= COVERED_MINE_CELL) {
                validMoves.add(i);
            }
        }

        if (!validMoves.isEmpty()) {
            int randomIdx = new Random().nextInt(validMoves.size());
            int moveIdx = validMoves.get(randomIdx);
            return new int[]{moveIdx / cols, moveIdx % cols, 0}; // Uncover
        }

        return null; // No moves available
    }

    // Execute robot move
    public void makeRobotMove(int row, int col, boolean isMarkAction) {
        int index = row * cols + col;

        if (isMarkAction) {
            // Mark cell
            if (field[index] <= COVERED_MINE_CELL && mines_left > 0) {
                field[index] += MARK_FOR_CELL;
                mines_left--;
                statusbar.setText(Integer.toString(mines_left));
            }
        } else {
            // Uncover cell
            if (field[index] > MINE_CELL && field[index] <= COVERED_MINE_CELL) {
                field[index] -= COVER_FOR_CELL;

                if (field[index] == MINE_CELL)
                    inGame = false;
                if (field[index] == EMPTY_CELL)
                    find_empty_cells(index);
            }
        }

        repaint();
    }

    // Override existing MinesAdapter to handle competitive play
    class MinesAdapter extends MouseAdapter {
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean rep = false;

            // Modify to add competitive mode logic
            if (competitiveMode && !isPlayerTurn) {
                return; // Not player's turn
            }

            if (!inGame) {
                newGame();
                repaint();
                return;
            }

            if ((x < cols * CELL_SIZE) && (y < rows * CELL_SIZE)) {

                if (competitiveMode) {
                    // Handle right-click (mark)
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        makeCompetitiveMove(cRow, cCol, true, true);
                    } else {
                        // Handle left-click (uncover)
                        makeCompetitiveMove(cRow, cCol, false, true);
                    }
                } else {
                    // Original non-competitive code
                    if (e.getButton() == MouseEvent.BUTTON3) {

                        if (field[(cRow * cols) + cCol] > MINE_CELL) {
                            rep = true;

                            if (field[(cRow * cols) + cCol] <= COVERED_MINE_CELL) {
                                if (mines_left > 0) {
                                    field[(cRow * cols) + cCol] += MARK_FOR_CELL;
                                    mines_left--;
                                    statusbar.setText(Integer.toString(mines_left));
                                } else
                                    statusbar.setText("No marks left");
                            } else {

                                field[(cRow * cols) + cCol] -= MARK_FOR_CELL;
                                mines_left++;
                                statusbar.setText(Integer.toString(mines_left));
                            }
                        }

                    } else {

                        if (field[(cRow * cols) + cCol] > COVERED_MINE_CELL) {
                            return;
                        }

                        if ((field[(cRow * cols) + cCol] > MINE_CELL) &&
                            (field[(cRow * cols) + cCol] < MARKED_MINE_CELL)) {

                            field[(cRow * cols) + cCol] -= COVER_FOR_CELL;
                            rep = true;

                            if (field[(cRow * cols) + cCol] == MINE_CELL)
                                inGame = false;
                            if (field[(cRow * cols) + cCol] == EMPTY_CELL)
                                find_empty_cells((cRow * cols) + cCol);
                        }
                    }

                    if (rep)
                        repaint();
                }
            }
        }
    }
}