package group7.cs3853.virtualmemory;

import group7.cs3853.tracefile.MemoryAccess;
import group7.cs3853.tracefile.TraceFile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public class PageTableData implements Runnable {
    private int virtualPagesMapped;
    private int pageTableHits;
    private int pagesFromFree;
    private int pageFaults;

    private long pagesAvailableToUser;
    private final int indexOfFile;
    private final String filename;

    private final ArrayList<MemoryAccess> memoryAccesses;

    // The set keeps track of the unique page numbers being sent to the page table
    private final HashSet<Long> pageTable;

    // FIFO virtual page replacement policy
    private final ArrayDeque<Long> queue =  new ArrayDeque<>();

    // Constant variables to avoid magic numbers
    private static final int BITS_IN_PAGE_SIZE = 12;
    private static final int NUMBER_OF_PAGE_TABLE_ENTRIES = 524288;

    public PageTableData(TraceFile traceFile, int indexOfFile, long pagesAvailableToUser) {
        this.filename = traceFile.getTraceFileName();
        this.virtualPagesMapped = 0;
        this.pageTableHits = 0;
        this.pagesFromFree = 0;
        this.pageFaults  = 0;
        this.indexOfFile = indexOfFile;
        this.pagesAvailableToUser = pagesAvailableToUser;
        this.memoryAccesses = traceFile.memoryAccesses;
        this.pageTable = new HashSet<>();
    }

    public int getVirtualPagesMapped () {
        return this.virtualPagesMapped;
    }

    public int getPageTableHits () {
        return this.pageTableHits;
    }

    public int getPagesFromFree () {
        return this.pagesFromFree;
    }

    public int getPageFaults () {
        return this.pageFaults;
    }

    @Override
    public void run () {
        for (MemoryAccess memoryAccess : memoryAccesses) {
            final long virtualPageNumber = extractVPNFromAddress(memoryAccess.virtualAddress());
            this.virtualPagesMapped += 1;
            if (pageTable.contains(virtualPageNumber)) {
                this.pageTableHits++;
            } else {
                // A page fault has occurred. Grab a free physical frame or execute a replacement policy
                if (this.pagesAvailableToUser > 0) {
                    // Decrement number of available frames
                    this.pagesAvailableToUser -= 1;

                    // Increment number of pages allocated from free physical frames
                    this.pagesFromFree += 1;

                    // Add this new virtual page to the queue of pages awaiting replacement in the event of no free frames
                    queue.offer(virtualPageNumber);

                    // Add the virtual page to the page table
                    pageTable.add(virtualPageNumber);
                } else {
                    // Increment the number of page faults which stands for number of pages replaced in this assignment
                    this.pageFaults += 1;

                    // Execute a First-In-First-Out replacement policy
                    if (queue.peek() != null) {
                        // Remove the oldest element from the queue
                        pageTable.remove(queue.poll());

                        // Insert the VPN at the end of the queue
                        queue.offer(virtualPageNumber);

                        // Add the address into the page table
                        pageTable.add(virtualPageNumber);
                    }
                }
            }
        }
    }

    private long extractVPNFromAddress (long virtualAddress) {
        return virtualAddress >>> BITS_IN_PAGE_SIZE;
    }

    private int divideRoundedUp (long value) {
        return Math.toIntExact((value + (long) 8 - 1) / (long) 8);
    }

    private float getPageTableUsagePercentage () {
        return 100f * ((float)pageTable.size() / (float)NUMBER_OF_PAGE_TABLE_ENTRIES);
    }

    private int getBytesOfPageTableWasted (long bitsPerPageTableEntry) {
        final int allocatedBytesForPageTable = divideRoundedUp(NUMBER_OF_PAGE_TABLE_ENTRIES * bitsPerPageTableEntry);
        final int bytesUsedToStoreEntries = divideRoundedUp(pageTable.size() * bitsPerPageTableEntry);

        return allocatedBytesForPageTable - bytesUsedToStoreEntries;
    }

    public void printResultsForTraceFile (long sizeOfPageTableEntry) {
        System.out.printf("[%d] %s:\n", this.indexOfFile, this.filename);
        System.out.printf("\tUsed Page Table Entries: %d (%.2f%%)\n", pageTable.size(), getPageTableUsagePercentage());
        System.out.printf("\tPage Table Wasted: %d bytes\n\n", getBytesOfPageTableWasted(sizeOfPageTableEntry));
    }

}
