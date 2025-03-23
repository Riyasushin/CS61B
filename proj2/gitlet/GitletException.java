package gitlet;

import static gitlet.Utils.message;

/** General exception indicating a Gitlet error.  For fatal errors, the
 *  result of .getMessage() is the error message to be printed.
 *  @author P. N. Hilfinger
 */
class GitletException extends RuntimeException {


    /** A GitletException with no message. */
    GitletException() {
        super();
    }

    /** A GitletException MSG as its message. */
    GitletException(String msg) {
        super(msg);
    }


    static void checkOfOperands(final int realArgNum, final int... args) {
        boolean ok = false;
        for (final int i : args) {
            if (i == realArgNum) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            message("Incorrect operands.");
            System.exit(0);
        }
    }

    static void checkGitInit() {
        if (!Repository.hasInited()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

}
