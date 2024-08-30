package com.skyapi.weatherforecast.realtime;

import static org.assertj.core.api.Assertions.assertThat;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false)
public class RealtimeWeatherRepositoryTests {

    @Autowired
    private RealtimeWeatherRepository realtimeWeatherRepository;

    @Test
    public void testUpdate() {
        String locationCode = "NYC_USA";
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findById(locationCode).get();
        realtimeWeather.setTemperature(-2);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setPrecipitation(42);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(12);
        realtimeWeather.setLastUpdated(new Date());

        RealtimeWeather updatedRealtimeWather = realtimeWeatherRepository.save(realtimeWeather);
        assertThat(updatedRealtimeWather.getHumidity()).isEqualTo(32);
    }


    @Test
    public void testFindByCountryCodeAndCityNameNotFound(){
        String countryCode = "JP";
        String cityName = "Tokyo";
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCityName(countryCode, cityName);
        assertThat(realtimeWeather).isNull();
    }

    @Test
    public void testFindByCountryCodeAndCityNameFound(){
        String countryCode = "US";
        String cityName = "New York City";
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCityName(countryCode, cityName);
        assertThat(realtimeWeather).isNotNull();
        assertThat(realtimeWeather.getLocation().getCityName()).isEqualTo(cityName);
    }

    @Test
    public void testFindByLocationCodeNotFound(){
        String locationCode = "LACA_USA";
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByLocationCode(locationCode);
        assertThat(realtimeWeather).isNull();
    }

    @Test
    public void testFindByTrashedLocationCodeNotFound(){
        String locationCode = "NYC_USA";
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByLocationCode(locationCode);
        assertThat(realtimeWeather).isNull();
    }

    @Test
    public void testFindByLocationCodeFound(){
        String locationCode = "DELHI_IN";
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByLocationCode(locationCode);
        assertThat(realtimeWeather).isNotNull();
        assertThat(realtimeWeather.getLocationCode()).isEqualTo(locationCode);
    }


}
