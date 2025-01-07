/** Class that prints the Collatz sequence starting from a given number.
 *  @author rj
 */
public class Collatz {

    /** Buggy implementation of nextNumber! */
    public static int nextNumber(int n) {
        return (n % 2 == 0 ? (n / 2) : (n * 3 + 1));
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
        System.out.println();
    }
}

