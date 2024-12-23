package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

	private FileService fileService;

	@TempDir
	Path tempDir;

	@BeforeEach
	void setUp() {
		fileService = new FileService();
		ReflectionTestUtils.setField(fileService, "uploadDir", tempDir.toString());
	}

	@Test
	void listTextFiles_Success() throws IOException {
		// Arrange
		createTestFile("test1.txt", "content1");
		createTestFile("test2.txt", "content2");
		createTestFile("nottext.pdf", "content3");

		// Act
		List<String> files = fileService.listTextFiles();

		// Assert
		assertEquals(2, files.size());
		assertTrue(files.contains("test1.txt"));
		assertTrue(files.contains("test2.txt"));
		assertFalse(files.contains("nottext.pdf"));
	}

	@Test
	void readTextFile_Success() throws IOException {
		// Arrange
		String content = "Test content";
		createTestFile("test.txt", content);

		// Act
		String readContent = fileService.readTextFile("test.txt");

		// Assert
		assertEquals(content, readContent);
	}

	@Test
	void readTextFile_FileNotFound_ThrowsException() {
		// Act & Assert
		assertThrows(IOException.class,
				() -> fileService.readTextFile("nonexistent.txt"));
	}

	@Test
	void readTextFile_NotTextFile_ThrowsException() throws IOException {
		// Arrange
		createTestFile("test.pdf", "content");

		// Act & Assert
		assertThrows(IOException.class,
				() -> fileService.readTextFile("test.pdf"));
	}

	private void createTestFile(String filename, String content) throws IOException {
		Path filePath = tempDir.resolve(filename);
		Files.writeString(filePath, content);
	}
}