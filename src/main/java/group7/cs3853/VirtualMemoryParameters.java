package group7.cs3853;

import java.util.Objects;

public class VirtualMemoryParameters {
    public final int physicalMemoryMB, percentMemoryUsedByOS, timeSlice;

    private VirtualMemoryParameters(int physicalMemoryMB, int percentMemoryUsedByOS, int timeSlice) {
        this.physicalMemoryMB = physicalMemoryMB;
        this.percentMemoryUsedByOS = percentMemoryUsedByOS;
        this.timeSlice = timeSlice;
    }

    public static VirtualMemoryParameters buildFromArgs(String[] args) {
        return parseArgs(args);
    }

    private static VirtualMemoryParameters parseArgs(String[] args) {
        Objects.requireNonNull(args);

        int physicalMemoryMB = 0;
        int percentMemoryUsedByOS = 0;
        int timeSlice = 0;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-p":
                    physicalMemoryMB = Integer.parseInt(args[++i]);
                    break;
                case "-u":
                    percentMemoryUsedByOS = Integer.parseInt(args[++i]);
                    break;
                case "-n":
                    timeSlice = Integer.parseInt(args[++i]);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid argument: " + args[i]);
            }
        }

        return new VirtualMemoryParameters(physicalMemoryMB, percentMemoryUsedByOS, timeSlice);
    }

}
