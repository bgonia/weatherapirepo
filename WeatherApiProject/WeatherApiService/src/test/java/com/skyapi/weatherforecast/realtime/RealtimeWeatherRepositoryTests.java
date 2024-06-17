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

        realtimeWeather.setTemprature(-2);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setPrecipitation(42);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(12);
        realtimeWeather.setLastUpdated(new Date());

        RealtimeWeather updatedRealtimeWather = realtimeWeatherRepository.save(realtimeWeather);

        assertThat(updatedRealtimeWather.getHumidity()).isEqualTo(32);


    }

}
