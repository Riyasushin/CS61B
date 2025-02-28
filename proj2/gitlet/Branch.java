package gitlet;

import java.io.File;
import java.io.Serializable;

public class Branch implements Serializable, Dumpable {

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

    public static Byte[] readCommits() {
        /// TODO
    }

    public static boolean updateBranch() {
        /// TODO
    }

    public static Branch changeBranchTo(String anotherBranchName) {
    }

    /**
     *
     * @param filePath 要保存的文件夹的路径的File
     */
    public void save(final File filePath) {
        /// TODO: 把这个分支保存到文件中
        final File branchFile = Utils.join(filePath, branchName);
        Utils.writeObject(branchFile, this);

        if (this.isHEADBranch) {
            final File headBranchFile = Utils.join(filePath, "head");
            Utils.writeObject(headBranchFile, this);
        }
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
