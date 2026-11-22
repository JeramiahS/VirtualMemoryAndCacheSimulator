package group7.cs3853;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class CacheRow {
    public ArrayList<Long> blocks;
    private final int associativity;
    private final String replacementPolicy;
    private int index;

    private final ArrayDeque<Integer> queue;

    public CacheRow (final int associativity, final String replacementPolicy) {
        this.associativity = associativity;
        this.replacementPolicy = replacementPolicy;
        this.blocks = new ArrayList<>(associativity);
        this.queue = new ArrayDeque<>(associativity);
        this.index = 0;
    }

    public boolean tagCanBeInsertedIntoBlock () {
        return this.index != associativity;
    }

    public void insertTagBitsIntoBlock (long tagBits) {
        blocks.add(this.index, tagBits);
        queue.offer(this.index);
        this.index += 1;
    }

    /// Evicts a blocks chosen by the replacement policy and replaces the existing tag with the provided tag
    public void evictAndReplaceBlock (long tagBits) {
        if (replacementPolicy.equals("Random")) {
            final int indexToReplace = ThreadLocalRandom.current().nextInt(this.associativity);
            blocks.add(indexToReplace, tagBits);
        } else {
            Objects.requireNonNull(queue.peek());
            final int indexToReplace = queue.poll();
            blocks.add(indexToReplace, tagBits);
            queue.offer(indexToReplace);
        }
    }
}
