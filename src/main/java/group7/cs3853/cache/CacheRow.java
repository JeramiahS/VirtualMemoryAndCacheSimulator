package group7.cs3853.cache;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class CacheRow {
    public ArrayList<Long> blocks;
    private final int associativity;
    private final String replacementPolicy;

    private final ArrayDeque<Integer> queue;

    public CacheRow (final int associativity, final String replacementPolicy) {
        this.associativity = associativity;
        this.replacementPolicy = replacementPolicy;
        this.blocks = new ArrayList<>(associativity);
        this.queue = new ArrayDeque<>(associativity);
    }

    public void insertIntoFreeBlock (long tagBits) {
        this.blocks.add(tagBits);
        queue.offer(this.blocks.indexOf(tagBits));
    }

    public boolean blocksAreFull () {
        return this.blocks.size() == this.associativity;
    }

    public void evictAndReplaceCacheBlock (long tagBits) {
        if (replacementPolicy.equals("Random")) {
            final int indexToReplace = ThreadLocalRandom.current().nextInt(this.associativity);
            this.blocks.set(indexToReplace, tagBits);
        } else {
            Objects.requireNonNull(queue.peek());
            final int indexToReplace = queue.poll();
            this.blocks.set(indexToReplace, tagBits);
            queue.offer(indexToReplace);
        }
    }

}
