package group7.cs3853;

public class Main {

    static void main(String[] args) {
        if (args.length < 16 || args.length > 21) {
            System.err.println("Error: Invalid amount of arguments");
            printUsageInstructions();
            System.exit(1);
        }

        // Build simulation objects from command line args
        TraceFileContainer traceFiles = TraceFileContainer.getTraceFilesFromArgs(args);
        VirtualCache virtualCache = VirtualCache.buildFromArgs(args);
        VirtualMemory virtualMemory = VirtualMemory.buildFromArgs(args);

        System.out.print("Cache Simulator - CS 3853 - Team #07\n\n");
        traceFiles.printTraceFiles();
        printInputParameters(virtualCache, virtualMemory);
        virtualCache.printCalculatedValues();
        virtualMemory.printCalculatedValues(traceFiles.fileNames.size());
    }

    public static void printUsageInstructions() {
        System.out.print("Usage: java -jar CacheSim.jar <-s> <-b> <-a> <-r> <-p> <-u> <-n> <-f> [-f] [-f]\n\n");
        System.out.print("Arguments:\n");
        System.out.print("\ts: Cache size in kibibytes\n");
        System.out.print("\tb: Cache block size in bytes\n");
        System.out.print("\ta: Cache associativity value\n");
        System.out.print("\tr: Cache replacement policy\n");
        System.out.print("\tp: Simulated physical memory of the computer in mebibytes\n");
        System.out.print("\tu: Simulated percentage of physical memory used by the operating system\n");
        System.out.print("\tn: Instructions/Time Slice value\n");
        System.out.print("\tf: The name of a trace file (Minimum 1, max 3 files)\n\n");
    }

    public static void printInputParameters(VirtualCache cache, VirtualMemory virtualMemory) {
        System.out.print("*** Cache Input parameters ***\n\n");
        System.out.printf("Cache Size:                    %d KB\n", cache.sizeKB);
        System.out.printf("Block Size:                    %d bytes\n", cache.blockSizeBytes);
        System.out.printf("Associativity:                 %d\n", cache.associativity);
        System.out.printf("Replacement Policy:            %s\n", cache.replacementPolicy);
        System.out.printf("Physical Memory:               %d MB\n", cache.physicalMemMB);
        System.out.printf("Percent Memory Used by System: %d.0%%\n", virtualMemory.percentMemoryUsedByOS);
        System.out.printf("Instructions / Time Slice:     %d\n\n", virtualMemory.timeSlice);
    }

}
