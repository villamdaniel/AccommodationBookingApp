package com.example.accommodationbookingapp;

public class Accommodation {
    private String id;
    private String name;
    private String location;
    private double pricePerNight;
    private boolean available;
    private String imageUrl;

    // ⚠ Üres konstruktor Firestore-hoz kötelező!
    public Accommodation() {}

    public Accommodation(String id, String name, String location, double pricePerNight, boolean available, String imageUrl) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.available = available;
        this.imageUrl = imageUrl;
    }

    // Getterek és setterek
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
