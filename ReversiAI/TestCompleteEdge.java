import java.util.HashSet;

public class TestCompleteEdge {

    int me = 1;
    int oldState[][] = new int[8][8];
    int newState[][] = new int[8][8];

    public static void main(String args[]) {
        // to run: javac TestCompleteEdge.java && java TestCompleteEdge
        new TestCompleteEdge();
    }

    TestCompleteEdge() {
        oldState[0] = new int[] { 0, 0, 2, 1, 1, 1, 0, 0 };
        oldState[1] = new int[] { 1, 0, 0, 0, 0, 0, 0, 0 };
        oldState[2] = new int[] { 1, 0, 0, 0, 0, 0, 0, 1 };
        oldState[3] = new int[] { 1, 0, 0, 0, 0, 0, 0, 1 };
        oldState[4] = new int[] { 0, 0, 0, 0, 0, 0, 0, 1 };
        oldState[5] = new int[] { 0, 0, 0, 0, 0, 0, 0, 1 };
        oldState[6] = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        oldState[7] = new int[] { 0, 2, 2, 0, 0, 0, 0, 0 };

        System.out.println( completeEdge(oldState, me, 0, 0, 1, 0) );
        System.out.println( completeEdge(oldState, me, 0, 0, 0, 1) );
        System.out.println( completeEdge(oldState, me, 7, 7, -1, 0) );
        System.out.println( completeEdge(oldState, me, 7, 7, 0, -1) );
        System.out.println( "---" );
        
        newState[0] = new int[] { 0, 1, 1, 1, 1, 1, 0, 0 };
        newState[1] = new int[] { 1, 0, 0, 0, 0, 0, 0, 0 };
        newState[2] = new int[] { 1, 0, 0, 0, 0, 0, 0, 1 };
        newState[3] = new int[] { 1, 0, 0, 0, 0, 0, 0, 1 };
        newState[4] = new int[] { 0, 0, 0, 0, 0, 0, 0, 1 };
        newState[5] = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        newState[6] = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        newState[7] = new int[] { 0, 2, 2, 0, 0, 0, 0, 0 };
        
        System.out.println( completeEdge(newState, me, 0, 0, 1, 0) );
        System.out.println( completeEdge(newState, me, 0, 0, 0, 1) );
        System.out.println( completeEdge(newState, me, 7, 7, -1, 0) );
        System.out.println( completeEdge(newState, me, 7, 7, 0, -1) );

        System.out.println( calculateNumberOfNewCompleteEdges(oldState, newState, me) );
    }

    private boolean completeEdge(int[][] state, int player, int rowStart, int colStart, int dRow, int dCol) {
        boolean haveSeenOurToken = false;
        boolean ourTokenLineEnded = false;
        for (int i = 0; i < 8; i++) {
            int tokenAtLocation = state[rowStart + i * dRow][colStart + i * dCol];
            if (tokenAtLocation != 0 && tokenAtLocation != player) {
                // the second we see an enemy, end it
                return false;
            }
            if (haveSeenOurToken) {
                if (tokenAtLocation == 0) {
                    // had a line, now see 0
                    ourTokenLineEnded = true;
                } else {
                    if (ourTokenLineEnded) {
                        // we had a consecutive line, then saw a 0, now we see our token again
                        return false;
                    }
                }
            } else {
                if (tokenAtLocation != 0) {
                    // we've seen our team's token
                    haveSeenOurToken = true;
                }
            }
        }
        if (!haveSeenOurToken) {
            // empty edge
            return false;
        }
        return true;
    }

    private int calculateNumberOfNewCompleteEdges(int[][] oldState, int[][] newState, int player) {
        int count = 0;

        if (completeEdge(newState, player, 0, 0, 1, 0) && !completeEdge(oldState, player, 0, 0, 1, 0)) {
            count += 1;
        }
        if (completeEdge(newState, player, 0, 0, 0, 1) && !completeEdge(oldState, player, 0, 0, 0, 1)) {
            count += 1;
        }
        if (completeEdge(newState, player, 7, 7, -1, 0) && !completeEdge(oldState, player, 7, 7, -1, 0)) {
            count += 1;
        }
        if (completeEdge(newState, player, 7, 7, 0, -1) && !completeEdge(oldState, player, 7, 7, 0, -1)) {
            count += 1;
        }

        return count;
    }

}