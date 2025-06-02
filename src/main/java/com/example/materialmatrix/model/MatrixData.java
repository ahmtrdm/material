package com.example.materialmatrix.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class MatrixData {
    private List<String> materials;
    private List<String> techniques;
    private List<String> forms;
    private List<Relationship> relationships;
    private Map<String, String> materialNumbers;
    private Map<String, String> techniqueNumbers;
    private Map<String, String> formNumbers;

    public MatrixData(List<String> materials, List<String> techniques, List<String> forms, 
                     List<Relationship> relationships, Map<String, String> materialNumbers,
                     Map<String, String> techniqueNumbers, Map<String, String> formNumbers) {
        this.materials = materials;
        this.techniques = techniques;
        this.forms = forms;
        this.relationships = relationships;
        this.materialNumbers = materialNumbers;
        this.techniqueNumbers = techniqueNumbers;
        this.formNumbers = formNumbers;
    }

    public List<String> getMaterials() {
        return materials;
    }

    public List<String> getTechniques() {
        return techniques;
    }

    public List<String> getForms() {
        return forms;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public Map<String, String> getMaterialNumbers() {
        return materialNumbers;
    }

    public Map<String, String> getTechniqueNumbers() {
        return techniqueNumbers;
    }

    public Map<String, String> getFormNumbers() {
        return formNumbers;
    }
} 