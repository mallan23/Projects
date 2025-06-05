import java.io.*;
import java.util.*;

public class MazeVerifier {
    private int[][] maze;
    private boolean[][] visited;
    private int[] rowDirection = {-1, 0, 1, 0};
    private int[] colDirection = {0, 1, 0, -1};
    private int startNode, endNode;
    private int rows, cols;
    private int[] solution;

    public MazeVerifier(String mazeFileName, String solutionFileName) {
        try {
            // Read the maze file
            Scanner scanner = new Scanner(new File(mazeFileName));
            String[] parameters = scanner.nextLine().split(":");
            rows = Integer.parseInt(parameters[0]);
            cols = Integer.parseInt(parameters[1]);
            startNode = Integer.parseInt(parameters[2]);
            endNode = Integer.parseInt(parameters[3]);
            maze = new int[rows][cols];
            visited = new boolean[rows][cols];
            String connectivityList = parameters[4];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    maze[i][j] = Character.getNumericValue(connectivityList.charAt(i * cols + j));
                }
            }

            // Read the solution file
            scanner = new Scanner(new File(solutionFileName));
            parameters = scanner.nextLine().split(":");
            int steps = Integer.parseInt(parameters[0].trim()); // Trim spaces before parsing
            String[] integerStrings = parameters[1].replaceAll("[()]", "").split(", "); // Extract integers within parentheses
            solution = new int[steps + 1];
            for (int i = 0; i < integerStrings.length; i++) {
                solution[i] = Integer.parseInt(integerStrings[i].trim());
            }
    
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    

