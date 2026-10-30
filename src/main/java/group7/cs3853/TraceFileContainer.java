package group7.cs3853;

import java.util.ArrayList;
import java.util.Objects;

public class TraceFileContainer {
    private static final int MAX_TRACE_FILES = 3;
    public ArrayList<String> fileNames;

    // Private constructor to keep instantiation logic within the class
    private TraceFileContainer() {
        fileNames = new ArrayList<>(MAX_TRACE_FILES);
    }

    // Returns a TraceFileContainer object containing the trace files specified in cmd line args
    public static TraceFileContainer getTraceFilesFromArgs(String[] args) {
        Objects.requireNonNull(args);

        TraceFileContainer t = new TraceFileContainer();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-f")) {
                final String filename = args[++i];
                if (filename != null) {
                    t.fileNames.add(filename);
                } else {
                    throw new IllegalArgumentException("Expected a trace file name. Found none.");
                }
            }
        }

        return t;
    }

    public void printTraceFiles() {
        System.out.println("Trace File(s):");

        for (String name : fileNames) {
            System.out.printf("\t %s\n", name);
        }
    }
}
