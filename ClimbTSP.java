import java.util.*;
import java.io.*;

public class ClimbTSP {
    static int N;
    static int[][] dp;
    static ArrayList<Integer> x;
    static ArrayList<Integer> y;
    static int[][] parent;
    static List<Integer> cycle = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        x = new ArrayList<>();
        y = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        N = Integer.parseInt(br.readLine());

        //read points and store in x and y lists
        for (int i = 0; i < N; i++) {
            String[] input = br.readLine().split(" ");
            x.add(Integer.parseInt(input[0]));
            y.add(Integer.parseInt(input[1]));
        }

        // Initialize cycle with 0 at the start and end
        cycle.add(0); 
        for (int i = 1; i < N; i++) {
            cycle.add(i);
        }
        cycle.add(0); 

        long startTime = System.currentTimeMillis();

        climb();

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        
        System.out.println("Execution time in milliseconds : " + timeElapsed);
        br.close();
    }

    //Hill climbing implemented with summulated annealing for neighbour selection, concept is that you occasionally accept worse solutions in hopes they can lead to better ones
    static void climb() {
        //calculate total distance once
        double bestDistance = 0;
        for (int i = 0; i < cycle.size() - 1; i++) {
            bestDistance += dist(cycle.get(i), cycle.get(i + 1));
        }

        //intialise variables for annealing
        double temperature = 10000.0;
        double coolingRate = 0.003;
    
        //stopping condition
        while (temperature > 1) {
            //calculates change in distance instead of total distance for each potential neighbour
            for (int i = 1; i < N - 1; i++) {
                for (int j = i + 1; j < N - 1; j++) {
                    double oldDistance, newDistance;
                    if (j == i + 1) { // when nodes are next to each other
                        oldDistance = dist(cycle.get(i - 1), cycle.get(i)) + dist(cycle.get(j), cycle.get(j + 1));
                        newDistance = dist(cycle.get(i - 1), cycle.get(j)) + dist(cycle.get(i), cycle.get(j + 1));
                    } else {
                        oldDistance = dist(cycle.get(i - 1), cycle.get(i)) + dist(cycle.get(i), cycle.get(i + 1))
                                    + dist(cycle.get(j - 1), cycle.get(j)) + dist(cycle.get(j), cycle.get(j + 1));
                        newDistance = dist(cycle.get(i - 1), cycle.get(j)) + dist(cycle.get(j), cycle.get(i + 1))
                                    + dist(cycle.get(j - 1), cycle.get(i)) + dist(cycle.get(i), cycle.get(j + 1));
                    }
                    //if probability is greater than a random probability, accept it 
                    if (acceptanceProbability(oldDistance, newDistance, temperature) > Math.random()) {
                        Collections.swap(cycle, i, j);
                        bestDistance -= (oldDistance - newDistance);
                    }
                }
            }
            //Adjust temp
            temperature *= 1-coolingRate;
        }
        System.out.println("Minimum cost: " + bestDistance);
        System.out.println("Cycle: " + cycle);
    }
    
    //calculates a probability of accepting a worse solution, probabilty is higher when temperature is higher
    static double acceptanceProbability(double energy, double newEnergy, double temperature) {
        if (newEnergy < energy) {
            return 1.0;
        }
        return Math.exp((energy - newEnergy) / temperature);
    }

    //Calculates euclidean distance
    static int dist(int i, int j) {
        return (int)Math.sqrt(Math.pow(x.get(j) - x.get(i), 2) + Math.pow(y.get(j) - y.get(i), 2));
    }
}
