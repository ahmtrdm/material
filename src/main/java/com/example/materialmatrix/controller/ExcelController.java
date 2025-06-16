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
import java.util.Comparator;
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
    public String intro1() {
        return "intro1";
    }

    @GetMapping("/intro2")
    public String intro2() {
        return "intro2";
    }

    @GetMapping("/matrix")
    public String matrix(Model model) {
        try {
            Map<String, Object> data = excelService.readExcelFiles();
            
            List<WebObject> materials = ((Map<String, WebObject>) data.get("materials")).values().stream()
                .sorted(Comparator.comparing(WebObject::getId))
                .collect(Collectors.toList());
                
            List<WebObject> techniques = ((Map<String, WebObject>) data.get("techniques")).values().stream()
                .sorted(Comparator.comparing(WebObject::getId))
                .collect(Collectors.toList());
                
            List<WebObject> forms = ((Map<String, WebObject>) data.get("forms")).values().stream()
                .sorted(Comparator.comparing(WebObject::getId))
                .collect(Collectors.toList());
            
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

    @GetMapping("/checkIcon")
    public ResponseEntity<Boolean> checkIcon(@RequestParam String type, @RequestParam String number) {
        System.out.println("Received type: " + type); // Debug log
        String referansPath = "src/main/resources/static/icons/" + type + " icon/" + number + "/Referans/";
        // Linux için dosya yolunu düzelt
        if (type.equalsIgnoreCase("form") || type.equalsIgnoreCase("Form")) {
            referansPath = "src/main/resources/static/icons/Form icon/" + number + "/Referans/";
        }
        System.out.println("Looking for directory: " + referansPath); // Debug log
        File directory = new File(referansPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));
            return ResponseEntity.ok(files != null && files.length > 0);
        }
        return ResponseEntity.ok(false);
    }

    @GetMapping("/listIcons")
    public ResponseEntity<List<String>> listIcons(@RequestParam String type, @RequestParam String number) {
        try {
            System.out.println("Received type: " + type); // Debug log
            String referansPath = "src/main/resources/static/icons/" + type + " icon/" + number + "/Referans/";
            // Linux için dosya yolunu düzelt
            if (type.equalsIgnoreCase("form") || type.equalsIgnoreCase("Form")) {
                referansPath = "src/main/resources/static/icons/Form icon/" + number + "/Referans/";
            }
            System.out.println("Looking for directory: " + referansPath); // Debug log
            File directory = new File(referansPath);
            List<String> iconNames = new ArrayList<>();
            
            if (!directory.exists()) {
                System.out.println("Directory does not exist: " + directory.getAbsolutePath());
                return ResponseEntity.ok(iconNames);
            }
            
            if (!directory.isDirectory()) {
                System.out.println("Path is not a directory: " + directory.getAbsolutePath());
                return ResponseEntity.ok(iconNames);
            }
            
            File[] files = directory.listFiles((dir, name) -> {
                String lowerName = name.toLowerCase();
                return lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
            });
            
            if (files != null) {
                for (File file : files) {
                    if (file.canRead()) {
                        iconNames.add(file.getName());
                    } else {
                        System.out.println("Cannot read file: " + file.getAbsolutePath());
                    }
                }
            } else {
                System.out.println("No files found in directory: " + directory.getAbsolutePath());
            }
            
            return ResponseEntity.ok(iconNames);
        } catch (Exception e) {
            System.err.println("Error in listIcons: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
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