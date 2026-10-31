package group7.cs3853;

import java.util.Objects;

public class VirtualCache {
    public final int sizeKB, blockSizeBytes, associativity, physicalMemMB;
    public final String replacementPolicy;
    private static final double cost_per_kb = 0.07;

    // Private constructor to prevent object creation from outside the class
    private VirtualCache(int sizeKB, int blockSizeBytes, int associativity, String replacementPolicy, int physicalMemMB) {
        this.sizeKB = sizeKB;
        this.blockSizeBytes = blockSizeBytes;
        this.associativity = associativity;
        this.replacementPolicy = replacementPolicy;
        this.physicalMemMB = physicalMemMB;
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
                    if (args[++i].equalsIgnoreCase("rr")) {
                        replacementPolicy = "Round Robin";
                    }
                    else if (args[++i].equalsIgnoreCase("rnd")) {
                        replacementPolicy = "Random";
                    }
                    else {
                        throw new IllegalArgumentException("Invalid replacement policy: " + args[++i]);
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
            throw new IllegalArgumentException("Invalid block size.");
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
        return 20 + Integer.numberOfTrailingZeros(physicalMemMB);
    }

    // Tag bits
    public int getTagBits() {
        return getPhysicalAddressBits()
                - getIndexBits()
                - getOffsetBits();
    }

    // Overhead size
    public long getOverheadSizeBytes() {

        long bitsPerBlock = getTagBits() + 1L;

        long totalBits = getTotalNumberOfBlocks() * bitsPerBlock;

        // Round up to the nearest whole byte
        return (totalBits + 7) / 8;
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
}
