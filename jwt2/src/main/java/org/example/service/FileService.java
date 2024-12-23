package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileService {
	@Value("${app.upload.dir}")
	private String uploadDir;

	public List<String> listTextFiles() throws IOException {
		Path path = Paths.get(uploadDir);
		if (!Files.exists(path)) {
			Files.createDirectories(path);
		}

		try (Stream<Path> stream = Files.list(path)) {
			return stream
					.filter(file -> !Files.isDirectory(file))
					.filter(file -> file.toString().endsWith(".txt"))
					.map(Path::getFileName)
					.map(Path::toString)
					.toList();
		}
	}

	public String readTextFile(String filename) throws IOException {
		Path filePath = Paths.get(uploadDir, filename);
		if (!Files.exists(filePath) || !filePath.toString().endsWith(".txt")) {
			throw new IOException("File not found or not a text file");
		}
		return Files.readString(filePath);
	}
}