package org.CLI;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class TouchTest {
    private Touch touch;
    private PathObj pathObj;

    @BeforeEach
    public void setUp() throws IOException {
        pathObj = new PathObj(Files.createTempDirectory("testDir"));
        touch = new Touch();
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.walk(pathObj.path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void createFile() throws Exception {
        String filename = "file1.txt";
        touch.execute(pathObj,filename);
        assertTrue(Files.exists(pathObj.path.resolve(filename)));
    }

    @Test
    public void createFileWithSpaces() throws Exception {
        String filename = "fi le1.txt";
        touch.execute(pathObj,'"'+filename+'"');
        assertTrue(Files.exists(pathObj.path.resolve(filename)));
    }

    @Test
    public void createMultipleFiles() throws Exception {
        String filesName = "file1.txt file2.txt";
        touch.execute(pathObj,filesName);
        assertTrue(Files.exists(pathObj.path.resolve("file1.txt")));
        assertTrue(Files.exists(pathObj.path.resolve("file2.txt")));
    }


    @Test
    public void updateFileTimestamp() throws Exception{
        String fileName = "timestamp.txt";
        touch.execute(pathObj,fileName);
        File file = new File(pathObj.path.resolve(fileName).toString());
        Long beforeLast = file.lastModified();
        Thread.sleep(100);
        touch.execute(pathObj,fileName);
        Long after = file.lastModified();
        assertNotEquals(beforeLast,after);
    }

    @Test
    public void wrongPath() throws Exception {
        String fileName = "/dir/dir/file.txt";
        Exception exception = assertThrows(Exception.class, () -> {
            touch.execute(pathObj, fileName);
        });
        assertTrue(exception.getMessage().contains("does not exist"));
    }

}
