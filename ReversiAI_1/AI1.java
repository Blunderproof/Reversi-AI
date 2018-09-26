import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.math.*;
import java.text.*;

class AI1 {

    public Socket s;
    public BufferedReader sin;
    public PrintWriter sout;
    // Random generator = new Random();

    double t1, t2;
    int me;
    int boardState;
    int state[][] = new int[8][8]; // state[0][0] is the bottom left corner of the board (on the GUI)
    int turn = -1;
    int round;

    int validMoves[] = new int[64];
    int numValidMoves;

    // ADDED
    double defaultHScore[][] = new double[8][8];
    final int MAX_DEPTH = 0;

    public AI1(int _me, String host) {
        me = _me;
        initClient(host);

        initHScores(1.0, .4, -.5, .1);

        int myMove;

        while (true) {
            System.out.println("Read");
            readMessage();

            if (turn == me) {
                System.out.println("Move");

                RNode parent = new RNode(null, 0, state, 0.0, me, -1);
                buildChildNodes(parent, 0);

                // minimax and alpha beta happen in here
                myMove = getBestMoveUsingMinMax(parent);

                String sel = validMoves[myMove] / 8 + "\n" + validMoves[myMove] % 8;

                System.out.println("Selection: " + validMoves[myMove] / 8 + ", " + validMoves[myMove] % 8);

                sout.println(sel);
            }
        }
    }

    public void buildChildNodes(RNode parent, int depth) {
        int[] currValidMoves = getCurrValidMoves(round, parent.getState(), parent.getPlayer());
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            int move = entry.getKey();
            double score = entry.getValue();
            int row = move / 8;
            int col = move % 8;
            System.out.println("(" + row + "," + col + ") :" + score);
        }

        for (int i = 0; i < currValidMoves.length; i++) {
            parent.addChild( buildChildNodeFromMove(parent, state, validMoves[i]) );
        }

