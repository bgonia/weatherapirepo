package com.skyapi.weatherforecast.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class HourlyWeatherRepositoryTests {

    @Autowired
    private HourlyWeatherRepository hourlyWeatherRepository;

    @Test
    public void testAdd(){
        String locationCode = "DELHI_IN";
        int hourOfDay = 12;

        Location location = new Location().code(locationCode);

        HourlyWeather forecast = new HourlyWeather()
                .location(location)
                .hourOfDay(hourOfDay)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeather updatedForecast = hourlyWeatherRepository.save(forecast);

        assertThat(updatedForecast.getId().getLocation().getCode()).isEqualTo(locationCode);
        assertThat(updatedForecast.getId().getHourOfDay()).isEqualTo(hourOfDay);

    }

    @Test
    public void testDelete(){
        String locationCode = "DELHI_IN";
        Location location = new Location().code(locationCode);
        HourlyWeatherId id = new HourlyWeatherId(10,location);
        hourlyWeatherRepository.deleteById(id);

        Optional<HourlyWeather> result = hourlyWeatherRepository.findById(id);
        assertThat(result).isNotPresent();

    }

    @Test
    public void testFindByLocationCodeFound(){
        String locationCode = "DELHI_IN";
        int currenHour = 10;
        List<HourlyWeather> hourlyForecast = hourlyWeatherRepository.findByLocationCode(locationCode, currenHour);
        assertThat(hourlyForecast).isNotEmpty();
    }

    @Test
    public void testFindByLocationCodeNotFound(){
        String locationCode = "DELHI_IN";
        int currenHour = 15;
        List<HourlyWeather> hourlyForecast = hourlyWeatherRepository.findByLocationCode(locationCode, currenHour);
        assertThat(hourlyForecast).isEmpty();
    }



}
