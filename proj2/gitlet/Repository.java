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

    private static Commit headCommit;

    /** 本仓库的所有的branch */
    // TODO: remote的怎么办
    private List<Branch> branches;
    /** 缓存区 */
    private static Stage stageArea;


    /** The current working directory. */
    private static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    private static final File GITLET_DIR = join(CWD, ".gitlet");

    private static final File STAGE_DIR = Utils.join(GITLET_DIR, "stage");

    private static final File BRANCH_AREA = Utils.join(GITLET_DIR, "branches");

    private static final File COMMIT_AREA = Utils.join((GITLET_DIR, "commits");

    private static final File OBJECTS = Utils.join(GITLET_DIR, "objects");

    /* TODO: fill in the rest of this class. */
    public static boolean hasInited() {
        return GITLET_DIR.exists();
    }

    /**
     *
     * @return True if all mkdir succeed
     *          False if somebody fail to be created
     */
    private static boolean setupDirStruture() {
        /// branches
        if (!BRANCH_AREA.exists()) {
            if ( !BRANCH_AREA.mkdir())
                return false;
        }
        /// commits
        if (!COMMIT_AREA.exists()) {
            if (!COMMIT_AREA.mkdir())
                return false;
        }
        /// stage
        if (!STAGE_DIR.exists()) {
            if (!STAGE_DIR.mkdir())
                return false;
        }

        /// objects
        if (!OBJECTS.exists()) {
            if (!OBJECTS.mkdir())
                return false;
        }

        return true;

    }

    private static boolean setupCommit() {
        setCurCommit(Commit.createInitCommit());

        headCommit.save(COMMIT_AREA);
        return true; /// 似乎没有必要这个 TODO
    }

    private static boolean setupBranch() {
        setCurBranch(Branch.createInitBranch(headCommit));
        curBranch.save(BRANCH_AREA);
        return true;
    }

    private static boolean setupStage() {

        stageArea = new Stage(STAGE_DIR);
        stageArea.clear();

        return true;

    }

    public static boolean init() {
        if( GITLET_DIR.mkdir()) {
            return setupDirStruture() && setupCommit() && setupBranch() && setupStage();
        }
        /// 没能成功创建.gitlet目录，返回失败
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
        File curBranchFile = Utils.join(BRANCH_AREA, "head");
        curBranch = readObject(curBranchFile, Branch.class);

        /// Stage
        File stageSavedFile = Utils.join(STAGE_DIR, "info.stage");
        stageArea = Utils.readObject(stageSavedFile, Stage.class);



    }

    private static void setCurBranch(final Branch newBranch) {
        curBranch = newBranch;
    }

    private static void setCurCommit(final Commit newCommit) {
        headCommit = newCommit;
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

    /**
     * 把文件保存到stage区域
     */
    public static void addFileToStage(final String filename4Stage) {
        File absFile = Utils.join(GITLET_DIR, filename4Stage);

    }
}
