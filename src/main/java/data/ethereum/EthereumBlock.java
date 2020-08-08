package main.java.data.ethereum;

import main.java.data.Block;
import main.java.node.nodes.ethereum.EthereumMinerNode;

import java.util.Set;

import static main.java.network.BlockFactory.ETHEREUM_BLOCK_HASH_SIZE;

public class EthereumBlock extends Block<EthereumBlock> {
    private final Set<EthereumBlock> uncles;
    private final long difficulty;

    public EthereumBlock(int size, int height, long creationTime, EthereumMinerNode creator, EthereumBlock parent,
                         Set<EthereumBlock> uncles, long difficulty) {
        super(size, height, creationTime, creator, parent, ETHEREUM_BLOCK_HASH_SIZE);
        this.uncles = uncles;
        this.difficulty = difficulty;

        long unclesDifficultySum = 0;
        for (EthereumBlock uncle:uncles) {
            unclesDifficultySum += uncle.getDifficulty();
        }
    }

    public Set<EthereumBlock> getUncles() {
        return this.uncles;
    }

    public long getDifficulty() {
        return this.difficulty;
    }
}
