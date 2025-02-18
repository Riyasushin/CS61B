package gitlet;

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
