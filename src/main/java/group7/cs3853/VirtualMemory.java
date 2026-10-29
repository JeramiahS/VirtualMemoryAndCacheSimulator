package group7.cs3853;

import java.util.Objects;

public class VirtualMemory {
    public final int physicalMemoryMB, percentMemoryUsedByOS, timeSlice;

    // Private constructor to keep instantiation logic within the class
    private VirtualMemory(int physicalMemoryMB, int percentMemoryUsedByOS, int timeSlice) {
        this.physicalMemoryMB = physicalMemoryMB;
        this.percentMemoryUsedByOS = percentMemoryUsedByOS;
        this.timeSlice = timeSlice;
    }

    // Returns a VirtualMemory object parsed form cmd line args
    public static VirtualMemory buildFromArgs(String[] args) {
        return parseArgs(args);
    }

    // Parses cmd line args to build a VirtualMemory object
    private static VirtualMemory parseArgs(String[] args) {
        Objects.requireNonNull(args);

        int physicalMemoryMB = 0, percentMemoryUsedByOS = 0, timeSlice = 0;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-p" -> physicalMemoryMB = Integer.parseInt(args[++i]);
                case "-u" -> percentMemoryUsedByOS = Integer.parseInt(args[++i]);
                case "-n" -> timeSlice = Integer.parseInt(args[++i]);
                default -> {
                    // Do nothing
                }
            }
        }

        return new VirtualMemory(physicalMemoryMB, percentMemoryUsedByOS, timeSlice);
    }

}
