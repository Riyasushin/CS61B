package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static gitlet.Branch.BRANCH_AREA;
import static gitlet.Commit.BLOB_PATH;
import static gitlet.Commit.COMMIT_AREA;
import static gitlet.Stage.STAGE_FILE;
import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author RiJoshin
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /**
     * 当前的branch
     */
    private static Branch curBranch;

    private static Commit headCommit;

    /*
    .gitlet
        Commit_edit_message
        Fetch_head 最后一次和服务器交互的Commit
        HEAD 当前工作群的Commit
        CUR_BRANCH 当前的所处的分支
        origin_commit 当前分支和远程分支最后一个COMMIT的 SHA1值
        branches
            branch1
            branch2
        config 配置远程地址、分支指向，作者，邮箱......
        stage 暂存区索引文件
        logs    提交日志
            HEAD
            refs
                heads
                remotes
        objects
            一堆存储文件夹，名称来着SHA1前三个字符
            info
                一堆。idx.pak文件文件打包后的东西
        blobs
            name
                blob1
                blob2
                blob3
        commits
            ahi
                ......
            hnu
                ......

     */
    /**
     * 本仓库的所有的branch
     */
    // TODO: remote的怎么办
    private List<Branch> branches;
    /**
     * 缓存区
     */
    private static Stage stageArea;


    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");


    private static final File HEAD_FILE = Utils.join(GITLET_DIR, "HEAD");
    private static final File CUR_BRANCH = Utils.join(GITLET_DIR, "CUR_BRANCH");

    /// point to the HEAD Commit File
    private static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
    /* TODO: fill in the rest of this class. */
    public static boolean hasInited() {
        return GITLET_DIR.exists();
    }

    /**
     * @return True if all mkdir succeed
     * False if somebody fail to be created
     */
    private static boolean setupDirStruture() {
        /// branches
        if (!BRANCH_AREA.exists()) {
            if (!BRANCH_AREA.mkdir())
                return false;
        }
        /// commits
        if (!COMMIT_AREA.exists()) {
            if (!COMMIT_AREA.mkdir())
                return false;
        }

        /// objects
        if (!BLOB_PATH.exists()) {
            if (!BLOB_PATH.mkdir())
                return false;
        }

        return true;

    }
//    if (!ddgFile.exists()) {
//        try {
//            ddgFile.createNewFile();
//        } catch (IOException e) {
//            System.out.println("Error: fail to create file " + ddgFile.getAbsolutePath());
//        }
//    }

    private static boolean setupCommit() {
        setCurCommit(Commit.createInitCommit());
        headCommit.save();
        updateHEADCommitTo(headCommit);
        return true; /// 似乎没有必要这个 TODO
    }

    private static boolean setupBranch() {
        setCurBranch(Branch.createInitBranch(headCommit));
        curBranch.saveTo(BRANCH_AREA);
        return true;
    }

    private static boolean setupStage() {

        stageArea = new Stage();
        stageArea.clear();

        return true;

    }

    public static boolean init() {
        if (GITLET_DIR.mkdir()) {
            return setupDirStruture() && setupCommit() && setupBranch() && setupStage();
        }
        /// 没能成功创建.gitlet目录，返回失败
        return false;
    }

    /**
     * 从 .gitlet 文件夹中读取信息，初始化文件
     */
    public static void loadRepository() {
        /// TODO: 设计下，要什么再加载什么，所以

        /// branch
        File curBranchFile = Utils.join(BRANCH_AREA, "head");
        curBranch = readObject(curBranchFile, Branch.class);

        /// Stage
        File stageSavedFile = Utils.join(STAGE_FILE, "info.stage");
        stageArea = Utils.readObject(stageSavedFile, Stage.class);


    }

    private static void setCurBranch(final Branch newBranch) {
        curBranch = newBranch;
    }

    private static void setCurCommit(final Commit newCommit) {
        headCommit = newCommit;
    }

    private static List<String> listAllBranches() {
        /// TODO
        return null;
    }


    public static boolean checkFileExist(final String name) {
        File file4Add = join(CWD, name);
        return file4Add.exists();
    }


    private static Commit getCommitRecur(String Filter) {
        /// TODO
        return null;
    }

    public static void log_status() {
        /// TODO

        /// branches

        /// staged files

        /// removed files

        /// modifications not staged

        /// untracked files
    }

    static void log_firstParents() {
        /// TODO

    }

    /**
     * 把文件保存到stage区域
     */
    public static void addFileToStage(final String filename4Stage) {
        File actual4AddFile = Utils.join(GITLET_DIR, filename4Stage);
        File curFilePosition = Utils.join(CWD, filename4Stage);

        /// 看和commit中的有没有、一不一样 用SHA1
        /// TODO

        /// 看存进去过没有，之后两个状态：一样or不一样；存了or没存

    }
    public static void updateHEADCommitTo(final Commit child) {
        /// update HEAD to Child
        child.save();
        headCommit = child;

        /// saveHEAD
        Utils.writeObjectToFileWithFileNotExistFix(HEAD_FILE, headCommit);
    }

    public static void makeCommit(final String messageInformation) {
        /// the date and time and message and id

        /// 从缓存区中提取修改的信息 创建childCommit 修改headCommit
        final Commit childCommit = headCommit.produceChildCommit(stageArea, messageInformation);

        updateHEADCommitTo(childCommit);
        /// 更新branch指向
        curBranch.updateCommitTo(headCommit);
        curBranch.saveTo(CUR_BRANCH);
        /// 清空stage
        stageArea.clear();

    }
}
