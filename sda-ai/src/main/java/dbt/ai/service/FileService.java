package dbt.ai.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileService {

    public List<File> readFromDir(String dir) throws IOException {
        try(Stream<Path> pathStream = Files.walk(Path.of(dir))){
            return pathStream.map(Path::toFile).toList();
        }
    }

    public Path getTempFilePath(String content) throws IOException {
        Path filePath = Files.createTempFile(Paths.get("src/main/resources"), "temp", "java");
        return Files.writeString(filePath, content);
    }
}
