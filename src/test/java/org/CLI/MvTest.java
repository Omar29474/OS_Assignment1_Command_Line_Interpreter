package org.CLI;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MvTest {
    private Path testDirectory;
    private Path testFile1;
    private Path testFile2;
    private Mv mvCommand;

    @BeforeEach
    public void setUp() throws IOException {
        mvCommand = new Mv();
        testDirectory = Files.createTempDirectory("testDir");
        testFile1 = testDirectory.resolve("file1.txt");
        testFile2 = testDirectory.resolve("file2.txt");
        Files.createFile(testFile1);
        Files.createFile(testFile2);
    }

    @Test
    public void moveFilesToDir() throws IOException {
        Path newDirectory = Files.createTempDirectory("newDir");
        PathObj pathObj = new PathObj(testDirectory);

        String result = mvCommand.execute(pathObj, "file1.txt file2.txt " + newDirectory.toString());

        String expected = "Moved: " + testFile1 + " to " + newDirectory.resolve("file1.txt") + "\n" +
                "Moved: " + testFile2 + " to " + newDirectory.resolve("file2.txt") + "\n";


        assertEquals(expected, result.replace("\r\n", "\n"));
        assertFalse(Files.exists(testFile1));
        assertFalse(Files.exists(testFile2));
        assertTrue(Files.exists(newDirectory.resolve("file1.txt")));
        assertTrue(Files.exists(newDirectory.resolve("file2.txt")));
    }

    @Test
    public void renameFile() throws IOException {
        PathObj pathObj = new PathObj(testDirectory);
        String newFileName = "renamedFile.txt";

        String result = mvCommand.execute(pathObj, "file1.txt " + newFileName);

        String expected = "Renamed: " + testFile1 + " to " + testDirectory.resolve(newFileName) + "\n";

        assertEquals(expected, result.replace("\r\n", "\n"));
        assertFalse(Files.exists(testFile1));
        assertTrue(Files.exists(testDirectory.resolve(newFileName)));
    }

    @Test
    public void moveFileToSameName() throws IOException {
        PathObj pathObj = new PathObj(testDirectory);

        String result = mvCommand.execute(pathObj, "file1.txt file1.txt");

        String expected = "This name is the same as the previous. No changes were made to " + testFile1.toAbsolutePath() + ".\n";

        assertEquals(expected, result.replace("\r\n", "\n")); // Handle line endings
        assertTrue(Files.exists(testFile1));
    }

    @Test
    public void invalidArgs() {
        PathObj pathObj = new PathObj(testDirectory);

        Exception exception = assertThrows(IOException.class, () -> {
            mvCommand.execute(pathObj, "file1.txt");
        });

        assertEquals("Invalid arguments. Usage: mv <source> <destination>", exception.getMessage());
    }

    @Test
    public void sourceNotExist() {
        PathObj pathObj = new PathObj(testDirectory);

        Exception exception = assertThrows(IOException.class, () -> {
            mvCommand.execute(pathObj, "nonexistent.txt newFile.txt");
        });

        assertEquals("Source file does not exist: " + testDirectory.resolve("nonexistent.txt"), exception.getMessage());
    }

    @Test
    public void moveToNonExistentDir() {
        PathObj pathObj = new PathObj(testDirectory);

        Exception exception = assertThrows(IOException.class, () -> {
            mvCommand.execute(pathObj, "file1.txt file2.txt nonexistentDir");
        });

        assertEquals("Error: When moving multiple sources, the destination must be an existing directory.", exception.getMessage());
    }
}
