package com.skyapi.weatherforecast.common;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;

@Data
@Entity(name = "weather_hourly")
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
public class HourlyWeather {

    @EmbeddedId
    private HourlyWeatherId id = new HourlyWeatherId();

    private int temperature;
    private int precipitation;

    @Column(length = 50)
    private String status;


    //Builder methods
    public HourlyWeather id(Location location, int hour){
        this.id.setLocation(location);
        this.id.setHourOfDay(hour);
        return this;
    }

    public HourlyWeather temperature(int temperature) {
        this.setTemperature(temperature);
        return this;
    }

    public HourlyWeather precipitation(int precipitation) {
        this.precipitation = precipitation;
        return this;
    }

    public HourlyWeather status(String status) {
        this.setStatus(status);
        return this;
    }

    public HourlyWeather location(Location location) {
        this.id.setLocation(location);
        return this;
    }

    public HourlyWeather hourOfDay(int hour) {
        this.id.setHourOfDay(hour);
        return this;
    }

}
