package gitlet;

import static gitlet.Repository.hasInited;
import static gitlet.Utils.message;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author rj
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init": {
                GitletException.checkOfOperands(args.length, 1);
                if (hasInited()) {
                    message("A Gitlet version-control system already exists in the current directory.");
                    System.exit(0);
                } else {
                    Repository.init();
                }
                break;
            }
            case "add": {
                // TODO: handle the `add [filename]` command
                GitletException.checkOfOperands(args.length, 2);
                GitletException.checkGitInit();
                final String fileName4Add = args[1];
                /// fileName4Add 是相对路径，没有开头的/，从CWD出发的相对路径
                if (Repository.checkFileExist(fileName4Add)) {
                    // TODO
                } else {
                    message("File does not exist.");
                    System.exit(0);
                }
                break;
            }
            case "commit":

                break;
            case "rm":

                break;
            case "log": {
                GitletException.checkOfOperands(args.length, 1);
                GitletException.checkGitInit();
                Repository.log_firstParents();

                break;
            }
            case "global-log":
                break;
            case "status":
                break;
            case "checkout":
                break;
            case "branch":
                break;
            case "rm-branch":
                break;
            case "reset":
                break;
            case "merge":
                break;
            default:
                message("No command with that name exists.");
                System.exit(0);
                break;
        }
    }
}
