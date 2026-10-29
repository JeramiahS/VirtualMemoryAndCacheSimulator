package group7.cs3853;

import java.util.Objects;

public class VirtualCache {
    public final int sizeKB, blockSizeBytes, associativity, physicalMemMB;
    public final String replacementPolicy;

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

        return new VirtualCache(sizeKB, blockSizeBytes, associativity, replacementPolicy, physicalMemMB);
    }

    // Sample cache calculated value
    public int getTotalNumberOfBlocks() {
        return (this.sizeKB * 1024) / this.blockSizeBytes;
    }
}
