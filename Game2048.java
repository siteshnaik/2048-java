import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * 2048 - console edition, written in pure Java (zero dependencies).
 *
 * Compile:  javac Game2048.java
 * Run:      java Game2048
 *
 * Controls: w = up, a = left, s = down, d = right, r = restart, q = quit
 */
public class Game2048 {

    private static final int SIZE = 4;
    private static final int WIN_TILE = 2048;

    /** The four possible move directions. */
    private enum Direction { LEFT, RIGHT, UP, DOWN }

    /** Holds the tiles, the score, and every rule of the game. */
    private static class Board {

        private final int[][] tiles = new int[SIZE][SIZE];
        private final Random random = new Random();
        private int score = 0;

        int getScore() { return score; }

        int tileAt(int r, int c) { return tiles[r][c]; }

        /** Places a 2 (90% chance) or a 4 (10% chance) on a random empty cell. */
        void spawn() {
            List<int[]> emptyCells = new ArrayList<>();
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (tiles[r][c] == 0) {
                        emptyCells.add(new int[] { r, c });
                    }
                }
            }
            if (emptyCells.isEmpty()) {
                return;
            }
            int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
            tiles[cell[0]][cell[1]] = (random.nextInt(10) == 0) ? 4 : 2;
        }

        /**
         * Applies one move to the whole board.
         * Returns true if anything moved or merged, false if the move was a no-op.
         * Every merge adds the value of the newly created tile to the score.
         */
        boolean move(Direction dir) {
            boolean changed = false;
            int gained = 0;
            for (int i = 0; i < SIZE; i++) {
                int[] line = extractLine(i, dir);
                int[] merged = new int[SIZE];
                gained += slideLeft(line, merged);
                if (!Arrays.equals(line, merged)) {
                    changed = true;
                }
                writeLine(i, dir, merged);
            }
            score += gained;
            return changed;
        }

        /** True while at least one move or merge is still possible. */
        boolean canMove() {
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (tiles[r][c] == 0) return true;
                    if (c + 1 < SIZE && tiles[r][c] == tiles[r][c + 1]) return true;
                    if (r + 1 < SIZE && tiles[r][c] == tiles[r + 1][c]) return true;
                }
            }
            return false;
        }

        /** True once any tile reaches 2048. */
        boolean hasWon() {
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (tiles[r][c] >= WIN_TILE) return true;
                }
            }
            return false;
        }

        /**
         * Reads row/column {@code index} as a line whose index 0 is the side the
         * tiles slide toward. For LEFT it is the row itself, for RIGHT the reversed
         * row, for UP/DOWN the column read top-down / bottom-up.
         */
        private int[] extractLine(int index, Direction dir) {
            int[] line = new int[SIZE];
            for (int k = 0; k < SIZE; k++) {
                switch (dir) {
                    case LEFT:  line[k] = tiles[index][k];            break;
                    case RIGHT: line[k] = tiles[index][SIZE - 1 - k]; break;
                    case UP:    line[k] = tiles[k][index];            break;
                    case DOWN:  line[k] = tiles[SIZE - 1 - k][index]; break;
                }
            }
            return line;
        }

        /** Writes a slid line back to the board - the exact inverse of extractLine. */
        private void writeLine(int index, Direction dir, int[] line) {
            for (int k = 0; k < SIZE; k++) {
                switch (dir) {
                    case LEFT:  tiles[index][k]            = line[k]; break;
                    case RIGHT: tiles[index][SIZE - 1 - k] = line[k]; break;
                    case UP:    tiles[k][index]            = line[k]; break;
                    case DOWN:  tiles[SIZE - 1 - k][index] = line[k]; break;
                }
            }
        }

        /**
         * Slides one line toward index 0 and merges equal neighbours.
         * Each tile merges at most once per move (standard 2048 rules).
         * The result is written to {@code out}; the score gained is returned.
         */
        private static int slideLeft(int[] in, int[] out) {
            int gained = 0;
            int pos = 0; // next free slot in out
            int i = 0;   // read cursor
            while (i < SIZE) {
                if (in[i] == 0) { // skip empty cells
                    i++;
                    continue;
                }
                int j = i + 1;   // find the next non-empty cell
                while (j < SIZE && in[j] == 0) {
                    j++;
                }
                if (j < SIZE && in[j] == in[i]) { // merge the pair
                    out[pos++] = in[i] * 2;
                    gained += in[i] * 2;
                    i = j + 1;
                } else {                          // keep the single tile
                    out[pos++] = in[i];
                    i = j;
                }
            }
            return gained;
        }
    }

    /** Renders the board as plain ASCII (works on every terminal, including Windows cmd). */
    private static String render(Board board) {
        StringBuilder border = new StringBuilder("+");
        for (int c = 0; c < SIZE; c++) {
            border.append("------+");
        }
        String b = border.toString();

        StringBuilder sb = new StringBuilder();
        sb.append(b);
        for (int r = 0; r < SIZE; r++) {
            sb.append("\n|");
            for (int c = 0; c < SIZE; c++) {
                int v = board.tileAt(r, c);
                sb.append(v == 0 ? "      " : String.format("%6d", v));
                sb.append("|");
            }
            sb.append("\n").append(b);
        }
        return sb.toString();
    }

    /** A fresh board with the two starting tiles placed. */
    private static Board newBoard() {
        Board b = new Board();
        b.spawn();
        b.spawn();
        return b;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Board board = newBoard();
        int best = 0;
        boolean celebrated = false;

        System.out.println("=================================");
        System.out.println("  2048 - Java console edition");
        System.out.println("=================================");
        System.out.println("w = up   a = left   s = down   d = right");
        System.out.println("r = restart   q = quit");
        System.out.println("Merge equal tiles. Reach 2048 to win.");

        while (true) {
            System.out.println();
            System.out.println(render(board));
            best = Math.max(best, board.getScore());
            System.out.println("Score: " + board.getScore() + "   Best: " + best);

            if (board.hasWon() && !celebrated) {
                celebrated = true;
                System.out.println(">>> 2048 reached! Keep playing for a higher score. <<<");
            }

            if (!board.canMove()) {
                System.out.println("No moves left. Game over. Final score: " + board.getScore());
                System.out.print("Play again? (y/n): ");
                String answer = in.hasNextLine() ? in.nextLine().trim().toLowerCase() : "n";
                if (answer.equals("y") || answer.equals("yes")) {
                    board = newBoard();
                    celebrated = false;
                    continue;
                }
                break;
            }

            System.out.print("Your move: ");
            String input = in.hasNextLine() ? in.nextLine().trim().toLowerCase() : "q";

            if (input.isEmpty()) {
                continue;
            }

            char c = input.charAt(0);
            if (c == 'q') {
                System.out.println("Thanks for playing! Final score: " + board.getScore());
                break;
            } else if (c == 'r') {
                board = newBoard();
                celebrated = false;
                System.out.println("New game started.");
            } else {
                Direction dir;
                switch (c) {
                    case 'w': dir = Direction.UP;    break;
                    case 'a': dir = Direction.LEFT;  break;
                    case 's': dir = Direction.DOWN;  break;
                    case 'd': dir = Direction.RIGHT; break;
                    default:  dir = null;
                }
                if (dir == null) {
                    System.out.println("Invalid input. Use w/a/s/d, r or q.");
                } else if (board.move(dir)) {
                    board.spawn();
                } else {
                    System.out.println("Nothing moved in that direction.");
                }
            }
        }
        in.close();
    }
}
