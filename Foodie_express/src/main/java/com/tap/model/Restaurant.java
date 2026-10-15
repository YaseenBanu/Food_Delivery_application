package com.tap.model;

public class Restaurant {

    private int restaurantId;
    private String restaurantName;
    private String cuisineType;
    private int deliveryTime;
    private String address;
    private int adminUserId;
    private float rating;
    private boolean active;
    private String imagePath;

    // Default Constructor
    public Restaurant() {

    }

    // Constructor with ID
    public Restaurant(int restaurantId, String restaurantName, String cuisineType,
                      int deliveryTime, String address, int adminUserId,
                      float rating, boolean active, String imagePath) {

        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.cuisineType = cuisineType;
        this.deliveryTime = deliveryTime;
        this.address = address;
        this.adminUserId = adminUserId;
        this.rating = rating;
        this.active = active;
        this.imagePath = imagePath;
    }

    // Constructor without ID
    public Restaurant(String restaurantName, String cuisineType,
                      int deliveryTime, String address,
                      int adminUserId, float rating,
                      boolean active, String imagePath) {

        this.restaurantName = restaurantName;
        this.cuisineType = cuisineType;
        this.deliveryTime = deliveryTime;
        this.address = address;
        this.adminUserId = adminUserId;
        this.rating = rating;
        this.active = active;
        this.imagePath = imagePath;
    }

    // Getters & Setters

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(int adminUserId) {
        this.adminUserId = adminUserId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantId=" + restaurantId +
                ", restaurantName='" + restaurantName + '\'' +
                ", cuisineType='" + cuisineType + '\'' +
                ", deliveryTime=" + deliveryTime +
                ", address='" + address + '\'' +
                ", adminUserId=" + adminUserId +
                ", rating=" + rating +
                ", active=" + active +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}