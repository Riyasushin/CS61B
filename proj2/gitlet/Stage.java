package gitlet;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Stage implements Serializable, Dumpable {

    public static final File STAGE_FILE = Utils.join(Repository.GITLET_DIR, "STAGE");

    /// 都有：增加的，删除的，修改的，和上一次一样的
    /// 被记录的没被记录的

    /// 引用不可变，但对象内部状态可变
    private final Set<File> addedFiles;
    private final Set<File> removedFiles;
    private final Set<File> modifiedFiles;
    private final Set<File> sameList;
    private final Set<File> addedFilesNotStaged;
    private final Set<File> removedFilesNotStaged;
    private final Set<File> modifiedFilesNotStaged;



    public Stage() {

        modifiedFiles = new TreeSet<>();
        removedFiles = new TreeSet<>();
        addedFiles = new TreeSet<>();
        sameList = new TreeSet<>();

        addedFilesNotStaged = new TreeSet<>();
        removedFilesNotStaged = new TreeSet<>();
        modifiedFilesNotStaged = new TreeSet<>();
        /// TODO  维护这些没有被保存的信息

        Utils.writeObject(STAGE_FILE, this);
    }

    public void clear() {
        /// 只清理缓存区的，不处理没有被stage的那些信息
        this.addedFiles.clear();
        this.modifiedFiles.clear();
        this.removedFiles.clear();

        this.save();
    }

    /**
     * 返回暂存区是不是空的
     * @return true: 暂存区四空的
     *          false：暂存区有内容
     */
    public boolean isTidy() {

        /// 问题：有没有STAGE的信息，可以算tidy吗
        /// 可以，只要stage的区域是空的就可以
        return modifiedFiles.isEmpty() && removedFiles.isEmpty() && addedFiles.isEmpty();
    }

    /**
     * 看是不是全部都追踪过了，一定要都有副本了
     * @return
     */
    public boolean isDeepTidy() {

    }


    /**
     * 返回目前Stage对象的各种信息，计划用于status命令
     * @return
     */
    public String getStatus() {
        /// TODO
        return "";
    }

    public void checkAll(final File workingDir, final Commit curCommit) {
        /// 多的
        /// 改的
        /// 删除了的
        /// TODO

    }


    /*

     switch (headCommit.cmpWithFile(curFilePosition)) {
            case 404: {
                stageArea.addNewFile(curFilePosition);
                break;
            }
            case 200: {
                stageArea.add2SameList(curFilePosition);
                break;
            }
            case 400: {
                stageArea.addModifiedFile(curFilePosition);
                break;
            }
        }
     */
    public void tryContain(final File filePath, final Commit curCommit) {

        if (sameList.contains(filePath)) {
            return;
        } else if (addedFilesNotStaged.contains(filePath)) {
            addedFilesNotStaged.remove(filePath);
            addedFiles.add(filePath);
        } else if (modifiedFilesNotStaged.contains(filePath)) {
            modifiedFilesNotStaged.remove(filePath);
            modifiedFiles.add(filePath);
        }

    }

    public boolean stagedForAdd(final File filePath) {
        return addedFiles.contains(filePath) || modifiedFiles.contains(filePath);
    }

    public boolean inSameList(final File filePath) {
        return sameList.contains(filePath);
    }
    public void add2SameList(final File filePath) {

        sameList.add(filePath);
    }
    public void addNewFile(final File filePath) {
        addedFiles.add(filePath);
    }
    public void addModifiedFile(final File filePath) {
        modifiedFiles.add(filePath);
    }


    /**
     * 将某文件在暂存区中删除，并记录下来
     * @param filePath 要删除的文件的名称
     * @return
     */
    public void removeFileFromStage(final File filePath) {
        addedFiles.remove(filePath);
        modifiedFiles.remove(filePath);
    }

    public void add2RmList(final File filePath) {
        removedFiles.remove(filePath);
    }

    @Override
    public void dump() {

    }

    /**
     * 把Stage对象中暂存信息传递为特定的格式，为了便于打印,这里直接按照要求输出的分行的结果，'\n'用于分行
     * @return
     */
    @Override
    public String toString() {
        /// TODO
        return "";
    }

    /**
     * 将目前对象存储到特定的stage文件中
     */
    public void save() {

        Utils.writeObjectToFileWithFileNotExistFix(STAGE_FILE, this);
    }

    /**
     *
     * @return 从文件中读取到的stage信息，可修改
     */
    public static Stage loadStage() {
        return Utils.readObject(STAGE_FILE, Stage.class);
    }

    public Set<File> getAddedFiles() {
        return addedFiles;
    }

    public Set<File> getRemovedFiles() {
        return removedFiles;
    }

    public Set<File> getModifiedFiles() {
        return modifiedFiles;
    }
}
