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

    // initHScores(10.0, 2.5, -0.25, 0.50);
    final double CORNER_SCORE = 60;
    final double PRECORNER_SCORE = -15;
    final double EDGE_SCORE = 3;
    final double NORMAL_SCORE = 0.4;

    public AI1(int _me, String host, int maxDepth) {
        MAX_DEPTH = maxDepth;
        me = _me;
        initClient(host);

        // state[3][3] = 2;
        // state[3][4] = 2;
        // state[4][3] = 1;
        // state[4][4] = 1;

        initHScores();

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
                    RNode parent = new RNode(null, 0, 0.0, me, -1, 0);
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

    public void buildChildNodes(RNode node, int[][] currState) {
        debugPrintln("Parent move: " + moveToString(node.getMove()) + " and depth: " + node.getDepth() + " and player: " + node.getPlayer());
        printState(currState);
        List<Integer> currValidMoves = getCurrValidMoves(round + node.getDepth(), currState, node.getPlayer());

        debugPrintln("\n\nPossible Moves for player: " + node.getPlayer());
        for (int move : currValidMoves) {
            node.addChild( buildChildNodeFromMove(node, move, currState) );
        }
    }

    public RNode buildChildNodeFromMove(RNode parent, int move, int[][] parentState) {
        int[][] childState = deepCopyState(parentState);
        int row = move / 8;
        int col = move % 8;

        int childPlayer = getChildPlayerFromPlayerAndDepth(parent.getPlayer(), parent.getDepth());

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
        int safe = countSafePositions(childState, parent.getPlayer());
        double safeWeight = 20;

        PieceCount count = PieceCount.countPiecesFromState(childState);
        debugPrintln("My count: " + count.getMyCountForPlayer(parent.getPlayer()));
        debugPrintln("Opponent count: " + count.getOpponentCountForPlayer(parent.getPlayer()));

        if (count.getMyCountForPlayer(parent.getPlayer()) < 3) {
            debugPrintln("AVOID THIS MOVE");
            moveScore -= 30; // * (parent.getDepth() / MAX_DEPTH);
        } 
        if (count.getOpponentCountForPlayer(parent.getPlayer()) < 3) {
            debugPrintln("SEIZE THIS MOVE");
            moveScore += 30; // * (parent.getDepth() / MAX_DEPTH);
        }
        moveScore += (double) (safe) * safeWeight; 

        childState[row][col] = parent.getPlayer();
        // add the current location
        moveScore += defaultHScore[row][col];
        
        debugPrintln( moveToString(move) + ": " + moveScore );
        printState(childState);

        //double childScore = parent.getNetScore() + moveScore * addOrSubtractForPlayer(parent.getPlayer())* ((MAX_DEPTH - parent.getDepth())/MAX_DEPTH); 
        double childScore = parent.getNetScore() + moveScore * addOrSubtractForPlayer(parent.getPlayer()); 

        debugPrintln("Parent player: " + parent.getPlayer() + " Child Player: " + childPlayer);

        RNode newChildNode = new RNode(parent, parent.getDepth() + 1, childScore, childPlayer, move, safe);

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
            if (turn == 1) {
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
            if (turn == 1) {
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

    private int countSafePositions(int [][]currState, int player){
        HashSet<String> safePositions = new HashSet<>(); 

        if(currState[0][0] == player){ //bottom left
            int x = 0;
            int y = 0;
            while(currState[x][y] == player){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x++;
                if (x >= 8) break;
            }
            x = 0;
            y = 0;
            while(currState[x][y] == player){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y++;
                if (y >= 8) break;
            }
            northwestLoop:
            for(x = 0; x < 8; x++){ // north-west
                y = 0;
                int currX = x;
                while(currX >= 0){
                    if(currState[currX][y] != player){
                        break northwestLoop;
                    }
                    safePositions.add(Integer.toString(currX) + " " + Integer.toString(y));
                    currX-=1;
                    y+=1;
                }
            }
            southeastLoop:
            for(; y < 8; y++){ // south-east
                x = 0;
                int currY = y;
                while(currY >= 0){
                    if(currState[x][currY] != player){
                        break southeastLoop;
                    }
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(currY));
                    x+=1;
                    currY-=1;
                }
            }
        
        }

        if(currState[7][0] == player){ // botton right
            int x = 7;
            int y = 0;
            while(currState[x][y] == player){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x--;
                if (x < 0) break;
            }
            x = 7;
            y = 0;
            while(currState[x][y] == player){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y++;
                if (y >= 8) break;
            }
            northeastLoop:
            for(x = 7; x >= 0; x--){ // north-east
                y = 0;
                int currX = x;
                while(currX < 8){
                    if(currState[currX][y] != player){
                        break northeastLoop;
                    }
                    safePositions.add(Integer.toString(currX) + " " + Integer.toString(y));
                    currX+=1;
                    y+=1;
                }
            }
            southwestLoop:
            for(; y < 8; y++){ // south-west
                x = 7;
                int currY = 0;
                while(currY >= 0){
                    if(currState[x][currY] != player){
                        break southwestLoop;
                    }
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(currY));
                    x-=1;
                    currY-=1;
                }
            }
        }
        if(currState[0][7] == player){ // upper left
            int x = 0;
            int y = 7;
            while(currState[x][y] == player){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x++;
                if (x >= 8) break;

            }
            x = 0;
            y = 7;
            while(currState[x][y] == player){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y--;
                if (y < 0) break;
            }
            northeastLoop:
            for(y = 7; y >= 0; y--){ // north-east
                x = 0;
                int currY = y;
                while(currY < 8){
                    if(currState[x][currY] != player){
                        break northeastLoop;
                    }
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(currY));
                    x+=1;
                    currY+=1;
                }
            }
            southwestLoop:
            for(x = 0; x < 8; x++){ // south-west
                y = 7;
                int currX = 0;
                while(currX >= 0){
                    if(currState[currX][y] != player){
                        break southwestLoop;
                    }
                    safePositions.add(Integer.toString(currX) + " " + Integer.toString(y));
                    currX-=1;
                    y-=1;
                }
            }
        }
        if(currState[7][7] == player){ // upper right
            int x = 7;
            int y = 7;
            while(currState[x][y] == player){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x--;
                if (x < 0) break;
            }
            x = 7;
            y = 7;
            while(currState[x][y] == player){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y--;
                if (y < 0) break;
            }
            southeastLoop:
            for(x = 7; x >= 0; x--){ // south-east
                y = 7;
                int currX = x;
                while(currX < 8){
                    if(currState[currX][y] != player){
                        break southeastLoop;
                    }
                    safePositions.add(Integer.toString(currX) + " " + Integer.toString(y));
                    currX+=1;
                    y-=1;
                }
            }
            northeastLoop:
            for(y = 7; y >= 0; y--){ // north-west
                x = 7;
                int currY = y;
                while(currY < 8){
                    if(currState[x][currY] != player){
                        break northeastLoop;
                    }
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(currY));
                    x-=1;
                    currY+=1;
                }
            }
        }
        return safePositions.size();
    }



    // create hScores
    private void initHScores() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 0 || i == 7 || j == 0 || j == 7) { // edges
                    defaultHScore[i][j] = EDGE_SCORE;
                } else {
                    defaultHScore[i][j] = NORMAL_SCORE;
                }

            }
        } // explicitly set the corner and "precorner" positions
        defaultHScore[0][0] = CORNER_SCORE;
        defaultHScore[0][7] = CORNER_SCORE;
        defaultHScore[7][0] = CORNER_SCORE;
        defaultHScore[7][7] = CORNER_SCORE;

        defaultHScore[1][1] = PRECORNER_SCORE;
        defaultHScore[0][1] = PRECORNER_SCORE;
        defaultHScore[1][0] = PRECORNER_SCORE;
        defaultHScore[6][6] = PRECORNER_SCORE;
        defaultHScore[7][6] = PRECORNER_SCORE;
        defaultHScore[6][7] = PRECORNER_SCORE;

        defaultHScore[1][6] = PRECORNER_SCORE;
        defaultHScore[0][6] = PRECORNER_SCORE;
        defaultHScore[1][7] = PRECORNER_SCORE;
        defaultHScore[6][1] = PRECORNER_SCORE;
        defaultHScore[7][1] = PRECORNER_SCORE;
        defaultHScore[6][0] = PRECORNER_SCORE;

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
                        if (couldBe(pState, player, i, j)) {
                            cValidMoves.add(i * 8 + j);
                            debugPrintln( moveToString(i, j) );
                        }
                    }
                }
            }
        }

        return cValidMoves;
    }

    private boolean checkDirection(int currState[][], int player, int row, int col, int incx, int incy) { // currState, 2, 3, -1, -1
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
            if (player == 1) { // if player 1
                if (sequence[i] == 2) // if enemy territory
                    count++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        return true;
                    break;
                }
            } else { // if player 2
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

    private boolean couldBe(int currState[][], int player, int row, int col) {
        int incx, incy;

        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                if (checkDirection(currState, player, row, col, incx, incy))
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
            // update pre-corner weights to make them good after corner capture
            for (int row = 0; row == 0 || row == 7; row += 7) {
                for (int col = 0; col == 0 || col == 7; col += 7) {
                    if (state[row][col] != 0) {
                        defaultHScore[Math.abs(row - 1)][col] = PRECORNER_SCORE * -1;
                        defaultHScore[row][Math.abs(col - 1)] = PRECORNER_SCORE * -1;
                        defaultHScore[Math.abs(row - 1)][Math.abs(col - 1)] = PRECORNER_SCORE * -1;
                        printSquareValues(defaultHScore);
                    }
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
        int maxDepth = 5;
        if (args.length >= 3 ) {
            maxDepth = Integer.parseInt(args[2]);
        }
        new AI1(Integer.parseInt(args[1]), args[0], maxDepth);
    }

}
