package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class MetaData {

    public static File BLOB_PATH = Utils.join(Repository.GITLET_DIR, "blob");

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
        final File blobPath = Utils.join(BLOB_PATH, blobName, String.valueOf(version));
        try {
            Files.copy(sourceFile.toPath(), blobPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Utils.message("Commit error: fail to create new blob");
        }
    }

    public File getFilePathOfBlob() {
        return Utils.join(BLOB_PATH, blobName, String.valueOf(version));
    }

    /// 保证，new MetaData 都是需要新的blob的，以此减少判断
    public MetaData(final File filepath, final Commit parentCommit) {
        blobName = filepath.getName();
        sourceFile = filepath;
        sha1ID = Utils.sha1(filepath);


        /// 处理和上次commit 一样不一样
        if (parentCommit == null) {
            /// 特判root,其实没有必要，因为root根本不会调用这个
            version = 0;
        } else {
            final MetaData dataofOldVersion = parentCommit.getMetaDataByFilename(blobName);
            if (dataofOldVersion == null) {
                /// add
                version = 0;
            } else {
                /// modify
                version = dataofOldVersion.getVersion() + 1;
            }
        }
        saveNewFileBlob();
    }
}
