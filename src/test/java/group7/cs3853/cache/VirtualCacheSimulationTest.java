package group7.cs3853.cache;

import group7.cs3853.tracefile.MemoryAccess;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VirtualCacheSimulationTest {
    @Test
    void classifiesMissesBySetValidity() {
        final String[] args = {
                "-s", "8", "-b", "8", "-a", "1", "-r", "rr", "-p", "128"
        };
        final VirtualCache cache = VirtualCache.buildFromArgs(args);
        final ArrayList<MemoryAccess> accesses = new ArrayList<>(3);

        accesses.add(new MemoryAccess("EIP", 0, 1));
        accesses.add(new MemoryAccess("EIP", 8192, 1));
        accesses.add(new MemoryAccess("EIP", 0, 1));

        cache.executeSimulation(accesses);

        assertEquals(3, cache.totalCacheAccesses);
        assertEquals(0, cache.totalCacheHits);
        assertEquals(1, cache.totalCompulsoryMisses);
        assertEquals(2, cache.totalConflictMisses);
    }

    @Test
    void doesNotTreatSameVirtualAddressInDifferentProcessesAsHit() {
        final String[] args = {
                "-s", "8", "-b", "8", "-a", "1", "-r", "rr", "-p", "128"
        };
        final VirtualCache cache = VirtualCache.buildFromArgs(args);
        final ArrayList<MemoryAccess> accesses = new ArrayList<>(1);
        accesses.add(new MemoryAccess("EIP", 0, 1));

        cache.executeSimulation(accesses, 0);
        cache.executeSimulation(accesses, 1);

        assertEquals(2, cache.totalCacheAccesses);
        assertEquals(0, cache.totalCacheHits);
        assertEquals(1, cache.totalCompulsoryMisses);
        assertEquals(1, cache.totalConflictMisses);
    }
}
