package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquare_1() {
        IntList lst = IntList.of(1, 1, 1, 1);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("1 -> 1 -> 1 -> 1", lst.toString());
        assertFalse(changed);
    }

    @Test
    public void testSquare_0() {
        IntList lst = IntList.of(0, 0, 0, 0);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("0 -> 0 -> 0 -> 0", lst.toString());
        assertFalse(changed);
    }
    @Test
    public void testSquare_endelayer() {
        IntList lst = IntList.of(2, 0, 7, 0);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 0 -> 49 -> 0", lst.toString());
        assertTrue(changed);
    }
}
