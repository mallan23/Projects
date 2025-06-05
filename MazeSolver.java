import java.util.*;
import java.io.*;

public class MazeSolver {
    private int[][] maze;
    private boolean[][] visited;
    private int[][][] parent;
    private int[] start;
    private int[] end;
    private int cols;
    private int rows;

    //Maze solver constructor takes input file as a parameter
    public MazeSolver(String filename) throws FileNotFoundException {
        //Read input file, split the parameters and assign them, create 2d maze array and visited array to keep track of visited nodes, for loop to populate maze
        Scanner scanner = new Scanner(new File(filename));
        String[] parameters = scanner.nextLine().split(":");
        this.rows = Integer.parseInt(parameters[0]);
        this.cols = Integer.parseInt(parameters[1]);
        start = new int[]{(Integer.parseInt(parameters[2])-1) / cols, (Integer.parseInt(parameters[2])-1) % cols};
        end = new int[]{(Integer.parseInt(parameters[3])-1) / cols, (Integer.parseInt(parameters[3])-1) % cols};
        maze = new int[rows][cols];
        visited = new boolean[rows][cols];
        parent = new int[rows][cols][];
        String connectivityList = parameters[4];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = Character.getNumericValue(connectivityList.charAt(i * cols + j));
            }
        }
    }

    public List<int[]> solveBFS() {
        return solve(new LinkedList<>());
    }

    public List<int[]> solveDFS() {
        return solve(new ArrayDeque<>());
    }

    //Deque is used to track nodes to visit, adds the start node to the deque, loops while the deque is not empty, if it reaches end node it tracks the path back with a parent array
    //if it doesnt, it gets all the neighbours for current node, depending on stack or queue the next node will either be the most recently added or least recently
    //prints path, path length, and the total number of nodes generated 
    private List<int[]> solve(Deque<int[]> deque) {
        int totalNodesGenerated = 0; // Counter for total nodes generated
        deque.add(start);
        while (!deque.isEmpty()) {
            int[] node;
            if (deque instanceof LinkedList) { // BFS remove from the front
                node = deque.remove(); 
            } else { // DFS removes from the end
                node = deque.pop(); 
            }
            if (Arrays.equals(node, end)) {
                List<int[]> path = new ArrayList<>();
                while (node != null) {
                    path.add(0, node);
                    node = parent[node[0]][node[1]];
                }
                System.out.print("(");
                for (int i = 0; i < path.size(); i++) {
                    int[] coordinates = path.get(i);
                    int nodeNumber = coordinates[0] * cols + coordinates[1] + 1; 
                    System.out.print(nodeNumber);
                    if (i < path.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println(")");
                System.out.println(path.size()-1);
                System.out.println(totalNodesGenerated); 
                return path;
            }
            visited[node[0]][node[1]] = true;
            for (int[] neighbour : getNeighbours(node)) {
                if (!visited[neighbour[0]][neighbour[1]]) {
                    deque.add(neighbour);
                    parent[neighbour[0]][neighbour[1]] = node;
                    totalNodesGenerated++; 
                }
            }
        }
        return null;
    }

    //Gets the neighbours of the node passed by checking if bottom or right wall are open, and checking if the bottom or right is open for the cell above or to the left respectively
    private List<int[]> getNeighbours(int[] node) {
        List<int[]> neighbours = new ArrayList<>();
        if (maze[node[0]][node[1]] == 1 || maze[node[0]][node[1]] == 3) {
            neighbours.add(new int[]{node[0], node[1] + 1});
        }
        if (maze[node[0]][node[1]] == 2 || maze[node[0]][node[1]] == 3) {
            neighbours.add(new int[]{node[0] + 1, node[1]});
        }
        if (node[0] > 0 && (maze[node[0] - 1][node[1]] == 2 || maze[node[0] - 1][node[1]] == 3)) {
            neighbours.add(new int[]{node[0] - 1, node[1]});
        }
        if (node[1] > 0 && (maze[node[0]][node[1] - 1] == 1 || maze[node[0]][node[1] - 1] == 3)) {
            neighbours.add(new int[]{node[0], node[1] - 1});
        }
        return neighbours;
    }

    public void printSolution(List<int[]> path, PrintStream out) {
        out.print(path.size() - 1 + " : ");
        out.print("(");
                for (int i = 0; i < path.size(); i++) {
                    int[] coordinates = path.get(i);
                    int nodeNumber = coordinates[0] * cols + coordinates[1] + 1; 
                    out.print(nodeNumber);
                    if (i < path.size() - 1) {
                        out.print(", ");
                    }
                }
                out.println(")");
    }

    //reset the state of the parent and visited array 
    public void reset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                visited[i][j] = false;
                parent[i][j] = null;
            }
        }
    }

    //take inputs, run the bfs, reset the array, run the dfs, then print
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 2) {
            System.out.println("Format: java MazeSolver <mazeFile> <outputFile>");
            return;
        }

        MazeSolver solver = new MazeSolver(args[0]);
        File file = new File(args[1]);
        
        System.out.println("BFS");
        long startTime = System.currentTimeMillis();
        List<int[]> bfsPath = solver.solveBFS();
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
        
        solver.reset();

        System.out.println("DFS");
        startTime = System.currentTimeMillis();
        List<int[]> dfsPath = solver.solveDFS();
        endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

        //Prints to file
        try (FileOutputStream fos = new FileOutputStream(file, true); PrintStream ps = new PrintStream(fos)) {
            solver.printSolution(bfsPath, ps);
            solver.printSolution(dfsPath, ps);
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
}
