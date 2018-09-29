import java.util.List;
import java.util.ArrayList;

/**
 * Helper class designed to return the right number of tokens for each player
 * so that we don't have to iterate over the state twice to count the same thing.
 * Can't return tuples in Java, so we build a helper class
 */
public class PieceCount {
    private int firstPlayerCount = 0;
    private int secondPlayerCount = 0;

    public PieceCount(int firstPlayerCount, int secondPlayerCount) {
        this.firstPlayerCount = firstPlayerCount;
        this.secondPlayerCount = secondPlayerCount;
    }

    public int getMyCountForPlayer(int player) {
      if (player == 1) {
        return firstPlayerCount;
      } else {
        return secondPlayerCount;
      }
    }

    public int getOpponentCountForPlayer(int player) {
      if (player == 1) {
        return secondPlayerCount;
      } else {
        return firstPlayerCount;
      }
    }

    public int getFirstPlayerCount() {
      return firstPlayerCount;
    }

    public int getSecondPlayerCount() {
      return secondPlayerCount;
    }

    public static PieceCount countPiecesFromState(int[][] state) {
      int firstPlayerCount = 0;
      int secondPlayerCount = 0;
      for (int row = 7; row >= 0; row--) {
        for (int col = 0; col < 8; col++) {
            if (state[row][col] == 1) {
              firstPlayerCount++;
            } else if (state[row][col] == 2) {
              secondPlayerCount++;
            }
        }
      }
      return new PieceCount(firstPlayerCount, secondPlayerCount);
    }

}