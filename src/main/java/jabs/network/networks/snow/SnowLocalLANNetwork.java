package jabs.network.networks.snow;

import jabs.consensus.config.ConsensusAlgorithmConfig;
import jabs.network.networks.Network;
import jabs.network.node.nodes.Node;
import jabs.network.node.nodes.snow.SnowNode;
import jabs.network.stats.lan.LAN100MNetworkStats;
import jabs.network.stats.lan.SingleNodeType;
import jabs.simulator.Simulator;
import jabs.simulator.randengine.RandomnessEngine;

import java.util.*;

public class SnowLocalLANNetwork extends Network<SnowNode, SingleNodeType> {
    public SnowLocalLANNetwork(RandomnessEngine randomnessEngine) {
        super(randomnessEngine, new LAN100MNetworkStats(randomnessEngine));
    }

    public SnowNode createNewSnowNode(Simulator simulator, int nodeID, int numAllParticipants) {
        return new SnowNode(simulator, this, nodeID,
                this.sampleDownloadBandwidth(SingleNodeType.LAN_NODE),
                this.sampleUploadBandwidth(SingleNodeType.LAN_NODE),
                numAllParticipants);
    }

    @Override
    public void populateNetwork(Simulator simulator, ConsensusAlgorithmConfig snowConsensusConfig) {
        populateNetwork(simulator, 40, snowConsensusConfig);
    }

    @Override
    public void populateNetwork(Simulator simulator, int numNodes, ConsensusAlgorithmConfig snowConsensusConfig) {
        for (int i = 0; i < numNodes; i++) {
            this.addNode(createNewSnowNode(simulator, i, numNodes), SingleNodeType.LAN_NODE);
        }

        generatePowerLawNeighbors(2.5, 20, randomnessEngine); // to create a connected network
        for (Node node:this.getAllNodes()) {
            node.getP2pConnections().connectToNetwork(this);
        }
    }

    @Override
    public void generatePowerLawNeighbors(double alpha, int averageDegree, RandomnessEngine randomnessEngine) {
        System.out.println("Building a connected network ...");
        for (SnowNode node : this.getAllNodes()) {
            List<SnowNode> nodeNeighbors = new ArrayList<>();
            int degree = calculateDegree(alpha, averageDegree, randomnessEngine);
            if (degree >= this.getAllNodes().size()){ // if fails to obtain a proper degree
                degree = randomnessEngine.nextInt(1, this.getAllNodes().size());
            }
            while (nodeNeighbors.size() < degree) {
                SnowNode randomNeighbor = getRandomNeighbor(randomnessEngine);
                if (!nodeNeighbors.contains(randomNeighbor) && randomNeighbor != node) {
                    nodeNeighbors.add(randomNeighbor);
                }
            }
            neighbors.put(node, nodeNeighbors);
        }
        connectGraph();
    }

    protected SnowNode getRandomNeighbor(RandomnessEngine random) {
        int index = random.nextInt(nodes.size());
        return nodes.get(index);
    }
    protected int calculateDegree(double alpha, int averageDegree, RandomnessEngine random) {
        double u = random.nextDouble();
        return (int) Math.round(Math.pow(1 - u, -1 / (alpha - 1)) * averageDegree);
    }

    protected void connectGraph() { //ensures that the graph is connected by performing a breadth-first search (BFS) starting from a randomly chosen node.
        Set<SnowNode> visited = new HashSet<>();
        Queue<SnowNode> queue = new LinkedList<>();

        SnowNode startNode = this.getAllNodes().get(0); // starting point

        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            SnowNode currentNode = queue.poll();
            List<SnowNode> currentNeighbors = neighbors.get(currentNode);

            for (SnowNode neighbor : currentNeighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // Check if any nodes are not visited (disconnected)
        if (visited.size() < this.getAllNodes().size()) {
            List<SnowNode> unvisitedNodes = new ArrayList<>(this.getAllNodes());
            unvisitedNodes.removeAll(visited);

            for (SnowNode unvisitedNode : unvisitedNodes) {
                SnowNode randomNeighbor = getRandomNeighbor(randomnessEngine);
                neighbors.get(unvisitedNode).add(randomNeighbor);
                neighbors.get(randomNeighbor).add(unvisitedNode);
            }
        }
    }
    /**
     * @param node A Snow node to add to the network
     */
    @Override
    public void addNode(SnowNode node) {
        //this.addNode(node, SingleNodeType.LAN_NODE);
    }
}
