package com.example.materialmatrix.model;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class WebObject {
    private String name;
    private String description;
    private String id;
    private List<String> features;
    private String filesDirectory;
    private Set<WebObject> relatedObjects;
    private boolean clickable;

    public WebObject() {
        this.features = new ArrayList<>();
        this.relatedObjects = new HashSet<>();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public void addFeature(String feature) {
        this.features.add(feature);
    }

    public String getFilesDirectory() {
        return filesDirectory;
    }

    public void setFilesDirectory(String filesDirectory) {
        this.filesDirectory = filesDirectory;
    }

    public Set<WebObject> getRelatedObjects() {
        return relatedObjects;
    }

    public void setRelatedObjects(Set<WebObject> relatedObjects) {
        this.relatedObjects = relatedObjects;
    }

    public void addRelatedObject(WebObject object) {
        this.relatedObjects.add(object);
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    public String toString() {
        return "WebObject{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", features=" + features +
                ", filesDirectory='" + filesDirectory + '\'' +
                ", relatedObjects=" + relatedObjects.stream().map(WebObject::getName).collect(Collectors.toList()) +
                ", clickable=" + clickable +
                '}';
    }
} 