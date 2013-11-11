package com.orbitz.weather.model;

import android.graphics.Bitmap;

public class CurrentWeather {

    private String currentTemp;
    private String description;
    private String precipitation;
    private Bitmap bitmap;

    public String getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(String currentTemp) {
        this.currentTemp = currentTemp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
