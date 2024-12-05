package org.CLI;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RmTest {

    private Rm rmCommand;
    private PathObj pathObj;
    private Path testFile;
    private Path testDirectory;

    @BeforeEach
    public void setUp() throws IOException {
        rmCommand = new Rm();
        pathObj = new PathObj(Files.createTempDirectory("testDir"));
        testFile = pathObj.path.resolve("testFile.txt");
        Files.createFile(testFile);
        testDirectory = pathObj.path.resolve("testDirectory");
        Files.createDirectories(testDirectory);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.walk(pathObj.path)
                .sorted((p1, p2) -> p2.compareTo(p1))
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    public void delFileSuccess() throws IOException {
        String result = rmCommand.execute(pathObj, "testFile.txt");
        assertEquals("Deleted: " + testFile.toString() + "\n", result);
        assertFalse(Files.exists(testFile), "File should be deleted");
    }

    @Test
    public void delDirThrows() {
        IOException exception = assertThrows(IOException.class, () -> {
            rmCommand.execute(pathObj, "testDirectory");
        });
        assertExceptionMessage(exception, "Cannot delete directory: " + testDirectory.toString() + ". Use rmdir command instead.");
    }

    @Test
    public void delNonExistentFile() {
        IOException exception = assertThrows(IOException.class, () -> {
            rmCommand.execute(pathObj, "nonExistentFile.txt");
        });
        assertExceptionMessage(exception, "File does not exist: " + pathObj.path.resolve("nonExistentFile.txt").toString());
    }

    @Test
    public void delSpecialCharsFile() throws IOException {
        Path specialFile = pathObj.path.resolve("file@123.txt");
        Files.createFile(specialFile);
        String result = rmCommand.execute(pathObj, "file@123.txt");
        assertEquals("Deleted: " + specialFile.toString() + "\n", result);
        assertFalse(Files.exists(specialFile), "File with special characters should be deleted");
    }

    private void assertExceptionMessage(IOException exception, String expectedMessage) {
        assertEquals(expectedMessage, exception.getMessage());
    }
}
