import java.awt.*;
import java.util.*;
import java.util.List;
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

    int numValidMoves;

    // ADDED
    double defaultHScore[][] = new double[8][8];
    final int MAX_DEPTH;
    final boolean shouldDebug = false;

    public AI1(int _me, String host, int max_depth) {
        MAX_DEPTH = max_depth;
        me = _me;
        initClient(host);

        // initHScores(10.0, 2.5, -0.25, 0.50);
        initHScores(10, -.8, 2.5, -.5, .5);

        while (true) {
            System.out.println("Read");
            readMessage();

            if (turn == me) {
                System.out.println("\n\n\n\nStarting Move");

                int chosenMove;
                if (round < 4) {
                    // randomize
                    List<Integer> moves = getCurrValidMoves(round, state, me);
                    chosenMove = moves.get( (int)(Math.random() * moves.size()) );
                } else {
                    RNode parent = new RNode(null, 0, 0.0, me, -1);
                    buildChildNodes(parent, state);
    
                    // minimax and alpha beta happen in here
                    chosenMove = getBestMoveUsingMinMax(parent);
                } 

                String sel = chosenMove / 8 + "\n" + chosenMove % 8;

                System.out.println("Selection: " + moveToString(chosenMove) + "\n\n");

                sout.println(sel);
            }
        }
    }

    public void buildChildNodes(RNode parent, int[][] currState) {
        debugPrintln("Parent move: " + moveToString(parent.getMove()) + " and depth: " + parent.getDepth() + " and player: " + parent.getPlayer());
        printState(currState);
        List<Integer> currValidMoves = getCurrValidMoves(round + parent.getDepth(), currState, parent.getPlayer());

        debugPrintln("\n\nPossible Moves");
        for (int move : currValidMoves) {
            parent.addChild( buildChildNodeFromMove(parent, move, currState) );
        }
    }

    public RNode buildChildNodeFromMove(RNode parent, int move, int[][] parentState) {
        int[][] childState = deepCopyState(parentState);
        int row = move / 8;
        int col = move % 8;

        int childPlayer = getChildPlayerFromPlayerAndDepth(parent.getPlayer(), parent.getDepth() + 1);

        int incx, incy;
        double moveScore = 0;
        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                // updated multiple times as the update can be from 1+ directions
                moveScore += updateStateAndCalculateScore(childState, row, col, incx, incy, parent.getPlayer()); 
            }
        }
        System.out.println("Safe positions: " + countSafePositions(childState, parent.getPlayer()) + "\n");
        
        childState[row][col] = parent.getPlayer();
        // add the current location
        moveScore += defaultHScore[row][col];
        
        debugPrintln( moveToString(move) + ": " + moveScore );
        printState(childState);

        double childScore = parent.getNetScore() + moveScore * addOrSubtractForPlayer(parent.getPlayer());
        RNode newChildNode = new RNode(parent, parent.getDepth() + 1, childScore, childPlayer, move);

        if (newChildNode.getDepth() < MAX_DEPTH) {
            // build another layer of children
            buildChildNodes(newChildNode, childState);
        }
        return newChildNode;
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

    private int countSafePositions(int [][]currState, int turn){
        HashSet<String> safePositions = new HashSet<>(); 

        if(currState[0][0] == turn){ //bottom left
            int x = 0;
            int y = 0;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x++;
            }
            x = 0;
            y = 0;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y++;
            }

            for(x = 0; x < 8; x++){ // north-west
                y = 0;
                while(x >= 0){
                    if(currState[x][y] != turn){
                        break;
                    }
                    x-=1;
                    y+=1;
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                }
            }
            for(y = 0; y < 8; y++){
                x = 0;
                while(y >= 0){
                    if(currState[x][y] != turn){
                        break;
                    }
                    x+=1;
                    y-=1;
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                }
            }
        
        }

        if(currState[7][0] == turn){ // botton right
            int x = 7;
            int y = 0;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x--;
            }
            x = 7;
            y = 0;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y++;
            }
            for(x = 7; x >= 0; x--){ // north-east
                y = 0;
                while(x >= 0){
                    if(currState[x][y] != turn){
                        break;
                    }
                    x+=1;
                    y+=1;
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                }
            }
            for(y = 0; y < 8; y++){ // south-west
                x = 7;
                while(y >= 0){
                    if(currState[x][y] != turn){
                        break;
                    }
                    x-=1;
                    y-=1;
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                }
            }
        }
        if(currState[0][7] == turn){ // upper left
            int x = 0;
            int y = 7;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x++;
            }
            x = 0;
            y = 7;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y--;
            }
            for(y = 7; y >= 0; y--){ // north-east
                x = 0;
                while(y < 8){
                    if(currState[x][y] != turn){
                        break;
                    }
                    x+=1;
                    y+=1;
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                }
            }
            for(x = 0; x < 8; x++){ // south-west
                y = 7;
                while(x >= 0){
                    if(currState[x][y] != turn){
                        break;
                    }
                    x-=1;
                    y-=1;
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                }
            }
        }
        if(currState[7][7] == turn){ // upper right
            int x = 7;
            int y = 7;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x--;
            }
            x = 7;
            y = 7;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y++;
            }
            for(x = 7; x >= 0; x--){ // south-east
                y = 7;
                while(x < 8){
                    if(currState[x][y] != turn){
                        break;
                    }
                    x+=1;
                    y-=1;
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                }
            }
            for(y = 7; y >= 0; y--){ // north-west
                x = 7;
                while(y < 8){
                    if(currState[x][y] != turn){
                        break;
                    }
                    x-=1;
                    y+=1;
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                }
            }
        }
        return safePositions.size();
    }



    // create hScores
    private void initHScores(double cornerScore, double precornerScore, double edgeScore, double oneInScore, double normalScore) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 0 || i == 7 || j == 0 || j == 7) { // edges
                    defaultHScore[i][j] = edgeScore;
                } else if ( (i == 1 || i == 6) && (j > 0 && j < 3 || j > 4 && j < 7) || (j == 1 || j == 6) && (i > 0 && i < 3 || i > 4 && i < 7)) { // oneIn
                    defaultHScore[i][j] = oneInScore;
                } else {
                    defaultHScore[i][j] = normalScore;
                }

            }
        } // explicitly set the corner and "precorner" positions
        defaultHScore[0][0] = cornerScore;
        defaultHScore[0][7] = cornerScore;
        defaultHScore[7][0] = cornerScore;
        defaultHScore[7][7] = cornerScore;

        defaultHScore[1][1] = precornerScore;
        defaultHScore[0][1] = precornerScore;
        defaultHScore[1][0] = precornerScore;
        defaultHScore[6][6] = precornerScore;
        defaultHScore[7][6] = precornerScore;
        defaultHScore[6][7] = precornerScore;

        printSquareValues(defaultHScore);
    }

    private int getBestMoveUsingMinMax(RNode parent) {
        // start with 0th child
        int bestMove = parent.getChildren().get(0).getMove();
        double bestScore = getScoreOfBestChild(parent.getChildren().get(0), Double.MIN_VALUE);

        for (int i = 1; i < parent.getChildren().size(); i++) {
            double currentScore = getScoreOfBestChild(parent.getChildren().get(i), bestScore);
            // at top level, we know this is going to be max
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = parent.getChildren().get(i).getMove();
            }
        }

        return bestMove;
    }

    private double getScoreOfBestChild(RNode node, double bestScoreSoFar) {
        // base case
        if (node.getDepth() == MAX_DEPTH + 1 || node.getChildren().size() == 0) {
            return evaluateNode(node);
        }
        
        double bestScore = getScoreOfBestChild(node.getChildren().get(0), worstScoreForPlayer(node.getPlayer()));
        if (isScoreBetterThanBest(node.getPlayer(), bestScore, bestScoreSoFar)) {
            // pruning
            debugPrintln("PRUNING");
            return bestScore;
        }

        for (int i = 1; i < node.getChildren().size(); i++) {
            double currentScore = getScoreOfBestChild(node.getChildren().get(i), bestScore);
            // at top level, we know this is going to be max
            if (isScoreBetterThanBest(node.getPlayer(), currentScore, bestScore)) {
                bestScore = currentScore;

                if (isScoreBetterThanBest(node.getPlayer(), bestScore, bestScoreSoFar)) {
                    // pruning
                    debugPrintln("PRUNING");
                    return bestScore;
                }
            }
        }

        return bestScore;
    }

    private boolean isScoreBetterThanBest(int player, double currentScore, double bestScoreSoFar) {
        if (isMaxNode(player)) {
            return currentScore > bestScoreSoFar;
        } else {
            return currentScore < bestScoreSoFar;
        }
    }

    private double evaluateNode(RNode node) {
        return node.getNetScore();
    }

    private List<Integer> getCurrValidMoves(int round, int[][] pState, int player) {
        List<Integer> cValidMoves = new ArrayList<Integer>();
        player = me;
        int i, j;

        // Game Start
        if (round < 4) {
            if (pState[3][3] == 0) {
                cValidMoves.add(3 * 8 + 3);
            }
            if (pState[3][4] == 0) {
                cValidMoves.add( 3 * 8 + 4);
            }
            if (pState[4][3] == 0) {
                cValidMoves.add(4 * 8 + 3);
            }
            if (pState[4][4] == 0) {
                cValidMoves.add(4 * 8 + 4);
            }
            debugPrintln("Valid Moves:");
            for (int move : cValidMoves) {
                debugPrintln( moveToString(move) );
            }
        } else {
            debugPrintln("Valid Moves:");
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    if (pState[i][j] == 0) {
                        if (couldBe(pState, i, j)) {
                            cValidMoves.add(i * 8 + j);
                            debugPrintln( moveToString(i, j) );
                        }
                    }
                }
            }
        }

        return cValidMoves;
    }

    // Already implemented, don't touch
    private boolean checkDirection(int currState[][], int row, int col, int incx, int incy) { // currState, 2, 3, -1, -1
        int sequence[] = new int[7];
        int seqLen;
        int i, r, c;

        seqLen = 0;
        for (i = 1; i < 8; i++) { // i=3
            r = row + incy * i; // r = -1 = 2 + (-1)*3
            c = col + incx * i; // c = 0 = 3 + (-1)*3

            if ((r < 0) || (r > 7) || (c < 0) || (c > 7))
                break;

            sequence[seqLen] = currState[r][c]; // sequence[0, 0]
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
    private boolean couldBe(int currState[][], int row, int col) {
        int incx, incy;

        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                if (checkDirection(currState, row, col, incx, incy))
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
        printState(state);
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

    private static String moveToString(int move) {
        if (move < 0) {
            return "<<Parent>>";
        }
        return ( (char)('a' + move / 8) ) + ", " + (move % 8 + 1);
    }

    private static String moveToString(int row, int col) {
        return ( (char)('a' + row) ) + ", " + (col + 1);
    }

    private void debugPrint(Object obj) {
        if (shouldDebug) {
            System.out.print(obj);
        }
    }

    private void debugPrintln() {
        if (shouldDebug) {
            System.out.println();
        }
    }

    private void debugPrintln(Object obj) {
        if (shouldDebug) {
            System.out.println(obj);
        }
    }

    private void printState(int[][] currState) {
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                debugPrint(currState[row][col]);
            }
            debugPrintln();
        }
    }

    private static void printSquareValues(double[][] scores) {
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                System.out.print(scores[row][col] + " ");
            }
            System.out.println();
        }
    }

    public static int[][] deepCopyState(int[][] original) {
        if (original == null) {
            return null;
        }
    
        final int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;
    }

    public static void main(String args[]) {
        new AI1(Integer.parseInt(args[1]), args[0], Integer.parseInt(args[2]));
    }

}
