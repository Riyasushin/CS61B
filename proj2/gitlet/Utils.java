package gitlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/** Assorted utilities.
 *
 * Give this file a good read as it provides several useful utility functions
 * to save you some time.
 *
 *  @author P. N. Hilfinger
 */
class Utils {

    /** The length of a complete SHA-1 UID as a hexadecimal numeral. */
    static final int UID_LENGTH = 40;

    /* SHA-1 HASH VALUES. */

    /** Returns the SHA-1 hash of the concatenation of VALS, which may
     *  be any mixture of byte arrays and Strings. */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    /// here !!!! ONLY STRING!!!
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /** Returns the SHA-1 hash of the concatenation of the strings in
     *  VALS. */
    static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* Added by me, for easier sha1 */

    /**
     * 根据文件的路径，读取文件的全文，得出sha1
     * ps:我觉得这一步太对了，只用改这里，别的地方都不用动！
     * @param filepath 要作为metadata的文件的路径
     * @return
     */
    static String sha1(final File filepath) {
        return Utils.sha1(Utils.readContentsAsString(filepath));
    }

    /* FILE DELETION */

    /** Deletes FILE if it exists and is not a directory.  Returns true
     *  if FILE was deleted, and false otherwise.  Refuses to delete FILE
     *  and throws IllegalArgumentException unless the directory designated by
     *  FILE also contains a directory named .gitlet. */
    static boolean restrictedDeleteUSER(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }
    static boolean restrictedDelete(File file) {
        // 检查文件路径上是否有 .gitlet 文件夹
        File currentDir = file.getParentFile();
        boolean gitletFound = false;

        while (currentDir != null) {
            File gitletDir = new File(currentDir, ".gitlet");
            if (gitletDir.isDirectory()) {
                gitletFound = true;
                break;
            }
            currentDir = currentDir.getParentFile();
        }

        if (!gitletFound) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     *
     */

    /** Deletes the file named FILE if it exists and is not a directory.
     *  Returns true if FILE was deleted, and false otherwise.  Refuses
     *  to delete FILE and throws IllegalArgumentException unless the
     *  directory designated by FILE also contains a directory named .gitlet. */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* READING AND WRITING FILE CONTENTS */

    /** Return the entire contents of FILE as a byte array.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return the entire contents of FILE as a String.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /** Write the result of concatenating the bytes in CONTENTS to FILE,
     *  creating or overwriting it as needed.  Each object in CONTENTS may be
     *  either a String or a byte array.  Throws IllegalArgumentException
     *  in case of problems. */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     *  Throws IllegalArgumentException in case of problems. */
    static<T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Write OBJ to FILE. */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /* DIRECTORIES */

    /** Filter out all but plain files. */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /** Returns a list of the names of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    static List<String> plainFilenamesInWithNullDull(File dir) {
        List<String> tmpRes = plainFilenamesIn(dir);
        if (tmpRes == null) {
            return new ArrayList<>();
        } else {
            return tmpRes;
        }
    }

    /** Returns a list of the names of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* OTHER FILE UTILITIES */

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths . #get(String, String[])}
     *  method. */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths#get(String, String[])}
     *  method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /* SERIALIZATION UTILITIES */

    /** Returns a byte array containing the serialized contents of OBJ. */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }


    /* MY FILE UTILITIES */

    /**
     * 从sourceRoot/relativePath 复制到 targetRoot/relativePath
     * 这是覆盖，如果有，先删除再复制 TOD
     * @param sourceRoot dir
     * @param targetRoot dir
     * @param relativePath 相对于sourceRoot的相对路径
     */
    static void copyFile(String sourceRoot, String targetRoot, String relativePath)  {
        Path sourcePath = Paths.get(sourceRoot, relativePath);
        Path targetPath = Paths.get(targetRoot, relativePath);

        // 确保目标目录存在
        File targetFile = targetPath.toFile();
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        // 复制文件
        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            message("Fail in copy file from root %s to root %s, with relative path: %s",
                        sourceRoot, targetRoot, relativePath);
            throw new RuntimeException(e);
        }
    }

    static void moveOroverwriteFileFromSrcToDist(final File src, final File dist) {
        if (src == null || dist == null) {
            throw new IllegalArgumentException("Source and destination files cannot be null.");
        }

        // Convert File objects to Path objects
        final Path srcPath = src.toPath();
        final Path distPath = dist.toPath();

        try {
            // Move the file from src to dist, overwriting if it already exists
            Files.move(srcPath, distPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("An error occurred while moving the file: " + e.getMessage());
        }
    }

    static void copyFileFromSrcToDist(final File src, final File dist) {
        if (src == null || dist == null) {
            throw new IllegalArgumentException("Source and destination files cannot be null.");
        }

        // Convert File objects to Path objects
        final Path srcPath = src.toPath();
        final Path distPath = dist.toPath();

        try {
            // Copy the file from src to dist, overwriting if it already exists
            Files.copy(srcPath, distPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("An error occurred while copying the file: " + e.getMessage());
        }
    }


    static void writeObjectToFileWithFileNotExistFix(final File filepath, Serializable obj) {
        if (!filepath.exists()) {
            try {
                filepath.createNewFile();
            } catch (IOException e) {
                System.out.println("Error: fail to create file " + filepath.getAbsolutePath());
            }
        }
        writeObject(filepath, obj);
    }

    public static void clearDir(File stagesDir) {
        // 检查目录是否存在
        if (!stagesDir.exists()) {
            return;
        }
        File[] files = stagesDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.getName().equals(".gitlet")) {
                    continue;
                }
                if (file.isDirectory()) {
                    clearDir(file);
                    file.delete();
                } else {
                    file.delete();
                }
            }
        }
    }
    /* MESSAGES AND ERROR REPORTING */

    /** Return a GitletException whose message is composed from MSG and ARGS as
     *  for the String.format method. */
    static GitletException error(String msg, Object... args) {
        return new GitletException(String.format(msg, args));
    }

    /** Print a message composed from MSG and ARGS as for the String.format
     *  method, followed by a newline. */
    static void message(String msg, Object... args) {
        System.out.printf(msg, args);
        System.out.println();
    }


    /* TIME FORMATER */

    private static final DateTimeFormatter FORMMATER = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy Z");

    /**
     *
     * @param timestamp (long) 时间戳
     * @return String:格式化后的时间
     */
    static String unixTimeFormatter(long timestamp) {
        Instant ins = Instant.ofEpochMilli(timestamp);
        ZonedDateTime zonedDateTime = ins.atZone(ZoneId.of("America/Los_Angeles"));
        return FORMMATER.format(zonedDateTime);
    }

    public static int findMaxNumber(List<String> stringList) {
        // 使用流操作将字符串列表转换为整数流并找到最大值
        OptionalInt maxOptional = stringList.stream().mapToInt(Integer::parseInt).max(); // 找到最大值

        // 如果找到最大值，返回它；否则返回一个默认值（比如 0）
        return maxOptional.orElse(0);
    }


}
