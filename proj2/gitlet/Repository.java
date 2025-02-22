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
    private static Branch curBranch;
    /** 本仓库的所有的branch */
    // TODO: remote的怎么办
    private List<Branch> branches;
    /** 缓存区 */
    private static Stage stageArea;


    /** The current working directory. */
    private static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    private static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
    public static boolean hasInited() {
        return GITLET_DIR.exists();
    }

    public static boolean init() {
        if( GITLET_DIR.mkdir()) {
            /// TODO: branch
            stageArea = new Stage();
            /// TODO: commit
            return true;
        }
        return false;
    }

    /**
     * 从 .gitlet 文件夹中读取信息，初始化文件
     */
    /*
    .gitlet
        head
        stage
        logmessage
        branches/branch1
        branches/branch2
     */
    public static void loadRepository() {
        /// TODO: 设计下，要什么再加载什么，所以

        /// branch
        File curBranchFile = Utils.join(GITLET_DIR, "head");
        curBranch = readObject(curBranchFile, Branch.class);

        /// Stage
        File stageFile = Utils.join(GITLET_DIR, "stageArea");
        stageArea = Utils.readObject(stageFile, Stage.class);

    }

    private static List<String> listAllBranches() {

    }


    public static boolean checkFileExist(final String name) {
        File file4Add = join(CWD, name);
        return file4Add.exists();
    }

    private static Commit getCommitRecur(String Filter) {

    }

    public static void log_status() {
        /// branches

        /// staged files

        /// removed files

        /// modifications not staged

        /// untracked files
    }
    static void log_firstParents() {





    }
}
