package group7.cs3853;

import java.util.Objects;

public class CacheParameters {
    public final int sizeKB, blockSizeBytes, associativity, physicalMemMB;
    public final String replacementPolicy;

    // Private constructor to prevent object creation from outside the class
    private CacheParameters(int sizeKB, int blockSizeBytes, int associativity, String replacementPolicy, int physicalMemMB) {
        this.sizeKB = sizeKB;
        this.blockSizeBytes = blockSizeBytes;
        this.associativity = associativity;
        this.replacementPolicy = replacementPolicy;
        this.physicalMemMB = physicalMemMB;
    }

    // Ensure a Cache object can only be created from cmd line args
    public static CacheParameters buildFromArgs(String[] args) {
        return parse(args);
    }

    // Parses the command line args and returns a Cache object built from the parsed args
    private static CacheParameters parse(String[] args) {
        // Assert that args is not null
        Objects.requireNonNull(args);

        int sizeKB = 0, blockSizeBytes = 0, associativity=  0, physicalMemMB = 0;
        String replacementPolicy = null;

        for (int i = 0; i < args.length; i++) {
            switch(args[i]) {
                case "-s":
                    sizeKB = Integer.parseInt(args[++i]);
                    break;
                case "-b":
                    blockSizeBytes = Integer.parseInt(args[++i]);
                    break;
                case "-a":
                    associativity = Integer.parseInt(args[++i]);
                    break;
                case "r":
                    if (args[++i].equals("rr")) replacementPolicy = "Round Robin";
                    else if (args[++i].equals("rnd")) replacementPolicy = "Random";
                    else throw new IllegalArgumentException("Invalid replacement policy: " + args[++i]);
                    break;
                case "-p":
                    physicalMemMB = Integer.parseInt(args[++i]);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid argument: " + args[i]);
            }
        }

        return new CacheParameters(sizeKB, blockSizeBytes, associativity, replacementPolicy, physicalMemMB);
    }
}
