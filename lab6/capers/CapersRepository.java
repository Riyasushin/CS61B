package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author rj
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join(CWD, ".capers", "story");


    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence()  {
        final File parent = Utils.join(CWD, ".capers");
        if ( !parent.exists()) {
            parent.mkdir();
        }
        final File dogs = Utils.join(parent, "dogs");
        final File story = Utils.join(parent, "story");

        if ( !dogs.exists()) {
            dogs.mkdir();
        }
        if ( !story.exists()) {
            try {
                story.createNewFile();
            } catch (IOException excp) {
                System.out.println("Error: fail to create file " + story.getAbsolutePath());
            }
        }


    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        String newText = readContentsAsString(CAPERS_FOLDER) + text + "\n";
        writeContents(CAPERS_FOLDER, newText);
        System.out.println(newText);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        Dog ddg = new Dog(name, breed, age);
        File ddgFile = Utils.join(CWD, ".capers", "dogs", (name + ".dat"));
        if (!ddgFile.exists()) {
            try {
                ddgFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error: fail to create file " + ddgFile.getAbsolutePath());
            }
        }
        writeObject(ddgFile, ddg);

        System.out.println(ddg.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        Dog dg = Dog.fromFile(name);
        dg.haveBirthday();
        File ddgFile = Utils.join(CWD, ".capers", "dogs", (name + ".dat"));
        ddgFile.delete();
        writeObject(ddgFile, dg);
    }
}
