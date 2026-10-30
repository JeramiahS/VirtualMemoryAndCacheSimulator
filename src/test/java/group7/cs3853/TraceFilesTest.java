package group7.cs3853;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TraceFilesTest {
    private final String[] sampleArgs = new String[]{"-f", "File1", "-f", "File2", "-f", "File3"};
    private final TraceFileContainer traceFileContainer = TraceFileContainer.getTraceFilesFromArgs(sampleArgs);

    @Test
    public void parsesCorrectNumberOfFiles() {
        assertEquals(3, traceFileContainer.fileNames.size(), "Provided 3 filenames, expecting 3.");
    }
}
