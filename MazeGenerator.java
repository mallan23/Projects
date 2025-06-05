import java.util.*;
import java.io.*;

public class MazeGenerator {
    private int rows;
    private int cols;
    private int[][] maze; //store wall info 
    private int[][] currDistArr; // store the dist from the start node for each node
    private boolean[][] visited; // store whether a node has been visited 
    private Random rand = new Random();
    private int startNode;
    private int endNode;
    private int currDist;
    private boolean allVisited = false;
    private int count = 0;

    public MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.maze = new int[rows][cols];
        this.visited = new boolean[rows][cols];
        this.currDistArr = new int[rows][cols];
    }

    public void generateMaze() {
        // Select a random starting node
        int startRow = rand.nextInt(rows);
        int startCol = rand.nextInt(cols);
        startNode = startRow * cols + startCol + 1;

        // Perform the random walk
        randomWalk(startRow, startCol);

        // Find the farthest node from the starting node
        int maxDistance = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (currDistArr[i][j] > maxDistance) {
                    maxDistance = currDistArr[i][j];
                    endNode = i * cols + j + 1;
                }
            }
        }
    }

    private void randomWalk(int row, int col) {
        visited[row][col] = true;

        int nextRow = 0;
        int nextCol = 0;
        while(allVisited==false){
            int direction = rand.nextInt(4);
            switch (direction) {
                case 0: //up, If we are moving above, current node walls dont change, next node opens from below (value 2)
                    if (row > 0 && !visited[row-1][col]) {
                        nextRow = row - 1;
                        nextCol = col;
                        maze[nextRow][nextCol] = 2;
                        break;
                    }
                case 1: //right, if we are moving to the right check if below is open, if so go to 3, else to 1 
                    if (col < visited[row].length - 1 && !visited[row][col+1]) {
                        nextRow = row;
                        nextCol = col + 1;
                        if (maze[row][col] == 2){
                            maze[row][col] = 3;
                        } else {
                            maze[row][col] = 1;
                        }
                        break;
                    }
                case 2: //down, If we are moving below check if right side is open (value 1), if so, value goes to 3, else to 2
                    if (row < visited.length - 1 && !visited[row+1][col]) {
                        nextRow = row + 1;
                        nextCol = col;
                        if (maze[row][col] == 1) {
                            maze[row][col] = 3;
                        } else {
                            maze[row][col] = 2;
                        }
                        break;
                    }
                case 3: //left, If we are moving to the left, curr node stays, next node opens to right
                    if (col > 0 && !visited[row][col-1]) {
                        nextRow = row;
                        nextCol = col - 1;
                        maze[nextRow][nextCol] = 1; 
                        break;
                    }
                default: //runs if a neighbour hasnt been found, checks to see if there is no current neighbours, if so, then it checks to see if all nodes are visited, if not then it finds the closest node from the next unvisited node and starts walking again 
                    if ((row > 0 && !visited[row-1][col]) || (col < visited[row].length - 1 && !visited[row][col+1]) 
                    || (row < visited.length - 1 && !visited[row+1][col]) || (col > 0 && !visited[row][col-1])){
                        nextRow = row;
                        nextCol = col;
                        break;
                    } else{
                        allVisited = true;
                        outerOuterLoop:
                        for (int i = 0; i < visited.length; i++) {
                            for (int j = 0; j < visited[i].length; j++) {
                                if (!visited[i][j]) {
                                    allVisited = false;
                                    if (j>0){//not the first col
                                        nextRow = i;
                                        nextCol = j-1;
                                    } else if (i>0){//make sure not the first node 
                                        nextRow = i-1;
                                        nextCol = j;
                                    } else {//if its the first node we need to reloop and find the next visited node to go from
                                        outerLoop:
                                        for (int k = 0; k < visited.length; k++) {
                                            for (int l = 0; l < visited[k].length; l++) {
                                                if (visited[k][l]) {
                                                    nextRow = k;
                                                    nextCol = l;
                                                    break outerLoop;
                                                }
                                            }
                                        }
                                    }
                                    break outerOuterLoop;
                                }
                            }
                        }
                        if (allVisited) {
                            nextRow = row;
                            nextCol = col;
                            break;
                        }
                    }
            }
            if (visited[nextRow][nextCol] == false){
                currDist = currDistArr[row][col];
                currDist += 1;
                currDistArr[nextRow][nextCol] = currDist;
            }
            // Continue the random walk from the selected neighbor
            randomWalk(nextRow, nextCol);
        }
    }

    //Prints the maze in the required format
    public void printMaze(PrintStream out) {
        out.print(rows + ":" + cols + ":" + startNode + ":" + endNode + ":");
        System.out.print(rows + ":" + cols + ":" + startNode + ":" + endNode + ":");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                out.print(maze[i][j]);
                System.out.print(maze[i][j]);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Use Format: java MazeGenerator <rows> <cols> <output_file>");
            System.exit(1);
        }

        File file = new File(args[2]);
        

        try {
            int rows = Integer.parseInt(args[0]);
            int cols = Integer.parseInt(args[1]);

            MazeGenerator generator = new MazeGenerator(rows, cols);
            
            try{
                generator.generateMaze();
            } catch (OutOfMemoryError e){
                System.err.println("Error: Unable to allocate required memory for the maze.");
                System.exit(1);
            } catch (StackOverflowError e) {
                System.err.println("Error: Stack overflow. The maze size may be too large for recursion.");
                System.exit(1);
            }
            
            try (FileOutputStream fos = new FileOutputStream(file); PrintStream ps = new PrintStream(fos)) {
                generator.printMaze(ps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid input: rows and cols must be integers");
            System.exit(1);
        }
    }
}