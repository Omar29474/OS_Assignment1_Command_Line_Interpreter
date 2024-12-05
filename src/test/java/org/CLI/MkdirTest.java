package org.CLI;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MkdirTest {

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
    void mkNewDir() throws IOException {
        Mkdir mkdir = new Mkdir();
        String result = mkdir.execute(new PathObj(tempDir), "newDir");

        assertEquals("Created directory: " + tempDir.resolve("newDir").toString(), result);
        assertTrue(Files.exists(tempDir.resolve("newDir")));
    }

    @Test
    void mkExistingDir() throws IOException {
        Mkdir mkdir = new Mkdir();
        mkdir.execute(new PathObj(tempDir), "existingDir");
        String result = mkdir.execute(new PathObj(tempDir), "existingDir");

        assertEquals("Directory already exists: " + tempDir.resolve("existingDir").toString(), result);
    }

    @Test
    void mkDirWithSpaces() throws IOException {
        Mkdir mkdir = new Mkdir();
        String result = mkdir.execute(new PathObj(tempDir), "\"dir with spaces\"");

        assertEquals("Created directory: " + tempDir.resolve("dir with spaces").toString(), result);
        assertTrue(Files.exists(tempDir.resolve("dir with spaces")));
    }

    @Test
    void mkFileExists() throws IOException {
        Path filePath = tempDir.resolve("file.txt");
        Files.createFile(filePath); // Create a file

        Mkdir mkdir = new Mkdir();
        String result = mkdir.execute(new PathObj(tempDir), "file.txt");

        assertEquals("A file with this name already exists: " + filePath.toString(), result);
    }
}
