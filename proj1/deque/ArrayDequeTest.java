package deque;

import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Performs some basic linked list tests.
 */
public class ArrayDequeTest{

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        ArrayDeque<String> lld1 = new ArrayDeque<String>();

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

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
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

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
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

        ArrayDeque<String> lld1 = new ArrayDeque<String>();
        ArrayDeque<Double> lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

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
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

    }

    @Test
    public void testRemove() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        ad.addFirst(1);
        ad.addFirst(2);
        ad.addFirst(3);
        ad.printDeque();
        ad.addLast(4);
        ad.addLast(5);
        ad.addLast(6);
        ad.printDeque();

        ad.removeFirst();
        ad.printDeque();
        ad.removeFirst();
        ad.printDeque();

        ad.removeLast();
        ad.printDeque();
        ad.removeLast();
        ad.printDeque();
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            int x = lld1.removeFirst();
//            System.out.println(x);
            assertEquals("Should have the same value", i, (double) x, 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            int x = lld1.removeLast();
//            System.out.println(x);
            assertEquals("Should have the same value", i, (double) x, 0.0);
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
            ArrayDeque<Integer> tmp = new ArrayDeque<>();
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
            ArrayDeque<Integer> tmp = new ArrayDeque<>();
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
            ArrayDeque<Integer> tmp = new ArrayDeque<>();
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
            ArrayDeque<Integer> tmp = new ArrayDeque<>();
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

    @Test
    public void testGet() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(0);
        ad.addFirst(1);
        ad.addLast(2);
        ad.addFirst(3);
        ad.addLast(4);
        ad.addLast(5);
        ad.addFirst(6);

        assertEquals("Should have the same value", 5, ad.removeLast(), 0);
    }
}
