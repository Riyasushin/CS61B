package gitlet;

// TODO: any imports you need here

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.List;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  @author RiJoshin
 */
public class Commit implements Serializable, Dumpable {
    /**
     * TODO: add instance variables here.
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The hashValue and unique name of this commit */
    private String id;
    /** The father commit of this commit, may not single */
    private List<Commit> parents;
    /** The time of this commit */
    private long timeStamp;
    /** The message of this Commit. */
    private String message;
    /** The author of this commit. */
    private String author = "Ri joshing"; /// 自己加的
    /** 空时候用SHA-1 会生成的结果 */
    private static String initID = "da39a3ee5e6b4b0d3255bfef95601890afd80709";


    /**
     * 返回是否是merge commit, 这由父亲节点的数量判断，多余一个就是merge
     */
    public boolean isMerge() {
        return parents.size() > 1;
    }

    /**
     * 构造函数是私有的，在类的外部无法直接通过 new Commit() 来创建 Commit 对象
     * 保证所有的Commit必须有父母qwq
     */
    private Commit() {}

    /**
     * 返回Init时候需要的commit
     * @return
     */
    static Commit createInitCommit() {
        return Commit.createCommit(initID, "initial commit", 0, null, true);
    }

    /**
     * 根据提供的信息返回关联好的commit
     * @param idT
     * @param messageT
     * @param timeT
     * @param parentsT
     * @param headIF
     * @return
     */
    static Commit createCommit(final String idT, final String messageT, final long timeT, final List<Commit> parentsT) {
        Commit ct = new Commit();
        ct.parents = parentsT;
        ct.id = idT;
        ct.timeStamp = timeT;
        ct.message = messageT;
        return ct;
    }


    /**
     * TODO: 返回的是简写不是全部，前6个
     * @return 该commit的特征值，即它的名称的简写
     */
    public String getUniqueID() {
        return id;
    }

    /**
     * 返回按照log需求对该commit信息的字符串内容的构造
     * 到时候log只需要直接print
     * @return String ordered message
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("===\n");
        sb.append("commit " + id + "\n");
        if (isMerge()) {
            /// 是 Merge ， 加上Merge信息
            sb.append("Merge:");
            for (Commit pare : parents) {
                sb.append(" " + pare.getUniqueID());
            }
            sb.append("\n");
        }
        sb.append("Date: " + (Utils.unixTimeFormatter(timeStamp)) + "\n");
        sb.append(message + "\n");

        return sb.toString();
    }


    @Override
    public void dump() {
        // TODO
    }

    /**
     * 把本对象存为文件
     * @param filePath 具体到文件路径
     */
    public void save(final File filePath) {
        final File realFile = Utils.join(filePath, this.getUniqueID());
        Utils.writeObject(realFile, this);
    }
}
