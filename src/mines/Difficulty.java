package mines;

public enum Difficulty {
    BEGINNER(9, 9, 10),
    INTERMEDIATE(16, 16, 40),
    EXPERT(30, 16, 99);
    
    private final int cols;
    private final int rows;
    private final int mines;
    
    Difficulty(int cols, int rows, int mines) {
        this.cols = cols;
        this.rows = rows;
        this.mines = mines;
    }
    
    public int getCols() {
        return cols;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getMines() {
        return mines;
    }
}