package gitlet;

// TODO: any imports you need here

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gitlet.Branch.BRANCH_AREA;
import static gitlet.Utils.readObject;


/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  Commit is immutable!!!!!
 *  @author RiJoshin
 */
public class Commit implements Serializable, Dumpable {
    /**
     * TODO: add instance variables here.
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    public static final File COMMIT_AREA = Utils.join(Repository.GITLET_DIR, "commits");
    /**
     * The hashValue and unique name of this commit
     */
    private String id;
    /**
     * The father commit of this commit, may not single
     */
    private List<String> parents;
    /**
     * The time of this commit
     */
    private long timeStamp;
    /**
     * The message of this Commit.
     */
    private String message;

    /**
     * String Repository.getRelativePathWitCWD(addFile),相对于CWD或者stages的相对路径
     */
    private Map<String, MetaData> metadataMap;

    /// metadata TODO

    /**
     * relative path (str) -> data
     * @param name
     * @return null if not find
     */
    public MetaData getMetaDataByFilename(final String name) {
        return metadataMap.getOrDefault(name, null);
    }

    public Set<String> getMetaDataNameList() {
        return metadataMap.keySet();
    }

    /**
     * 返回是否是merge commit, 这由父亲节点的数量判断，多余一个就是merge
     */
    public boolean isMerge() {
        return parents.size() > 1;
    }

    /**
     *
     * @return True if this is root Commit; false if it has parent.
     */
    public boolean hasDad() {
        return !parents.isEmpty();
    }
    public String getDadID() {
        return parents.get(0);
    }

    /**
     * 构造函数是私有的，在类的外部无法直接通过 new Commit() 来创建 Commit 对象
     * 保证所有的Commit必须有父母qwq
     */
    private Commit() {
    }

    /**
     * 返回Init时候需要的commit
     *
     * @return
     */
    static Commit createInitCommit() {
        return Commit.createCommit("initial commit", 0, null, null);
    }

    /// 根据提供的信息返回关联好的commit
    static Commit createCommit(final String messageT, final long timeT, final List<String> parentsT, final Map<String, MetaData> datas) {
        Commit ct = new Commit();
        ct.parents = parentsT;
        ct.timeStamp = timeT;
        ct.message = (messageT == null? "" : messageT); /// 根据commit要求，一定不是null !
        ct.metadataMap = datas;

        ct.id = Utils.sha1((ct.parents == null ? "" : ct.parents), ct.timeStamp, ct.message, (ct.metadataMap == null ? " " : ct.metadataMap));
        return ct;
    }


    public Commit produceChildCommit(final Stage stageStatus, final String messageInfo) {
        /// TODO: 克隆父提交，根据Stage修改MetaData
        List<String> parentsT = new ArrayList<>();
        parentsT.add(this.id);
        Map<String, MetaData> cloneMetaData = this.metadataMap;
        final Set<File> added = stageStatus.getAddedFiles();
        final Set<File> removed = stageStatus.getRemovedFiles();
        final Set<File> modified = stageStatus.getModifiedFiles();
        /// 修改clone的结果，满足最新条件
        for (File addFile : added) {
            cloneMetaData.put(Repository.getRelativePathWit(addFile, Stage.stagesDir), new MetaData(addFile, this));
        }
        for (File rmFile : removed) {
            cloneMetaData.remove(Repository.getRelativePathWit(rmFile, Stage.stagesDir));
        }
        for (File mdFile : modified) {
            cloneMetaData.put(Repository.getRelativePathWit(mdFile, Stage.stagesDir), new MetaData(mdFile, this));
        }
        long timeT = System.currentTimeMillis();

        return Commit.createCommit(messageInfo, timeT, parentsT, cloneMetaData);
    }


    /**
     * TODO: 返回的是简写不是全部，前6个
     *
     * @return 该commit的特征值，即它的名称的简写
     */
    public String getSimpleID() {
        return id.substring(0, 7);
    }


    public String getFullID() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 返回按照log需求对该commit信息的字符串内容的构造
     * 到时候log只需要直接print
     *
     * @return String ordered message
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("commit ").append(id).append("\n");
        if (isMerge()) {
            /// 是 Merge ， 加上Merge信息
            sb.append("Merge:");
            for (String pareID : parents) {
                sb.append(" ").append(pareID);
            }
            sb.append("\n");
        }
        sb.append("Date: ").append(Utils.unixTimeFormatter(timeStamp)).append("\n");
        sb.append(message).append("\n"); /// 自带空行！！！

        return sb.toString();
    }
    /**
     *
     */
    public void log() {
        System.out.println(this.toString());
    }


    @Override
    public void dump() {
        // TODO
    }

    /**
     * 把本对象存为文件
     */
    public void save() {
        final File realFile = Utils.join(COMMIT_AREA, this.getFullID());
        Utils.writeObjectToFileWithFileNotExistFix(realFile, this);
    }

    /**
     *
     * @param id 全名
     * @return  Commit 对象
     *          null, if not exists
     */
    static Commit loadCommitByID(final String id) {
        final File commitFile = Utils.join(COMMIT_AREA, id);
        /// deal with fail error. if not exits, return null
        if (!commitFile.exists()) {
            return null;
        }
        return readObject(commitFile, Commit.class);
    }

    /**
     * 深度比较一个文件的信息，通过SHA1
     * @param filePath4Cmp
     * @return  404 - 没有这个文件
     *          400 - 请求与存储不一致
     *          200 - 完全一致
     */
    public int cmpWithFile(final File filePath4Cmp) {
        final MetaData trueVersion = getMetaDataByFilename(filePath4Cmp.getName());
        if (trueVersion == null) return 404;
        else {
            String sha1OfNew = Utils.sha1(filePath4Cmp);
            if (sha1OfNew.equals(trueVersion.getSHA1()) ) {
                return 200;
            } else {
                return 200;
            }
        }
    }

    /**
     * 通过查询metadataMap中有没有相对路径的string，查到说明有，没查到说明没
     * @param filePath 文件在CWD中的路径信息
     * @return
     */
    public boolean findByName(final File filePath) {

        return metadataMap.containsKey(Repository.getRelativePathWitCWD(filePath));
    }


}
