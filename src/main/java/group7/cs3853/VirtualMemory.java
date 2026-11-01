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

        validateParameters(physicalMemoryMB, percentMemoryUsedByOS, timeSlice);

        return new VirtualMemory(physicalMemoryMB, percentMemoryUsedByOS, timeSlice);
    }

    private static void validateParameters(int physicalMemoryMB, int percentMemoryUsedByOS, int timeSlice) {
        if(physicalMemoryMB < 128 || physicalMemoryMB > 4096 || Integer.bitCount(physicalMemoryMB) != 1) {
            throw new IllegalArgumentException("Invalid physical memory size.");
        }

        if(percentMemoryUsedByOS < 0 || percentMemoryUsedByOS > 100) {
            throw new IllegalArgumentException("Invalid OS memory percentage.");
        }
        if(timeSlice != -1 && timeSlice < 1) {
            throw new IllegalArgumentException("Invalid instruction time slice.");
        }
    }

    //number of physical pages
    public long getNumberOfPhysicalPages() {
        return(physicalMemoryMB * 1024L * 1024L)/ 4096;
    }
    //number of pages by the OS
    public long getNumberOfSystemPages() {
        return Math.round(getNumberOfPhysicalPages() * percentMemoryUsedByOS / 100.00);
    }
    //page table entry size in bits
    public long getPageTableEntryBits() {
        return 1 + Long.numberOfTrailingZeros(getNumberOfPhysicalPages());
    }

    /*public long getPageTableMemoryBytes() {
        return (524288L * numOfProcesses * getPageTableEntryBits() + 7) / 8;
    }*/

}
