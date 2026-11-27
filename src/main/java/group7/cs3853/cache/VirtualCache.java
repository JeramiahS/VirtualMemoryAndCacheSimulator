package group7.cs3853.cache;

import group7.cs3853.tracefile.MemoryAccess;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class VirtualCache {
    public final int sizeKB, blockSizeBytes, associativity, physicalMemMB;
    public final String replacementPolicy;
    private static final double cost_per_kb = 0.07;

    private final ConcurrentHashMap<Long, CacheRow> cpuCache;
    protected int totalCacheAccesses;
    protected int totalInstructionBytesRead;
    protected int totalSrcDstBytesRead;
    protected int totalCacheHits;
    protected int totalCacheMisses;
    protected int totalCompulsoryMisses;
    protected int totalConflictMisses;
    protected int totalCacheBlocksUsed;

    // Private constructor to prevent object creation from outside the class
    private VirtualCache(int sizeKB, int blockSizeBytes, int associativity, String replacementPolicy, int physicalMemMB) {
        this.sizeKB = sizeKB;
        this.blockSizeBytes = blockSizeBytes;
        this.associativity = associativity;
        this.replacementPolicy = replacementPolicy;
        this.physicalMemMB = physicalMemMB;
        this.cpuCache = new ConcurrentHashMap<>();

        this.totalCacheAccesses = (0);
        this.totalInstructionBytesRead = (0);
        this.totalSrcDstBytesRead = (0);
        this.totalCacheHits = (0);
        this.totalCacheMisses = 0;
        this.totalCompulsoryMisses = (0);
        this.totalConflictMisses = (0);
        this.totalCacheBlocksUsed = 0;
    }

    // Ensure a Cache object can only be created from cmd line args
    public static VirtualCache buildFromArgs(String[] args) {
        return parse(args);
    }

    // Parses the command line args and returns a Cache object built from the parsed args
    protected static VirtualCache parse(String[] args) {
        // Assert that args is not null
        Objects.requireNonNull(args);

        int sizeKB = 0, blockSizeBytes = 0, associativity=  0, physicalMemMB = 0;
        String replacementPolicy = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-s" -> sizeKB = Integer.parseInt(args[++i]);
                case "-b" -> blockSizeBytes = Integer.parseInt(args[++i]);
                case "-a" -> associativity = Integer.parseInt(args[++i]);
                case "-r" -> {
                    final String policy = args[++i];
                    if (policy.equalsIgnoreCase("rr")) {
                        replacementPolicy = "Round Robin";
                    } else if (policy.equalsIgnoreCase("rnd")) {
                        replacementPolicy = "Random";
                    } else {
                        throw new IllegalArgumentException("Invalid replacement policy: " + policy);
                    }
                }
                case "-p" -> physicalMemMB = Integer.parseInt(args[++i]);
                default -> {
                    // Do nothing
                }
            }
        }
        validateParameters(sizeKB, blockSizeBytes, associativity, physicalMemMB);

        return new VirtualCache(sizeKB, blockSizeBytes, associativity, replacementPolicy, physicalMemMB);
    }

    private static void validateParameters(
            int sizeKB, int blockSizeBytes,
            int associativity, int physicalMemMB) {

        if (sizeKB < 8 || sizeKB > 8192 ||
                Integer.bitCount(sizeKB) != 1) {
            throw new IllegalArgumentException("Invalid cache size.");
        }

        if (blockSizeBytes != 8 && blockSizeBytes != 16 &&
                blockSizeBytes != 32 && blockSizeBytes != 64) {
            throw new IllegalArgumentException("Invalid blocks size.");
        }

        if (associativity != 1 && associativity != 2 &&
                associativity != 4 && associativity != 8 &&
                associativity != 16) {
            throw new IllegalArgumentException("Invalid associativity.");
        }

        if (physicalMemMB < 128 || physicalMemMB > 4096 ||
                Integer.bitCount(physicalMemMB) != 1) {
            throw new IllegalArgumentException("Invalid physical memory size.");
        }

        if (replacementPolicyMissing()) {
            throw new IllegalArgumentException("Missing replacement policy.");
        }

        long totalBlocks = (sizeKB * 1024L) / blockSizeBytes;

        if (totalBlocks % associativity != 0) {
            throw new IllegalArgumentException(
                    "Associativity must divide evenly into the number of blocks."
            );
        }
    }

    // Cache Calculated Values

    // cache blocks
    public long getTotalNumberOfBlocks() {
        return (sizeKB * 1024L) / blockSizeBytes;
    }

    // cache rows
    public long getTotalNumberOfRows() {
        return getTotalNumberOfBlocks() / associativity;
    }

    // Block offset bits
    public int getOffsetBits() {
        return Integer.numberOfTrailingZeros(blockSizeBytes);
    }

    // Index bits
    public int getIndexBits() {
        return Long.numberOfTrailingZeros(getTotalNumberOfRows());
    }

    // Physical address bits
    public int getPhysicalAddressBits() {
        final long physicalMemorySizeBytes = physicalMemMB * 1024L * 1024L;
        return Long.numberOfTrailingZeros(physicalMemorySizeBytes);
    }

    // Tag bits
    public int getTagBits() {
        return getPhysicalAddressBits() - getIndexBits() - getOffsetBits();
    }

    // Overhead size
    public long getOverheadSizeBytes() {
        long bitsPerBlock = getTagBits() + 1L;
        long totalBits = getTotalNumberOfBlocks() * bitsPerBlock;
        return (totalBits + 7) / 8; // Round up to the nearest whole byte using integer division rounding
    }

    // implementation memory
    public long getImplementationMemoryBytes() {

        long cacheBytes = sizeKB * 1024L;

        return cacheBytes + getOverheadSizeBytes();
    }

    // Implementation memory in KB
    public double getImplementationMemoryKB() {
        return getImplementationMemoryBytes() / 1024.0;
    }

    // Cache implementation cost
    public double getImplementationCost() {
        return getImplementationMemoryKB() * cost_per_kb;
    }

    private static boolean replacementPolicyMissing() {
        return false;
    }

    // CPU Cache methods
    public void addToInstructionBytesRead (int bytesRead) {
        this.totalInstructionBytesRead += bytesRead;
    }

    public void addToSrcDstBytesRead (int bytesRead) {
        this.totalSrcDstBytesRead += bytesRead;
    }

    public void incrementTotalCacheAccesses () {
        this.totalCacheAccesses += 1;
    }

    public boolean isValidBitSetAtIndex (long indexBits) {
        return cpuCache.containsKey(indexBits);
    }

    public boolean rowIsFull (long indexBits) {
        return cpuCache.get(indexBits).blocksAreFull();
    }

    public void incrementTotalCacheHits () {
        this.totalCacheHits += 1;
    }

    public boolean isCacheHit (long indexBits, long tagBits) {
        return cpuCache.get(indexBits).blocks.contains(tagBits);
    }

    public void handleCompulsoryMiss (long indexBits, long tagBits) {
        this.totalCompulsoryMisses += 1;
        final CacheRow row = cpuCache.computeIfAbsent(
                indexBits,
                k -> new CacheRow(this.associativity, this.replacementPolicy)
        );
        if (row.blocksAreFull()) {
            row.evictAndReplaceCacheBlock(tagBits);
        } else {
            row.insertIntoFreeBlock(tagBits);
        }
    }

    public void handleConflictMiss (long indexBits, long tagBits) {
        this.totalConflictMisses += 1;
        cpuCache.get(indexBits).evictAndReplaceCacheBlock(tagBits);
    }

    public void executeSimulation (ArrayList<MemoryAccess> memoryAccesses) {
        executeSimulation(memoryAccesses, 0);
    }

    public void executeSimulation (ArrayList<MemoryAccess> memoryAccesses, int processId) {

        for (MemoryAccess memoryAccess : memoryAccesses) {
            // Determine the start and end virtual addresses based on number of bytes read at the start address
            final long startingVirtualAddress = memoryAccess.virtualAddress();
            final long endVirtualAddress = startingVirtualAddress + (memoryAccess.bytesRead() - 1L);

            // Determine the cache indexes accessed based on the start and end addresses
            final long firstBlockNumber = startingVirtualAddress >>> this.getOffsetBits();
            final long lastBlockNumber = endVirtualAddress >>> this.getOffsetBits();

            // Store the appropriate amount of bytes read based on memory address origin
            if (memoryAccess.origin().equals("EIP")) addToInstructionBytesRead(memoryAccess.bytesRead());
            else addToSrcDstBytesRead(memoryAccess.bytesRead());

            // Check the cache for each index accessed
            for (long blockNumber = firstBlockNumber; blockNumber <= lastBlockNumber; blockNumber++) {
                incrementTotalCacheAccesses();

                // Extract the memory address and the corresponding index and tag bits
                final long address = blockNumber << this.getOffsetBits();
                final long indexBits = extractIndexBitsFromVirtualAddress(address);
                final long tagBits = extractTagBitsFromVirtualAddress(address) | (Integer.toUnsignedLong(processId) << 32);

                // Check the cache
                if (isValidBitSetAtIndex(indexBits)) {
                    if (isCacheHit(indexBits, tagBits)) {
                        incrementTotalCacheHits();
                    } else if (!rowIsFull(indexBits)) {
                        handleCompulsoryMiss(indexBits, tagBits);
                    } else {
                        handleConflictMiss(indexBits, tagBits);
                    }
                } else {
                    // The index is invalid, handle the compulsory miss
                    handleCompulsoryMiss(indexBits, tagBits);
                }
            }
        }
    }

    private long extractIndexBitsFromVirtualAddress (long virtualAddress) {
        final long indexMask = (1L << this.getIndexBits()) - 1L;
        return (virtualAddress >>> this.getOffsetBits()) & indexMask;
    }

    private long extractTagBitsFromVirtualAddress (long virtualAddress) {
        return virtualAddress >>> (this.getOffsetBits() + this.getIndexBits());
    }

    public void printCalculatedValues () {
        System.out.print("***** Cache Calculated Values *****\n\n");
        System.out.printf("Total # Blocks:                %d\n", getTotalNumberOfBlocks());
        System.out.printf("Tag Size:                      %d bits\n", getTagBits());
        System.out.printf("Index Size:                    %d bits\n", getIndexBits());
        System.out.printf("Total # Rows:                  %d\n", getTotalNumberOfRows());
        System.out.printf("Overhead Size:                 %d bytes\n", getOverheadSizeBytes());
        System.out.printf("Implementation Memory Size:    %.2f KB (%d bytes)\n", getImplementationMemoryKB(), getImplementationMemoryBytes());
        System.out.printf("Cost:                          $%.2f @ $%.2f per KB\n\n", getImplementationCost(), cost_per_kb);
    }

    public void printSimulationResults (int virtualPagesMapped) {
        System.out.print("***** CACHE SIMULATION RESULTS *****\n\n");
        System.out.printf("Total Cache Accesses:  %d (%d addresses)\n", this.totalCacheAccesses, virtualPagesMapped);
        System.out.printf("--- Instruction Bytes: %d\n", this.totalInstructionBytesRead);
        System.out.printf("--- SrcDst Bytes:      %d\n", this.totalSrcDstBytesRead);
        System.out.printf("Cache Hits:            %d\n", this.totalCacheHits);
        System.out.printf("Cache Misses:          %d\n", this.totalCompulsoryMisses + this.totalConflictMisses);
        System.out.printf("--- Compulsory Misses: %d\n", this.totalCompulsoryMisses);
        System.out.printf("--- Conflict Misses:   %d\n\n", this.totalConflictMisses);
        System.out.print("***** ***** CACHE HIT & MISS RATE: ***** *****\n\n");
        final float cacheHitRate = (this.totalCacheHits * 100f) / this.totalCacheAccesses;
        System.out.printf("Hit  Rate:           %.4f%%\n", cacheHitRate);
        System.out.printf("Miss Rate:           %.4f%%\n", 100f - cacheHitRate);
        // TODO: Finish this print statement
        System.out.printf("CPI:                 %d PLACEHOLDER\n", 0);
        // TODO: Finish this print statement
        System.out.printf("Unused Cache Space:  %.2f / %.2f = %.2f%% Waste: $%.2f/chip PLACEHOLDER\n", 0f, 0f, 0f, 0f);
        System.out.printf("Unused Cache Blocks: %d / %d\n\n", this.totalCacheBlocksUsed, this.getTotalNumberOfBlocks());
    }
}
