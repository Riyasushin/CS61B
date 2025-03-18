package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Branch.BRANCH_AREA;
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


    /// 指向headCommit
    private static final File HEAD_FILE = Utils.join(GITLET_DIR, "HEAD");
    /// 指向curBranch
    private static final File CUR_BRANCH = Utils.join(GITLET_DIR, "CUR_BRANCH");


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
        if (!MetaData.BLOB_PATH.exists()) {
            if (!MetaData.BLOB_PATH.mkdir())
                return false;
        }

        /// stage
        if (!Stage.stagesDir.exists()) {
            if (!Stage.stagesDir.mkdir()) {
                return false;
            }
        }

        return true;

    }

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
        stageArea.save();

        return true;
    }

    public static boolean init() {
        if (GITLET_DIR.mkdir()) {
            return (setupDirStruture() && setupCommit() && setupBranch() && setupStage());
        } else {
            return false;
        }
        /// 没能成功创建.gitlet目录，返回失败
    }

    /**
     * 从 .gitlet 文件夹中读取信息，初始化文件
     */
    public static void loadRepository() {
        /// TODO: 设计下，要什么再加载什么，所以

        curBranch = readObject(CUR_BRANCH, Branch.class);

        headCommit = readObject(HEAD_FILE, Commit.class);

        stageArea = Stage.loadStage();
        stageArea.checkAll(CWD, headCommit);


    }

    private static void setCurBranch(final Branch newBranch) {
        curBranch = newBranch;
    }

    private static void setCurCommit(final Commit newCommit) {
        headCommit = newCommit;
    }


    public static boolean checkFileExist(final String name) {
        File file4Add = join(CWD, name);
        return file4Add.exists();
    }

    /**
     *      * 把文件保存到stage区域
     * @param filename4Stage 是相对路径，没有开头的/，从CWD出发的相对路径
     */
    public static void addFileToStage(final String filename4Stage) {

        if (!Repository.checkFileExist(filename4Stage)) {
            message("File does not exist.");
            System.exit(0);
        }

        File curFilePosition = Utils.join(CWD, filename4Stage);

        stageArea.tryAdd(curFilePosition, headCommit);
    }

    private static Commit getCommitRecur(String Filter) {
        /// TODO
        return null;
    }

    public static void log_status() {
        /// TODO

        /// branches
        System.out.println("=== Branches ===");
        List<String> allBranchName = Utils.plainFilenamesIn(COMMIT_AREA);
        assert allBranchName != null;
        for (String bhName : allBranchName) {
            if (bhName.equals(curBranch.getBranchName())) {
                System.out.println("*" + bhName);
            } else {
                System.out.println(bhName);
            }
        }
        System.out.print('\n');

        /// staged files
        System.out.println("=== Staged Files ===");

        /// removed files
        System.out.println("=== Removed Files ===");

        /// modifications not staged
        System.out.println("=== Modifications Not Staged For Commit ===");

        /// untracked files
        System.out.println("=== Untracked Files ===");

    }

    private static void logOneRecurisive(final Commit cmt) {
        System.out.println("===");
        cmt.log();
        if (cmt.hasDad()) {
            final Commit parDadCommit = Commit.loadCommitByID(cmt.getDadID());
            logOneRecurisive(parDadCommit);
        }

    }

    static void log_firstParents() {
        /// TODO
        logOneRecurisive(headCommit);
    }


    static void global_log() {
        List<String> allCommitsID = Utils.plainFilenamesIn(COMMIT_AREA);
        for (String id : allCommitsID) {
            Commit cmt = Commit.loadCommitByID(id);
            System.out.println("===");
            cmt.log();
        }
    }

    static void find(final String msg) {
        List<String> allCommitsID = Utils.plainFilenamesIn(COMMIT_AREA);
        List<String> matchedIDs = new ArrayList<>();
        for (String id : allCommitsID) {
            Commit cmt = Commit.loadCommitByID(id);
            if (msg.equals(cmt.getMessage())) {
                matchedIDs.add(cmt.getFullID());
            }
        }
        if (matchedIDs.isEmpty()) {
            message("Found no commit with that message.");
            System.exit(0);
        } else {
            for (String id : matchedIDs) {
                System.out.println(id);
            }
        }
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

    static void createNewBranchAsCurBranch(final String branchName) {
        /// 茶宠
        /// 新的，作为主
        /// 新的，指向headCommit
        /// 旧的，删除作为主的mark
        /// 保存新的和旧的
        List<String> allBranch = Utils.plainFilenamesIn(BRANCH_AREA);
        if (allBranch.contains(branchName)) {
            message("A branch with that name already exists.");
            System.exit(0);
        }
        Branch newBranch = Branch.createBranch(branchName, headCommit);
        newBranch.markAsCurBranch();
        curBranch.unmarkd();
        curBranch.saveTo(BRANCH_AREA);
        newBranch.saveTo(BRANCH_AREA);
        curBranch = newBranch;
    }

    public static void rm(final String fileName) {
        final File curFilePath = Utils.join(CWD, fileName);

        if (headCommit.findByName(curFilePath)) {
            /// 在commit中有
            /// remove it from the working directory, 这一步被stageArea处理了
            stageArea.add2RmList(curFilePath);
            ///

        } else {
            /// 在commit中无
            if (stageArea.stagedForAdd(curFilePath)) {
                stageArea.removeFileFromStage(curFilePath);
            } else {
                message("No reason to remove the file.");
                System.exit(0);
            }
        }

    }


    /* 一些工具方法 */

    /**
     * TODO  和我想的结果不一样，得去重写，我想要的不是变成../../..这种，是只保留相对路径
     * 这算一个核心算法，注意
     * @param filePath 一个在CWD中的文件的File信息
     * @return filePath 文件和CWD的相对路径的String值
     */
    static String getRelativePathWitCWD(final File filePath) {
        final Path cwdPath = Paths.get(CWD.toURI());
        Path fileAbsPath = filePath.toPath().toAbsolutePath();

        // 计算相对路径（若路径合法且在同一文件系统）
        if (cwdPath.getRoot() != null &&
                cwdPath.getRoot().equals(fileAbsPath.getRoot())) {
            return cwdPath.relativize(fileAbsPath).toString();
        } else {
            // 若跨文件系统，返回绝对路径
            return fileAbsPath.toString();
        }


    }

    public static void checkoutBranch(final String branchName) {
        final File newBranchFile = Utils.join(BRANCH_AREA, branchName);
        if (!newBranchFile.exists()) {
            message("No such branch exists.");
            System.exit(0);
        }
        Branch newBranch = Branch.loadBranch(newBranchFile);
        if (newBranch.getBranchName().equals(curBranch.getBranchName())) {
            message("No need to checkout the current branch.");
            System.exit(0);
        }
        if (stageArea.isDeepTidy(Repository.CWD, Repository.headCommit)) {

            /// branch checkout
            curBranch.unmarkd();
            newBranch.markAsCurBranch();
            curBranch.saveTo(BRANCH_AREA);
            curBranch.saveTo(BRANCH_AREA);
            curBranch = newBranch;

            curBranch.rollBack(CWD);

            /// TODO  Stage中没有存

        } else {
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
    }

    public static void checkoutByIdName(final String commitID, final String filename) {
        final Commit neededCommit = Commit.loadCommitByID(commitID);
        neededCommit.checkFileByName(filename);

        /// Stage  TODO  

    }

    public static void checkoutFileName(String fileName) {
        /// TODO

    }
}
