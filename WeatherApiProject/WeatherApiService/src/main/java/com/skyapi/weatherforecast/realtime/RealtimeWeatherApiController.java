package com.skyapi.weatherforecast.realtime;


import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/realtime")
@AllArgsConstructor
public class RealtimeWeatherApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);
    private final GeolocationService geolocationService;
    private final RealtimeWeatherService realtimeWeatherService;


    //It will give Realtime weather info based on client IP address
    @GetMapping
    public ResponseEntity<?> getRealtimeWeahterByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIPAddress(request);

        try {
            Location locationFromIP = geolocationService.getLocation(ipAddress);
            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);
            return ResponseEntity.ok(realtimeWeather);
        } catch (GeolocationException e){
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e){
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }

    }

}
