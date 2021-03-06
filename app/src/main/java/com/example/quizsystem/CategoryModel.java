package com.example.quizsystem;

@SuppressWarnings("ALL")
public class CategoryModel {
    private String id;
    private String name;
    private String numberOfVictorins;
    private String victorinCounter;

    public CategoryModel(String id, String name, String numberOfVictorins, String victorinCounter) {
        this.id = id;
        this.name = name;
        this.numberOfVictorins = numberOfVictorins;
        this.victorinCounter = victorinCounter;
    }

    public String getVictorinCounter() {
        return victorinCounter;
    }

    public void setVictorinCounter(String victorinCounter) {
        this.victorinCounter = victorinCounter;
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
