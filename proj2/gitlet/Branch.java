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
        bh.pointerTo = commitToPoint.clone();

        return bh;
    }

    public void updateCommitTo(final Commit newCommit) {
        this.pointerTo = newCommit;
        final File thisFilePath = Utils.join(BRANCH_AREA, this.branchName);
        this.saveTo(Repository.CUR_BRANCH);
    }

    public Commit getCommitPointed() {
        return pointerTo;
    }

    public String getBranchName() {
        return branchName;
    }

    /**
     *
     * @param headFilePath 要保存的文件夹的路径的File
     */
    public void saveTo(final File headFilePath) {
        /// TODO: 把这个分支保存到文件中
        final File branchFile = Utils.join(Branch.BRANCH_AREA, branchName);
        Utils.writeObjectToFileWithFileNotExistFix(branchFile, this);

        if (this.isHEADBranch) {
            Utils.writeObjectToFileWithFileNotExistFix(headFilePath, this);
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

    public void unmarkd() {
        this.isHEADBranch = false;
    }

    /**
     *
     * @param workingDir CWD
     */
    public void rollBack(final File workingDir) {
        /// TODO
        pointerTo.rollBack(workingDir);


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