    public void verifyMaze() {
        int allWalls = 0, noWalls = 0;
        boolean circularPath = false, allNodesVisited = true;

        visited = new boolean[rows][cols];
    
        //Check cells to see if no walls or all walls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Check the number of open walls for each cell
                int openWalls = 0;
                if (i > 0 && (maze[i - 1][j] == 2 || maze[i - 1][j] == 3)) { // Above wall is open
                    openWalls++;
                }
                if (j > 0 && (maze[i][j - 1] == 1 || maze[i][j - 1] == 3)) { // Left wall is open
                    openWalls++;
                }
                if (maze[i][j] == 1 || maze[i][j] == 3) { // Right wall is open
                    openWalls++;
                }
                if (maze[i][j] == 2 || maze[i][j] == 3) { // Below wall is open
                    openWalls++;
                }
                if (openWalls == 0) {
                    allWalls++;
                } else if (openWalls == 4) {
                    noWalls++;
                }
            }
        }


        // Perform a DFS from the starting node
        circularPath = dfs(startNode, startNode);

        // Display the maze with the solution
        displayMazeWithSolution();
    
        // Check if all nodes have been visited
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!visited[i][j]) {
                    allNodesVisited = false;
                    break;
                }
            }
            if (!allNodesVisited) {
                break;
            }
        }
    
        // Display the information on the screen
        System.out.println("Number of cells having all four walls: " + allWalls);
        System.out.println("Number of cells with none of the four walls: " + noWalls);
        System.out.println("Is there a circular path in the maze? " + (circularPath ? "Yes" : "No"));
        System.out.println("Can all nodes be visited from the starting node? " + (allNodesVisited ? "Yes" : "No"));
    }
    

    public void verifySolution() {
        boolean validSolution = isValidSolution();
        
        if (validSolution) {
            System.out.println("Valid");
        } else {
            System.out.println("Invalid");
        }
    }
    
    private boolean isValidSolution() {
        int currentRow = (startNode - 1) / cols;
        int currentCol = (startNode - 1) % cols;
        
        for (int i = 1; i < solution.length; i++) {
            int nextNode = solution[i];
            int nextRow = (nextNode - 1) / cols;
            int nextCol = (nextNode - 1) % cols;
            
            if (!isValidMove(currentRow, currentCol, nextRow, nextCol)) {
                // Invalid move in the solution, print the partial path and then return false
                System.out.print("Partial Path: ");
                for (int j = 0; j < i; j++) {
                    System.out.print(solution[j]);
                }
                System.out.println();
                return false;
            }
            
            currentRow = nextRow;
            currentCol = nextCol;
        }

        // Check if the final position matches the endNode
        boolean isFinalValid = (currentRow * cols + currentCol + 1) == endNode;
        
        if (!isFinalValid) {
            // The final position is not the endNode, print the partial path
            System.out.print("Partial Path: ");
            for (int j = 0; j < solution.length - 1; j++) {
                System.out.print(solution[j]);
            }
            System.out.println();
        }
            
        return isFinalValid;
    }

    private boolean isValidMove(int currentRow, int currentCol, int nextRow, int nextCol) {
        boolean wallOpen;
        
        // Check if the wall is open in the direction being moved to
        if (nextRow > currentRow) { // Down
            wallOpen = (maze[currentRow][currentCol] == 2 || maze[currentRow][currentCol] == 3);
        } else if (nextRow < currentRow) { // Up
            wallOpen = (maze[nextRow][nextCol] == 2 || maze[nextRow][nextCol] == 3);
        } else if (nextCol > currentCol) { // Right
            wallOpen = (maze[currentRow][currentCol] == 1 || maze[currentRow][currentCol] == 3);
        } else { // Left
            wallOpen = (maze[nextRow][nextCol] == 1 || maze[nextRow][nextCol] == 3);
        }        
        
        return wallOpen;
    }

    private boolean dfs(int node, int parent) {
        int row = (node - 1) / cols;
        int col = (node - 1) % cols;
        visited[row][col] = true; // Mark the current node as visited

        for (int i = 0; i < 4; i++) { // Loop through all four possible directions (up, right, down, left)
            int nextRow = row + rowDirection[i]; // Find the row of the next node
            int nextCol = col + colDirection[i]; // Find the column of the next node

            if (nextRow >= 0 && nextRow < rows && nextCol >= 0 && nextCol < cols) { // If the next node is within the maze and has not been visited yet
                boolean wallOpen = false;

                if (i == 0) { // Up
                    wallOpen = (maze[nextRow][nextCol] == 2 || maze[nextRow][nextCol] == 3);
                } else if (i == 1) { // Right
                    wallOpen = (maze[row][col] == 1 || maze[row][col] == 3);
                } else if (i == 2) { // Down
                    wallOpen = (maze[row][col] == 2 || maze[row][col] == 3);
                } else if (i == 3) { // Left
                    wallOpen = (maze[nextRow][nextCol] == 1 || maze[nextRow][nextCol] == 3);
                }
                
                if (wallOpen && !visited[nextRow][nextCol]) {
                    // If the wall is open and the next cell has not been visited, recursively visit it
                    if (dfs(nextRow * cols + nextCol+1, node)) {
                        return true; // Detected a circular path
                    }
                } else if ((nextRow * cols + nextCol+1) != parent && visited[nextRow][nextCol] && wallOpen) {
                    // If the next cell has been visited and is not the parent node, it's a circular path
                    return true;
                }
            }
        }
        return false;
    }

    private void displayMazeWithSolution() {
        if (rows > 10 || cols > 10){
            return;
        }
        
        // Display the maze with '-' or '|' for walls, including outer walls
        for (int i = 0; i < rows; i++) {
            
            if (i==0){
                // Display top outer wall
                for (int j = 0; j < cols; j++) {
                    if (i == 0) {
                        System.out.print("+--");
                    } else {
                        System.out.print("+  ");
                    }
                }
                System.out.println("+");
            }
    
            for (int j = 0; j < cols; j++) {
                // Display left outer wall and inner vertical walls
                if (j == 0 || (maze[i][j - 1] != 1 && maze[i][j - 1] != 3)) {
                    System.out.print("|");
                } else {
                    System.out.print(" "); 
                }
    
                int currentCell = i * cols + j + 1;
    
                if (isCellVisited(currentCell)) {
                    if (currentCell == startNode){
                        System.out.print("S "); // Start cell
                    } else if (currentCell == endNode){
                        System.out.print("F "); // End cell
                    }else {
                        System.out.print("* "); // Visited cell
                    }
                } else {
                    System.out.print("  "); // Unvisited cell
                }
    
                // Display right outer wall
                if (j == cols - 1){ 
                    System.out.print("|");
                }
            }
            System.out.println();            
            
            // Display middle rows horizontal walls
            if (i != rows - 1) {
                for (int j = 0; j < cols; j++) {
                    if ((maze[i][j] != 2 && maze[i][j] != 3)){ 
                        System.out.print("+--");
                    } else {
                        System.out.print("+  ");
                    }
                }
                System.out.println("+");
            }
        }
    
        // Display the bottom outer wall
        for (int j = 0; j < cols; j++) {
            System.out.print("+--");
        }
        System.out.println("+");
        System.out.println();
    }
    
    
    private boolean isCellVisited(int cell) {
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == cell) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java MazeVerifier <maze_file> <solution_file>");
            return;
        }

        MazeVerifier verifier = new MazeVerifier(args[0], args[1]);
        verifier.verifyMaze();
        verifier.verifySolution();
        
    }
}
