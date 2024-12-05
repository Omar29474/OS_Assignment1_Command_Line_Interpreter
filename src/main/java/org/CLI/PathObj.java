package org.CLI;
import java.nio.file.Path;

public class PathObj {
    public Path path;
    public PathObj(Path path) {
        this.path = path;
    }
    public Path getPath() {
        return path;
    }
}