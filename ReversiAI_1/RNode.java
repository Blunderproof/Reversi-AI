import java.util.List;
import java.util.ArrayList;

public class RNode {
    private List<RNode> children = new ArrayList<>();
    
    private RNode parent;
    private int depth;
    private int [][]state = new int[8][8];
    private double netScore;
    private int player;
    private int move;

    public RNode(RNode parent, int depth, int[][] state, double netScore, int player, int move) {
        this.parent = parent;
        this.depth = depth;
        this.state = state;
        this.netScore = netScore;
        this.player = player;
        this.move = move;
    }

    public void addChild(RNode child){
        children.add(child);
    }

    public List<RNode> getChildren(){
        return children;
    }

    public double getNetScore() {
        return netScore;
    }

    public int getDepth(){
        return depth;
    }

    public int getMove(){
        return move;
    }

    public int getPlayer() {
        return player;
    }

    public int[][] getState(){
        return state;
    }


}