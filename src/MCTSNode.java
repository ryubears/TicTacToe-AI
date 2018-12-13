import java.util.ArrayList;

class MCTSNode {
    int row;
    int col;
    int height;
    double points;
    int numTries;
    int player;
    int[][] board;
    int[][][] board3D;
    MCTSNode parent;
    ArrayList<MCTSNode> children;
}
