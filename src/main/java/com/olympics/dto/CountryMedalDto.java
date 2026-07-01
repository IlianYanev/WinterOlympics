package com.olympics.dto;

public class CountryMedalDto {
    private String country;
    private int gold;
    private int silver;
    private int bronze;

    public CountryMedalDto(String country) {
        this.country = country;
    }

    public String getCountry() { return country; }
    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }
    public int getSilver() { return silver; }
    public void setSilver(int silver) { this.silver = silver; }
    public int getBronze() { return bronze; }
    public void setBronze(int bronze) { this.bronze = bronze; }
    public int getTotal() { return gold + silver + bronze; }
}