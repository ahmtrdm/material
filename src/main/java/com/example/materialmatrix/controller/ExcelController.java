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
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
            List<String> iconNames = new ArrayList<>();
            
            if (!resource.exists()) {
                System.out.println("Resource does not exist: " + referansPath);
                return ResponseEntity.ok(iconNames);
            }

            try {
                URL url = resource.getURL();
                System.out.println("Resource URL: " + url);
                
                if (url.getProtocol().equals("jar")) {
                    // Handle JAR resources
                    String jarPath = url.getPath();
                    System.out.println("JAR path: " + jarPath);
                    
                    // Extract the path inside the JAR
                    String pathInJar = jarPath.substring(jarPath.indexOf("!") + 1);
                    System.out.println("Path in JAR: " + pathInJar);
                    
                    // Get the JAR file
                    String jarFile = jarPath.substring(5, jarPath.indexOf("!"));
                    System.out.println("JAR file: " + jarFile);
                    
                    try (JarFile jar = new JarFile(jarFile)) {
                        // List all entries in the JAR
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            
                            // Check if the entry is in our target directory and is an image
                            if (name.startsWith(pathInJar.substring(1)) && 
                                (name.toLowerCase().endsWith(".png") || 
                                 name.toLowerCase().endsWith(".jpg") || 
                                 name.toLowerCase().endsWith(".jpeg"))) {
                                
                                // Extract just the filename
                                String fileName = name.substring(name.lastIndexOf("/") + 1);
                                System.out.println("Found image in JAR: " + fileName);
                                iconNames.add(fileName);
                            }
                        }
                    }
                } else {
                    // Handle filesystem resources
                    File directory = resource.getFile();
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
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error accessing resource: " + e.getMessage());
                e.printStackTrace();
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