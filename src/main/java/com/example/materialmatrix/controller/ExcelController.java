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
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

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

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

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
            String pattern;
            
            switch(type.toLowerCase()) {
                case "form":
                    pattern = "classpath:static/icons/Form icon/" + number + "/Referans/*.{png,jpg,jpeg}";
                    break;
                case "technique":
                    pattern = "classpath:static/icons/Teknikler icon/" + number + "/Referans/*.{png,jpg,jpeg}";
                    break;
                case "material":
                    pattern = "classpath:static/icons/Malzeme icon/" + number + "/Referans/*.{png,jpg,jpeg}";
                    break;
                default:
                    pattern = "classpath:static/icons/" + type + " icon/" + number + "/Referans/*.{png,jpg,jpeg}";
            }
            
            System.out.println("Looking for pattern: " + pattern); // Debug log
            
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            List<String> iconNames = new ArrayList<>();
            
            System.out.println("Found " + resources.length + " resources");
            
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null) {
                    System.out.println("Found resource: " + filename);
                    iconNames.add(filename);
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