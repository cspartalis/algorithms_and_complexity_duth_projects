import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import gr.james.influence.algorithms.iterators.GraphStateIterator;
import gr.james.influence.algorithms.scoring.DeGroot;
import gr.james.influence.algorithms.scoring.PageRank;
import gr.james.influence.graph.DirectedGraph;
import gr.james.influence.graph.Graphs;
import gr.james.influence.util.collections.GraphState;
import gr.james.influence.util.collections.Weighted;

public class Exercise3 {

    public static double getInfluenceOfStubbornNode(DirectedGraph<Integer, Object> g, Integer node) {
        double influence = 0;
        //TODO
        GraphState<Integer, Double> gs = PageRank.execute(g, 1.0);
        GraphStateIterator<Integer, Double> gsi = new GraphStateIterator <Integer, Double> (gs);
        double sumRank = gs.getSum();
        Map<Integer, Double> influenceVector = new HashMap<Integer, Double>();
        while (gsi.hasNext()) {
            Weighted<Integer, Double> n = gsi.next();
            influenceVector.put(n.object, (n.weight / sumRank));
        }

        if (node==1)
            influence = influenceVector.get(1);
        else if (node==34)
            influence = influenceVector.get(34);

        return influence;

    }

    public static Set<Integer> getBestResponseOfNode(DirectedGraph<Integer, Object> g, Integer node, Set<Integer> possibleMoves) {
        Set<Integer> bestResponse = new HashSet<Integer>();

        double bestValue = -1;
        for (Integer move: possibleMoves) {
            g.addEdge(move, node);
            double influence = getInfluenceOfStubbornNode(g, node);
            g.removeEdge(move,  node);

            if (influence > bestValue) {
                bestResponse.clear(); // in case there were moves with less utility
                bestResponse.add(move);
                bestValue = influence;
            } else if (influence == bestValue) {
                bestResponse.add(move);
            }
        }
        return bestResponse;
    }

    public static boolean checkIfProfileIsNE(DirectedGraph<Integer, Object> g, Integer move1, Integer move34, Map<Integer,Set<Integer>> bestResponsesOfNode1, Map<Integer,Set<Integer>> bestResponsesOfNode34) {

        boolean isNE = false;

        //TODO
        boolean flag1;
        boolean flag34;

        Set<Integer> responsesOfNode1 = bestResponsesOfNode1.get(move34);
        flag1=responsesOfNode1.contains(move1);
        Set<Integer> responsesOfNode34 = bestResponsesOfNode34.get(move1);
        flag34=responsesOfNode34.contains(move34);

        if (flag1 && flag34){
            isNE = true;
            System.out.println();
            System.out.println("("+move1+", "+move34+")");

        }

        return isNE;
    }

