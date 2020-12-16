import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.StdOut;

// Models a board in the 8-puzzle game or its generalization.
public class Board {
    private int[][] tiles;
    private int N;
    private int hamming;
    private int manhattan;

    // Construct a board from an N-by-N array of tiles, where
    // tiles[i][j] = tile at row i and column j, and 0 represents the blank
    // square.
    public Board(int[][] tiles) {
        this.N = tiles.length;
        this.tiles = new int[N][N];

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                this.tiles[i][j] = tiles[i][j]; // construct the board

        this.hamming = 0;
        // loop every tile
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (this.tiles[i][j] == 0) //skip the [0][0] == 0
                    continue;
                if (this.tiles[i][j] != i * N + j + 1) // if blocks in the wrong position
                    this.hamming++;
            }
        }

        this.manhattan = 0;
        // loop every tile
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != i * N + j + 1 && tiles[i][j] != 0) { // if in the wrong position
                    int row = (tiles[i][j] - 1) / N; //row gap
                    int col = (tiles[i][j] - 1) - (row * N); //column gap
                    this.manhattan += Math.abs(i - row) + Math.abs(j - col);
                }
            }
        }
    }


    // Tile at row i and column j.
    public int tileAt(int i, int j) {
        return tiles[i][j];
    }

    // Size of this board.
    public int size() {
        return N * N;
    }

    // Number of tiles out of place.
    public int hamming() {
        return this.hamming;
    }

    // Sum of Manhattan distances between tiles and goal.
    public int manhattan() {
        return this.manhattan;
    }

    // Is this board the goal board?
    public boolean isGoal() {
        return blankPos() == N * N && inversions() == 0;
    }

    // Is this board solvable?
    public boolean isSolvable() {
        if (N % 2 != 0) {  // odd broad size
            if (inversions() % 2 != 0) { //if inversions even
                return false;
            }
        } else { // even broad size
            int sum = ((blankPos() - 1) / N) + inversions();
            if (sum % 2 == 0) {
                return false;
            }
        }
        return true;
    }

    // Does this board equal that?
    public boolean equals(Board that) {
        if (that == this) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (that.getClass() != this.getClass()) {
            return false;
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (this.tiles[i][j] != that.tiles[i][j])
                    return false; //return false if not equal that
            }
        }
        return true;
    }

    // All neighboring boards.
    public Iterable<Board> neighbors() {
        LinkedQueue<Board> list = new LinkedQueue<>();
        int[][] neighbor;
        int i = (blankPos() - 1) / N; // row
        int j = (blankPos() - 1) % N; // col
        int temp;

        if (i - 1 >= 0 && i < N) { // checking north
            neighbor = cloneTiles(); //clone into neighbor array
            temp = neighbor[i][j]; //swap neighbor[i][j] with neighbor[i - 1][j]
            neighbor[i][j] = neighbor[i - 1][j];
            neighbor[i - 1][j] = temp;
            list.enqueue(new Board(neighbor));
        }
        if (i + 1 > 0 && i < N - 1) { // checking south
            neighbor = cloneTiles(); //clone into neighbor array
            temp = neighbor[i][j]; //swap neighbor[i][j] with neighbor[i + 1][j]
            neighbor[i][j] = neighbor[i + 1][j];
            neighbor[i + 1][j] = temp;
            list.enqueue(new Board(neighbor));
        }
        if (j + 1 > 0 && j < N - 1) { // checking east
            neighbor = cloneTiles(); //clone into neighbor array
            temp = neighbor[i][j]; //swap neighbor[i][j] with neighbor[i][j + 1]
            neighbor[i][j] = neighbor[i][j + 1];
            neighbor[i][j + 1] = temp;
            list.enqueue(new Board(neighbor));
        }
        if (j - 1 >= 0 && j < N) { // checking west
            neighbor = cloneTiles(); //clone into neighbor array
            temp = neighbor[i][j]; //swap neighbor[i][j] with neighbor[i][j - 1]
            neighbor[i][j] = neighbor[i][j - 1];
            neighbor[i][j - 1] = temp;
            list.enqueue(new Board(neighbor));
        }
        return list;
    }

    // String representation of this board.
    public String toString() {
        String s = N + "\n";
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s += String.format("%2d", tiles[i][j]);
                if (j < N - 1) {
                    s += " ";
                }
            }
            if (i < N - 1) {
                s += "\n";
            }
        }
        return s;
    }

    // Helper method that returns the position (in row-major order) of the
    // blank (zero) tile.
    private int blankPos() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] == 0) {
                    return N * i + j + 1;
                }
            }
        }
        return -1;
    }

    // Helper method that returns the number of inversions.
    private int inversions() {
        int count = 0;
        int p1 = 0;
        int p2 = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                p1++;
                for (int k = 0; k < N; k++) {
                    for (int l = 0; l < N; l++) {
                        p2++;
                        if (tiles[i][j] == 0 || tiles[k][l] == 0)
                            continue;
                        else if (p1 < p2 && tiles[i][j] > tiles[k][l]) {
                            count++;
                        }
                    }
                }
                p2 = 0;
            }
        }
        return count;
    }

    // Helper method that clones the tiles[][] array in this board and
    // returns it.
    private int[][] cloneTiles() {
        int[][] c = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                c[i][j] = tiles[i][j];
        return c;
    }

    // Test client. [DO NOT EDIT]
    public static void main(String[] args) {
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board board = new Board(tiles);
        StdOut.println(board.hamming());
        StdOut.println(board.manhattan());
        StdOut.println(board.isGoal());
        StdOut.println(board.isSolvable());
        for (Board neighbor : board.neighbors()) {
            StdOut.println(neighbor);
        }
    }
}
