package com.example.materialmatrix.controller;

import com.example.materialmatrix.model.MatrixData;
import com.example.materialmatrix.model.Relationship;
import com.example.materialmatrix.model.WebObject;
import com.example.materialmatrix.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @GetMapping("/checkIcon")
    public ResponseEntity<Boolean> checkIcon(@RequestParam String type, @RequestParam String number) {
        String referansPath = "src/main/resources/static/icons/" + type + " icon/" + number + "/Referans/";
        File directory = new File(referansPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));
            return ResponseEntity.ok(files != null && files.length > 0);
        }
        return ResponseEntity.ok(false);
    }

    @GetMapping("/listIcons")
    public ResponseEntity<List<String>> listIcons(@RequestParam String type, @RequestParam String number) {
        String referansPath = "src/main/resources/static/icons/" + type + " icon/" + number + "/Referans/";
        File directory = new File(referansPath);
        List<String> iconNames = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));
            if (files != null) {
                for (File file : files) {
                    iconNames.add(file.getName());
                }
            }
        }
        return ResponseEntity.ok(iconNames);
    }

    @GetMapping("/getFeatures")
    public ResponseEntity<Set<String>> getFeatures(@RequestParam String type, @RequestParam String number) {
        Set<String> features = new HashSet<>();
        
        switch(type) {
            case "material":
                features = excelService.getMaterialFeatures(number);
                break;
            case "technique":
                features = excelService.getTechniqueFeatures(number);
                break;
            case "form":
                features = excelService.getFormFeatures(number);
                break;
        }
        
        return ResponseEntity.ok(features);
    }
} 