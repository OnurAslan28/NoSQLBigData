package org.example;

public class CityData {

    private final String cityId;
    private final String cityName;
    private final String state;

    public CityData(String cityId, String cityName, String state) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.state = state;
    }

    public String getCityId() {
        return cityId;
    }

    @Override
    public String toString() {
        return  "Id: " + cityId + " Name: " + cityName + " State: " + state;
    }
}
