import java.util.ArrayList;

public class TicTacToe3D {

    private static final int MAX = 1;
    private static final int EMPTY = 0;
    private static final int MIN = -1;

    private static final int NOT_OVER = -2;
    private static final int LOSE = -1;
    private static final int DRAW = 0;
    private static final int WIN = 1;

    private static int[][][] board = new int[3][3][3];

    private static int evaluate(int[][][] state) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // Check rows
                if (state[i][0][j] != EMPTY && state[i][0][j] == state[i][1][j] && state[i][0][j] == state[i][2][j]) {
                    return state[i][0][j];
                }
                // Check columns
                if (state[0][i][j] != EMPTY && state[0][i][j] == state[1][i][j] && state[0][i][j] == state[2][i][j]) {
                    return state[0][i][j];
                }
                // Check height
                if (state[i][j][0] != EMPTY && state[i][j][0] == state[i][j][1] && state[i][j][0] == state[i][j][2]) {
                    return state[i][j][0];
                }
            }
        }

        // Check diagonals
        for (int i = 0; i < 3; i++) {
            if (state[0][0][i] != EMPTY && state[0][0][i] == state[1][1][i] && state[0][0][i] == state[2][2][i]) {
                return state[0][0][i];
            }
            if (state[2][0][i] != EMPTY && state[2][0][i] == state[1][1][i] && state[2][0][i] == state[0][2][i]) {
                return state[2][0][i];
            }
            if (state[0][i][0] != EMPTY && state[0][i][0] == state[1][i][1] && state[0][i][0] == state[2][i][2]) {
                return state[0][i][0];
            }
            if (state[2][i][0] != EMPTY && state[2][i][0] == state[1][i][1] && state[2][i][0] == state[0][i][2]) {
                return state[2][i][0];
            }
            if (state[i][0][0] != EMPTY && state[i][0][0] == state[i][1][1] && state[i][0][0] == state[i][2][2]) {
                return state[i][0][0];
            }
            if (state[i][2][0] != EMPTY && state[i][2][0] == state[i][1][1] && state[i][2][0] == state[i][0][2]) {
                return state[i][2][0];
            }
        }
        if (state[0][0][0] != EMPTY && state[0][0][0] == state[1][1][1] && state[0][0][0] == state[2][2][2]) {
            return state[0][0][0];
        }
        if (state[2][0][0] != EMPTY && state[2][0][0] == state[1][1][1] && state[2][0][0] == state[0][2][2]) {
            return state[2][0][0];
        }

        // Check if game is a draw
        boolean isFull = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (state[i][j][k] == EMPTY) {
                        isFull = false;
                        break;
                    }
                }
                if (!isFull) break;
            }
            if (!isFull) break;
        }
        if (isFull) return DRAW;

        return NOT_OVER;
    }

    private static ArrayList<ArrayList<Integer>> getEmptyCells(int[][][] state) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (state[i][j][k] == 0) {
                        ArrayList<Integer> empty = new ArrayList<>();
                        empty.add(i);
                        empty.add(j);
                        empty.add(k);
                        result.add(empty);
                    }
                }
            }
        }
        return result;
    }

    private static String printMark(int mark) {
        if (mark == MAX) {
            return "O";
        } else if (mark == MIN) {
            return "X";
        } else {
            return " ";
        }
    }

    private static void printBoard(int[][][] state) {
        System.out.println("Height 1: Height 2: Height 3:");
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                for (int j = 0; j < 3; j++) {
                    System.out.print("[" + printMark(state[i][j][k]) + "]");
                }
                System.out.print(" ");
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

    private static int[] minimax(int[][][] state, int player, int parentValue) {
        // Initialize array containing info of best move
        int[] bestMove = new int[4];
        bestMove[0] = -1;
        bestMove[1] = -1;
        bestMove[2] = -1;
        if (player == MAX) {
            bestMove[3] = Integer.MIN_VALUE;
        } else {
            bestMove[3] = Integer.MAX_VALUE;
        }

        // Return score if game is over
        if (evaluate(state) != NOT_OVER) {
            bestMove[3] = evaluate(state);
            return bestMove;
        }

        // Simulate all possible moves and pick best move
        ArrayList<ArrayList<Integer>> emptyCells = getEmptyCells(state);
        for (ArrayList<Integer> empty : emptyCells) {
            state[empty.get(0)][empty.get(1)][empty.get(2)] = player;
            int[] result = minimax(state, -player, bestMove[3]);
            state[empty.get(0)][empty.get(1)][empty.get(2)] = 0;

            if (player == MAX) {
                if (result[3] > bestMove[3]) {
                    bestMove[0] = empty.get(0);
                    bestMove[1] = empty.get(1);
                    bestMove[2] = empty.get(2);
                    bestMove[3] = result[3];
                }
                if (bestMove[3] > parentValue) break;
            } else {
                if (result[3] < bestMove[3]) {
                    bestMove[0] = empty.get(0);
                    bestMove[1] = empty.get(1);
                    bestMove[2] = empty.get(2);
                    bestMove[3] = result[3];
                }
                if (bestMove[3] < parentValue) break;
            }
        }

        return bestMove;
    }

    private static ArrayList<MCTSNode> createChildren(MCTSNode parent) {
        ArrayList<MCTSNode> children = new ArrayList<>();
        ArrayList<ArrayList<Integer>> emptyCells = getEmptyCells(parent.board3D);
        if (emptyCells.isEmpty()) return null;
        for (ArrayList<Integer> empty : emptyCells) {
            int[][][] boardCopy = new int[3][3][3];
            for (int i = 0; i < 3; i++) {
                int[][] copy = new int[3][3];
                for (int j = 0; j < 3; j++) {
                    copy[j] = parent.board3D[i][j].clone();
                }
                boardCopy[i] = copy;
            }
            boardCopy[empty.get(0)][empty.get(1)][empty.get(2)] = -parent.player;

            MCTSNode child = new MCTSNode();
            child.row = empty.get(0);
            child.col = empty.get(1);
            child.height = empty.get(2);
            child.points = 0;
            child.numTries = 0;
            child.player = -parent.player;
            child.board3D = boardCopy;
            child.parent = parent;
            child.children = null;

            children.add(child);
        }

        return children;
    }

    private static int runRandomly(int[][][] state, int player) {
        while (evaluate(state) == NOT_OVER) {
            ArrayList<ArrayList<Integer>> emptyCells = getEmptyCells(state);
            ArrayList<Integer> empty = emptyCells.get((int)(Math.random() * emptyCells.size()));
            state[empty.get(0)][empty.get(1)][empty.get(2)] = player;
            player = -player;
        }

        return evaluate(state);
    }

    private static double getUCBValue(MCTSNode node) {
        if (node.numTries == 0) return Integer.MAX_VALUE;
        return (node.points / node.numTries) + Math.sqrt(2 * Math.log(node.parent.numTries) / node.numTries);
    }

    private static int[] mcts(int[][][] board, int player) {
        MCTSNode root = new MCTSNode();
        root.row = -1;
        root.col = -1;
        root.height = -1;
        root.points = 0;
        root.numTries = 0;
        root.player = -player; // Because children player is opposite of parent
        root.board3D = board;
        root.parent = null;
        root.children = createChildren(root);

        for (int i = 0; i < 100000; i++) {
            // Selection
            MCTSNode ptr = root;
            while (ptr.children != null) {
                double bestValue = Integer.MIN_VALUE;
                MCTSNode bestNode = null;
                for (MCTSNode child : ptr.children) {
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
            int score = runRandomly(ptr.board3D, -ptr.player);
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
        MCTSNode bestNode = null;
        for (MCTSNode child : root.children) {
            if ((child.points / child.numTries) > bestValue) {
                bestValue = (child.points / child.numTries);
                bestNode = child;
            }
        }

        int[] bestMove = new int[3];
        bestMove[0] = bestNode.row;
        bestMove[1] = bestNode.col;
        bestMove[2] = bestNode.height;

        return bestMove;
    }

    public static void main(String[] args) {
        // Both players playing minimax (player 1: MAX, player 2: MIN)
        int player = MIN;
        while (evaluate(board) == NOT_OVER) {
            player = -player;
            int[] nextMove = minimax(board, player, Integer.MAX_VALUE);
            board[nextMove[0]][nextMove[1]][nextMove[2]] = player;

            System.out.println("Player " + printMark(player) + ": " + nextMove[0] + ", " + nextMove[1] + ", " + nextMove[2] + ".");

            printBoard(board);
        }

        System.out.println();
        System.out.println("Minimax Game 1 Result: " + printResult(evaluate(board)));
        System.out.println();

        // Player 1 playing minimax and player 2 playing random
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    board[i][j][k] = 0;
                }
            }
        }

        while (evaluate(board) == NOT_OVER) {
            if (count % 2 == 0) {
                int[] nextMove = minimax(board, MIN, Integer.MAX_VALUE);
                board[nextMove[0]][nextMove[1]][nextMove[2]] = MAX;
                System.out.println("Player O: " + nextMove[0] + ", " + nextMove[1] + ".");
            } else {
                ArrayList<ArrayList<Integer>> emptyCells = getEmptyCells(board);
                ArrayList<Integer> nextMove = emptyCells.get((int) (Math.random() * emptyCells.size()));
                board[nextMove.get(0)][nextMove.get(1)][nextMove.get(2)] = MIN;
                System.out.println("Player X: " + nextMove.get(0) + ", " + nextMove.get(1) + ", " + nextMove.get(2) + ".");
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
                for (int k = 0; k < 3; k++) {
                    board[i][j][k] = 0;
                }
            }
        }

        while (evaluate(board) == NOT_OVER) {
            if (count % 2 == 0) {
                int[] nextMove = mcts(board, MAX);
                board[nextMove[0]][nextMove[1]][nextMove[2]] = MAX;
                System.out.println("Player O: " + nextMove[0] + ", " + nextMove[1] + ", " + nextMove[2] + ".");
            } else {
                ArrayList<ArrayList<Integer>> emptyCells = getEmptyCells(board);
                ArrayList<Integer> nextMove = emptyCells.get((int) (Math.random() * emptyCells.size()));
                board[nextMove.get(0)][nextMove.get(1)][nextMove.get(2)] = MIN;
                System.out.println("Player X: " + nextMove.get(0) + ", " + nextMove.get(1) + ".");
            }
            printBoard(board);
            count++;
        }

        System.out.println();
        System.out.println("Monte Carlo Tree Search Game 1 Result: " + printResult(evaluate(board)));
    }

}
