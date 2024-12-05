package org.CLI;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class CdTest {
    private Cd cd;
    private PathObj pathObj;

    @BeforeEach
    public void setUp() throws IOException {
        pathObj = new PathObj(Files.createTempDirectory("testDir"));
        cd = new Cd();
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.walk(pathObj.path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void getCurrentPathName() throws Exception {
        String cdPath = cd.execute(pathObj, "");
        assertEquals(cdPath.trim(), pathObj.path.toString());
    }

    @Test
    public void previousFolder() throws Exception {
        Path newDir = pathObj.path.resolve("newDir");
        Files.createDirectory(newDir);
        cd.execute(pathObj, "newDir");
        cd.execute(pathObj, "..");
        String cdPath = cd.execute(pathObj, "");
        assertEquals(pathObj.path.toString(), cdPath.trim());
    }
}
