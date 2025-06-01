package com.example.materialmatrix.service;

import com.example.materialmatrix.model.Relationship;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

@Service
public class ExcelService {
    
    public Map<String, Object> readExcelData() throws IOException {
        ClassPathResource resource = new ClassPathResource("M-T_F Matrisi.xlsx");
        Workbook workbook = WorkbookFactory.create(resource.getInputStream());
        Sheet sheet = workbook.getSheetAt(1); // Using second sheet (index 1)
        
        Set<String> uniqueMaterials = new LinkedHashSet<>();
        Set<String> uniqueTechniques = new LinkedHashSet<>();
        List<String> forms = new ArrayList<>();
        List<Relationship> relationships = new ArrayList<>();
        
        // Get forms (Row 5, Columns H to AG)
        Row formRow = sheet.getRow(4);
        for (int i = 7; i < 32; i++) {
            Cell cell = formRow.getCell(i);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String form = cell.getStringCellValue();
                if (form != null && !form.trim().isEmpty()) {
                    forms.add(form);
                }
            }
        }
        
        // Get materials and techniques (Column E and G, starting from row 6)
        for (int i = 5; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            Cell materialCell = row.getCell(4); // Column E
            Cell techniqueCell = row.getCell(6); // Column G
            
            if (materialCell != null && techniqueCell != null) {
                String material = materialCell.getStringCellValue();
                String technique = techniqueCell.getStringCellValue();
                
                if (material != null && !material.trim().isEmpty() &&
                    technique != null && !technique.trim().isEmpty()) {
                    
                    uniqueMaterials.add(material);
                    uniqueTechniques.add(technique);
                    
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
        
        workbook.close();
        return result;
    }
} 