package group7.cs3853;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class CacheSimulationDataTest {

    @Test
    void completesOneByteAccessAndVisitsEveryBlockSpanned() {
        final VirtualCache cache = VirtualCache.buildFromArgs(new String[] {
                "-s", "8", "-b", "16", "-a", "1", "-r", "rr", "-p", "128"
        });
        final CacheSimulationData simulation = new CacheSimulationData(
                new ArrayList<>(List.of(
                        new MemoryAccess("EIP", 0x1000, 1),
                        new MemoryAccess("EIP", 0x100f, 4)
                )),
                cache
        );

        assertTimeoutPreemptively(Duration.ofSeconds(1), simulation::run);
        assertEquals(3, simulation.totalCacheAccesses);
    }
}
