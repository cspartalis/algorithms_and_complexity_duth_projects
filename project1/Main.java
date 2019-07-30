import gr.james.influence.algorithms.scoring.PageRank;
import gr.james.influence.graph.DirectedGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        // Load the Zachary Karate Club graph
        // The graph contains 34 integer vertices: 1 through 34
        // Node 1 stands for the instructor, node 34 for the club administrator / president.
        final DirectedGraph<Integer, Object> g = KarateClub.loadKarateClub();

        // Instantiate PageRank algorithm with damping factor 0 (aka eigenvector centrality)
        final PageRank<Integer> pageRank = new PageRank<>(g, 0.0, -1);

        // Print a string representation of the graph
        System.out.println(g);

        // Print all the vertices
        final Set<Integer> vertexSet = g.vertexSet();
        System.out.println(vertexSet);

        // Print the connections of the instructor
        final Set<Integer> instructorConnections = g.adjacentOut(1);
        System.out.println(instructorConnections);

        // Write code here to calculate the PageRank value of the vertices
        System.out.println(pageRank.run());

        // Write code here to calculate the strengths of the vertices

        // You may start from the fragment below but it is not necessary
        final Map<Integer, Double> strengths = new HashMap<>();
        for (int v : g) { // For all vertices in the graph
            double strength = 0;
            for (int z : g.adjacentOut(v)) // Get all outbound vertices of node {z}
                strength += g.getWeight(v,z); // Calculate the strength of node {z}
            strengths.put(v, strength);
        }
        System.out.println(strengths);

        // Normalize the strengths map so that the average of its values is 1.0
        double sum = 0;
        for (double j : strengths.values())
            sum += j;


        // I have to devide the strengths by the sum and then multiply by the number of vertices(=34)
        for (int v : g){
            strengths.put(v, strengths.get(v)*34/sum);
        }

        System.out.println(strengths);

        // Print the strengths as wanted in the presentation of the exercise
        for (int v : g)
            System.out.print(strengths.get(v)+" ");

    }
}


