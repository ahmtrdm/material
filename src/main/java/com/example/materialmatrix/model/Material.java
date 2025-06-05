package com.example.materialmatrix.model;

public class Material extends WebObject {
    private String materialCode;
    private String materialCategory;

    public Material() {
        super();
        this.setClickable(true);
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialCategory() {
        return materialCategory;
    }

    public void setMaterialCategory(String materialCategory) {
        this.materialCategory = materialCategory;
    }
} 