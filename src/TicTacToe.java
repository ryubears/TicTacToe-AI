import java.util.ArrayList;

public class TicTacToe {

    private static final int MAX = 1;
    private static final int MIN = -1;
    private static final int NOT_OVER = -2;
    private static final int LOSE = -1;
    private static final int DRAW = 0;
    private static final int WIN = 1;
    private static int[][] board = new int[3][3];

    /**
     * -2: game not over
     * -1: lose
     *  0: draw
     *  1: win
     */
    private static int evaluate(int[][] board) {
        // check if game is a draw
        boolean isFull = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (!isFull) break;
        }
        if (isFull) return 0;

        // check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                return board[i][0];
            }
        }

        // check columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i] != 0 && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
                return board[0][i];
            }
        }

        // check diagonals
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            return board[0][0];
        }

        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
            return board[0][2];
        }

        return -2;
    }

    private static ArrayList<int[]> getEmptyCells(int[][] board) {
        ArrayList<int[]> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    int[] empty = new int[2];
                    empty[0] = i;
                    empty[1] = j;
                    result.add(empty);
                }
            }
        }
        return result;
    }

    private static void printBoard(int[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String mark;
                if (board[i][j] == 0) {
                    mark = " ";
                } else if (board[i][j] == 1) {
                    mark = "O";
                } else {
                    mark = "X";
                }
                System.out.print("[" + mark + "]");
            }
            System.out.println();
        }
    }

    private static int[] minimax(int[][] state, int player) {
        // initialize best array
        int[] best = new int[3];
        best[0] = -1;
        best[1] = -1;
        if (player == MAX) {
            best[2] = Integer.MIN_VALUE;
        } else {
            best[2] = Integer.MAX_VALUE;
        }

        // return score if game is over
        if (evaluate(state) != -2) {
            best[2] = evaluate(state);
            return best;
        }

        // simulate all possible moves and pick best move
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == 0) {
                    state[i][j] = player;
                    int[] result = minimax(state, -player);
                    state[i][j] = 0;

                    if (player == MAX) {
                        if (result[2] > best[2]) {
                            best[0] = i;
                            best[1] = j;
                            best[2] = result[2];
                        }
                    } else {
                        if (result[2] < best[2]) {
                            best[0] = i;
                            best[1] = j;
                            best[2] = result[2];
                        }
                    }
                }
            }
        }

        return best;
    }

    private static ArrayList<TreeNode> createChildren(TreeNode parent) {
        ArrayList<TreeNode> children = new ArrayList<>();
        ArrayList<int[]> empty = getEmptyCells(parent.board);
        if (empty.isEmpty()) return null;
        for (int[] pos : empty) {
            int[][] boardCopy = new int[3][3];
            for (int i = 0; i < parent.board.length; i++) {
                boardCopy[i] = parent.board[i].clone();
            }
            boardCopy[pos[0]][pos[1]] = -parent.player;

            TreeNode child = new TreeNode();
            child.row = pos[0];
            child.col = pos[1];
            child.points = 0;
            child.numTries = 0;
            child.player = -parent.player;
            child.board = boardCopy;
            child.parent = parent;
            child.children = null;

            children.add(child);
        }
        return children;
    }

    private static int runRandomly(int[][] state, int player) {
        while (evaluate(state) == -2) {
            ArrayList<int[]> empty = getEmptyCells(state);
            int[] pos = empty.get((int)(Math.random() * empty.size()));
            state[pos[0]][pos[1]] = player;
            player = -player;
        }

        return evaluate(state);
    }

    private static double getUCBValue(TreeNode node) {
        if (node.numTries == 0) return Integer.MAX_VALUE;
        return (node.points / node.numTries) + Math.sqrt(2 * Math.log(node.parent.numTries) / node.numTries);
    }

    private static int[] mcts(int[][] board, int player) {
        TreeNode root = new TreeNode();
        root.row = -1;
        root.col = -1;
        root.points = 0;
        root.numTries = 0;
        root.player = -player;
        root.board = board;
        root.parent = null;
        root.children = createChildren(root);

        for (int i = 0; i < 1000000; i++) {
            // selection
            TreeNode ptr = root;
            while (ptr.children != null) {
                double bestValue = Integer.MIN_VALUE;
                TreeNode bestNode = null;
                for (TreeNode child : ptr.children) {
                    if (getUCBValue(child) > bestValue) {
                        bestValue = getUCBValue(child);
                        bestNode = child;
                    }
                }
                ptr = bestNode;
            }

            // expansion
            ptr.children = createChildren(ptr);

            // simulation
            int score = runRandomly(ptr.board, -ptr.player);
            if (player == MIN) score = -score;

            // back propagation
            while (ptr != null) {
                if (score == 1) {
                    ptr.points += 5;
                } else if (score == -1) {
                    ptr.points -= 10;
                } else {
                    ptr.points += 1;
                }

                ptr.numTries++;
                ptr = ptr.parent;
            }
        }

        // return best move after iterations
        double bestValue = Integer.MIN_VALUE;
        TreeNode bestNode = null;
        for (TreeNode child : root.children) {
            if ((child.points / child.numTries) > bestValue) {
                bestValue = (child.points / child.numTries);
                bestNode = child;
            }
        }

        int[] nextMove = new int[2];
        nextMove[0] = bestNode.row;
        nextMove[1] = bestNode.col;
        return nextMove;
    }

    public static void main(String[] args) {
        // both players using minimax
        int player = MIN;
        while (evaluate(board) == -2) {
            player = -player;
            int[] next= minimax(board, player);
            board[next[0]][next[1]] = player;

            // print move
            String playerStr;
            if (player == 1) {
                playerStr = "O";
            } else {
                playerStr = "X";
            }
            System.out.println("Player " + playerStr + ": " + next[0] + ", " + next[1] + ".");

            printBoard(board);
        }

        System.out.println();
        System.out.println("Minimax Game 1 Result: " + evaluate(board));
        System.out.println();

        // player 1 playing minimax and player 2 playing random
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }

        while (evaluate(board) == -2) {
            if (count % 2 == 0) {
                int[] next= minimax(board, MIN);
                board[next[0]][next[1]] = MAX;
                System.out.println("Player O: " + next[0] + ", " + next[1] + ".");
            } else {
                ArrayList<int[]> emptyCells = getEmptyCells(board);
                int[] next = emptyCells.get((int) (Math.random() * emptyCells.size()));
                board[next[0]][next[1]] = MIN;
                System.out.println("Player X: " + next[0] + ", " + next[1] + ".");
            }
            printBoard(board);
            count++;
        }

        System.out.println();
        System.out.println("Minimax Game 2 Result: " + evaluate(board));
        System.out.println();

        // player 1 using Monte Carlo Tree Search and player 2 playing random
        count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }

        while (evaluate(board) == -2) {
            if (count % 2 == 0) {
                int[] next= mcts(board, MAX);
                board[next[0]][next[1]] = MAX;
                System.out.println("Player O: " + next[0] + ", " + next[1] + ".");
            } else {
                ArrayList<int[]> emptyCells = getEmptyCells(board);
                int[] next = emptyCells.get((int) (Math.random() * emptyCells.size()));
                board[next[0]][next[1]] = MIN;
                System.out.println("Player X: " + next[0] + ", " + next[1] + ".");
            }
            printBoard(board);
            count++;
        }

        System.out.println();
        System.out.println("Monte Carlo Tree Search Game 1 Result: " + evaluate(board));
        System.out.println();
    }

}
