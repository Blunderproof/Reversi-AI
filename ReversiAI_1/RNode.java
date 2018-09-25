import java.util.LinkedList;

public class RNode {
    private List<RNode> children = new LinkedList<>();
    
    private RNode parent;
    private int depth;
    private int [][]state = new int[8][8];
    private double netScore;
    private int player;
    private int move;

    public RNode(RNode parent, int depth, int[][] state, double netScore, int player, int move) {
        self.parent = parent;
        self.depth = depth;
        self.state = state;
        self.netScore = netScore;
        self.player = player;
        self.move = move;
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