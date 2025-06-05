package com.example.materialmatrix.service;

import com.example.materialmatrix.model.Relationship;
import com.example.materialmatrix.model.WebObject;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import com.example.materialmatrix.model.Material;
import com.example.materialmatrix.model.Technique;
import com.example.materialmatrix.model.Form;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.io.File;

@Service
public class ExcelService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);
    
    public Map<String, Object> readExcelFiles() {
        Map<String, Object> result = new HashMap<>();
        Map<String, WebObject> materials = new HashMap<>();
        Map<String, WebObject> techniques = new HashMap<>();
        Map<String, WebObject> forms = new HashMap<>();
        
        try {
            ClassPathResource resource = new ClassPathResource("M-T_F Matrisi.xlsx");
            Workbook workbook = WorkbookFactory.create(resource.getInputStream());
            Sheet sheet = workbook.getSheetAt(1); // Using second sheet (index 1)
            
            Set<String> uniqueMaterials = new LinkedHashSet<>();
            Set<String> uniqueTechniques = new LinkedHashSet<>();
            List<String> formsList = new ArrayList<>();
            List<Relationship> relationships = new ArrayList<>();
            Map<String, String> materialNumbers = new HashMap<>();
            Map<String, String> techniqueNumbers = new HashMap<>();
            Map<String, String> formNumbers = new HashMap<>();
            
            // Get form numbers (Row 3, Columns H to AG)
            Row formNumberRow = sheet.getRow(2);
            for (int i = 7; i < 32; i++) {
                Cell cell = formNumberRow.getCell(i);
                if (cell != null) {
                    String formNumber = "";
                    switch (cell.getCellType()) {
                        case STRING:
                            formNumber = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            formNumber = String.format("%02d", (int) cell.getNumericCellValue());
                            break;
                    }
                    if (!formNumber.trim().isEmpty()) {
                        formNumbers.put(formNumber, formNumber);
                        logger.info("Form numarası: {}", formNumber);
                    }
                }
            }
            
            // Get forms (Row 5, Columns H to AG)
            Row formRow = sheet.getRow(4);
            for (int i = 7; i < 32; i++) {
                Cell cell = formRow.getCell(i);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String form = cell.getStringCellValue();
                    if (form != null && !form.trim().isEmpty()) {
                        formsList.add(form);
                        // Get the corresponding form number
                        Cell formNumberCell = formNumberRow.getCell(i);
                        if (formNumberCell != null) {
                            String formNumber = "";
                            switch (formNumberCell.getCellType()) {
                                case STRING:
                                    formNumber = formNumberCell.getStringCellValue();
                                    break;
                                case NUMERIC:
                                    formNumber = String.format("%02d", (int) formNumberCell.getNumericCellValue());
                                    break;
                            }
                            if (!formNumber.trim().isEmpty()) {
                                formNumbers.put(form, formNumber);
                                logger.info("Form: {} - Numara: {}", form, formNumber);
                            }
                        }
                    }
                }
            }
            
            // Get materials and techniques (Column D, E, F and G, starting from row 6)
            for (int i = 5; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Cell materialNumberCell = row.getCell(3); // Column D
                Cell materialCell = row.getCell(4); // Column E
                Cell techniqueNumberCell = row.getCell(5); // Column F
                Cell techniqueCell = row.getCell(6); // Column G
                
                if (materialCell != null && techniqueCell != null) {
                    String materialNumber = "";
                    if (materialNumberCell != null) {
                        switch (materialNumberCell.getCellType()) {
                            case STRING:
                                materialNumber = materialNumberCell.getStringCellValue();
                                break;
                            case NUMERIC:
                                materialNumber = String.format("%02d", (int) materialNumberCell.getNumericCellValue());
                                break;
                        }
                    }
                    
                    String techniqueNumber = "";
                    if (techniqueNumberCell != null) {
                        switch (techniqueNumberCell.getCellType()) {
                            case STRING:
                                techniqueNumber = techniqueNumberCell.getStringCellValue();
                                break;
                            case NUMERIC:
                                techniqueNumber = String.format("%02d", (int) techniqueNumberCell.getNumericCellValue());
                                break;
                        }
                    }
                    
                    String material = materialCell.getStringCellValue();
                    String technique = techniqueCell.getStringCellValue();
                    
                    if (material != null && !material.trim().isEmpty() &&
                        technique != null && !technique.trim().isEmpty()) {
                        
                        uniqueMaterials.add(material);
                        uniqueTechniques.add(technique);
                        
                        if (!materialNumber.trim().isEmpty()) {
                            materialNumbers.put(material, materialNumber);
                            logger.info("Malzeme: {} - Numara: {}", material, materialNumber);
                        }
                        
                        if (!techniqueNumber.trim().isEmpty()) {
                            techniqueNumbers.put(technique, techniqueNumber);
                            logger.info("Teknik: {} - Numara: {}", technique, techniqueNumber);
                        }
                        
                        // Check relationships (Columns H to AG)
                        for (int j = 7; j < 32; j++) {
                            Cell relationshipCell = row.getCell(j);
                            if (relationshipCell != null) {
                                String cellValue = "";
                                switch (relationshipCell.getCellType()) {
                                    case STRING:
                                        cellValue = relationshipCell.getStringCellValue().trim();
                                        break;
                                    case NUMERIC:
                                        cellValue = String.valueOf((int) relationshipCell.getNumericCellValue());
                                        break;
                                    case BOOLEAN:
                                        cellValue = String.valueOf(relationshipCell.getBooleanCellValue());
                                        break;
                                }
                                
                                if ("x".equalsIgnoreCase(cellValue) || "1".equals(cellValue) || "true".equalsIgnoreCase(cellValue)) {
                                    String form = formsList.get(j - 7);
                                    if (form != null) {
                                        Relationship relationship = new Relationship();
                                        relationship.setMaterial(material);
                                        relationship.setTechnique(technique);
                                        relationship.setForm(form);
                                        relationships.add(relationship);
                                        logger.info("İlişki bulundu: {} - {} - {}", material, technique, form);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Convert sets to lists and create ordered maps for IDs
            List<String> materialsList = new ArrayList<>(uniqueMaterials);
            List<String> techniquesList = new ArrayList<>(uniqueTechniques);
            
            // Create ordered maps for materials and techniques
            Map<String, Integer> materialOrder = new LinkedHashMap<>();
            Map<String, Integer> techniqueOrder = new LinkedHashMap<>();
            
            // Assign sequential numbers to materials
            for (int i = 0; i < materialsList.size(); i++) {
                materialOrder.put(materialsList.get(i), i + 1);
            }
            
            // Assign sequential numbers to techniques
            for (int i = 0; i < techniquesList.size(); i++) {
                techniqueOrder.put(techniquesList.get(i), i + 1);
            }
            
            // Malzeme detaylarını oku
            try {
                ClassPathResource materialResource = new ClassPathResource("01 Malzeme listesi.xlsx");
                logger.info("Excel file path: {}", materialResource.getURL());
                
                try (InputStream materialListStream = materialResource.getInputStream()) {
                    Workbook materialWorkbook = WorkbookFactory.create(materialListStream);
                    Sheet materialSheet = materialWorkbook.getSheetAt(0);
                    
                    logger.info("Reading material details from Excel file...");
                    logger.info("Total rows in sheet: {}", materialSheet.getLastRowNum());
                    
                    // Malzeme adlarını ve özelliklerini tutacak map
                    Map<String, Set<String>> materialFeatures = new HashMap<>();
                    
                    // J5'ten başlayarak malzeme isimlerini ve L5'ten başlayarak özellikleri oku
                    for (int i = 4; i <= materialSheet.getLastRowNum(); i++) {
                        Row row = materialSheet.getRow(i);
                        if (row == null) {
                            logger.debug("Row {} is null", i + 1);
                            continue;
                        }
                        
                        Cell materialNameCell = row.getCell(9); // J kolonu
                        Cell featureCell = row.getCell(11); // L kolonu
                        
                        logger.debug("Processing row {}: Material cell: {}, Feature cell: {}", 
                            i + 1, materialNameCell != null ? materialNameCell.getCellType() : "null", 
                            featureCell != null ? featureCell.getCellType() : "null");
                        
                        if (materialNameCell != null) {
                            String materialName = getCellValueAsString(materialNameCell);
                            logger.info("Row {}: Reading material name: '{}'", i + 1, materialName);
                            
                            if (!materialName.isEmpty() && !materialName.equals("Malzemeler") && !materialName.equals("Malzeme Kataloğu")) {
                                if (featureCell != null) {
                                    String feature = getCellValueAsString(featureCell);
                                    logger.info("Row {}: Reading feature for '{}': '{}'", i + 1, materialName, feature);
                                    
                                    if (!feature.isEmpty() && !feature.equals("Özellikler")) {
                                        materialFeatures.computeIfAbsent(materialName, k -> new LinkedHashSet<>()).add(feature);
                                        logger.info("Added feature '{}' to material '{}'", feature, materialName);
                                    }
                                }
                            }
                        }
                    }
                    
                    logger.info("=== Material Features Summary ===");
                    materialFeatures.forEach((material, features) -> {
                        logger.info("Material: '{}'", material);
                        logger.info("Features:");
                        features.forEach(feature -> logger.info("  - {}", feature));
                        logger.info("-------------------");
                    });
                    
                    // Şimdi malzeme detaylarını oluştur
                    for (int i = 4; i <= materialSheet.getLastRowNum(); i++) {
                        Row row = materialSheet.getRow(i);
                        if (row == null) continue;
                        
                        Cell codeCell = row.getCell(1); // B kolonu (Unnamed: 1)
                        Cell categoryCell = row.getCell(2); // C kolonu (Unnamed: 2)
                        Cell nameCell = row.getCell(9); // J kolonu
                        
                        if (nameCell == null) continue;
                        String name = getCellValueAsString(nameCell);
                        if (name.isEmpty() || name.equals("Malzemeler") || name.equals("Malzeme Kataloğu")) continue;
                        
                        // Ana malzeme gruplarını kontrol et
                        if (name.equals("Seramik") || name.equals("Kompozit") || name.equals("Beton") || 
                            name.equals("Cam") || name.equals("Deri") || name.equals("Metal") || 
                            name.equals("Karton") || name.equals("Plastik") || name.equals("Taş") || 
                            name.equals("Tekstil") || name.equals("Ahşap")) {
                            
                            WebObject material = new WebObject();
                            material.setName(name);
                            // Unique malzeme sırasına göre ID ata
                            Integer order = materialOrder.get(name);
                            if (order != null) {
                                material.setId("M" + String.format("%02d", order));
                            } else {
                                material.setId("M" + String.format("%02d", materials.size() + 1));
                            }
                            material.setDescription("Bu malzeme, belirli teknikler ve formlarla uyumlu çalışabilir.");
                            // Malzeme ikonları için dosya yolu: src/main/resources/static/icons/Malzeme icon/M01.jpg
                            String materialPath = "src/main/resources/static/icons/Malzeme icon/" + material.getId();
                            // Check for both JPG and PNG extensions
                            if (new File(materialPath + ".jpg").exists() || new File(materialPath + ".JPG").exists()) {
                                material.setFilesDirectory(materialPath + ".jpg");
                            } else if (new File(materialPath + ".png").exists() || new File(materialPath + ".PNG").exists()) {
                                material.setFilesDirectory(materialPath + ".png");
                            } else {
                                material.setFilesDirectory(materialPath + ".jpg"); // Default to jpg if no file exists
                            }
                            material.setClickable(true);
                            
                            // Malzeme özelliklerini ekle
                            Set<String> features = materialFeatures.get(name);
                            if (features != null && !features.isEmpty()) {
                                features.forEach(material::addFeature);
                            }
                            
                            materials.put(name, material);
                            
                            logger.info("=== Material Object Created ===");
                            logger.info("Name: {}", material.getName());
                            logger.info("ID: {}", material.getId());
                            logger.info("Description: {}", material.getDescription());
                            logger.info("Files Directory: {}", material.getFilesDirectory());
                            logger.info("Features: {}", material.getFeatures());
                            logger.info("Clickable: {}", material.isClickable());
                            
                            // İlişkileri ekle
                            logger.info("=== Processing relationships for material: {} ===", name);
                            int relationshipCount = 0;
                            for (Relationship rel : relationships) {
                                if (rel.getMaterial().equals(name)) {
                                    relationshipCount++;
                                    WebObject relatedTech = techniques.get(rel.getTechnique());
                                    WebObject relatedForm = forms.get(rel.getForm());
                                    
                                    if (relatedTech != null) {
                                        material.addRelatedObject(relatedTech);
                                        relatedTech.addRelatedObject(material);
                                        logger.info("Malzeme-Teknik ilişkisi {}: {} - {}", relationshipCount, name, relatedTech.getName());
                                    }
                                    
                                    if (relatedForm != null) {
                                        material.addRelatedObject(relatedForm);
                                        relatedForm.addRelatedObject(material);
                                        logger.info("Malzeme-Form ilişkisi {}: {} - {}", relationshipCount, name, relatedForm.getName());
                                    }
                                }
                            }
                            logger.info("Total relationships found for {}: {}", name, relationshipCount);
                            logger.info("Final related objects for {}: {}", name, material.getRelatedObjects().stream()
                                .map(WebObject::getName)
                                .collect(Collectors.toList()));
                            logger.info("===================");
                        }
                    }
                    
                    materialWorkbook.close();
                }
            } catch (Exception e) {
                logger.error("Error reading material details from Excel: {}", e.getMessage(), e);
            }
            
            // Teknik detaylarını oku
            try {
                ClassPathResource techniqueResource = new ClassPathResource("02 Teknik listesi.xlsx");
                logger.info("Excel file path: {}", techniqueResource.getURL());
                
                try (InputStream techniqueListStream = techniqueResource.getInputStream()) {
                    Workbook techniqueWorkbook = WorkbookFactory.create(techniqueListStream);
                    Sheet techniqueSheet = techniqueWorkbook.getSheetAt(0);
                    
                    logger.info("Reading technique details from Excel file...");
                    logger.info("Total rows in sheet: {}", techniqueSheet.getLastRowNum());
                    
                    // Teknik adlarını ve özelliklerini tutacak map
                    Map<String, Set<String>> techniqueFeatures = new HashMap<>();
                    
                    // H5'ten başlayarak teknik isimlerini ve J5'ten başlayarak özellikleri oku
                    for (int i = 4; i <= techniqueSheet.getLastRowNum(); i++) {
                        Row row = techniqueSheet.getRow(i);
                        if (row == null) {
                            logger.debug("Row {} is null", i + 1);
                            continue;
                        }
                        
                        Cell nameCell = row.getCell(7); // H kolonu
                        Cell codeCell = row.getCell(6); // G kolonu
                        
                        if (nameCell == null) continue;
                        String name = getCellValueAsString(nameCell);
                        if (name.isEmpty() || name.equals("Teknikler") || name.equals("Teknik Kataloğu")) continue;
                        
                        WebObject technique = new WebObject();
                        technique.setName(name);
                        // Teknik kodunu G kolonundan al
                        String techniqueCode = "";
                        if (codeCell != null) {
                            techniqueCode = getCellValueAsString(codeCell);
                            if (!techniqueCode.isEmpty()) {
                                technique.setId(techniqueCode);
                            } else {
                                // Eğer kod boşsa, sıralı ID oluştur
                                Integer order = techniqueOrder.get(name);
                                if (order != null) {
                                    technique.setId("T" + String.format("%02d", order));
                                } else {
                                    technique.setId("T" + String.format("%02d", techniques.size() + 1));
                                }
                            }
                        } else {
                            // Eğer kod hücresi null ise, sıralı ID oluştur
                            Integer order = techniqueOrder.get(name);
                            if (order != null) {
                                technique.setId("T" + String.format("%02d", order));
                            } else {
                                technique.setId("T" + String.format("%02d", techniques.size() + 1));
                            }
                        }
                        technique.setDescription("Bu teknik, belirli malzemeler ve formlarla uyumlu çalışabilir.");
                        // Teknik ikonları için dosya yolu: src/main/resources/static/icons/Teknikler icon/T01.jpg
                        String techniquePath = "src/main/resources/static/icons/Teknikler icon/" + technique.getId();
                        // Check for both JPG and PNG extensions
                        if (new File(techniquePath + ".jpg").exists() || new File(techniquePath + ".JPG").exists()) {
                            technique.setFilesDirectory(techniquePath + ".jpg");
                        } else if (new File(techniquePath + ".png").exists() || new File(techniquePath + ".PNG").exists()) {
                            technique.setFilesDirectory(techniquePath + ".png");
                        } else {
                            technique.setFilesDirectory(techniquePath + ".jpg"); // Default to jpg if no file exists
                        }
                        technique.setClickable(true);
                        
                        // Teknik özelliklerini ekle
                        Set<String> features = techniqueFeatures.get(name);
                        if (features != null && !features.isEmpty()) {
                            features.forEach(technique::addFeature);
                        }
                        
                        techniques.put(name, technique);
                        
                        logger.info("=== Technique Object Created ===");
                        logger.info("Name: {}", technique.getName());
                        logger.info("ID: {}", technique.getId());
                        logger.info("Description: {}", technique.getDescription());
                        logger.info("Files Directory: {}", technique.getFilesDirectory());
                        logger.info("Features: {}", technique.getFeatures());
                        logger.info("Clickable: {}", technique.isClickable());
                        
                        // İlişkileri ekle
                        logger.info("=== Processing relationships for technique: {} ===", name);
                        int relationshipCount = 0;
                        for (Relationship rel : relationships) {
                            if (rel.getTechnique().equals(name)) {
                                relationshipCount++;
                                WebObject relatedMat = materials.get(rel.getMaterial());
                                WebObject relatedForm = forms.get(rel.getForm());
                                
                                if (relatedMat != null) {
                                    technique.addRelatedObject(relatedMat);
                                    relatedMat.addRelatedObject(technique);
                                    logger.info("Teknik-Malzeme ilişkisi {}: {} - {}", relationshipCount, name, relatedMat.getName());
                                }
                                
                                if (relatedForm != null) {
                                    technique.addRelatedObject(relatedForm);
                                    relatedForm.addRelatedObject(technique);
                                    logger.info("Teknik-Form ilişkisi {}: {} - {}", relationshipCount, name, relatedForm.getName());
                                }
                            }
                        }
                        logger.info("Total relationships found for {}: {}", name, relationshipCount);
                        logger.info("Final related objects for {}: {}", name, technique.getRelatedObjects().stream()
                            .map(WebObject::getName)
                            .collect(Collectors.toList()));
                        logger.info("===================");
                    }
                    
                    techniqueWorkbook.close();
                }
            } catch (Exception e) {
                logger.error("Error reading technique details from Excel: {}", e.getMessage(), e);
            }
            
            // Form detaylarını oku
            try {
                ClassPathResource formResource = new ClassPathResource("03 Form listesi.xlsx");
                logger.info("Excel file path: {}", formResource.getURL());
                
                // Form sıralaması için map oluştur
                Map<String, Integer> formOrder = new LinkedHashMap<>();
                for (int i = 0; i < formsList.size(); i++) {
                    formOrder.put(formsList.get(i), i + 1);
                }
                
                try (InputStream formListStream = formResource.getInputStream()) {
                    Workbook formWorkbook = WorkbookFactory.create(formListStream);
                    Sheet formSheet = formWorkbook.getSheetAt(0);
                    
                    logger.info("Reading form details from Excel file...");
                    logger.info("Total rows in sheet: {}", formSheet.getLastRowNum());
                    
                    // Form adlarını ve özelliklerini tutacak map
                    Map<String, Set<String>> formFeatures = new HashMap<>();
                    
                    // D3'ten başlayarak form isimlerini ve F,G,H kolonlarından özellikleri oku
                    for (int i = 2; i <= formSheet.getLastRowNum(); i++) {
                        Row row = formSheet.getRow(i);
                        if (row == null) {
                            logger.debug("Row {} is null", i + 1);
                            continue;
                        }
                        
                        Cell formNameCell = row.getCell(3); // D kolonu
                        
                        logger.debug("Processing row {}: Form cell: {}", 
                            i + 1, formNameCell != null ? formNameCell.getCellType() : "null");
                        
                        if (formNameCell != null) {
                            String formName = getCellValueAsString(formNameCell);
                            logger.info("Row {}: Reading form name: '{}'", i + 1, formName);
                            
                            if (!formName.isEmpty() && !formName.equals("Formlar") && !formName.equals("Form Kataloğu")) {
                                // F, G, H kolonlarından özellikleri oku
                                for (int col = 5; col <= 7; col++) { // F, G, H kolonları
                                    Cell featureCell = row.getCell(col);
                                    if (featureCell != null) {
                                        String feature = getCellValueAsString(featureCell);
                                        logger.info("Row {}: Reading feature for '{}' from column {}: '{}'", 
                                            i + 1, formName, col, feature);
                                        
                                        if (!feature.isEmpty() && !feature.equals("Özellikler")) {
                                            formFeatures.computeIfAbsent(formName, k -> new LinkedHashSet<>()).add(feature);
                                            logger.info("Added feature '{}' to form '{}'", feature, formName);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    logger.info("=== Form Features Summary ===");
                    formFeatures.forEach((form, features) -> {
                        logger.info("Form: '{}'", form);
                        logger.info("Features:");
                        features.forEach(feature -> logger.info("  - {}", feature));
                        logger.info("-------------------");
                    });
                    
                    // Form detaylarını oluştur
                    for (int i = 2; i <= formSheet.getLastRowNum(); i++) {
                        Row row = formSheet.getRow(i);
                        if (row == null) continue;
                        
                        Cell nameCell = row.getCell(3); // D kolonu
                        Cell codeCell = row.getCell(2); // C kolonu
                        
                        if (nameCell == null) continue;
                        String name = getCellValueAsString(nameCell);
                        if (name.isEmpty() || name.equals("Formlar") || name.equals("Form Kataloğu")) continue;
                        
                        WebObject formObj = new WebObject();
                        formObj.setName(name);
                        // Form kodunu C kolonundan al
                        String formCode = "";
                        if (codeCell != null) {
                            formCode = getCellValueAsString(codeCell);
                            if (!formCode.isEmpty()) {
                                formObj.setId(formCode);
                            } else {
                                // Eğer kod boşsa, sıralı ID oluştur
                                Integer order = formOrder.get(name);
                                if (order != null) {
                                    formObj.setId("F" + String.format("%02d", order));
                                } else {
                                    formObj.setId("F" + String.format("%02d", forms.size() + 1));
                                }
                            }
                        } else {
                            // Eğer kod hücresi null ise, sıralı ID oluştur
                            Integer order = formOrder.get(name);
                            if (order != null) {
                                formObj.setId("F" + String.format("%02d", order));
                            } else {
                                formObj.setId("F" + String.format("%02d", forms.size() + 1));
                            }
                        }
                        formObj.setDescription("Bu form, belirli malzemeler ve tekniklerle uyumlu çalışabilir.");
                        // Form ikonları için dosya yolu: src/main/resources/static/icons/Form icon/F01/F01.jpg
                        String formDir = formObj.getId();
                        formObj.setFilesDirectory("src/main/resources/static/icons/Form icon/" + formDir + "/" + formObj.getId() + ".jpg");
                        formObj.setClickable(true);
                        
                        // Form özelliklerini ekle
                        Set<String> features = formFeatures.get(name);
                        if (features != null && !features.isEmpty()) {
                            features.forEach(formObj::addFeature);
                        }
                        
                        forms.put(name, formObj);
                        
                        logger.info("=== Form Object Created ===");
                        logger.info("Name: {}", formObj.getName());
                        logger.info("ID: {}", formObj.getId());
                        logger.info("Description: {}", formObj.getDescription());
                        logger.info("Files Directory: {}", formObj.getFilesDirectory());
                        logger.info("Features: {}", formObj.getFeatures());
                        logger.info("Clickable: {}", formObj.isClickable());
                        
                        // İlişkileri ekle
                        logger.info("=== Processing relationships for form: {} ===", name);
                        int relationshipCount = 0;
                        for (Relationship rel : relationships) {
                            if (rel.getForm().equals(name)) {
                                relationshipCount++;
                                WebObject relatedMat = materials.get(rel.getMaterial());
                                WebObject relatedTech = techniques.get(rel.getTechnique());
                                
                                if (relatedMat != null) {
                                    formObj.addRelatedObject(relatedMat);
                                    relatedMat.addRelatedObject(formObj);
                                    logger.info("Form-Malzeme ilişkisi {}: {} - {}", relationshipCount, name, relatedMat.getName());
                                }
                                
                                if (relatedTech != null) {
                                    formObj.addRelatedObject(relatedTech);
                                    relatedTech.addRelatedObject(formObj);
                                    logger.info("Form-Teknik ilişkisi {}: {} - {}", relationshipCount, name, relatedTech.getName());
                                }
                            }
                        }
                        logger.info("Total relationships found for {}: {}", name, relationshipCount);
                        logger.info("Final related objects for {}: {}", name, formObj.getRelatedObjects().stream()
                            .map(WebObject::getName)
                            .collect(Collectors.toList()));
                        logger.info("===================");
                    }
                    
                    formWorkbook.close();
                }
            } catch (Exception e) {
                logger.error("Error reading form details from Excel: {}", e.getMessage(), e);
            }
            
            result.put("materials", materials);
            result.put("techniques", techniques);
            result.put("forms", forms);
            result.put("relationships", relationships);
            result.put("materialNumbers", materialNumbers);
            result.put("techniqueNumbers", techniqueNumbers);
            result.put("formNumbers", formNumbers);
            
            workbook.close();
        } catch (Exception e) {
            logger.error("Error reading Excel files", e);
        }
        
        return result;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            logger.debug("Cell is null");
            return "";
        }
        
        try {
            String value = "";
            CellType cellType = cell.getCellType();
            logger.debug("Cell type: {}", cellType);
            
            switch (cellType) {
                case STRING:
                    value = cell.getStringCellValue().trim();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        value = cell.getDateCellValue().toString();
                    } else {
                        value = String.valueOf((int) cell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                case FORMULA:
                    try {
                        value = cell.getStringCellValue().trim();
                    } catch (IllegalStateException e) {
                        try {
                            value = String.valueOf((int) cell.getNumericCellValue());
                        } catch (IllegalStateException ex) {
                            value = String.valueOf(cell.getNumericCellValue());
                        }
                    }
                    break;
                case BLANK:
                    value = "";
                    break;
                default:
                    value = "";
            }
            logger.debug("Cell value read: '{}' (Type: {})", value, cellType);
            return value;
        } catch (Exception e) {
            logger.warn("Error reading cell value: {}", e.getMessage());
            return "";
        }
    }

    private String normalizeTurkishChars(String str) {
        return str
            .replaceAll("[\n\r]+", " ") // Satır sonlarını boşluğa çevir
            .replaceAll("\\s+", " ") // Birden fazla boşluğu tek boşluğa çevir
            .replace("ğ", "g")
            .replace("Ğ", "G")
            .replace("ü", "u")
            .replace("Ü", "U")
            .replace("ş", "s")
            .replace("Ş", "S")
            .replace("ı", "i")
            .replace("İ", "I")
            .replace("ö", "o")
            .replace("Ö", "O")
            .replace("ç", "c")
            .replace("Ç", "C")
            .trim(); // Baştaki ve sondaki boşlukları temizle
    }
} 