        if (depth <= MAX_DEPTH) {
            // build another layer of children
            for (RNode child : parent.getChildren()) {
                buildChildNodes(child, child.getDepth());
            }
        }

    }

    public RNode buildChildNodeFromMove(RNode parent, int[][] state, int move) {
        int[][] childState = currState.clone();
        int row = move / 8;
        int col = move % 8;

        int incx, incy;
        int moveScore = 0;
        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                // updated multiple times as the update can be from 1+ directions
                moveScore += updateStateAndCalculateScore(childState, row, col, incx, incy, turn); 
            }
        }
        // add the current location
        moveScore += defaultHScore[row][col];

        int childPlayer = getChildPlayerFromPlayerAndDepth(parent.getPlayer(), parent.getDepth());
        double childScore = parent.getNetScore() + moveScore * addOrSubtractForPlayer(childPlayer);
        return new RNode(parent, depth + 1, childState, childScore, childPlayer);
    }

    public double updateStateAndCalculateScore(int[][] currState, int row, int col, int incx, int incy, int turn) {
        int sequence[] = new int[7];
        int seqLen;
        int i, r, c;

        seqLen = 0;
        for (i = 1; i < 8; i++) {
            r = row + incy * i;
            c = col + incx * i;

            if ((r < 0) || (r > 7) || (c < 0) || (c > 7))
                break;

            sequence[seqLen] = currState[r][c];
            seqLen++;
        }

        int count = 0;
        double score = 0;
        for (i = 0; i < seqLen; i++) {
            if (turn == 0) {
                if (sequence[i] == 2)
                    count++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        count = 20;
                    break;
                }
            } else {
                if (sequence[i] == 1)
                    count++;
                else {
                    if ((sequence[i] == 2) && (count > 0))
                        count = 20;
                    break;
                }
            }
        }

        if (count > 10) {
            if (turn == 0) {
                i = 1;
                r = row + incy * i;
                c = col + incx * i;
                while (currState[r][c] == 2) {
                    currState[r][c] = 1;
                    // 2 * because we gain and other loses
                    score += 2 * defaultHScore[r][c];
                    i++;
                    r = row + incy * i;
                    c = col + incx * i;
                }
            } else {
                i = 1;
                r = row + incy * i;
                c = col + incx * i;
                while (currState[r][c] == 1) {
                    currState[r][c] = 2;
                    // 2 * because we gain and other loses
                    score += 2 * defaultHScore[r][c];
                    i++;
                    r = row + incy * i;
                    c = col + incx * i;
                }
            }
        }
        return score; 
    }

    // create hScores
    private void initHScores(double cornerScore, double edgeScore, double oneInScore, double normalScore) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 0 || i == 7 || j == 0 || j == 7) { // edges
                    defaultHScore[i][j] = edgeScore;
                } else if ((i == 1 || i == 6) && (j > 0 && j < 7) || (j == 1 || j == 6) && (i > 0 && i < 7)) { // oneIn
                    defaultHScore[i][j] = oneInScore;
                } else {
                    defaultHScore[i][j] = normalScore;
                }

            }
        } // explicitly set the corners
        defaultHScore[0][0] = cornerScore;
        defaultHScore[0][7] = cornerScore;
        defaultHScore[7][0] = cornerScore;
        defaultHScore[7][7] = cornerScore;
    }

    private int getBestMoveUsingMinMax(RNode parent) {
        // start with 0th child
        int bestMove = parent.getChildren().get(0).getMove();
        double bestScore = getScoreOfBestChild(parent.getChildren().get(0), Double.MIN_VALUE);

        for (int i = 1; i < parent.getChildren().length; i++) {
            double currentScore = getScoreOfBestChild(parent.getChildren().get(i), bestScore);
            // at top level, we know this is going to be max
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = parent.getChildren().get(i).getMove();
            }
        }

        return bestMove;
    }

    private int getScoreOfBestChild(RNode node, double bestScoreSoFar) {
        // base case
        if (node.getDepth() == MAX_DEPTH + 1) {
            return evaluateNode(node);
        }

        double bestScore = getScoreOfBestChild(node.getChildren().get(0), worstScoreForPlayer(node.getPlayer()));
        if (isScoreBetterThanBest(node.getPlayer(), bestScore, bestScoreSoFar)) {
            // pruning
            return bestScore;
        }

        for (int i = 1; i < node.getChildren().length; i++) {
            double currentScore = getScoreOfBestChild(node.getChildren().get(i), bestScore);
            // at top level, we know this is going to be max
            if (isScoreBetterThanBest(node.getPlayer(), currentScore, bestScore)) {
                bestScore = currentScore;

                if (isScoreBetterThanBest(node.getPlayer(), bestScore, bestScoreSoFar)) {
                    // pruning
                    return bestScore;
                }
            }
        }

        return bestScore;
    }

    private boolean isScoreBetterThanBest(int player, double currentScore, double bestScoreSoFar) {
        if (isMaxNode(node.getPlayer())) {
            if (currentScore > bestScoreSoFar) {
                // prune
                return true;
            }
        } else {
            if (currentScore < bestScoreSoFar) {
                // prune
                return true;
            }
        }
        return false;
    }

    private double evaluateNode(RNode node) {
        return node.getNetScore();
    }

    private int getCurrValidMoves(int round, int[][] pState, int player) {
        int cValidMoves[] = new int[64];
        int cNumValidMoves = 0;
        player = me;
        int i, j;

        // Game Start
        if (round < 4) {
            if (pState[3][3] == 0) {
                cValidMoves[cNumValidMoves] = 3 * 8 + 3;
                cNumValidMoves++;
            }
            if (pState[3][4] == 0) {
                cValidMoves[cNumValidMoves] = 3 * 8 + 4;
                cNumValidMoves++;
            }
            if (pState[4][3] == 0) {
                cValidMoves[cNumValidMoves] = 4 * 8 + 3;
                cNumValidMoves++;
            }
            if (pState[4][4] == 0) {
                cValidMoves[cNumValidMoves] = 4 * 8 + 4;
                cNumValidMoves++;
            }
            System.out.println("Valid Moves:");
            for (i = 0; i < cNumValidMoves; i++) {
                System.out.println(cValidMoves[i] / 8 + ", " + cValidMoves[i] % 8);
            }
        } else {
            System.out.println("Valid Moves:");
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    if (pState[i][j] == 0) {
                        if (couldBe(pState, i, j)) {
                            cValidMoves[cNumValidMoves] = i * 8 + j;
                            cNumValidMoves++;
                            System.out.println(i + ", " + j);
                        }
                    }
                }
            }
        }

        return cValidMoves;
    }

    // Already implemented, don't touch
    private boolean checkDirection(int state[][], int row, int col, int incx, int incy) { // state, 2, 3, -1, -1
        int sequence[] = new int[7];
        int seqLen;
        int i, r, c;

        seqLen = 0;
        for (i = 1; i < 8; i++) { // i=3
            r = row + incy * i; // r = -1 = 2 + (-1)*3
            c = col + incx * i; // c = 0 = 3 + (-1)*3

            if ((r < 0) || (r > 7) || (c < 0) || (c > 7))
                break;

            sequence[seqLen] = state[r][c]; // sequence[0, 0]
            seqLen++; // 2
        }

        int count = 0;
        for (i = 0; i < seqLen; i++) { // i = 0
            if (me == 1) { // if I'm player 1
                if (sequence[i] == 2) // if enemy territory
                    count++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        return true;
                    break;
                }
            } else { // if I'm player 2
                if (sequence[i] == 1)
                    count++;
                else {
                    if ((sequence[i] == 2) && (count > 0))
                        return true;
                    break;
                }
            }
        }

        // not a valid move to take a piece
        return false;
    }

    // Already implemented, don't touch
    private boolean couldBe(int state[][], int row, int col) {
        int incx, incy;

        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                if (checkDirection(state, row, col, incx, incy))
                    return true;
            }
        }

        return false;
    }

    // Already implemented, don't touch
    public void readMessage() {
        int i, j;
        String status;
        try {
            // System.out.println("Ready to read again");
            turn = Integer.parseInt(sin.readLine());

            if (turn == -999) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

                System.exit(1);
            }

            // System.out.println("Turn: " + turn);
            round = Integer.parseInt(sin.readLine());
            t1 = Double.parseDouble(sin.readLine());
            System.out.println(t1);
            t2 = Double.parseDouble(sin.readLine());
            System.out.println(t2);
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    state[i][j] = Integer.parseInt(sin.readLine());
                }
            }
            sin.readLine();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        System.out.println("Turn: " + turn);
        System.out.println("Round: " + round);
        for (i = 7; i >= 0; i--) {
            for (j = 0; j < 8; j++) {
                System.out.print(state[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    // Already implemented, don't touch
    public void initClient(String host) {
        int portNumber = 3333 + me;

        try {
            s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
            sin = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String info = sin.readLine();
            System.out.println(info);
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    private int getChildPlayerFromPlayerAndDepth(int player, int depth) {
        if (depth == 0) {
            return player;
        }
        if (player == 1) {
            return 2;
        } else {
            return 1;
        }
    }

    private int addOrSubtractForPlayer(int player) {
        if (player == me) {
            // positive to add
            return 1;
        } else {
            // negative to subtract
            return -1;
        }
    }

    private double worstScoreForPlayer(int player) {
        if (player == me) {
            return Double.MIN_VALUE;
        } else {
            return Double.MAX_VALUE;
        }
    }

    private boolean isMaxNode(int player) {
        return player == me;
    }

    public static void main(String args[]) {
        new AI1(Integer.parseInt(args[1]), args[0]);
    }

}
