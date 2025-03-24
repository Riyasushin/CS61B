package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/// HARD :: here Dumpable lost my time!!!
public class MetaData implements Dumpable {

    public static File BLOBPATH = Utils.join(Repository.GITLET_DIR, "blob");

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
        final File blobPath = Utils.join(BLOBPATH, blobName, String.valueOf(version));
        if (!blobPath.exists()) {
            blobPath.mkdirs();
        }
        try {
            Files.copy(sourceFile.toPath(), blobPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Utils.message("Commit error: fail to create new blob");
        }
    }

    public File getFilePathOfBlob() {
        return Utils.join(BLOBPATH, blobName, String.valueOf(version));
    }

    /// 保证，new MetaData 都是需要新的blob的，以此减少判断
    public MetaData(final File filepath, final Commit parentCommit) {
        blobName = filepath.getName();
        sourceFile = filepath;
        sha1ID = Utils.sha1(filepath);

        final File blobDirPath = Utils.join(BLOBPATH, blobName);
        int lastVersion = -1;
        if (blobDirPath.exists()) {
            List<String> oldVersions = Utils.plainFilenamesIn(blobDirPath);
            if (oldVersions != null) {
                lastVersion = Utils.findMaxNumber(oldVersions);
            }
        }

        version = lastVersion + 1;
        saveNewFileBlob();
    }

    @Override
    public String toString() {
        return " " + getSHA1() + " " + blobName + " " + String.valueOf(version) + " " + sourceFile.toString();
    }

    @Override
    public void dump() {

    }
}
