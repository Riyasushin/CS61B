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
        if (firstArg.equals("init")) {
            ///  init
            GitletException.checkOfOperands(args.length, 1);
            if (hasInited()) {
                message("A Gitlet version-control system already exists in the current directory.");
                System.exit(0);
            } else {
                Repository.init();
            }
        } else {
            ///  先检查init了没有
            GitletException.checkGitInit();
            Repository.loadRepository();

            switch (firstArg) {

                case "add": {
                    GitletException.checkOfOperands(args.length, 2);
                    final String fileName4Add = args[1];

                    Repository.addFileToStage(fileName4Add);
                    break;
                }
                case "commit": {
                    GitletException.checkOfOperands(args.length, 2);
                    final String message = args[1];
                    Repository.makeCommit(message);
                    break;
                }
                case "rm": {
                    GitletException.checkOfOperands(args.length, 2);
                    final String fileRelativePath2Remove = args[1];
                    Repository.rm(fileRelativePath2Remove);
                    break;
                }
                case "log": {
                    GitletException.checkOfOperands(args.length, 1);
                    Repository.logFirstParents();

                    break;
                }
                case "global-log": {
                    GitletException.checkOfOperands(args.length, 1);
                    Repository.globalLog();

                    break;
                }
                case "find": {
                    GitletException.checkOfOperands(args.length, 2);
                    final String msg = args[1];
                    Repository.find(msg);
                    break;
                }
                case "status": {
                    GitletException.checkOfOperands(args.length, 1);
                    Repository.logStatus();

                    break;
                }
                case "checkout": {
                    GitletException.checkOfOperands(args.length, 3, 2, 4);
                    switch (args.length) {
                        case 2: {
                            Repository.checkoutBranch(args[1]);
                            break;
                        }
                        case 4: {
                            /// HARD ,,,, lu bang xing
                            if (!args[2].equals("--")) {
                                message("Incorrect operands.");
                                System.exit(0);
                            }
                            Repository.checkoutByIdName(args[1], args[3]);
                            break;
                        }
                        case 3: {
                            if (!args[1].equals("--")) {
                                message("Incorrect operands.");
                                System.exit(0);
                            }
                            Repository.checkoutFileName(args[2], Repository.getHeadCommit());
                            break;
                        }
                        default: {
                            message("Incorrect operands.");
                            System.exit(0);
                            break;
                        }
                    }
                    break;
                }
                case "branch": {
                    GitletException.checkOfOperands(args.length, 2);
                    final String branchName = args[1];
                    Repository.createNewBranch(branchName);
                    break;
                }
                case "rm-branch": {
                    GitletException.checkOfOperands(args.length, 2);
                    Repository.removeBranchByName(args[1]);
                    break;
                }
                case "reset": {
                    GitletException.checkOfOperands(args.length, 2);
                    Repository.resetByCommitID(args[1]);
                    break;
                }
                case "merge": {
                    GitletException.checkOfOperands(args.length, 2);
                    Repository.merge(args[1]);
                    break;
                }
                default:
                    message("No command with that name exists.");
                    System.exit(0);
                    break;
            }
        }

    }
}
