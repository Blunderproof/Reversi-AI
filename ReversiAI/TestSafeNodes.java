import java.util.HashSet;

public class TestSafeNodes {

    public static void main(String args[]) {
        new TestSafeNodes();
    }

    TestSafeNodes(){
        int[][] currState = new int[8][8];
        currState[0][0] = 1;
        currState[0][1] = 1;
        currState[1][0] = 1;
        currState[0][2] = 1;
        currState[2][0] = 1;
        currState[1][1] = 1;
        currState[0][3] = 1;

        // 1 1 1 1
        // 1 2
        // 1

        System.out.println("yo about to count");

        int safe = countSafePositions(currState, 1);
        System.out.println(safe);
    }

    private int countSafePositions(int [][]currState, int turn){
        HashSet<String> safePositions = new HashSet<>(); 

        if(currState[0][0] == turn){ //bottom left
            int x = 0;
            int y = 0;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x++;
                if (x >= 8) break;
            }
            x = 0;
            y = 0;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y++;
                if (y >= 8) break;
            }
            northwestLoop:
            for(x = 0; x < 8; x++){ // north-west
                y = 0;
                int currX = x;
                while(currX >= 0){
                    if(currState[currX][y] != turn){
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
                    if(currState[x][currY] != turn){
                        break southeastLoop;
                    }
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(currY));
                    x+=1;
                    currY-=1;
                }
            }
        
        }

        if(currState[7][0] == turn){ // botton right
            int x = 7;
            int y = 0;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x--;
                if (x < 0) break;
            }
            x = 7;
            y = 0;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y++;
                if (y >= 8) break;
            }
            northeastLoop:
            for(x = 7; x >= 0; x--){ // north-east
                y = 0;
                int currX = x;
                while(currX < 8){
                    if(currState[currX][y] != turn){
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
                    if(currState[x][currY] != turn){
                        break southwestLoop;
                    }
                    safePositions.add(Integer.toString(x) + " " + Integer.toString(currY));
                    x-=1;
                    currY-=1;
                }
            }
        }
        if(currState[0][7] == turn){ // upper left
            int x = 0;
            int y = 7;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x++;
                if (x >= 8) break;

            }
            x = 0;
            y = 7;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y--;
                if (y < 0) break;
            }
            northeastLoop:
            for(y = 7; y >= 0; y--){ // north-east
                x = 0;
                int currY = y;
                while(currY < 8){
                    if(currState[x][currY] != turn){
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
                    if(currState[currX][y] != turn){
                        break southwestLoop;
                    }
                    safePositions.add(Integer.toString(currX) + " " + Integer.toString(y));
                    currX-=1;
                    y-=1;
                }
            }
        }
        if(currState[7][7] == turn){ // upper right
            int x = 7;
            int y = 7;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                x--;
                if (x < 0) break;
            }
            x = 7;
            y = 7;
            while(currState[x][y] == turn){
                safePositions.add(Integer.toString(x) + " " + Integer.toString(y));
                y--;
                if (y < 0) break;
            }
            southeastLoop:
            for(x = 7; x >= 0; x--){ // south-east
                y = 7;
                int currX = x;
                while(currX < 8){
                    if(currState[currX][y] != turn){
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
                    if(currState[x][currY] != turn){
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

}