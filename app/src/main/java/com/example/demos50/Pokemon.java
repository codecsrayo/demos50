package com.example.demos50;

public class Pokemon {
    private final int    id;
    private final String name;
    private final String imageUrl;

    public Pokemon(int id, String name, String imageUrl) {
        this.id       = id;
        this.name     = name;
        this.imageUrl = imageUrl;
    }

    public int    getId()       { return id; }
    public String getName()     { return name; }
    public String getImageUrl() { return imageUrl; }
}
