package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.IOException;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
	private final FileService fileService;

	@GetMapping
	public String listFiles(Model model) {
		try {
			model.addAttribute("files", fileService.listTextFiles());
			return "files/list";
		} catch (IOException e) {
			model.addAttribute("error", "Error listing files: " + e.getMessage());
			return "error";
		}
	}

	@GetMapping("/{filename}")
	public String viewFile(@PathVariable String filename, Model model) {
		try {
			model.addAttribute("filename", filename);
			model.addAttribute("content", fileService.readTextFile(filename));
			return "files/view";
		} catch (IOException e) {
			model.addAttribute("error", "Error reading file: " + e.getMessage());
			return "error";
		}
	}
}