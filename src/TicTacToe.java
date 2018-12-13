import java.util.ArrayList;

public class TicTacToe {

    private static final int MAX = 1;
    private static final int EMPTY = 0;
    private static final int MIN = -1;
    private static final int NOT_OVER = -2;
    private static final int LOSE = -1;
    private static final int DRAW = 0;
    private static final int WIN = 1;
    private static int[][] board = new int[3][3];

    private static int evaluate(int[][] state) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (state[i][0] != 0 && state[i][0] == state[i][1] && state[i][0] == state[i][2]) {
                return state[i][0];
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (state[0][i] != 0 && state[0][i] == state[1][i] && state[0][i] == state[2][i]) {
                return state[0][i];
            }
        }

        // Check diagonals
        if (state[0][0] != 0 && state[0][0] == state[1][1] && state[0][0] == state[2][2]) {
            return state[0][0];
        }

        if (state[0][2] != 0 && state[0][2] == state[1][1] && state[0][2] == state[2][0]) {
            return state[0][2];
        }

        // Check if game is a draw
        boolean isFull = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == EMPTY) {
                    isFull = false;
                    break;
                }
            }
            if (!isFull) break;
        }
        if (isFull) return DRAW;

        return NOT_OVER;
    }

    private static ArrayList<ArrayList<Integer>> getEmptyCells(int[][] state) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == 0) {
                    ArrayList<Integer> empty = new ArrayList<>();
                    empty.add(i);
                    empty.add(j);
                    result.add(empty);
                }
            }
        }
        return result;
    }

    private static void printBoard(int[][] state) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String mark;
                if (state[i][j] == 0) {
                    mark = " ";
                } else if (state[i][j] == 1) {
                    mark = "O";
                } else {
                    mark = "X";
                }
                System.out.print("[" + mark + "]");
            }
            System.out.println();
        }
    }

    private static String printResult(int score) {
        if (score == WIN) {
            return "WIN";
        } else if (score == DRAW) {
            return "DRAW";
        } else {
            return "LOSE";
        }
    }

    private static int[] minimax(int[][] state, int player) {
        // Initialize array containing info of best move
        int[] bestMove = new int[3];
        bestMove[0] = -1;
        bestMove[1] = -1;
        if (player == MAX) {
            bestMove[2] = Integer.MIN_VALUE;
        } else {
            bestMove[2] = Integer.MAX_VALUE;
        }

        // Return score if game is over
        if (evaluate(state) != NOT_OVER) {
            bestMove[2] = evaluate(state);
            return bestMove;
        }

        // Simulate all possible moves and pick best move
        ArrayList<ArrayList<Integer>> emptyCells = getEmptyCells(state);
        for (ArrayList<Integer> empty : emptyCells) {
            state[empty.get(0)][empty.get(1)] = player;
            int[] result = minimax(state, -player);
            state[empty.get(0)][empty.get(1)] = 0;

            if (player == MAX) {
                if (result[2] > bestMove[2]) {
                    bestMove[0] = empty.get(0);
                    bestMove[1] = empty.get(1);
                    bestMove[2] = result[2];
                }
            } else {
                if (result[2] < bestMove[2]) {
                    bestMove[0] = empty.get(0);
                    bestMove[1] = empty.get(1);
                    bestMove[2] = result[2];
                }
            }
        }

        return bestMove;
    }

    private static ArrayList<TreeNode> createChildren(TreeNode parent) {
        ArrayList<TreeNode> children = new ArrayList<>();
        ArrayList<ArrayList<Integer>> emptyCells = getEmptyCells(parent.board);
        if (emptyCells.isEmpty()) return null;
        for (ArrayList<Integer> empty: emptyCells) {
            int[][] boardCopy = new int[3][3];
            for (int i = 0; i < parent.board.length; i++) {
                boardCopy[i] = parent.board[i].clone();
            }
            boardCopy[empty.get(0)][empty.get(1)] = -parent.player;

            TreeNode child = new TreeNode();
            child.row = empty.get(0);
            child.col = empty.get(1);
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
        while (evaluate(state) == NOT_OVER) {
            ArrayList<ArrayList<Integer>> emptyCells = getEmptyCells(state);
            ArrayList<Integer> empty = emptyCells.get((int)(Math.random() * emptyCells.size()));
            state[empty.get(0)][empty.get(1)] = player;
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
        root.player = -player; // Because children player is opposite of parent
        root.board = board;
        root.parent = null;
        root.children = createChildren(root);

        for (int i = 0; i < 1000000; i++) {
            // Selection
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

            // Expansion
            ptr.children = createChildren(ptr);

            // Simulation
            int score = runRandomly(ptr.board, -ptr.player);
            if (player == MIN) score = -score;

            // Back Propagation
            while (ptr != null) {
                if (score == WIN) {
                    ptr.points += 10;
                } else if (score == LOSE) {
                    ptr.points -= 10;
                }

                ptr.numTries++;
                ptr = ptr.parent;
            }
        }

        // Return best move after certain number of iterations
        double bestValue = Integer.MIN_VALUE;
        TreeNode bestNode = null;
        for (TreeNode child : root.children) {
            if ((child.points / child.numTries) > bestValue) {
                bestValue = (child.points / child.numTries);
                bestNode = child;
            }
        }

        int[] bestMove = new int[2];
        bestMove[0] = bestNode.row;
        bestMove[1] = bestNode.col;

        return bestMove;
    }

    public static void main(String[] args) {
        // Both players playing minimax (player 1: MAX, player 2: MIN)
        int player = MIN;
        while (evaluate(board) == NOT_OVER) {
            player = -player;
            int[] nextMove = minimax(board, player);
            board[nextMove[0]][nextMove[1]] = player;

            // Print move
            String playerStr;
            if (player == 1) {
                playerStr = "O";
            } else {
                playerStr = "X";
            }
            System.out.println("Player " + playerStr + ": " + nextMove[0] + ", " + nextMove[1] + ".");

            printBoard(board);
        }

        System.out.println();
        System.out.println("Minimax Game 1 Result: " + printResult(evaluate(board)));
        System.out.println();

        // Player 1 playing minimax and player 2 playing random
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }

        while (evaluate(board) == NOT_OVER) {
            if (count % 2 == 0) {
                int[] nextMove = minimax(board, MIN);
                board[nextMove[0]][nextMove[1]] = MAX;
                System.out.println("Player O: " + nextMove[0] + ", " + nextMove[1] + ".");
            } else {
                ArrayList<ArrayList<Integer>> emptyCells = getEmptyCells(board);
                ArrayList<Integer> nextMove = emptyCells.get((int) (Math.random() * emptyCells.size()));
                board[nextMove.get(0)][nextMove.get(1)] = MIN;
                System.out.println("Player X: " + nextMove.get(0) + ", " + nextMove.get(1) + ".");
            }
            printBoard(board);
            count++;
        }

        System.out.println();
        System.out.println("Minimax Game 2 Result: " + printResult(evaluate(board)));
        System.out.println();

        // Player 1 using Monte Carlo Tree Search and player 2 playing random
        count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }

        while (evaluate(board) == NOT_OVER) {
            if (count % 2 == 0) {
                int[] nextMove = mcts(board, MAX);
                board[nextMove[0]][nextMove[1]] = MAX;
                System.out.println("Player O: " + nextMove[0] + ", " + nextMove[1] + ".");
            } else {
                ArrayList<ArrayList<Integer>> emptyCells = getEmptyCells(board);
                ArrayList<Integer> nextMove = emptyCells.get((int) (Math.random() * emptyCells.size()));
                board[nextMove.get(0)][nextMove.get(1)] = MIN;
                System.out.println("Player X: " + nextMove.get(0) + ", " + nextMove.get(1) + ".");
            }
            printBoard(board);
            count++;
        }

        System.out.println();
        System.out.println("Monte Carlo Tree Search Game 1 Result: " + printResult(evaluate(board)));
        System.out.println();
    }

}
