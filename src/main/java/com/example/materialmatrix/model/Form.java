package com.example.materialmatrix.model;

public class Form extends WebObject {
    private String formCode;
    private String formCategory;

    public Form() {
        super();
        this.setClickable(true);
    }

    public String getFormCode() {
        return formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }

    public String getFormCategory() {
        return formCategory;
    }

    public void setFormCategory(String formCategory) {
        this.formCategory = formCategory;
    }
} 