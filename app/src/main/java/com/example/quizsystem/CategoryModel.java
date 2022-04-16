package com.example.quizsystem;

public class CategoryModel {
    private String id;
    private String name;
    private String numberOfVictorins;

    public CategoryModel(String id, String name, String numberOfVictorins) {
        this.id = id;
        this.name = name;
        this.numberOfVictorins = numberOfVictorins;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberOfVictorins() {
        return numberOfVictorins;
    }

    public void setNumberOfVictorins(String numberOfVictorins) {
        this.numberOfVictorins = numberOfVictorins;
    }
}
