public class Client{
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
    double moveValue[][] = new double[8][8]; // the number of tokens that will be gained by the move. 0 if invalid
    final int MAX_DEPTH = 3;

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
        Map<Integer, Double> currMoveScores = getMoveScores(parent, currValidMoves);

        for (int i = 0; i < currValidMoves.length; i++) {
            int childPlayer = getChildPlayerFromPlayerAndDepth(parent.getPlayer(), parent.getDepth());
            double childScore = parent.getNetScore() + currMoveScore.get(parent.getMove()) * addOrSubtractForPlayer(childPlayer);
            parent.addChild(
                new RNode(parent, depth + 1, applyMoveToState(validMoves[i], state), childScore, childPlayer)
            );
        }

        if (depth <= MAX_DEPTH) {
            // build another layer of children
            for (RNode child : parent.getChildren()) {
                buildChildNodes(child, child.getDepth());
            }
        }

    }

    public int[][] applyMoveToState(int move, int[][] currState) {
        int[][] newState = currState.clone();
        int row = move / 8;
        int col = move % 8;

        int incx, incy;
        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                // updated multiple times as the update can be from 1+ directions
                newState = updateState(newState, row, col, incx, incy, turn); 
            }
        }

        return newState;
    }

    public int[][] updateState(int[][] currState, int row, int col, int incx, int incy, int turn) {
        int newState[][] = currState;
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
                while (newState[r][c] == 2) {
                    newState[r][c] = 1;
                    i++;
                    r = row + incy * i;
                    c = col + incx * i;
                }
            } else {
                i = 1;
                r = row + incy * i;
                c = col + incx * i;
                while (newState[r][c] == 1) {
                    newState[r][c] = 2;
                    i++;
                    r = row + incy * i;
                    c = col + incx * i;
                }
            }
        }
        return newState;
    }

    public Map<Integer, Double> getMoveScores(RNode parent, int[] validMoves) {
        Map<Integer, Double> moveScores = new HashMap<Integer, Double>();

        // TODO
        // for (int move : validMoves) { // calculate what the scores are
        //     // call check direction special, and take center row move the value there
        //     int score = 0;
        //     for () {
        //         for {
        //             if (i == 0 && j == 0) {
        //                 continue;
        //             }
        //             // do all of moves
        //             score += calcMoveScore(moveScores);
        //         }
        //     }
            
        //     moveScores.put(move, score);
        // }
        return moveScores;
    }

    private void calcMoveScore(Map<Integer, Double> moveScores) {
        if (moveScores.get(move) != null) {
            moveScores.put(move, moveScores.get(move) + newScore);
        } else {
            moveScores.put(move, newScore);
        }
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
    public static void main(String args[]) {
        new AI1(Integer.parseInt(args[1]), args[0]);
    }

}
