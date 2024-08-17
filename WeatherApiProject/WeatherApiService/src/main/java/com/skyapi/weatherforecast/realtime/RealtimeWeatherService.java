package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class RealtimeWeatherService {

    private final RealtimeWeatherRepository realtimeWeatherRepository;
    private final LocationRepository locationRepository;


    public RealtimeWeather getByLocation(Location location) throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCityName(countryCode, cityName);

        if(realtimeWeather == null) {
            throw new LocationNotFoundException("No location found with the given country code and country name");
        }
        return realtimeWeather;
    }


    public RealtimeWeather getByLocationCode(String locationCode) throws LocationNotFoundException {
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByLocationCode(locationCode);
        if(realtimeWeather == null) {
            throw new LocationNotFoundException("No location found with the given location code: " + locationCode);
        }
        return realtimeWeather;
    }

    public RealtimeWeather update(String locationCode, RealtimeWeather realtimeWeather) throws LocationNotFoundException {
        Location location = locationRepository.findByCode(locationCode);
        if(location == null) {
            throw new LocationNotFoundException("No location found with the given location code: " + locationCode);
        }

        realtimeWeather.setLocation(location);
        realtimeWeather.setLastUpdated(new Date());

        if(location.getRealtimeWeather() == null){
            location.setRealtimeWeather(realtimeWeather);
            Location updatedLocation = locationRepository.save(location);
            return updatedLocation.getRealtimeWeather();
        }

        return realtimeWeatherRepository.save(realtimeWeather);
    }



}
