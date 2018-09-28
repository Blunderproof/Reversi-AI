import java.util.HashSet;

public class TestPruning {

    final int MAX_DEPTH = 3;
    int me = 1;

    public static void main(String args[]) {
        new TestPruning();
    }

    TestPruning(){
        // create parent node and all children
        RNode parent = new RNode(null, 0, 0.0, 1, -1, 0);
        
        RNode t1L = new RNode(parent, 0, 0.0, 2, 10, 1);
        RNode t1C = new RNode(parent, 0, 0.0, 2, 11, 1);
        RNode t1R = new RNode(parent, 0, 0.0, 2, 12, 1);
        parent.addChild(t1L);
        parent.addChild(t1C);
        parent.addChild(t1R);
        
        RNode t1Lt2L = new RNode(t1L, 0, 0.0, 1, 15, 2);

        RNode t1Lt2Lt3L = new RNode(t1Lt2L, 0, 8.0, 2, 20, 3);
        RNode t1Lt2Lt3C = new RNode(t1Lt2L, 0, 2, 2, 20, 3);
        RNode t1Lt2Lt3R = new RNode(t1Lt2L, 0, 0, 2, 20, 3);
        
        RNode t1Lt2R = new RNode(t1L, 0, 0.0, 1, 15, 2);

        RNode t1Lt2Rt3L = new RNode(t1Lt2R, 0, 1, 2, 20, 3);
        RNode t1Lt2Rt3C = new RNode(t1Lt2R, 0, 9, 2, 20, 3);
        RNode t1Lt2Rt3R = new RNode(t1Lt2R, 0, 2, 2, 20, 3);

        t1L.addChild(t1Lt2L);
        t1L.addChild(t1Lt2R);

        t1Lt2L.addChild(t1Lt2Lt3L);
        t1Lt2L.addChild(t1Lt2Lt3C);
        t1Lt2L.addChild(t1Lt2Lt3R);

        t1Lt2R.addChild(t1Lt2Rt3L);
        t1Lt2R.addChild(t1Lt2Rt3C);
        t1Lt2R.addChild(t1Lt2Rt3R);
        
        RNode t1Ct2L = new RNode(t1C, 0, 0.0, 1, 15, 2);

        RNode t1Ct2Lt3L = new RNode(t1Ct2L, 0, 2.0, 2, 20, 3);
        RNode t1Ct2Lt3C = new RNode(t1Ct2L, 0, 3, 2, 20, 3);
        RNode t1Ct2Lt3R = new RNode(t1Ct2L, 0, 0, 2, 20, 3);
        
        RNode t1Ct2R = new RNode(t1C, 0, 0.0, 1, 15, 2);

        RNode t1Ct2Rt3L = new RNode(t1Ct2R, 0, 0, 2, 20, 3);
        RNode t1Ct2Rt3C = new RNode(t1Ct2R, 0, 3, 2, 20, 3);
        RNode t1Ct2Rt3R = new RNode(t1Ct2R, 0, 1, 2, 20, 3);
        
        t1C.addChild(t1Ct2L);
        t1C.addChild(t1Ct2R);

        t1Ct2L.addChild(t1Ct2Lt3L);
        t1Ct2L.addChild(t1Ct2Lt3C);
        t1Ct2L.addChild(t1Ct2Lt3R);

        t1Ct2R.addChild(t1Ct2Rt3L);
        t1Ct2R.addChild(t1Ct2Rt3C);
        t1Ct2R.addChild(t1Ct2Rt3R);
        
        RNode t1Rt2L = new RNode(t1R, 0, 0.0, 1, 15, 2);

        RNode t1Rt2Lt3L = new RNode(t1Rt2L, 0, 0.0, 2, 20, 3);
        RNode t1Rt2Lt3C = new RNode(t1Rt2L, 0, 7, 2, 20, 3);
        RNode t1Rt2Lt3R = new RNode(t1Rt2L, 0, 5, 2, 20, 3);
        
        RNode t1Rt2R = new RNode(t1R, 0, 0.0, 1, 15, 2);

        RNode t1Rt2Rt3L = new RNode(t1Rt2R, 0, 5, 2, 20, 3);
        RNode t1Rt2Rt3C = new RNode(t1Rt2R, 0, 4, 2, 20, 3);
        RNode t1Rt2Rt3R = new RNode(t1Rt2R, 0, 7, 2, 20, 3);
        
        t1R.addChild(t1Rt2L);
        t1R.addChild(t1Rt2R);

        t1Rt2L.addChild(t1Rt2Lt3L);
        t1Rt2L.addChild(t1Rt2Lt3C);
        t1Rt2L.addChild(t1Rt2Lt3R);

        t1Rt2R.addChild(t1Rt2Rt3L);
        t1Rt2R.addChild(t1Rt2Rt3C);
        t1Rt2R.addChild(t1Rt2Rt3R);

        int move = getBestMoveUsingMinMax(parent);
        System.out.println(move);
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
          return node.getNetScore();
      }
      
      double bestScore = getScoreOfBestChild(node.getChildren().get(0), worstScoreForPlayer(node.getPlayer()));
      if (isScoreBetterThanBest(node.getPlayer(), bestScore, bestScoreSoFar)) {
          // pruning
          System.out.println("PRUNING 1 - bestScore: " + bestScore + " bestScoreSoFar: " + bestScoreSoFar);
          return bestScore;
      }

      for (int i = 1; i < node.getChildren().size(); i++) {
          double currentScore = getScoreOfBestChild(node.getChildren().get(i), bestScore);
          // at top level, we know this is going to be max
          if (isScoreBetterThanBest(node.getPlayer(), currentScore, bestScore)) {
              bestScore = currentScore;

              if (isScoreBetterThanBest(node.getPlayer(), bestScore, bestScoreSoFar)) {
                  // pruning
                  System.out.println("PRUNING 2 - bestScore: " + bestScore + " bestScoreSoFar: " + bestScoreSoFar);
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

  private boolean isMaxNode(int player) {
    return player == me;
  }


  private double worstScoreForPlayer(int player) {
    if (player == me) {
        return Double.MIN_VALUE;
    } else {
        return Double.MAX_VALUE;
    }
  }

}