package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gitlet.Branch.BRANCH_AREA;
import static gitlet.Commit.COMMIT_AREA;
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
    public static Commit getHeadCommit() {
        return headCommit;
    }

    /**
     * 本仓库的所有的branch
     */
    // TODO: remote的怎么办
    private List<Branch> branches;
    /**
     * 缓存区
     */
    private static Stage stageController;


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
    public static final File CUR_BRANCH = Utils.join(GITLET_DIR, "CUR_BRANCH");


    /* TODO: fill in the rest of this class. */
    public static boolean hasInited() {
        return GITLET_DIR.exists() && HEAD_FILE.exists()
                && CUR_BRANCH.exists() && Stage.stagesDir.exists() && Stage.STAGE_FILE.exists()
                && COMMIT_AREA.exists() && BRANCH_AREA.exists();
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

        if (!MetaData.BLOB_PATH.exists()) {
            if (!MetaData.BLOB_PATH.mkdir())
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
        headCommit = Commit.createInitCommit();
        headCommit.save();
        updateHEADCommitTo(headCommit);
        return true; /// 似乎没有必要这个 TODO
    }

    private static boolean setupBranch() {
        setCurBranch(Branch.createInitBranch(headCommit));
        curBranch.saveTo(CUR_BRANCH);
        return true;
    }

    private static boolean setupStage() {

        stageController = new Stage();
        stageController.save();

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

        stageController = Stage.loadStage();

        stageController.checkStatus(headCommit, CWD);


    }

    private static void setCurBranch(final Branch newBranch) {
        curBranch = newBranch;
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

        final File curFilePosition = Utils.join(CWD, filename4Stage);

        stageController.add(curFilePosition, headCommit);
    }

    private static Commit getCommitRecur(String Filter) {
        /// TODO
        return null;
    }

    public static void log_status() {

        /// branches
        System.out.println("=== Branches ===");
        List<String> allBranchName = Utils.plainFilenamesIn(BRANCH_AREA);
        assert allBranchName != null;
        for (String bhName : allBranchName) {
            if (bhName.equals(curBranch.getBranchName())) {
                System.out.println("*" + bhName);
            } else {
                System.out.println(bhName);
            }
        }
        System.out.print('\n');

        stageController.checkStatus(headCommit, CWD);
        /// staged files
        System.out.println("=== Staged Files ===");
        final Set<File> added = stageController.getAddedFiles();
        for (File f : added) {
            message(f.getName());
        }
        final Set<File> modified = stageController.getModifiedFiles();
        for (File f : modified) {
            message(f.getName());
        }
        System.out.print("\n");

        /// removed files
        System.out.println("=== Removed Files ===");
        final Set<File> rmed = stageController.getRemovedFiles();
        for (File f : rmed) {
            message(f.getName());
        }
        System.out.print("\n");

        /// modifications not staged
        System.out.println("=== Modifications Not Staged For Commit ===");

//        final Set<File> removedNotStaged  = stageController.getRemovedNotStaged();
//        for (File f : removedNotStaged) {
//            message(f.getName() + " (deleted)");
//        }
//
//        final Set<File> modifiedNotStaged = stageController.getModifiedNotStaged();
//        for (File f : modifiedNotStaged) {
//            message(f.getName() + " (modified)");
//        }
        System.out.print("\n");


        /// untracked files
        System.out.println("=== Untracked Files ===");
//        final Set<File> untracked = stageController.getUntracked();
//        for (File f : untracked) {
//            message(f.getName());
//        }
        System.out.print("\n");

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
        Set<File> allCommitsID = Commit.getAllCommits();
        for (final File id : allCommitsID) {
            Commit cmt = Commit.loadCommitByID(id.getName());
            System.out.println("===");
            cmt.log();
        }
    }

    static void find(final String msg) {

        Set<File> allCommitsID = Commit.getAllCommits();
        List<String> matchedIDs = new ArrayList<>();
        for (File commitFile : allCommitsID) {
            Commit cmt = Commit.loadCommitByID(commitFile.getName());
            if (cmt == null) {
                continue;
            }
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
//        child.dump();
//        headCommit.dump();
        child.save();
        headCommit = child;
        /// saveHEAD
        Utils.writeObjectToFileWithFileNotExistFix(HEAD_FILE, headCommit);
    }

    /**
     *
     * @param messageInformation information
     */
    public static void makeCommit(final String messageInformation) {
        /// the date and time and message and id
        if (messageInformation.isEmpty()) {
            message("Please enter a commit message.");
            System.exit(0);
        }

        if (!stageController.canCommit()) {
            message("No changes added to the commit.");
            System.exit(0);
        }
        /// 从缓存区中提取修改的信息 创建childCommit 修改headCommit
        final Commit childCommit = headCommit.produceChildCommit(stageController, messageInformation);

        updateHEADCommitTo(childCommit);
        /// 更新branch指向
        curBranch.updateCommitTo(headCommit);
        curBranch.saveTo(CUR_BRANCH);
        /// 清空stage
        stageController.clearCommited();

    }

    static void createNewBranch(final String branchName) {
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
        newBranch.saveTo(CUR_BRANCH);
    }

    public static void rm(final String fileName) {
        final File curFilePath = Utils.join(CWD, fileName);

        if (headCommit.findByName(curFilePath)) {
            /// 在commit中有
            /// remove it from the working directory, 这一步被stageArea处理了 TODO
            stageController.addToRemove(curFilePath);
            if (curFilePath.exists()) {
                curFilePath.delete();
            }

        } else {
            /// 在commit中无
            if (!stageController.removeFromStage(curFilePath)) {
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
        return getRelativePathWit(filePath, CWD);
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

        stageController.checkStatus(headCommit, CWD);

//        if (newBranch.getBranchName().equals("master")) {
//            message("%s\n%s\n%s\n%s\n", (filesBeOverwrittedHasCommited(newBranch, CWD) ? "Y" : "N"), stageController.getModifiedFiles(), stageController.getRemovedFiles(), stageController.getAddedFiles());
//        }
        /// 未被跟踪，并且会被签出覆盖!!! TODO  0323
        if (stageController.canCheckoutBranch() && filesBeOverwrittedHasCommited(newBranch.getCommitPointed(), CWD)) { /// HARD!!



            /// branch checkout
            curBranch.unmarkd();
            newBranch.markAsCurBranch();
            curBranch.saveTo(CUR_BRANCH);
            newBranch.saveTo(CUR_BRANCH);
            curBranch = newBranch;
            updateHEADCommitTo(curBranch.getCommitPointed());

            Utils.ClearDir(CWD);
            curBranch.rollBack(CWD);

            stageController.clearTotally();
        } else {
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
    }

    /**
     *
     * @param newCommit the commit of newBranch checking with
     * @param WorkingDir cur CWD
     * @return
     */
    private static boolean filesBeOverwrittedHasCommited(final Commit newCommit, final File WorkingDir) {
        File[] curFiles = WorkingDir.listFiles();

//        if (newBranch.getBranchName().equals("master")) {
//            message("\n");
//            for (File curFile : curFiles) {
//                message("%s", curFile.toString());
//            }
//            log_firstParents();
//            message("\n");
//
//        }

        for (File curFile : curFiles) {
            if (curFile.getName().equals(".gitlet")) {
                continue;
            }
            if (curFile.isFile()) {
//                filesOverWritted(Strs)
                final String fileRelativePath = Repository.getRelativePathWitCWD(curFile);
                MetaData metaDataOfBranch = newCommit.getMetaDataByFilename(fileRelativePath);
                if (metaDataOfBranch != null) {
                    /// will be overwrite
                    MetaData commitData = headCommit.getMetaDataByFilename(fileRelativePath);
                    final String sha1Cur = Utils.sha1(curFile);
                    if (! (commitData != null && commitData.getSHA1().equals(sha1Cur))) {
                        /// not commited
                    /// HARD type a ugly ! and ......
                        return false;
                    }
                }
            } else {
                if (!filesBeOverwrittedHasCommited(newCommit, curFile)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void checkoutByIdName(final String commitID, final String filename) {
        final Commit neededCommit = Commit.loadCommitByID(commitID);
        if (neededCommit == null) {
            Utils.message("No commit with that id exists.");
            System.exit(0);
        } else {
            ///
            Repository.checkoutFileName(filename, neededCommit);

        }


    }

    /**
     * Takes the version of the file as it exists in the head commit
     * and puts it in the working directory, overwriting the version
     * of the file that’s already there if there is one.
     * The new version of the file is not staged.
     * @param fileRelativePath the file for change to the curCommit, the relative path to CWD!!!
     */
    public static void checkoutFileName(String fileRelativePath, final Commit workingCommit) {

        MetaData fileData = workingCommit.getMetaDataByFilename(fileRelativePath);
        if (fileData == null) {
            Utils.message("File does not exist in that commit. ");
            System.exit(0);
        }

        final File backFile = fileData.getFilePathOfBlob();
//        Utils.message(backFile.getAbsolutePath());
        final File distFile = Utils.join(CWD, fileRelativePath);
        Utils.moveOroverwriteFileFromSrcToDist(backFile, distFile);

        /// not stage
        stageController.removeFromStage(distFile);
    }

    public static String getRelativePathWit(final File filePath, final File stagesDir) {
        final Path cwdPath = Paths.get(stagesDir.toURI());
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

    public static void removeBranchByName(final String branchName) {
        final File newBranchFile = Utils.join(BRANCH_AREA, branchName);
        if (!newBranchFile.exists()) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(curBranch.getBranchName())) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }
        newBranchFile.delete();
    }

    public static void resetByCommitID(final String commitID) {
        final Commit neededCommit = Commit.loadCommitByID(commitID);
        if (neededCommit == null) {
            Utils.message("No commit with that id exists.");
            System.exit(0);
        } else {
            if (filesBeOverwrittedHasCommited(neededCommit, CWD)) {
                neededCommit.rollBack(CWD);
                updateHEADCommitTo(neededCommit);

                curBranch.updateCommitTo(neededCommit);
            } else {
                message("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }
}