    public static void main(String[] args) throws IOException {

        final DirectedGraph<Integer, Object> g = SocialNetwork.loadGraph("network-valued-uniform-stubborn.txt");

        // Stubborn Agents
        System.out.println("Stubborn agents:");
        System.out.println(Graphs.getStubbornVertices(g));

        // Print all the vertices
        final Set<Integer> vertexSet = g.vertexSet();
        System.out.println(vertexSet);

        // Print the connections of the instructor
        final Set<Integer> instructorConnections = g.adjacentOut(1);
        System.out.println(instructorConnections);

        // Find possible moves
        Set<Integer> adjacentInOfNode1 = g.adjacentIn(1);
        Set<Integer> adjacentInOfNode34 = g.adjacentIn(34);

        Set<Integer> allVertices = g.vertexSet();

        Set<Integer> possibleMovesOfNode1 = allVertices.stream().map(Integer::new).collect(Collectors.toSet());
        possibleMovesOfNode1.removeAll(adjacentInOfNode1);
        possibleMovesOfNode1.remove(34);

        Set<Integer> possibleMovesOfNode34 = allVertices.stream().map(Integer::new).collect(Collectors.toSet());
        possibleMovesOfNode34.removeAll(adjacentInOfNode34);
        possibleMovesOfNode34.remove(1);

        System.out.println();
        System.out.println("Not adjacent-in to node 1");
        System.out.println(possibleMovesOfNode1);

        System.out.println();
        System.out.println("Not adjacent-in to node 34");
        System.out.println(possibleMovesOfNode34);

        /*------------------------------------------*/
        System.out.println();
        System.out.println("Influence of 1: "+getInfluenceOfStubbornNode(g,1));
        System.out.println("Influence of 34: "+getInfluenceOfStubbornNode(g,34));
        /*------------------------------------------*/


        // Compute all best responses
        Map <Integer, Set<Integer>> bestResponsesOfNode1 = new HashMap <Integer, Set<Integer>>();
        // TODO
        for (Integer opMove : possibleMovesOfNode34){
            g.addEdge(opMove,34);
            Set<Integer> bestResponse1 = getBestResponseOfNode(g,1,possibleMovesOfNode1);
            bestResponsesOfNode1.put(opMove,bestResponse1);
            g.removeEdge(opMove,34);

        }
        System.out.println();
        System.out.println(bestResponsesOfNode1);





        Map <Integer, Set<Integer>> bestResponsesOfNode34 = new HashMap <Integer, Set<Integer>>();
        // TODO

        for (Integer opMove : possibleMovesOfNode1) {
            g.addEdge(opMove,1);
            Set<Integer> bestResponse34 = getBestResponseOfNode(g, 34, possibleMovesOfNode34);
            bestResponsesOfNode34.put(opMove,bestResponse34);
            g.removeEdge(opMove, 1);
        }

        System.out.println();
        System.out.println(bestResponsesOfNode34);


        for ( Integer move1: possibleMovesOfNode1) {
            for ( Integer move34: possibleMovesOfNode34) {
                if (checkIfProfileIsNE(g, move1, move34, bestResponsesOfNode1, bestResponsesOfNode34)) {
                    System.out.println("Profile: " + move1 + ", " + move34 + " is a NE");
                } else {
//					System.out.println("Profile: " + move1 + ", " + move34 + " is a NOT a NE");
                }
            }
        }


        /*---------------------------------------------------------------*/
        DirectedGraph<Integer, Object> k = DirectedGraph.create(g);

        // Execute PageRank and store results in a GraphState object for k
        GraphState<Integer, Double> ks = PageRank.execute(k, 1);

        // Calculate Sum of Ranks for k
        GraphStateIterator<Integer, Double> ksi = new GraphStateIterator <Integer, Double> (ks);

        Map<Integer, Double> influenceVector2 = new HashMap<Integer, Double>();

        while (ksi.hasNext()) {
            Weighted<Integer, Double> node2 = ksi.next();
            influenceVector2.put(node2.object, (node2.weight));  // the influence vector is not normalized
        }

        while (ksi.hasNext()) {
            Weighted<Integer, Double> node2 = ksi.next();
            influenceVector2.put(node2.object, (node2.weight));  // the influence vector is not normalized
        }


        double sumOfOpinions = 0;
        for (int i=1; i<=34; i++){
            sumOfOpinions += influenceVector2.get(i);
        }

        double meanOfOpinions = sumOfOpinions/34;

        System.out.println();
        System.out.println("Influence Vector before adding edges");
        System.out.println(influenceVector2);
        System.out.println("Mean of opinions before adding edges");
        System.out.println(meanOfOpinions);

        /*-------------------------------------------------------------*/

        //1st pure NE
        k.addEdge(26,1);
        k.addEdge(5,34);

        // Execute PageRank and store results in a GraphState object for k
        ks = PageRank.execute(k, 1);

        // Calculate Sum of Ranks for k
        ksi = new GraphStateIterator <Integer, Double> (ks);

        influenceVector2 = new HashMap<Integer, Double>();

        while (ksi.hasNext()) {
            Weighted<Integer, Double> node2 = ksi.next();
            influenceVector2.put(node2.object, (node2.weight));  // the influence vector is not normalized
        }


        sumOfOpinions = 0;
        for (int i=1; i<=34; i++){
            sumOfOpinions += influenceVector2.get(i);
        }

        meanOfOpinions = sumOfOpinions/34;

        System.out.println();
        System.out.println("Influence Vector after adding edges (26,1) & (5,34) to the initial graph");
        System.out.println(influenceVector2);
        System.out.println("Mean of opinions after adding edges (26,1) & (5,34) to the initial graph");
        System.out.println(meanOfOpinions);

        //Going back to the initial graph
        k.removeEdge(26,1);
        k.removeEdge(5,34);

        /*-------------------------------------------------------------------*/

        //2nd pure NE
        k.addEdge(26,1);
        k.addEdge(11,34);

        // Execute PageRank and store results in a GraphState object for k
        ks = PageRank.execute(k, 1);

        // Calculate Sum of Ranks for k
        ksi = new GraphStateIterator <Integer, Double> (ks);

        influenceVector2 = new HashMap<Integer, Double>();

        while (ksi.hasNext()) {
            Weighted<Integer, Double> node2 = ksi.next();
            influenceVector2.put(node2.object, (node2.weight));  // the influence vector is not normalized
        }


        sumOfOpinions = 0;
        for (int i=1; i<=34; i++){
            sumOfOpinions += influenceVector2.get(i);
        }

        meanOfOpinions = sumOfOpinions/34;

        System.out.println();
        System.out.println("Influence Vector after adding edges (26,1) & (11,34) to the initial graph");
        System.out.println(influenceVector2);
        System.out.println("Mean of opinions after adding edges (26,1) & (11,34) to the initial graph");
        System.out.println(meanOfOpinions);

        //Going back to the initial graph
        k.removeEdge(26,1);
        k.removeEdge(11,34);



    }
}
