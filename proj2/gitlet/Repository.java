package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /** 当前的branch */
    private Branch curBranch;
    /** 本仓库的所有的branch */
    // TODO: remote的怎么办
    private List<Branch> branches;
    /** 缓存区 */
    private Stage stageArea;


    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
    public static boolean hasInited() {
        return GITLET_DIR.exists();
    }

    public static boolean init() {
        if( GITLET_DIR.mkdir()) {
            /// TODO: branch
            /// TODO: commit
            return true;
        }
        return false;
    }

    public static boolean checkFileExist(final String name) {
        File file4Add = join(CWD, name);
        return file4Add.exists();
    }

    static void log_firstParents() {

    }
}
