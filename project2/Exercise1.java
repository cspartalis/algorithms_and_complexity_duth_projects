import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gr.james.influence.algorithms.iterators.GraphStateIterator;
import gr.james.influence.algorithms.scoring.PageRank;
import gr.james.influence.graph.DirectedGraph;
import gr.james.influence.util.collections.GraphState;
import gr.james.influence.util.collections.Weighted;

public class Exercise1 {


    public static void main(String[] args) throws IOException {
        // Exercise
        // Load the graph of the Social Network
        // The graph contains 34 integer vertices: 1 through 34
        final DirectedGraph<Integer, Object> g = SocialNetwork.loadGraph("network-valued.txt");

        System.out.println(g);

        // Exercise
        // Execute PageRank and store results in a GraphState object
        GraphState<Integer, Double> gs = PageRank.execute(g, 1.0);
        System.out.println("Page Rank");
        System.out.println(gs);

        // TEST
        // Normalize ranks
        // Calculate Sum of Ranks
        GraphStateIterator <Integer, Double> gsi = new GraphStateIterator <Integer, Double> (gs);

        double sumRank = sumRank = gs.getSum();
        System.out.println("sumRank :"+sumRank);;


        // Normalize Ranks to obtain the influence vector
        Map<Integer, Double> influenceVector = new HashMap<Integer, Double>();
        while (gsi.hasNext()) {
            Weighted<Integer, Double> node = gsi.next();
            influenceVector.put(node.object, (node.weight / sumRank));
        }


        //Check sumOfInfluence must be 1 after we normalization
        gsi = new GraphStateIterator <Integer, Double> (gs);
        double sumOfInfluence = 0;
        for (int v:g){
            sumOfInfluence += influenceVector.get(v);
        }


        // Solution
        List<Double> influence = new ArrayList<Double>(influenceVector.values());

        System.out.println();
        System.out.println("Influence:");
        System.out.println("Sum of Influence:" + sumOfInfluence);
        System.out.println("Max Influence:" + Collections.max(influence));
        System.out.println("Min Influence:" + Collections.min(influence));

        /*Get the node with max influence*/
        int nodeMaxInf = -1;
        int nodeMinInf = -1;
        for (int v:g){
            if (influenceVector.get(v)==Collections.max(influence))
                nodeMaxInf = v;
            else if (influenceVector.get(v)==Collections.min(influence))
                nodeMinInf = v;
        }

        System.out.println();
        System.out.println("Node with max influence: " + nodeMaxInf);
        System.out.println("Node with min influence: " + nodeMinInf);



        // TEST
        // sort the map
        // TODO

        //TEST
        Map<Integer, Double> sortedInfluenceVector = new LinkedHashMap<>();
        // TODO
        List valuesList = new ArrayList(influenceVector.values());
        Collections.sort(valuesList);

        for (int listIndx=0; listIndx<34; listIndx++) {
            for (int v : g) {
                if (influenceVector.get(v) == valuesList.get(listIndx))
                    sortedInfluenceVector.put(v, influenceVector.get(v));
            }
        }

        System.out.println();
        System.out.println("Sorted Influence Vector:");
        System.out.println(sortedInfluenceVector);

    }
}
