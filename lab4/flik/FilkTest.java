package flik;
import org.junit.Assert;
import org.junit.Test;

import static flik.Flik.isSameNumber;

public class FilkTest {

    @Test
    public void testFrom1To1000() {
        for (int i = 0; i < 1000; ++i) {
            Assert.assertTrue(isSameNumber((Integer)i,(Integer) i));
        }
    }

    @Test
    public void test127() {
        Assert.assertTrue((isSameNumber(127, 127)));
    }
    @Test
    public void test128() {
        Assert.assertTrue((isSameNumber(128, 128)));
    }
}
