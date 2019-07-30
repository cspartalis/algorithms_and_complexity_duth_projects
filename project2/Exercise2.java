import java.io.IOException;
import java.util.*;

import gr.james.influence.algorithms.iterators.GraphStateIterator;
import gr.james.influence.util.collections.Weighted;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import gr.james.influence.algorithms.scoring.PageRank;
import gr.james.influence.graph.DirectedGraph;
import gr.james.influence.util.collections.GraphState;

public class Exercise2 {

    public static void main(String[] args) throws IOException {

        // Exercise
        // Load the graph of the Social Network
        final DirectedGraph<Integer, Object> g = SocialNetwork.loadGraph("network-valued.txt");

        // Solution
        // Execute PageRank and store results in a GraphState object
        GraphState<Integer, Double> gs = PageRank.execute(g, 1);

        // Solution
        // Calculate Sum of Ranks
        GraphStateIterator<Integer, Double> gsi = new GraphStateIterator <Integer, Double> (gs);
        double sumRank = gs.getSum();

        // Solution
        // Normalize Ranks to obtain the influence vector
        Map<Integer, Double> influenceVector = new HashMap<Integer, Double>();

        while (gsi.hasNext()) {
            Weighted<Integer, Double> node = gsi.next();
            influenceVector.put(node.object, (node.weight / sumRank));
        }

        List<Double> influenceList = new ArrayList<Double>(influenceVector.values());
        Double[] influence = new Double[influenceList.size()];
        influenceList.toArray(influence);

        // Parameters for the experiments
        long seed = 123;
        double mean = 800;
        double deviation = 800;
        int rounds = 800000;

        // Influence vector from social network
        Crowd crowd = new Crowd(influence);
        crowd.initializePoll(seed, mean, deviation);
        DescriptiveStatistics stats = crowd.experiment(rounds);

        System.out.println();
        System.out.println("Stats");
        System.out.println(stats.getMean());
        System.out.println(stats.getStandardDeviation());

        int size = influenceVector.size();
        int sum = (size - 1) * size / 2;
        double[] gradientVector = new double[size];
        for (int i = 0; i < size; i++ ) {
            gradientVector[i] = ((double)i) / sum;
        }


//           // Influence vector 1, 2, 3, 4, ..., 34
//	        crowd = new Crowd(gradientVector);
//
//
//	        System.out.println();
//	        System.out.println("Stats (gradient)");
//	        System.out.println(stats.getMean());
//	        System.out.println(stats.getStandardDeviation());
//
//	        double[] equalVector = new double[influenceVector.size()];
//	        Arrays.fill(equalVector, 1.0/34);
//
//	        // Influence vector 1.0/34
//	        crowd = new Crowd(equalVector);
//	        //TODO
//
//	        System.out.println();
//	        System.out.println("Stats (equal)");
//	        System.out.println(stats.getMean());

        /*-------------------------------------------------------------*/
        DirectedGraph<Integer, Object> k = DirectedGraph.create(g);

        // Execute PageRank and store results in a GraphState object for k
        GraphState<Integer, Double> ks = PageRank.execute(k, 1);

        // Calculate Sum of Ranks for k
        GraphStateIterator<Integer, Double> ksi = new GraphStateIterator <Integer, Double> (ks);
        double sumRank2 = ks.getSum();

        Map<Integer, Double> influenceVector2 = new HashMap<Integer, Double>();

        while (ksi.hasNext()) {
            Weighted<Integer, Double> node2 = ksi.next();
            influenceVector2.put(node2.object, (node2.weight / sumRank2));
        }

        List<Double> influenceList2 = new ArrayList<Double>(influenceVector2.values());
        Double[] influence2 = new Double[influenceList2.size()];
        influenceList2.toArray(influence2);

        /*-----------------------------------------------------------------------*/

        /*Add edges => reduce influence of strong + boost influence of weak*/

//           for (int i=0; i<5; i++) {
//               // Execute PageRank and store results in a GraphState object for k
//               ks = PageRank.execute(k, 1);
//
//               // Calculate Sum of Ranks for k
//               ksi = new GraphStateIterator <Integer, Double> (ks);
//               sumRank2 = ks.getSum();
//
//               influenceVector2 = new HashMap<Integer, Double>();
//
//               while (ksi.hasNext()) {
//                   Weighted<Integer, Double> node2 = ksi.next();
//                   influenceVector2.put(node2.object, (node2.weight / sumRank2));
//               }
//
//               influenceList2 = new ArrayList<Double>(influenceVector2.values());
//               influence2 = new Double[influenceList2.size()];
//               influenceList2.toArray(influence2);
//
//               int nodeMax = -1;
//               int nodeMin = -1;
//               for (int v : k) {
//                   if (influenceVector2.get(v) == Collections.max(influenceList2))
//                       nodeMax = v;
//                   else if (influenceVector2.get(v) == Collections.min(influenceList2))
//                       nodeMin = v;
//               }
//
//               k.addEdge(nodeMax, nodeMin);
//               System.out.println(nodeMax+"----->"+nodeMin);
//
//           }


        /*remove edge => reduce influence of strong*/

        for (int i=0; i<5; i++) {
            // Execute PageRank and store results in a GraphState object for k
            ks = PageRank.execute(k, 1);

            // Calculate Sum of Ranks for k
            ksi = new GraphStateIterator<Integer, Double>(ks);
            sumRank2 = ks.getSum();

            influenceVector2 = new HashMap<Integer, Double>();

            while (ksi.hasNext()) {
                Weighted<Integer, Double> node2 = ksi.next();
                influenceVector2.put(node2.object, (node2.weight / sumRank2));
            }

            influenceList2 = new ArrayList<Double>(influenceVector2.values());
            influence2 = new Double[influenceList2.size()];
            influenceList2.toArray(influence2);

            int nodeMax = -1;
            int nodeMin = -1;
            for (int v : k) {
                if (influenceVector2.get(v) == Collections.max(influenceList2))
                    nodeMax = v;
            }

            List geitones = new ArrayList();
            for (int v:k.adjacentIn(nodeMax))
                if (v!=nodeMax)
                    geitones.add(v);

            Object geitonasMin = Collections.min(geitones);
            k.removeEdge((int)geitonasMin,nodeMax);
            System.out.println();
            System.out.println("Interfernce no:"+(i+1));
            System.out.println("Node with max influence: "+nodeMax);
            System.out.println("Neighbours of node "+nodeMax);
            System.out.println(geitones);
            System.out.println("Neighbour with min influence: "+geitonasMin);
            System.out.println("Remove this edge:");
            System.out.println(geitonasMin+"----->"+nodeMax);

        }

        System.out.println();
        System.out.println("Influence vector before interferance");
        System.out.println(influenceVector);
        System.out.println();
        System.out.println("Influence vector after interferance");
        System.out.println(influenceVector2);

        // Influence vector from social network
        Crowd crowd2 = new Crowd(influence2);
        crowd2.initializePoll(seed, mean, deviation);
        DescriptiveStatistics stats2 = crowd2.experiment(rounds);

        System.out.println();
        System.out.println("Stats for our graph");
        System.out.println(stats2.getMean());
        System.out.println(stats2.getStandardDeviation());

    }
}
