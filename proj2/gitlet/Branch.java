package gitlet;

import java.io.File;
import java.io.Serializable;

public class Branch implements Serializable, Dumpable {

    public static final File BRANCH_AREA = Utils.join(Repository.GITLET_DIR, "branches");

    /** 提前存储是不是当前branch，利于log */
    private boolean isHEADBranch;

    /** 这个分支目前指向的commit */
    private Commit pointerTo;

    /** 当前branch的名称 */
    private String branchName;

    private Branch() {

    }

    public static Branch createInitBranch(final Commit firstCommit) {
        Branch bh = createBranch("master", firstCommit);
        bh.markAsCurBranch();

        return bh;
    }

    public static Branch createBranch(final String bhName, final Commit commitToPoint) {
        Branch bh = new Branch();
        bh.isHEADBranch = false;
        bh.branchName = bhName;
        bh.pointerTo = commitToPoint;

        return bh;
    }

    public void updateCommitTo(final Commit newCommit) {
        this.pointerTo = newCommit;
        final File thisFilePath = Utils.join(BRANCH_AREA, this.branchName);
        this.saveTo(thisFilePath);
    }

    public static Byte[] readCommits() {
        /// TODO
        return null;
    }

    public static boolean updateBranch() {
        /// TODO
        return true;
    }

    public static Branch changeBranchTo(String anotherBranchName) {
        /// TODO
        return null;
    }

    /**
     *
     * @param filePath 要保存的文件夹的路径的File
     */
    public void saveTo(final File filePath) {
        /// TODO: 把这个分支保存到文件中
        final File branchFile = Utils.join(filePath, branchName);
        Utils.writeObjectToFileWithFileNotExistFix(branchFile, this);

        if (this.isHEADBranch) {
            final File headBranchFile = Utils.join(filePath, "head");
            Utils.writeObjectToFileWithFileNotExistFix(headBranchFile, this);
        }
    }

    /**
     *
     * @param BranchPath    完整的路径，应为不确定是cur还是什么
     * @return  一个根据文件内容得到的Branch的对象
     */
    public static Branch loadBranch(final File BranchPath) {
        /// TODO
        return Utils.readObject(BranchPath, Branch.class);
    }

    public void markAsCurBranch() {
        this.isHEADBranch = true;
    }




    /**
     * log时候方便
     */
    @Override
    public String toString() {
        if (isHEADBranch) {
            return "*" + branchName;
        } else {
            return branchName;
        }
    }

    @Override
    public void dump() {
        // TODO
    }
}
