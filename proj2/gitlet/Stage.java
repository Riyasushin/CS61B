package gitlet;

import java.io.Serializable;
import java.util.List;

public class Stage implements Serializable, Dumpable {

    private List<String> addedFiles;
    private List<String> removedFiles;
    private List<String> modifiedFiles;

    private boolean Tidy = false;

    public Stage() {
        Tidy = true; /// when init, it`s tidy
    }

    /**
     * 返回暂存区是不是空的
     * @return true: 暂存区四空的
     */
    public boolean isTidy() {
        return Tidy;
    }


    public static void checkStatus(Stage mystage) {

    }

    @Override
    public void dump() {

    }

    @Override
    public String toString() {

    }
}
