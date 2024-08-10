package com.skyapi.weatherforecast.realtime;


import com.skyapi.weatherforecast.GeolocationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/realtime")
@AllArgsConstructor
public class RealtimeWeatherApiController {

    private final GeolocationService geolocationService;
    private final RealtimeWeatherService realtimeWeatherService;



}
