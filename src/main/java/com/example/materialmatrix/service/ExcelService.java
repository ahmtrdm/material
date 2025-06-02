package com.example.materialmatrix.service;

import com.example.materialmatrix.model.Relationship;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

@Service
public class ExcelService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);
    
    public Map<String, Object> readExcelData() throws IOException {
        ClassPathResource resource = new ClassPathResource("M-T_F Matrisi.xlsx");
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(1); // Using second sheet (index 1)
        
        Set<String> uniqueMaterials = new LinkedHashSet<>();
        Set<String> uniqueTechniques = new LinkedHashSet<>();
        List<String> forms = new ArrayList<>();
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
                    forms.add(form);
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
                        if (relationshipCell != null && 
                            relationshipCell.getCellType() == CellType.STRING &&
                            "x".equalsIgnoreCase(relationshipCell.getStringCellValue().trim())) {
                            
                            Relationship relationship = new Relationship();
                            relationship.setMaterial(material);
                            relationship.setTechnique(technique);
                            relationship.setForm(forms.get(j - 7));
                            relationships.add(relationship);
                        }
                    }
                }
            }
        }
        
        // Convert sets to lists
        List<String> materials = new ArrayList<>(uniqueMaterials);
        List<String> techniques = new ArrayList<>(uniqueTechniques);
        
        Map<String, Object> result = new HashMap<>();
        result.put("materials", materials);
        result.put("techniques", techniques);
        result.put("forms", forms);
        result.put("relationships", relationships);
        result.put("materialNumbers", materialNumbers);
        result.put("techniqueNumbers", techniqueNumbers);
        result.put("formNumbers", formNumbers);
        
        workbook.close();
        return result;
    }
} 