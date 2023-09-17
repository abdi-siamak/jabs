package jabs.network.p2p;

import jabs.network.networks.Network;
import jabs.network.node.nodes.Node;

/*
 * in the method: this.peerNeighbors.addAll()
 * use "network.getAllNodes()" for a fully-connected network.
 * use "network.getNeighbors(this.getNode())" for a connected network-generatePowerLawNeighbors() in LocalLANNetwork should be activated!
 */
public class SnowP2P extends AbstractP2PConnections{
    @Override
    public void connectToNetwork(Network network) {
        this.peerNeighbors.addAll(network.getNeighbors(this.getNode()));
        node.getNodeNetworkInterface().connectNetwork(network, network.getRandom());
    }

    @Override
    public boolean requestConnection(Node node) {
        return false;
    }
}
