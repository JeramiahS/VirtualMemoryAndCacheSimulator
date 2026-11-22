package group7.cs3853;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class TraceFile implements Runnable {
    private final Path filepath;

    private static final int DEFAULT_NUMBER_ADDRESSES_IN_FILE = 262_144;
    private static final int BASE_16 = 16;
    private static final int READ_WRITE_BYTES_READ = 4;

    public final ArrayList<MemoryAccess> memoryAccesses;

    public TraceFile(final String filename) {
        this.filepath = Path.of(filename);
        this.memoryAccesses = new ArrayList<>(DEFAULT_NUMBER_ADDRESSES_IN_FILE);
    }

    @Override
    public void run() {
        try (BufferedReader reader = Files.newBufferedReader(this.filepath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    if (lineIsInstructionFetchLine(line)) {
                        final long virtualAddress = extractVirtualAddressFromInstructionLine(line);
                        final int bytesRead = extractBytesReadFromInstructionLine(line);
                        memoryAccesses.add(new MemoryAccess("EIP", virtualAddress, bytesRead));
                    } else {
                        if (!dataInDestinationAddressIsInvalid(line)) {
                            final long virtualAddress = extractVirtualAddressFromDestination(line);
                            memoryAccesses.add(new MemoryAccess("dstM", virtualAddress, READ_WRITE_BYTES_READ));
                        }
                        if (!dataInSourceAddressIsInvalid(line)) {
                            final long virtualAddress = extractVirtualAddressFromSource(line);

                            memoryAccesses.add(new MemoryAccess("srcM", virtualAddress, READ_WRITE_BYTES_READ));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean lineIsInstructionFetchLine (String lineOfText) {
        return lineOfText.charAt(0) == 'E';
    }

    private long extractVirtualAddressFromInstructionLine (String lineOfText) {
        return Long.parseLong(lineOfText.substring(10, 18), BASE_16);
    }

    private int extractBytesReadFromInstructionLine (String lineOfText) {
        return Integer.parseInt(lineOfText.substring(5, 7));
    }

    private boolean dataInDestinationAddressIsInvalid (String lineOfText) {
        return lineOfText.charAt(15) == '-';
    }

    private long extractVirtualAddressFromDestination (String lineOfText) {
        return Long.parseLong(lineOfText.substring(6, 14), BASE_16);
    }

    private boolean dataInSourceAddressIsInvalid (String lineOfText) {
        return lineOfText.charAt(42) == '-';
    }

    private long extractVirtualAddressFromSource (String lineOfText) {
        return Long.parseLong(lineOfText.substring(33, 41), BASE_16);
    }

    public String getTraceFileName() {
        return this.filepath.getFileName().toString();
    }

}
