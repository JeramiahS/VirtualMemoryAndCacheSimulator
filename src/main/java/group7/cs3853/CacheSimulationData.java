package group7.cs3853;

import java.util.ArrayList;
import java.util.HashMap;

public class CacheSimulationData implements Runnable {
    private final ArrayList<MemoryAccess> memoryAccesses;
    private final String replacementPolicy;
    private final HashMap<Long, CacheRow> cache;
    private final int cacheAssociativity;
    private final long numberOfIndexBits;
    private final long numberOfOffsetBits;

    public int totalCacheAccesses;
    public int instructionBytesRead;
    public int srcDstBytesRead;
    public int numberOfCacheHits;
    public int numberOfConflictMisses;
    public int numberOfCompulsoryMisses;

    public CacheSimulationData (ArrayList<MemoryAccess> memoryAccesses, VirtualCache cache) {
        this.memoryAccesses = memoryAccesses;
        this.replacementPolicy = cache.replacementPolicy;
        this.cache = new HashMap<>(Math.toIntExact(cache.getTotalNumberOfRows()));
        this.cacheAssociativity = cache.associativity;
        this.numberOfIndexBits = cache.getIndexBits();
        this.numberOfOffsetBits = cache.getOffsetBits();

        this.totalCacheAccesses = 0;
        this.instructionBytesRead = 0;
        this.srcDstBytesRead = 0;
        this.numberOfCacheHits = 0;
        this.numberOfConflictMisses = 0;
        this.numberOfCompulsoryMisses = 0;
    }

    @Override
    public void run() {
        for (MemoryAccess memoryAccess : memoryAccesses) {
            final long startingVirtualAddress = memoryAccess.virtualAddress();
            final long endVirtualAddress = startingVirtualAddress + (memoryAccess.bytesRead() - 1L);
            final long firstBlockNumber = startingVirtualAddress >>> numberOfOffsetBits;
            final long lastBlockNumber = endVirtualAddress >>> numberOfOffsetBits;

            // Check the origin of the virtual address and sum up the corresponding number of bytes read
            if (memoryAccess.origin().equals("EIP")) this.instructionBytesRead += memoryAccess.bytesRead();
            else this.srcDstBytesRead += memoryAccess.bytesRead();

            // Access each cache block touched by this memory access
            for (long blockNumber = firstBlockNumber; blockNumber <= lastBlockNumber; blockNumber++) {
                final long address = blockNumber << numberOfOffsetBits;
                this.totalCacheAccesses += 1;

                // Extract the index and tag bits from the address
                final long indexBits = extractIndexBitsFromVirtualAddress(address);
                final long tagBits = extractTagBitsFromVirtualAddress(address);

                // Check the Cache for the index
                if (cache.containsKey(indexBits)) {
                    // Check if the blocks at the associated index contains the tag
                    if (cache.get(indexBits).blocks.contains(tagBits)) {
                        this.numberOfCacheHits += 1;
                    } else {
                        // A conflict miss has occurred, insert the tag bits into a block in the cache row
                        this.numberOfConflictMisses += 1;

                        if (cache.get(indexBits).tagCanBeInsertedIntoBlock()) {
                            cache.get(indexBits).insertTagBitsIntoBlock(tagBits);
                        } else {
                            cache.get(indexBits).evictAndReplaceBlock(tagBits);
                        }
                    }
                } else {
                    // A compulsory miss has occurred; there is no data in the cache at the associated index
                    this.numberOfCompulsoryMisses += 1;

                    // Store the new index into the cache and instantiate the cache row associated at the index
                    cache.put(indexBits, new CacheRow(this.cacheAssociativity, this.replacementPolicy));

                    // Insert the tag bits into the cache row at the appropriate blocks
                    if (cache.get(indexBits).tagCanBeInsertedIntoBlock()) {
                        cache.get(indexBits).insertTagBitsIntoBlock(tagBits);
                    } else {
                        throw new ArrayStoreException("Cannot insert tag: " + Long.toHexString(tagBits) + " to new index: " + Long.toHexString(indexBits));
                    }
                }
            }
        }
    }

    private long extractIndexBitsFromVirtualAddress (long virtualAddress) {
        final long indexMask = (1L << numberOfIndexBits) - 1L;
        return (virtualAddress >>> numberOfOffsetBits) & indexMask;
    }

    private long extractTagBitsFromVirtualAddress (long virtualAddress) {
        return virtualAddress >>> (numberOfOffsetBits + numberOfIndexBits);
    }

    public int getTotalCacheMisses () {
        return this.numberOfCompulsoryMisses + this.numberOfConflictMisses;
    }

    public int getCacheBlocksUsed() {
        return cache.size();
    }

}
