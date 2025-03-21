package gitlet;
import org.junit.Test;

import java.io.File;

public class test {

    public static void main2(String[] args) {
//        System.out.println(Utils.sha1(""));
        /// 结果，不能是null
        final File ff = Utils.join(Repository.CWD, "xx", "aa/a.x");
        Utils.message(Repository.getRelativePathWitCWD(ff));
    }
}
