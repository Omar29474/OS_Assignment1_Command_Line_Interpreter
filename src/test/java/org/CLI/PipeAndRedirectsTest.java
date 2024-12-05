package org.CLI;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PipeAndRedirectsTest {
    private CLI cli;
    private Path testDir;

    @BeforeEach
    public void setUp() throws IOException {
        CommandFactory.add("cd", new Cd());
        testDir = Files.createTempDirectory("testPipe");
        cli = new CLI(testDir);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.walk(testDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    // Pipe
    @Test
    public void testPipe() throws Exception {
        Path beforePath = cli.pathobj.path;
        List<ParsedCommand> pcs = cli.parseCommand("cd | cd");
        cli.executeCommands(pcs);

        // There are two commands
        assertEquals(2, pcs.size());
        // The value affected by this command is testDir
        // cd without any args return the current path
        // so passing current path to cd won't change the path
        assertEquals(beforePath.toString(),testDir.toString());
    }

    // Test >
    @Test
    public void testOutputToFile() throws Exception {
        String fileName = "output.txt";
        // Output the current dir to file
        List<ParsedCommand> pcs = cli.parseCommand("cd > " + fileName);
        cli.executeCommands(pcs);
        // Get the file content
        String content = cli.readFile(testDir.resolve(fileName).toString());
        assertEquals(1, pcs.size());
        // Check if they are equal
        assertEquals(content.trim() , testDir.toString().trim());
    }

    // Test >>
    @Test
    public void testAppendToFile() throws Exception {
        String fileName = "output1.txt";
        cli.writeFile(fileName,"Test",false);
        // Output the current dir to file
        List<ParsedCommand> pcs = cli.parseCommand("cd >> " + fileName);
        cli.executeCommands(pcs);
        // Get The file content after creating it
        String content = cli.readFile(testDir.resolve(fileName).toString());
        assertEquals(1, pcs.size());
        // Check if the current path appended to "Test"
        assertEquals("Test"+testDir.toString(), content.trim() );
    }

}


