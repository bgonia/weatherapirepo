package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class HourlyWeatherService {

    private final HourlyWeatherRepository hourlyWeatherRepository;
    private final LocationRepository locationRepository;

    public List<HourlyWeather> getByLocation(Location location, int currentHour) throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);

        if (locationInDB == null) {
            throw new LocationNotFoundException("No location found with the given country code and city name");
        }

        return hourlyWeatherRepository.findByLocationCode(locationInDB.getCode(), currentHour);
    }


    public List<HourlyWeather> getByLocationCode(String locationCode, int currentHour) throws LocationNotFoundException {

        Location locationInDB = locationRepository.findByCode(locationCode);
        if (locationInDB == null) {
            throw new LocationNotFoundException("No location found with the given location code" + locationCode);
        }
        return hourlyWeatherRepository.findByLocationCode(locationCode, currentHour);
    }


    public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyForecastInRequest ){
        return Collections.emptyList();

    }





}
