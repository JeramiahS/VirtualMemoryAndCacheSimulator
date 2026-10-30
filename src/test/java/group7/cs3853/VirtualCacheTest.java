package group7.cs3853;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VirtualCacheTest {
    final String[] simulatedArgs = new String[]{"-s", "512", "-b", "16", "-a", "4", "-r", "rr", "-p", "1024", "-n", "100", "-u", "75", "-f", "Trace1.trc"};
    VirtualCache cache = VirtualCache.buildFromArgs(simulatedArgs);

    @Test
    public void parsedCacheSizeCorrectly() {
        assertEquals(512, cache.sizeKB, "Cache size should be 512 KiB");
    }

    @Test
    public void parsedBlockSizeCorrectly() {
        assertEquals(16, cache.blockSizeBytes, "Cache block size should be 16 bytes per block");
    }

    @Test
    public void parsedAssociativityCorrectly() {
        assertEquals(4, cache.associativity, "Cache should be 4-way set associative");
    }

    @Test
    public void parsedReplacementPolicyCorrectly() {
        assertEquals("Round Robin", cache.replacementPolicy, "Replacement Policy should be Round Robin");
    }

    @Test
    public void parsedPhysicalMemorySizeCorrectly() {
        assertEquals(1024, cache.physicalMemMB, "Physical memory should be 1024 MiB");
    }

    @Test
    public void calculatesCorrectNumberOfBlocks() {
        assertEquals(32768, cache.getTotalNumberOfBlocks(), "A 512 KiB cache with a block size of 16 bytes should have 32,768 blocks");
    }
}
