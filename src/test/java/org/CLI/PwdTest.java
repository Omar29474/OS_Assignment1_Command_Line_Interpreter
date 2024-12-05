package org.CLI;

import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PwdTest {
    private PathObj pathObj;
    private Pwd pwdCommand;

    @BeforeEach
    void setUp() throws Exception {
        Path tempDir = Files.createTempDirectory("testDir");
        pathObj = new PathObj(tempDir);
        pwdCommand = new Pwd();
    }

    @Test
    void currDir() throws Exception {
        String expectedDir = pathObj.getPath().toString() + "\n";
        assertEquals(expectedDir, pwdCommand.execute(pathObj, ""), "The pwd command should return the current working directory");
    }
}
