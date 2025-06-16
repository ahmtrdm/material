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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

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

    @Autowired
    private ResourceLoader resourceLoader;

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
        String referansPath;
        
        switch(type.toLowerCase()) {
            case "form":
                referansPath = "src/main/resources/static/icons/Form icon/" + number + "/Referans/";
                break;
            case "technique":
                referansPath = "src/main/resources/static/icons/Teknikler icon/" + number + "/Referans/";
                break;
            case "material":
                referansPath = "src/main/resources/static/icons/Malzeme icon/" + number + "/Referans/";
                break;
            default:
                referansPath = "src/main/resources/static/icons/" + type + " icon/" + number + "/Referans/";
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
            String referansPath;
            
            switch(type.toLowerCase()) {
                case "form":
                    referansPath = "classpath:static/icons/Form icon/" + number + "/Referans/";
                    break;
                case "technique":
                    referansPath = "classpath:static/icons/Teknikler icon/" + number + "/Referans/";
                    break;
                case "material":
                    referansPath = "classpath:static/icons/Malzeme icon/" + number + "/Referans/";
                    break;
                default:
                    referansPath = "classpath:static/icons/" + type + " icon/" + number + "/Referans/";
            }
            
            System.out.println("Looking for directory: " + referansPath); // Debug log
            Resource resource = resourceLoader.getResource(referansPath);
            File directory = resource.getFile();
            List<String> iconNames = new ArrayList<>();
            
            if (!directory.exists()) {
                System.out.println("Directory does not exist: " + directory.getAbsolutePath());
                return ResponseEntity.ok(iconNames);
            }
            
            if (!directory.isDirectory()) {
                System.out.println("Path is not a directory: " + directory.getAbsolutePath());
                return ResponseEntity.ok(iconNames);
            }

            // Add detailed directory information
            System.out.println("Directory exists and is a directory");
            System.out.println("Directory absolute path: " + directory.getAbsolutePath());
            System.out.println("Directory can read: " + directory.canRead());
            System.out.println("Directory permissions: " + directory.getAbsolutePath() + " - " + 
                             (directory.canRead() ? "r" : "-") +
                             (directory.canWrite() ? "w" : "-") +
                             (directory.canExecute() ? "x" : "-"));
            
            File[] files = directory.listFiles((dir, name) -> {
                String lowerName = name.toLowerCase();
                boolean isImage = lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
                if (isImage) {
                    System.out.println("Found image file: " + name);
                }
                return isImage;
            });
            
            if (files != null) {
                System.out.println("Number of files found: " + files.length);
                for (File file : files) {
                    if (file.canRead()) {
                        System.out.println("Adding file to list: " + file.getName() + 
                                         " (can read: " + file.canRead() + 
                                         ", size: " + file.length() + " bytes)");
                        iconNames.add(file.getName());
                    } else {
                        System.out.println("Cannot read file: " + file.getAbsolutePath() + 
                                         " (permissions: " + 
                                         (file.canRead() ? "r" : "-") +
                                         (file.canWrite() ? "w" : "-") +
                                         (file.canExecute() ? "x" : "-") + ")");
                    }
                }
            } else {
                System.out.println("No files found in directory: " + directory.getAbsolutePath());
                // List all files in directory to help debug
                File[] allFiles = directory.listFiles();
                if (allFiles != null) {
                    System.out.println("Directory contents:");
                    for (File f : allFiles) {
                        System.out.println("- " + f.getName() + 
                                         " (is file: " + f.isFile() + 
                                         ", can read: " + f.canRead() + 
                                         ", size: " + f.length() + " bytes)");
                    }
                }
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