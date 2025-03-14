package gitlet;

import org.apache.commons.math3.analysis.solvers.RiddersSolver;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Stage implements Serializable, Dumpable {

    public static final File STAGE_FILE = Utils.join(Repository.GITLET_DIR, "STAGE");
    public static final File stagesDir = Utils.join(Repository.GITLET_DIR, "stages");

/// === Staged Files === add, modify
/// === Removed Files ===
/// === Modifications Not Staged For Commit ===
/// === Untracked Files ===

    /// 这里的File都是staging区域暂存的 File
    private final Set<File> addedFiles;/// 新的文件，需要加入到commit中去
    private final Set<File> removedFiles; /// commit中有, 但是被用命令删除的
    private final Set<File> modifiedFiles; /// 修改了的，也被add了
    private final Set<File> modifiedNotStagedForCommit; /// 被修改，但是没有被保存的
    private final Set<File> sameList; /// 完全相同的，用于减少运算；似乎维护起来很困难
    private final Set<File> untrackedFile;


    /*
A file in the working directory is “modified but not staged” if it is

Tracked in the current commit, changed in the working directory, but not staged; or
Staged for addition, but with different contents than in the working directory; or
Staged for addition, but deleted in the working directory; or
Not staged for removal, but tracked in the current commit and deleted from the working directory.
     */

    public Stage() {

        modifiedFiles = new TreeSet<>();
        removedFiles = new TreeSet<>();
        addedFiles = new TreeSet<>();
        sameList = new TreeSet<>();

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

    /**
     *
     * @param filePath 要加入的文件在工作区的路径
     * @param curCommit 当前的commit，用于查找此文件是否被修改\为增加\相同
     */
    public void tryAdd(final File filePath, final Commit curCommit) {

        /// Staging an already-staged file overwrites the previous entry in the staging area with the new contents.

        /// Staging an already-staged file overwrites the previous entry in the staging area with the new contents.

        /// remove it from the staging area if it is already there (as can happen when a file is changed, added, and then changed back to it’s original version)
        MetaData fileData = curCommit.getMetaDataByFilename(Repository.getRelativePathWitCWD(filePath));
        final File hasAddedFile = Utils.join(stagesDir, Repository.getRelativePathWitCWD(filePath));
        if (fileData == null) {
            /// 船新版本

            /// 重复添加，自动忽略
            /// TODO  
            addedFiles.add(hasAddedFile);
            Utils.copyFile(Repository.CWD.getAbsolutePath(), stagesDir.getAbsolutePath(), Repository.getRelativePathWitCWD(filePath));

        } else {
            /// commit中有老版本的ta
            if (fileData.getSHA1().equals(Utils.sha1(filePath))) {
                /// 修改回原版本?
                addedFiles.remove(hasAddedFile);
                Utils.restrictedDelete(hasAddedFile);
            }
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
        /// TODO
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
