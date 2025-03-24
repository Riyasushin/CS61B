package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Stage implements Serializable, Dumpable {

    public static final File STAGE_FILE = Utils.join(Repository.GITLET_DIR, "STAGE");
    public static final File STAGESDIR = Utils.join(Repository.GITLET_DIR, "stages");

    private Set<File> inCommitAndStaged;
    private Set<File> notInCommitAndStaged;
    private Set<File> removed;
    private Set<File> removedButNotStaged;
    private Set<File> modifiedNotStaged;
    private Set<File> untracked;

    public Stage() {
        inCommitAndStaged = new TreeSet<>();
        notInCommitAndStaged = new TreeSet<>();
        removed = new TreeSet<>();
        removedButNotStaged = new TreeSet<>();
        modifiedNotStaged = new TreeSet<>();
        untracked = new TreeSet<>();

        save();
    }

    /**
     * save stage object to the file STAGE_FILE
     */
    public void save() {
        Utils.writeObjectToFileWithFileNotExistFix(STAGE_FILE, this);
    }

    @Override
    public void dump() {
    }

    /**
     *
     * @return Stage st
     */
    public static Stage loadStage() {
        return Utils.readObject(STAGE_FILE, Stage.class);
    }





    public Set<File> getAddedFiles() {
        return notInCommitAndStaged;
    }

    public Set<File> getRemovedFiles() {
        return removed;
    }

    public Set<File> getModifiedFiles() {
        return inCommitAndStaged;
    }


    public Set<File> getModifiedNotStaged() {
        return modifiedNotStaged;
    }

    public Set<File> getRemovedNotStaged() {
        return removedButNotStaged;
    }

    public Set<File> getUntracked() {
        return untracked;
    }


    /**
     * remove from stage,
     * @param curFileWaitingRemoveFromStage as name
     */
    public boolean removeFromStage(final File curFileWaitingRemoveFromStage) {
        final String fileRelativePathStr = Repository.getRelativePathWitCWD(curFileWaitingRemoveFromStage);
        final File inStageFile = Utils.join(STAGESDIR, fileRelativePathStr);
        if (inCommitAndStaged.contains(inStageFile) || notInCommitAndStaged.contains(inStageFile)) {
            /// currently staged for addition.
            inCommitAndStaged.remove(inStageFile);
            notInCommitAndStaged.remove(inStageFile);
            untracked.add(inStageFile);

            save();
            return true;
        } else {
            return false;
        }
    }

    /**
     *  Adds a copy of the file as it currently exists to the staging area
     *
     *  If the current working version of the file is identical to the version in the current commit,
     *  do not stage it to be added, and remove it from the staging area if it is already there
     *
     * @param curFilePosition filePath in CWD
     * @param headCommit headCommit,
     */
    public void add(final File curFilePosition, final Commit headCommit) {
        final String fileRelativePathStr = Repository.getRelativePathWitCWD(curFilePosition);
        MetaData fileDataInCommit = headCommit.getMetaDataByFilename(fileRelativePathStr);
        final File inStageFile = Utils.join(STAGESDIR, fileRelativePathStr);

        modifiedNotStaged.remove(inStageFile);
        untracked.remove(inStageFile);

        if (fileDataInCommit == null) {
            /// not in commit
            notInCommitAndStaged.add(inStageFile);
            Utils.copyFileFromSrcToDist(curFilePosition, inStageFile);
        } else {
            /// in commit
            final String commitSHA1 = fileDataInCommit.getSHA1();
            if (commitSHA1.equals(Utils.sha1(curFilePosition))) {
                /// roll backed
                notInCommitAndStaged.remove(inStageFile);
                removed.remove(inStageFile);
                Utils.restrictedDelete(inStageFile);
            } else {
                inCommitAndStaged.add(inStageFile);
                Utils.copyFileFromSrcToDist(curFilePosition, inStageFile);
            }

        }

        save();
    }


    /**
     *
     * @param curFilePath the name of file to be added to remove list and removed from commit
     */
    public void addToRemove(final File curFilePath) {
        final String fileRelativePathStr = Repository.getRelativePathWitCWD(curFilePath);
        final File inStageFile = Utils.join(STAGESDIR, fileRelativePathStr);
        removed.add(inStageFile);

        save();
    }

    private void checkFileInStageChangedOrDeletedCmpWithCWD(final Set<File> setToCheck, final File cwd) {
        Iterator<File> iterator = setToCheck.iterator();
        while (iterator.hasNext()) {
            File fileInStage = iterator.next();
            final String fileRelativePathStr = Repository.getRelativePathWit(fileInStage, STAGESDIR);
            final File fileInCWD = Utils.join(cwd, fileRelativePathStr);

            if (!fileInCWD.exists()) {
                // deletedNotStaged
                removedButNotStaged.add(fileInStage);
                Utils.restrictedDelete(fileInStage);
                iterator.remove(); // 使用迭代器移除
            } else {
                // modifiedNotStaged
                final String cwdSHA1 = Utils.sha1(fileInCWD);
                final String stageSHA1 = Utils.sha1(fileInStage);
                if (!cwdSHA1.equals(stageSHA1)) {
                    modifiedNotStaged.add(fileInStage);
                    Utils.restrictedDelete(fileInStage);
                    iterator.remove(); // 使用迭代器移除
                }
            }
        }
    }
    /**
     * check deletedNotStaged and modifiedNotStaged
     * @param headCommit as name
     * @param cwd current working directory
     */
    public void checkStatus(final Commit headCommit, final File cwd) {
        /// if sth in stage is changed
        checkFileInStageChangedOrDeletedCmpWithCWD(inCommitAndStaged, cwd);
        checkFileInStageChangedOrDeletedCmpWithCWD(notInCommitAndStaged, cwd);

        /// if sth in CWD is deleted(In commit but not in cwd)
        final Set<String> cwdNameList = new TreeSet<>(Utils.plainFilenamesInWithNullDull(cwd));
        final Set<String> commitFileList = headCommit.getMetaDataNameList();
        for (final String relativePathStr : commitFileList) {
            if (!cwdNameList.contains(relativePathStr)) {
                removedButNotStaged.add(Utils.join(STAGESDIR, relativePathStr));
            }
        }

        save();
    }

    public boolean isTidy() {
        return (modifiedNotStaged.isEmpty() && removedButNotStaged.isEmpty());
    }

    public boolean canCommit() {
        return !(inCommitAndStaged.isEmpty() && notInCommitAndStaged.isEmpty() && removed.isEmpty());
    }

    /**
     * clear inCommitAndStaged and notInCommitAndStaged and removed
     */
    public void clearCommited() {
        Utils.clearDir(STAGESDIR);
        inCommitAndStaged.clear();
        notInCommitAndStaged.clear();
        removed.clear();
        save();
    }

    /**
     * forget everything
     */
    public void clearTotally() {
        clearCommited();
        modifiedNotStaged.clear();
        removedButNotStaged.clear();
        untracked.clear();

        save();
    }

    /**
     * check if can checkoutBranch
     * @return
     */
    public boolean canCheckoutBranch() {
            return !canCommit();
    }

}