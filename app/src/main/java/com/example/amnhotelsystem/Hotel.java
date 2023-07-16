package com.example.amnhotelsystem;

public class Hotel {
    private String id;
    private String name;
    private String region;
    private String imageUrl;
    private String priceInfo;
    private boolean isFavorite;

    private boolean isReserved;
    private String reviewScore;
    private String totalReviewCount;
    private int numberOfDays;

    public Hotel() {
        // Default constructor required for Firebase database operations
    }

    public Hotel(String id, String name, String region, String imageUrl, String priceInfo, boolean isFavorite, boolean isReserved, String reviewScore, String totalReviewCount, int numberOfDays) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.imageUrl = imageUrl;
        this.priceInfo = priceInfo;
        this.isFavorite = isFavorite;
        this.isReserved = isReserved;
        this.reviewScore = reviewScore;
        this.totalReviewCount = totalReviewCount;
        this.numberOfDays = numberOfDays;
    }

    public Hotel(String hotelId, String hotelName, String hotelRegion, String priceInfo, int numberOfDays, double reviewScore, int totalReviewCount, String imageUrl) {
        this.id = hotelId;
        this.name = hotelName;
        this.region = hotelRegion;
        this.priceInfo = priceInfo;
        this.numberOfDays = numberOfDays;
        this.reviewScore = String.valueOf(reviewScore);
        this.totalReviewCount = String.valueOf(totalReviewCount);
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPriceInfo() {
        return priceInfo;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getReviewScore() {
        return reviewScore;
    }

    public String getTotalReviewCount() {
        return totalReviewCount;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }
}
