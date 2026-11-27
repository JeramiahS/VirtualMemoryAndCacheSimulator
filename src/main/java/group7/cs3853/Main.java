package group7.cs3853;

import group7.cs3853.cache.VirtualCache;
import group7.cs3853.tracefile.TraceFile;
import group7.cs3853.virtualmemory.PageTableData;
import group7.cs3853.virtualmemory.VirtualMemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static void main(String[] args) {
        if (args.length < 16 || args.length > 21) {
            System.err.println("Error: Invalid amount of arguments");
            printUsageInstructions();
            System.exit(1);
        }

        // Build simulation objects from command line args
        final VirtualCache virtualCache = VirtualCache.buildFromArgs(args);
        final VirtualMemory virtualMemory = VirtualMemory.buildFromArgs(args);

        // Create data structures to hold the data within the trace files
        final ArrayList<TraceFile> traceFiles = new ArrayList<>();
        getTraceFilesFromArgs(args, traceFiles);

        // Use virtual threads to load each trace file into memory simultaneously
        // This avoids having to read each trace file twice for each simulation
        try(ExecutorService traceFileReaderService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (TraceFile traceFile : traceFiles) {
                traceFileReaderService.submit(traceFile);
            }
            traceFileReaderService.shutdown();
        }

        System.out.print("Cache Simulator - CS 3853 - Team #07\n\n");
        printTraceFileNames(traceFiles);
        printInputParameters(virtualCache, virtualMemory);
        virtualCache.printCalculatedValues();
        virtualMemory.printCalculatedValues(traceFiles.size());

        // Create an ArrayList to hold the page table data for each trace file
        final ArrayList<PageTableData> pageTableDataArrayList = new ArrayList<>(traceFiles.size());
        for (TraceFile traceFile : traceFiles) {
            pageTableDataArrayList.add(
                    new PageTableData(
                            traceFile,
                            traceFiles.indexOf(traceFile),
                            virtualMemory.getNumberOfAvailablePages()
                    )
            );
        }

        // Create a subprocess for each trace file to perform the page table simulation
        try (ExecutorService pageTableSubprocessService = Executors.newFixedThreadPool(traceFiles.size())) {
            for (PageTableData data : pageTableDataArrayList) {
                pageTableSubprocessService.submit(data);
            }
            pageTableSubprocessService.shutdown();
        }

        // Store the page table simulation results
        for (PageTableData data : pageTableDataArrayList) {
            virtualMemory.storeSimulationResults(data);
        }

        for (int processId = 0; processId < traceFiles.size(); processId++) {
            virtualCache.executeSimulation(traceFiles.get(processId).memoryAccesses, processId);
        }

        // Print all simulation results
        virtualMemory.printSimulationTotals();
        for (PageTableData data : pageTableDataArrayList) {
            data.printResultsForTraceFile(virtualMemory.getPageTableEntryBits());
        }

        virtualCache.printSimulationResults(virtualMemory.totalVirtualPagesMapped);
    }

    public static void printUsageInstructions () {
        System.out.print("Usage: java -jar CacheSim.jar <-s> <-b> <-a> <-r> <-p> <-u> <-n> <-f> [-f] [-f]\n\n");
        System.out.print("Arguments:\n");
        System.out.print("\ts: Cache size in kibibytes\n");
        System.out.print("\tb: Cache blocks size in bytes\n");
        System.out.print("\ta: Cache associativity value\n");
        System.out.print("\tr: Cache replacement policy\n");
        System.out.print("\tp: Simulated physical memory of the computer in mebibytes\n");
        System.out.print("\tu: Simulated percentage of physical memory used by the operating system\n");
        System.out.print("\tn: Instructions/Time Slice value\n");
        System.out.print("\tf: The name of a trace file (Minimum 1, max 3 files)\n\n");
    }

    public static void getTraceFilesFromArgs (String[] args, ArrayList<TraceFile> traceFiles) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-f")) traceFiles.add(new TraceFile(args[i+1]));
        }
    }

    public static void printTraceFileNames (ArrayList<TraceFile> traceFiles) {
        System.out.println("Trace file(s):");
        for (TraceFile traceFile : traceFiles) System.out.print("\t" + traceFile.getTraceFileName() + "\n");
        System.out.print("\n");
    }

    public static void printInputParameters (VirtualCache cache, VirtualMemory virtualMemory) {
        System.out.print("***** Cache Input parameters *****\n\n");
        System.out.printf("Cache Size:                    %d KB\n", cache.sizeKB);
        System.out.printf("Block Size:                    %d bytes\n", cache.blockSizeBytes);
        System.out.printf("Associativity:                 %d\n", cache.associativity);
        System.out.printf("Replacement Policy:            %s\n", cache.replacementPolicy);
        System.out.printf("Physical Memory:               %d MB\n", cache.physicalMemMB);
        System.out.printf("Percent Memory Used by System: %d.0%%\n", virtualMemory.percentMemoryUsedByOS);
        System.out.printf("Instructions / Time Slice:     %d\n\n", virtualMemory.timeSlice);
    }

}
