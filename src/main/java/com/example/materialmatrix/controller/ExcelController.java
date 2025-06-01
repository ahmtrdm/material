package com.example.materialmatrix.controller;

import com.example.materialmatrix.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @GetMapping("/")
    public String index(Model model) {
        try {
            model.addAllAttributes(excelService.readExcelData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "index";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Get the file and save it
            String fileName = "M-T_F Matrisi.xlsx";
            Path path = Paths.get("src/main/resources/" + fileName);
            
            // Delete existing file if it exists
            Files.deleteIfExists(path);
            
            // Save the new file
            Files.copy(file.getInputStream(), path);
            
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
} 