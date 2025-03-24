package gitlet;


//import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.util.*;

import static gitlet.Utils.readObject;


/**
 * Represents a gitlet commit object.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  Commit is immutable!!!!!
 *  @author RiJoshin
 */
public class Commit implements  Dumpable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    public static final File COMMIT_AREA = Utils.join(Repository.GITLET_DIR, "commits");
    /**
     * The hashValue and unique name of this commit
     */
    private String id;
    private static final int ID_SHORT_LENGTH = 8;
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

    private int layer;

    /**
     * String Repository.getRelativePathWitCWD(addFile),相对于CWD或者stages的相对路径
     */
    private Map<String, MetaData> metadataMap;

    /**
     * get all Commits
     * @return a TreeSet of commit files
     */
    public static Set<File> getAllCommits() {
        Set<File> commits = new TreeSet<>();
        File[] commitSubDirs = COMMIT_AREA.listFiles();
        if (commitSubDirs != null) {
            for (final File subDir : commitSubDirs) {
                if (subDir.isDirectory()) {
                    File[] commitFiles = subDir.listFiles();
                    if (commitFiles == null) {
                        continue;
                    }
                    commits.addAll(Arrays.asList(commitFiles));
                }
            }
        }


        return commits;
    }

    public static Commit findMergeBase(Commit commit1, Commit commit2) {
        Map<Commit, Integer> depthMap1 = getDepthMap(commit1);
        Map<Commit, Integer> depthMap2 = getDepthMap(commit2);

        Set<Commit> commonAncestors = new HashSet<>(depthMap1.keySet());
        commonAncestors.retainAll(depthMap2.keySet());

        if (commonAncestors.isEmpty()) {
            return null; // 没有公共祖先
        }

        // 找到距离两个提交最近的公共祖先（按深度之和最小）
        return commonAncestors.stream().min(
                Comparator.comparingInt(commit -> depthMap1.get(commit) + depthMap2.get(commit))).orElse(null);
    }

    private static Map<Commit, Integer> getDepthMap(Commit commit) {
        Map<Commit, Integer> depthMap = new HashMap<>();
        Queue<Commit> queue = new LinkedList<>();
        queue.add(commit);
        depthMap.put(commit, 0);

        while (!queue.isEmpty()) {
            Commit current = queue.poll();
            if (current.parents == null) {
                continue;
            }
            for (final String parentID : current.parents) {
                Commit parent = Commit.loadCommitByID(parentID);
                if (!depthMap.containsKey(parent)) {
                    depthMap.put(parent, depthMap.get(current) + 1);
                    queue.add(parent);
                }
            }
        }

        return depthMap;
    }

    /**
     * fine the common ancestor of A and B
     * @return  the common ancestor of A and B
     */
    public static Commit findSplitPoint(Commit A, Commit B) {
        return findMergeBase(A, B);
    }

    /// metadata

    /**
     * relative path (str) -> data
     * @param name name should be the relative path to (CWD / storageDir)
     * @return null if not find
     */
    public MetaData getMetaDataByFilename(final String name) {
        return metadataMap.getOrDefault(name, null);
    }

    /**
     *
     * @return empty Set if
     */
    public Set<String> getMetaDataNameList() {
        /// ATTENTION!!!
        return metadataMap.keySet();
    }

    /**
     * 返回是否是merge commit, 这由父亲节点的数量判断，多余一个就是merge
     */
    public boolean isMerge() {
        if (parents == null) {
            return false;
        } else {
            return parents.size() > 1;
        }
    }

    /**
     *
     * @return True if this is root Commit; false if it has parent.
     */
    public boolean hasDad() {
        return !(parents == null);  /// HARD nullptr!!!
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

    @Override
    public Commit clone() {
        Commit ct = new Commit();
        ct.id = this.id;
        ct.parents = this.parents;
        ct.timeStamp = this.timeStamp;
        ct.message = this.message;
        ct.metadataMap = Map.copyOf(metadataMap);
        return ct;
    }

    /**
     * 返回Init时候需要的commit
     *
     * @return
     */
    static Commit createInitCommit() {
        Map<String, MetaData> tidyDataSet = new TreeMap<>();
        return Commit.createCommit(
                "initial commit", 0, null, tidyDataSet, 0);
    }

    /// 根据提供的信息返回关联好的commit
    static Commit createCommit(final String messageT, final long timeT,
                               final List<String> parentsT, final Map<String, MetaData> datas,
                               final int layerDepth) {
        Commit ct = new Commit();
        ct.parents = (parentsT == null ? null : new ArrayList<>(parentsT));
        ct.timeStamp = timeT;
        ct.message = (messageT == null? "" : messageT); /// 根据commit要求，一定不是null !
        ct.metadataMap = Map.copyOf(datas);
        ct.layer = layerDepth;

        ct.id = Utils.sha1((ct.parents == null ? " " : ct.parents.toString()),
                String.valueOf(ct.timeStamp), ct.message, (ct.metadataMap == null ? " " : ct.metadataMap.toString()));
        return ct;
    }


    public Commit produceChildCommit(final Stage stageStatus, final String messageInfo) {
        List<String> parentsT = new ArrayList<>();
        parentsT.add(this.id);
        Map<String, MetaData> cloneMetaData = new TreeMap<>(Map.copyOf(this.metadataMap));
        final Set<File> added = stageStatus.getAddedFiles();
        final Set<File> removed = stageStatus.getRemovedFiles();
        final Set<File> modified = stageStatus.getModifiedFiles();
        /// 修改clone的结果，满足最新条件
        for (File addFile : added) {
            cloneMetaData.put(Repository.getRelativePathWit(addFile, Stage.STAGESDIR), new MetaData(addFile, this));
        }
        for (File rmFile : removed) {
            cloneMetaData.remove(Repository.getRelativePathWit(rmFile, Stage.STAGESDIR));
        }
        for (File mdFile : modified) {
            cloneMetaData.put(Repository.getRelativePathWit(mdFile, Stage.STAGESDIR), new MetaData(mdFile, this));
        }
        long timeT = System.currentTimeMillis();


        return Commit.createCommit(messageInfo, timeT, parentsT, cloneMetaData, layer + 1);
    }

    public String getFullID() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void addMergeParent(final String mergeParentID) {
        this.parents.add(mergeParentID);
        this.save();
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
                sb.append(" ").append(pareID, 0, 7);
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
    }

    /**
     * 把本对象存为文件
     */
    public void save() {
        final File commitFileSubDir = Utils.join(COMMIT_AREA, this.getFullID().substring(0, ID_SHORT_LENGTH));
        if (!commitFileSubDir.exists()) {
            commitFileSubDir.mkdirs();
        }
        final File realFile = Utils.join(commitFileSubDir, this.getFullID());
        Utils.writeObjectToFileWithFileNotExistFix(realFile, this);
    }

    /**
     *
     * @param id 全名
     * @return  Commit 对象
     *          null, if not exists
     */
    static Commit loadCommitByID(final String id) {
        final File commitFileSubDir = Utils.join(COMMIT_AREA, id.substring(0, ID_SHORT_LENGTH));
        if (!commitFileSubDir.exists()) {
            return null;
        }
        if (id.length() == ID_SHORT_LENGTH) {
            if (commitFileSubDir.isDirectory() && commitFileSubDir.list().length == 1) {
                return readObject(commitFileSubDir.listFiles()[0], Commit.class);
            } else {
                return null;
            }
        } else {
            final File commitFile = Utils.join(commitFileSubDir, id);
            if (!commitFile.exists())  {
                return null;
            }
            return readObject(commitFile, Commit.class);
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


    /**
     * using stored blob rollback the workingDirectory
     * @param workingDir CWD
     */
    public void rollBack(final File workingDir) {
        /// clear the dir totally expect .gitlet
        List<String> fileList = Utils.plainFilenamesIn(workingDir);
        for (String fileName : fileList) {
            Utils.restrictedDelete(Utils.join(workingDir, fileName));
        }

        for (MetaData value : metadataMap.values()) {
            final File distFile = Utils.join(value.getName());
            final File srcFile = value.getFilePathOfBlob();
            Utils.copyFileFromSrcToDist(srcFile, distFile);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Commit other = (Commit) o;

        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return (message + timeStamp + id).hashCode();
    }
}
