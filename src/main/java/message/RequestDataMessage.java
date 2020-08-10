package main.java.message;

import main.java.data.Hash;

import static main.java.network.BlockFactory.GET_DATA_OVERHEAD;

public class RequestDataMessage extends Message {
    private final Hash payloadHash;

    public RequestDataMessage(Hash hash) {
        super(hash.getSize() + GET_DATA_OVERHEAD);
        this.payloadHash = hash;
    }

    public Hash getHash() {
        return this.payloadHash;
    }
}
