package org.CLI;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RmdirTest {

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("testDir");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    void rmEmptyDir() throws IOException {
        Path emptyDir = tempDir.resolve("emptyDir");
        Files.createDirectory(emptyDir);

        Rmdir rmdir = new Rmdir();
        String result = rmdir.execute(new PathObj(tempDir), "emptyDir");

        assertEquals("Deleted directory: " + emptyDir.toString(), result);
        assertFalse(Files.exists(emptyDir));
    }

    @Test
    void rmNonEmptyDir() throws IOException {
        Path nonEmptyDir = tempDir.resolve("nonEmptyDir");
        Files.createDirectory(nonEmptyDir);
        Files.createFile(nonEmptyDir.resolve("file.txt"));

        Rmdir rmdir = new Rmdir();
        String result = rmdir.execute(new PathObj(tempDir), "nonEmptyDir");

        assertEquals("Directory is not empty: " + nonEmptyDir.toString(), result);
        assertTrue(Files.exists(nonEmptyDir));
    }

    @Test
    void rmNonExistent() throws IOException {
        Rmdir rmdir = new Rmdir();
        String result = rmdir.execute(new PathObj(tempDir), "nonExistentDir");

        assertEquals("Directory does not exist: " + tempDir.resolve("nonExistentDir").toString(), result);
    }

    @Test
    void rmDirWithSpaces() throws IOException {
        Path emptyDir = tempDir.resolve("dir with spaces");
        Files.createDirectory(emptyDir);

        Rmdir rmdir = new Rmdir();
        String result = rmdir.execute(new PathObj(tempDir), "\"dir with spaces\"");

        assertEquals("Deleted directory: " + emptyDir.toString(), result);
        assertFalse(Files.exists(emptyDir));
    }
}
