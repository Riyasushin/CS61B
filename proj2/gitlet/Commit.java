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

    public static File BLOB_PATH = Utils.join(Repository.GITLET_DIR, "blob");
    public static final File COMMIT_AREA = Utils.join(Repository.GITLET_DIR, "commits");
    /**
     * The hashValue and unique name of this commit
     */
    private String id;
    /**
     * The father commit of this commit, may not single
     */
    private List<Commit> parents;
    /**
     * The time of this commit
     */
    private long timeStamp;
    /**
     * The message of this Commit.
     */
    private String message;

    public class MetaData {
        private int version;
        private String blobName;
        private String sha1ID;
        private File sourceFile;

        public String getSHA1() {
            return sha1ID;
        }

        public int getVersion() {
            return version;
        }

        public String getName() {
            return blobName;
        }

        private void saveNewFileBlob() {
            final File blobPath = Utils.join(BLOB_PATH, blobName, String.valueOf(version));
            try {
                Files.copy(sourceFile.toPath(), blobPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Utils.message("Commit error: fail to create new blob");
            }
        }

        /// 保证，new MetaData 都是需要新的blob的，以此减少判断
        public MetaData(final File filepath, final Commit parentCommit) {
            blobName = filepath.getName();
            sourceFile = filepath;
            sha1ID = Utils.sha1((String.valueOf(filepath.lastModified()) + Utils.readContentsAsString(filepath)));


            /// 处理和上次commit 一样不一样
            if (parentCommit == null) {
                /// 特判root,其实没有必要，因为root根本不会调用这个
                version = 0;
            } else {
                final MetaData dataofOldVersion = parentCommit.getMetaDataByFilename(blobName);
                if (dataofOldVersion == null) {
                    /// add
                    version = 0;
                } else {
                    /// modify
                    version = dataofOldVersion.getVersion() + 1;
                }
            }
            saveNewFileBlob();
        }
    }

    private Map<String, MetaData> metadataMap;

    /// metadata TODO

    public MetaData getMetaDataByFilename(final String name) {
        return metadataMap.getOrDefault(name, null);
    }

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
    static Commit createCommit(final String messageT, final long timeT, final List<Commit> parentsT, final Map<String, MetaData> datas) {
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
        List<Commit> parentsT = new ArrayList<>();
        parentsT.add(this);
        Map<String, MetaData> cloneMetaData = this.metadataMap;
        final List<File> added = stageStatus.getAddedFiles();
        final List<File> removed = stageStatus.getRemovedFiles();
        final List<File> modified = stageStatus.getModifiedFiles();
        /// 修改clone的结果，满足最新条件
        for (File addFile : added) {
            cloneMetaData.put(addFile.getName(), new MetaData(addFile, this));
        }
        for (File rmFile : removed) {
            cloneMetaData.remove(rmFile.getName());
        }
        for (File mdFile : modified) {
            cloneMetaData.put(mdFile.getName(), new MetaData(mdFile, this));
        }
        long timeT = System.currentTimeMillis();

        return Commit.createCommit(messageInfo, timeT, parentsT, cloneMetaData);
    }


    /**
     * TODO: 返回的是简写不是全部，前6个
     *
     * @return 该commit的特征值，即它的名称的简写
     */
    public String getUniqueID() {
        return id;
    }

    /**
     * 返回按照log需求对该commit信息的字符串内容的构造
     * 到时候log只需要直接print
     *
     * @return String ordered message
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("===\n");
        sb.append("commit ").append(id).append("\n");
        if (isMerge()) {
            /// 是 Merge ， 加上Merge信息
            sb.append("Merge:");
            for (Commit pare : parents) {
                sb.append(" ").append(pare.getUniqueID());
            }
            sb.append("\n");
        }
        sb.append("Date: ").append(Utils.unixTimeFormatter(timeStamp)).append("\n");
        sb.append(message).append("\n");

        return sb.toString();
    }


    @Override
    public void dump() {
        // TODO
    }

    /**
     * 把本对象存为文件
     */
    public void save() {
        final File realFile = Utils.join(COMMIT_AREA, this.getUniqueID());
        Utils.writeObjectToFileWithFileNotExistFix(realFile, this);
    }

    static Commit readCommitByName(final String commitID) {
        /// 通过Commmit 的 id识别commit变量，并读取ta
        final File commitFile = Utils.join(COMMIT_AREA, commitID);
        return Utils.readObject(commitFile, Commit.class);
    }
}
