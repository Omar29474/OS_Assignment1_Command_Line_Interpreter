package org.CLI;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class CatTest {
    private Cat cat;
    private PathObj pathObj;

    @BeforeEach
    public void setUp() throws IOException {
        pathObj = new PathObj(Files.createTempDirectory("testDir"));
        cat = new Cat();
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.walk(pathObj.path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void content() throws Exception {
        String fileName = "cat.txt";
        String content = "This is file content";
        Files.write(pathObj.path.resolve(fileName), content.getBytes());
        String output = cat.execute(pathObj, fileName);
        assertEquals(content, output);
    }

    @Test
    public void contentWithSpaces() throws Exception {
        String fileName = "ca t.txt";
        String content = "This is file content";
        Files.write(pathObj.path.resolve(fileName), content.getBytes());
        String output = cat.execute(pathObj, '"' + fileName + '"');
        assertEquals(content, output);
    }

    @Test
    public void multipleFilesContent() throws Exception {
        String fileName1 = "cat.txt";
        String fileName2 = "cat2.txt";
        String content1 = "This is file content";
        String content2 = "This is the second file content";
        Files.write(pathObj.path.resolve(fileName1), content1.getBytes());
        Files.write(pathObj.path.resolve(fileName2), content2.getBytes());
        String output = cat.execute(pathObj, fileName1 + " " + fileName2);
        assertEquals(content1 + content2, output);
    }

    @Test
    public void unlistedFile()  {
        String fileName = "cat.txt";
        Exception exception = assertThrows(Exception.class, () -> {
            cat.execute(pathObj, fileName);
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void wrongPath(){
        String fileName = "/dir/dir/file.txt";
        Exception exception = assertThrows(Exception.class, () -> {
            cat.execute(pathObj, fileName);
        });
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    public void emptyArg() throws Exception {
        String input = "This is user input.\nEXIT\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        String output = cat.execute(pathObj, "");
        assertEquals("This is user input.", output);
    }
}
