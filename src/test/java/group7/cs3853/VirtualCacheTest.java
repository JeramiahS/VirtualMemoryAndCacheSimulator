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

    @Test
    public void calculatesCorrectTagBits() {
        assertEquals(13, cache.getTagBits(), "Tag size should be 13 bits based on the physical address size.");
    }

    @Test
    public void calculatesCorrectIndexBits() {
        assertEquals(13, cache.getIndexBits(), "An 8,192-row cache should require 13 index bits.");
    }

    @Test
    public void calculatesCorrectNumberOfRows() {
        assertEquals(8192, cache.getTotalNumberOfRows(), "A 32,768-block cache with 4-way associativity should have 8,192 rows.");
    }

    @Test
    public void calculatesCorrectOverheadSize() {
        assertEquals(57344, cache.getOverheadSizeBytes(), "A cache with 32,768 blocks and 14 overhead bits per block should have 57,344 overhead bytes.");
    }

    @Test
    public void calculatesCorrectImplementationMemory() {
        assertEquals(581632, cache.getImplementationMemoryBytes(), "A 512 KiB cache with 57,344 overhead bytes should require 581,632 total implementation bytes.");
    }

    @Test
    public void calculatesCorrectImplementationCost() {
        assertEquals(39.76, cache.getImplementationCost(), 0.001, "A 568 KB implementation at $0.07 per KB should cost $39.76.");
    }
}
