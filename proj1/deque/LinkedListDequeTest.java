package deque;

import org.junit.Test;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Performs some basic linked list tests.
 */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double> lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }

    }

    @Test
    public void testTime_addLast() {
        // TODO: YOUR CODE HERE
        List<Integer> Ns = new ArrayList<>();
        Ns.add(1000);
        Ns.add(2000);
        Ns.add(4000);
        Ns.add(8000);
        Ns.add(16000);
        Ns.add(32000);
        Ns.add(64000);
        Ns.add(128000);
        List<Double> times = new ArrayList<>();

        for (int i = 0; i < Ns.size(); ++i) {
            int N = Ns.get(i);
            LinkedListDeque<Integer> tmp = new LinkedListDeque<>();
            Stopwatch sw = new Stopwatch();
            for (int ig = 0; ig < N; ++ig) {
                tmp.addLast(ig);
            }
            times.add(sw.elapsedTime());
        }

        printTimingTable(Ns, times, Ns);
    }

    @Test
    public void testTime_addFirst() {
        // TODO: YOUR CODE HERE
        List<Integer> Ns = new ArrayList<>();
        Ns.add(1000);
        Ns.add(2000);
        Ns.add(4000);
        Ns.add(8000);
        Ns.add(16000);
        Ns.add(32000);
        Ns.add(64000);
        Ns.add(128000);
        List<Double> times = new ArrayList<>();

        for (int i = 0; i < Ns.size(); ++i) {
            int N = Ns.get(i);
            LinkedListDeque<Integer> tmp = new LinkedListDeque<>();
            Stopwatch sw = new Stopwatch();
            for (int ig = 0; ig < N; ++ig) {
                tmp.addFirst(ig);
            }
            times.add(sw.elapsedTime());
        }

        printTimingTable(Ns, times, Ns);
    }

    @Test
    public void testTime_removeFirst() {
        // TODO: YOUR CODE HERE
        List<Integer> Ns = new ArrayList<>();
        Ns.add(1000);
        Ns.add(2000);
        Ns.add(4000);
        Ns.add(8000);
        Ns.add(16000);
        Ns.add(32000);
        Ns.add(64000);
        Ns.add(128000);
        List<Double> times = new ArrayList<>();

        for (int i = 0; i < Ns.size(); ++i) {
            int N = Ns.get(i);
            LinkedListDeque<Integer> tmp = new LinkedListDeque<>();
            for (int ig = 0; ig < N; ++ig) {
                tmp.addFirst(ig);
            }
            Stopwatch sw = new Stopwatch();
            for (int ig = 0; ig < N; ++ig) {
                tmp.removeFirst();
            }
            times.add(sw.elapsedTime());
        }

        printTimingTable(Ns, times, Ns);
    }

    @Test
    public void testTime_removeLast() {
        // TODO: YOUR CODE HERE
        List<Integer> Ns = new ArrayList<>();
        Ns.add(1000);
        Ns.add(2000);
        Ns.add(4000);
        Ns.add(8000);
        Ns.add(16000);
        Ns.add(32000);
        Ns.add(64000);
        Ns.add(128000);
        List<Double> times = new ArrayList<>();

        for (int i = 0; i < Ns.size(); ++i) {
            int N = Ns.get(i);
            LinkedListDeque<Integer> tmp = new LinkedListDeque<>();
            for (int ig = 0; ig < N; ++ig) {
                tmp.addFirst(ig);
            }
            Stopwatch sw = new Stopwatch();
            for (int ig = 0; ig < N; ++ig) {
                tmp.removeLast();
            }
            times.add(sw.elapsedTime());
        }

        printTimingTable(Ns, times, Ns);
    }

    private static void printTimingTable(List<Integer> Ns, List<Double> times, List<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.print("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }
}
