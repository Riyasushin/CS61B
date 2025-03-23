package gitlet;
import org.junit.Test;

import java.io.File;

import static gitlet.Commit.COMMIT_AREA;
import static gitlet.Repository.CWD;

public class test {

    public static void main(String[] args) {
//        System.out.println(Utils.sha1(""));
        /// 结果，不能是null
//        final File ff = Utils.join(Repository.CWD, "xx", "aa/a.x");
//        Utils.message(Repository.getRelativePathWitCWD(ff));
//        System.out.print((Utils.plainFilenamesIn(CWD)).toString());
        final File realFile = Utils.join(CWD, "59f2f571c8e41e9c62135ad7d6c81fe4b13384af");
        System.out.println(realFile.toString());
        Commit init = Commit.createInitCommit();
        init.save();
//        Utils.writeObjectToFileWithFileNotExistFix(realFile, Commit.createInitCommit());
    }
}
