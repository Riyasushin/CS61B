package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stage implements Serializable, Dumpable {

    private List<String> addedFiles;
    private List<String> removedFiles;
    private List<String> modifiedFiles;

    private File stageAREA;
    private static String stageInformationName = "information.stage";

    private static void clearDirectory(final File dir) {
        if (!dir.isDirectory()) return;

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    clearDirectory(file);  // 递归删除子目录内容
                    file.delete();         // 删除空子目录
                } else {
                    file.delete();         // 删除文件
                }
            }
        }
    }
    public Stage(final File stageArea) {
        stageAREA = stageArea;
        clearDirectory(stageArea);

        modifiedFiles = new ArrayList<>();
        removedFiles = new ArrayList<>();
        addedFiles = new ArrayList<>();


        final File stageInfo = Utils.join(stageArea, stageInformationName);
        Utils.writeObject(stageInfo, this);
    }

    public void clear() {
        Stage.clearDirectory(this.stageAREA);
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

        final File stageInfo = Utils.join(stageAREA, stageInformationName);

    }


    /**
     * 返回目前Stage对象的各种信息，计划用于status命令
     * @return
     */
    public String getStatus() {

    }

    public boolean addFile(final String Filename) {

    }

    /**
     * 将某文件在暂存区中删除，并记录下来
     * @param Filename 要删除的文件的名称
     * @return
     */
    public boolean removeFileFromStage(final String Filename) {

    }

    /**
     * 得到当前工作区和最新一次提交的差距， how
     * @param latestCommit 最新一次提交
     * @return
     */
    public boolean checkForChangedFile(final Commit latestCommit) {

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

    }

    /**
     * 将目前对象存储到特定的stage文件中
     */
    public void save() {
        final File infoStage = Utils.join(stageAREA, stageInformationName);
        Utils.writeObject(infoStage, this);
    }

    /**
     *
     * @param stageDIR stage文件的路径，是绝对路径，不包括文件的名字，文件的名字由stage类进行管理
     * @return 从文件中读取到的stage信息，可修改
     */
    public static Stage loadStage(File stageDIR) {

    }
}
