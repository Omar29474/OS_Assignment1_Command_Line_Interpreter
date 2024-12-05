package org.CLI;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class LsTest {

    private Path tempDir;
    private Ls lsCommand;
    private PathObj pathObj;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("cliTestDir");
        lsCommand = new Ls();
        pathObj = new PathObj(tempDir);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void noFlags() throws IOException {
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createDirectory(tempDir.resolve("dir1"));

        String expected = "dir1/\nfile1.txt";
        String result = lsCommand.execute(pathObj, "");

        assertEquals(expected, result);
    }

    @Test
    void withHidden() throws IOException {
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createDirectory(tempDir.resolve("dir1"));
        Files.createFile(tempDir.resolve(".hiddenfile"));

        String expected = "./\n../\n.hiddenfile\ndir1/\nfile1.txt";
        String result = lsCommand.execute(pathObj, "-a");

        assertEquals(expected, result);
    }

    @Test
    void reverse() throws IOException {
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createDirectory(tempDir.resolve("dir1"));
        Files.createFile(tempDir.resolve("file2.txt"));

        String expected = "file2.txt\nfile1.txt\ndir1/";
        String result = lsCommand.execute(pathObj, "-r");

        assertEquals(expected, result);
    }

    @Test
    void sameName() throws IOException {
        Files.createFile(tempDir.resolve("sameNameFile"));
        Files.createDirectory(tempDir.resolve("sameNameDir"));

        String expected = "sameNameDir/\nsameNameFile";
        String result = lsCommand.execute(pathObj, "");

        assertEquals(expected, result);
    }

    @Test
    void invalidFlag() {
        Exception exception = assertThrows(IOException.class, () -> {
            lsCommand.execute(pathObj, "-x");
        });

        String expectedMessage = "Invalid option: -x";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void combinedFlags() throws IOException {
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createDirectory(tempDir.resolve("dir1"));
        Files.createFile(tempDir.resolve(".hiddenfile"));
        Files.createFile(tempDir.resolve("file2.txt"));

        String expected = "file2.txt\nfile1.txt\ndir1/\n.hiddenfile\n../\n./";
        String result = lsCommand.execute(pathObj, "-ar");
        assertEquals(expected, result);
    }

    @Test
    void listSpecificPath() throws IOException {
        Path specificDir = tempDir.resolve("specificDir");
        Files.createDirectory(specificDir);
        Files.createFile(specificDir.resolve("fileInSpecificDir.txt"));

        String expected = "fileInSpecificDir.txt\n";
        String result = lsCommand.execute(pathObj, specificDir.toString());

        assertEquals(expected.trim(), result.trim());
    }

    @Test
    void listPathWithFlags() throws IOException {
        Path specificDir = tempDir.resolve("specificDir");
        Files.createDirectory(specificDir);
        Files.createFile(specificDir.resolve("fileInSpecificDir.txt"));
        Files.createFile(specificDir.resolve(".hiddenfile"));

        String expected = "./\n../\n.hiddenfile\nfileInSpecificDir.txt\n";
        String result = lsCommand.execute(pathObj, specificDir.toString() + " -a");

        assertEquals(expected.trim(), result.trim());
    }

    @Test
    void invalidPath() {
        Exception exception = assertThrows(IOException.class, () -> {
            lsCommand.execute(pathObj, "invalidPath");
        });

        String expectedMessage = "The specified path does not exist: invalidPath";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void pathNotDirectory() throws IOException {
        Path file = tempDir.resolve("file.txt");
        Files.createFile(file);

        Exception exception = assertThrows(IOException.class, () -> {
            lsCommand.execute(pathObj, file.toString());
        });

        String expectedMessage = "The specified path is not a directory: " + file.toString();
        assertEquals(expectedMessage, exception.getMessage());
    }
}
