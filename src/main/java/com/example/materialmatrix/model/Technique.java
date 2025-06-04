package com.example.materialmatrix.model;

public class Technique extends WebObject {
    private String techniqueCode;
    private String techniqueCategory;

    public Technique() {
        super();
        this.setClickable(true);
    }

    public String getTechniqueCode() {
        return techniqueCode;
    }

    public void setTechniqueCode(String techniqueCode) {
        this.techniqueCode = techniqueCode;
    }

    public String getTechniqueCategory() {
        return techniqueCategory;
    }

    public void setTechniqueCategory(String techniqueCategory) {
        this.techniqueCategory = techniqueCategory;
    }
} 