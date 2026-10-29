package group7.cs3853;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class TraceFileContainer {
    private static final int MAX_TRACE_FILES = 3;
    public ArrayList<File> traceFiles;

    // Private constructor to keep instantiation logic within the class
    private TraceFileContainer() {
        traceFiles = new ArrayList<>(MAX_TRACE_FILES);
    }

    // Returns a TraceFileContainer object containing the trace files specified in cmd line args
    public static TraceFileContainer getTraceFilesFromArgs(String[] args) throws FileNotFoundException {
        Objects.requireNonNull(args);

        TraceFileContainer t = new TraceFileContainer();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-f")) {
                if (args[++i] != null) {
                    File traceFile = new File(args[++i]);
                    if (traceFile.exists()) t.traceFiles.add(traceFile);
                    else throw new FileNotFoundException("Specified file not found: " + args[++i]);
                } else {
                    throw new IllegalArgumentException("Expected a trace file name. Found none.");
                }
            }
        }

        return t;
    }

    public void printTraceFiles() {
        System.out.println("Trace File(s):");

        for (File traceFile : traceFiles) {
            System.out.printf("\t %s\n", traceFile.getName());
        }
    }
}
