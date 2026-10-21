public class Main {

    public static void main(String[] args) {
        if (args.length < 17 || args.length > 21) {
            System.err.println("Error: Invalid amount of arguments");
            printUsageInstructions();
            System.exit(1);
        }
    }

    public static void printUsageInstructions() {
        System.out.print("Usage: java -jar CacheSim.jar <-s> <-b> <-a> <-r> <-p> <-u> <-n> <-f> [-f] [-f]\n\n");
        System.out.print("Arguments:\n");
        System.out.print("\ts: Cache size in kibibytes\n");
        System.out.print("\tb: Cache block size in bytes\n");
        System.out.print("\ta: Cache associativity value\n");
        System.out.print("\tr: Cache replacement policy\n");
        System.out.print("\tp: Simulated physical memory of the computer in mebibytes\n");
        System.out.print("\tu: Simulated percentage of physical memory used by the operating system\n");
        System.out.print("\tn: Instructions/Time Slice value\n");
        System.out.print("\tf: The name of a trace file (Minimum 1, max 3 files)\n");
        System.out.print("\n");
    }

}
