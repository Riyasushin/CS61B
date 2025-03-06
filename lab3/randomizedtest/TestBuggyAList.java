package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        BuggyAList inp = new BuggyAList<>();
        AListNoResizing exp = new AListNoResizing<>();
        inp.addLast(1);
        inp.addLast(2);
        inp.addLast(3);
        exp.addLast(1);
        exp.addLast(2);
        exp.addLast(3);
        Assert.assertEquals(exp.removeLast(), inp.removeLast());
        Assert.assertEquals(exp.removeLast(), inp.removeLast());
        inp.addLast(1);
        inp.addLast(2);
        inp.addLast(3);
        exp.addLast(1);
        exp.addLast(2);
        exp.addLast(3);
        Assert.assertEquals(exp.removeLast(), inp.removeLast());
    }

    @Test
    public void randomizeTest() {
        BuggyAList inp = new BuggyAList<>();
        AListNoResizing exp = new AListNoResizing<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                inp.addLast(randVal);
                exp.addLast(randVal);
                Assert.assertEquals(exp.getLast(), inp.getLast());
            } else if (operationNumber == 1) {
                // size
                Assert.assertEquals(exp.size(), inp.size());
            } else if (operationNumber == 2) {
                if (exp.size() > 0) {
                    Assert.assertEquals(exp.removeLast(), inp.removeLast());
                }
            }
        }
    }
}
