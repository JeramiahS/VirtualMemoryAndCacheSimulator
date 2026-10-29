package group7.cs3853;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VirtualMemoryTest {
    final String[] simulatedArgs = new String[]{"-s", "512", "-b", "16", "-a", "4", "-r", "rr", "-p", "1024", "-n", "100", "-u", "75", "-f", "Trace1.trc"};
    final VirtualMemory virtualMemory = VirtualMemory.buildFromArgs(simulatedArgs);

    @Test
    public void parsedPhysicalMemoryCorrectly() {
        assertEquals(1024, virtualMemory.physicalMemoryMB);
    }

    @Test
    public void parsedPercentMemoryUsedByOSCorrectly() {
        assertEquals(75, virtualMemory.percentMemoryUsedByOS);
    }

    @Test
    public void parsedTimeSliceCorrectly() {
        assertEquals(100, virtualMemory.timeSlice);
    }

}
