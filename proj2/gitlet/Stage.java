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
    /// 新的文件，需要加入到commit中去
    private final Set<File> addedFiles;
    /// commit中有, 但是被用命令删除的
    private final Set<File> removedFiles;
    /// 修改了的commit过的
    private final Set<File> modifiedFiles;
    /// 没有被stage的修改 TODO
//    private final Set<File> modifiedNotStagedForCommit;
    /// not追踪的文件
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
//        modifiedNotStagedForCommit = new TreeSet<>(); TODO !!!!
        untrackedFile = new TreeSet<>();

        /// TODO  维护这些没有被保存的信息

        Utils.writeObject(STAGE_FILE, this);
    }

    public void clear() {
        /// 只清理缓存区的，不处理没有被stage的那些信息
        /// files
        for (File f : addedFiles) {
            f.delete();
        }
        for (File f : modifiedFiles) {
            f.delete();
        }
        this.addedFiles.clear();
        this.modifiedFiles.clear();
        this.removedFiles.clear();
        this.save();
    }

    /**
     * 返回暂存区是不是空的
     *
     * @return true: 暂存区四空的
     * false：暂存区有内容
     */
    public boolean isTidy() {

        /// 问题：有没有STAGE的信息，可以算tidy吗
        /// 可以，只要stage的区域是空的就可以
        return modifiedFiles.isEmpty() && removedFiles.isEmpty() && addedFiles.isEmpty();
    }

    /**
     * 看是不是全部都追踪过了，一定要都有副本了
     *
     * @return
     */
    public boolean isDeepTidy() {
        /// TODO
//        return modifiedNotStagedForCommit.isEmpty();
        return true;
    }

    public void checkUntracked(final Commit headCommit, final File curWorkDir) {
        /// TODO
        /// here check, not use stored data
        /// TODO ,check  plainFilenamesIn impl
        /// TODO !!!!! Wrong algorithmn, cmp sha1!!!!
        final Set<String> commitedFiles = headCommit.getMetaDataNameList();
        List<String> stagedFiles = Utils.plainFilenamesIn(stagesDir);
        List<String> workingFiles = Utils.plainFilenamesIn(Repository.CWD);

        if (workingFiles == null) {
            return;
        }
        for (String filePathRelative : workingFiles) {
            if (stagedFiles != null && stagedFiles.contains(filePathRelative)) {
                /// added or modified
                if (commitedFiles.contains(filePathRelative)) {
                    modifiedFiles.add(Utils.join(stagesDir, filePathRelative));
                } else {
                    addedFiles.add(Utils.join(stagesDir, filePathRelative));
                }
                /// TODO  can i do like this?
                stagedFiles.remove(filePathRelative);
            } else {
                /// untracked
                untrackedFile.add(Utils.join(stagesDir, filePathRelative));
            }
            if (commitedFiles != null && commitedFiles.contains(filePathRelative)) {
                removedFiles.add(Utils.join(stagesDir, filePathRelative));
                commitedFiles.remove(filePathRelative);
            }
        }
        if (stagedFiles != null) {
            /// ???? forget to delete when working before
            while (!stagedFiles.isEmpty()) {
                final String fileTrash = stagedFiles.get(0);
                Utils.restrictedDelete(Utils.join(stagesDir, fileTrash));
                stagedFiles.remove(fileTrash);
            }
        }


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
     * @param filePath  要加入的文件在工作区的路径
     * @param curCommit 当前的commit，用于查找此文件是否被修改\为增加\相同
     */
    public void tryAdd(final File filePath, final Commit curCommit) {

        /// Staging an already-staged file overwrites the previous entry in the staging area with the new contents.

        /// Staging an already-staged file overwrites the previous entry in the staging area with the new contents.

        /// remove it from the staging area if it is already there (as can happen when a file is changed, added, and then changed back to it’s original version)
        MetaData fileData = curCommit.getMetaDataByFilename(Repository.getRelativePathWitCWD(filePath));
        final File file4Add = Utils.join(stagesDir, Repository.getRelativePathWitCWD(filePath));
        if (fileData != null) {
            /// commit中有老版本的ta
            if (fileData.getSHA1().equals(Utils.sha1(filePath))) {
                /// 修改回原版本
                addedFiles.remove(file4Add); /// 如果没有的话，set会自动返回，有的话被删除
                Utils.restrictedDelete(file4Add);
            } else {
                /// 之后，存了，与原版不同，看stage中有没有了,有的话进入modify区域
                if (modifiedFiles.contains(file4Add)) {
                    if (!Utils.sha1(file4Add).equals(Utils.sha1(filePath))) {
                        /// 相同不用管，现在不同了
                        /// TODO  泛化，抽象一层
                        modifiedFiles.add(file4Add);
                        Utils.copyFile(Repository.CWD.getAbsolutePath(), stagesDir.getAbsolutePath(), Repository.getRelativePathWitCWD(filePath));
                    }
                }
            }
        } else {
            /// 看stage区
            if (addedFiles.contains(file4Add)) {
                /// stage过，看新或旧
                if (!Utils.sha1(file4Add).equals(Utils.sha1(filePath))) {
                    /// 相同不用管，现在不同了
                    /// TODO  泛化，抽象一层
                    addedFiles.add(file4Add);
                    Utils.copyFile(Repository.CWD.getAbsolutePath(), stagesDir.getAbsolutePath(), Repository.getRelativePathWitCWD(filePath));
                }
            } else {
                /// 没有stage过，纯纯新的
                addedFiles.add(file4Add);
                Utils.copyFile(Repository.CWD.getAbsolutePath(), stagesDir.getAbsolutePath(), Repository.getRelativePathWitCWD(filePath));
            }
        }

        this.save();
    }

    public boolean stagedForAdd(final File filePath) {
        return addedFiles.contains(filePath) || modifiedFiles.contains(filePath);
    }

    public void tryForgetFile(final File file2ForgetInCWD) {
        final File filePathInStage = Utils.join(stagesDir, Repository.getRelativePathWitCWD(file2ForgetInCWD));

        addedFiles.remove(filePathInStage);
        modifiedFiles.remove(filePathInStage);
        Utils.restrictedDelete(filePathInStage);

        this.save();
    }

    /**
     * 将某文件在暂存区中删除，并记录下来
     *
     * @param filePath 要删除的文件的名称
     * @return
     */
    public void removeFileFromStage(final File filePath) {
        addedFiles.remove(filePath);
//        modifiedFiles.remove(filePath);
        final File addedFileInStage = Utils.join(stagesDir, Repository.getRelativePathWitCWD(filePath));
        Utils.restrictedDelete(addedFileInStage);
        this.save();
    }

    public void add2RmList(final File filePath) {
        removedFiles.add(filePath);
        /// addedFile里面只可能是新加入的，这个被调用的前提是commit里面有，如果存在一定在modified里面
        if ( modifiedFiles.contains(filePath)) {
            final File addedFileInStage = Utils.join(stagesDir, Repository.getRelativePathWitCWD(filePath));
            Utils.restrictedDelete(addedFileInStage);
        }
        /// 记得保存为文件
        this.save();
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
