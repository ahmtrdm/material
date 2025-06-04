package com.example.materialmatrix.controller;

import com.example.materialmatrix.model.MatrixData;
import com.example.materialmatrix.model.Relationship;
import com.example.materialmatrix.model.WebObject;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @GetMapping("/")
    public String index(Model model) {
        try {
            Map<String, Object> data = excelService.readExcelFiles();
            
            List<WebObject> materials = ((Map<String, WebObject>) data.get("materials")).values().stream().collect(Collectors.toList());
            List<WebObject> techniques = ((Map<String, WebObject>) data.get("techniques")).values().stream().collect(Collectors.toList());
            List<WebObject> forms = ((Map<String, WebObject>) data.get("forms")).values().stream().collect(Collectors.toList());
            
            MatrixData matrixData = new MatrixData(
                materials.stream().map(WebObject::getName).collect(Collectors.toList()),
                techniques.stream().map(WebObject::getName).collect(Collectors.toList()),
                forms.stream().map(WebObject::getName).collect(Collectors.toList()),
                (List<Relationship>) data.get("relationships"),
                (Map<String, String>) data.get("materialNumbers"),
                (Map<String, String>) data.get("techniqueNumbers"),
                (Map<String, String>) data.get("formNumbers")
            );
            
            model.addAttribute("matrixData", matrixData);
            model.addAttribute("materials", materials);
            model.addAttribute("techniques", techniques);
            model.addAttribute("forms", forms);
            return "index";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
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