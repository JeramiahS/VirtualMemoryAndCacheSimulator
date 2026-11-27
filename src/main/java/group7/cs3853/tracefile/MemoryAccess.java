package group7.cs3853.tracefile;

public record MemoryAccess(String origin, long virtualAddress, int bytesRead) {
    @Override
    public String toString() {
        return bytesRead + " bytes read from address " + Long.toHexString(virtualAddress) + " at " + origin;
    }
}
