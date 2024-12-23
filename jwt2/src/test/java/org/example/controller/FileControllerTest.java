package org.example.controller;

import org.example.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FileService fileService;

	@Test
	@WithMockUser
	void listFiles_Success() throws Exception {
		// Arrange
		List<String> files = Arrays.asList("file1.txt", "file2.txt");
		when(fileService.listTextFiles()).thenReturn(files);

		// Act & Assert
		mockMvc.perform(get("/files"))
				.andExpect(status().isOk())
				.andExpect(view().name("files/list"))
				.andExpect(model().attribute("files", files));

		verify(fileService).listTextFiles();
	}

	@Test
	@WithMockUser
	void listFiles_Error() throws Exception {
		// Arrange
		when(fileService.listTextFiles()).thenThrow(new IOException("Error"));

		// Act & Assert
		mockMvc.perform(get("/files"))
				.andExpect(status().isOk())
				.andExpect(view().name("error"))
				.andExpect(model().attributeExists("error"));

		verify(fileService).listTextFiles();
	}

	@Test
	@WithMockUser
	void viewFile_Success() throws Exception {
		// Arrange
		String filename = "test.txt";
		String content = "File content";
		when(fileService.readTextFile(filename)).thenReturn(content);

		// Act & Assert
		mockMvc.perform(get("/files/{filename}", filename))
				.andExpect(status().isOk())
				.andExpect(view().name("files/view"))
				.andExpect(model().attribute("filename", filename))
				.andExpect(model().attribute("content", content));

		verify(fileService).readTextFile(filename);
	}

	@Test
	@WithMockUser
	void viewFile_Error() throws Exception {
		// Arrange
		String filename = "test.txt";
		when(fileService.readTextFile(filename)).thenThrow(new IOException("Error"));

		// Act & Assert
		mockMvc.perform(get("/files/{filename}", filename))
				.andExpect(status().isOk())
				.andExpect(view().name("error"))
				.andExpect(model().attributeExists("error"));

		verify(fileService).readTextFile(filename);
	}
}