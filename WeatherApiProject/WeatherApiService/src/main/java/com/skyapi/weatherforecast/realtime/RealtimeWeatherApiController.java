package com.skyapi.weatherforecast.realtime;


import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/realtime")
@AllArgsConstructor
public class RealtimeWeatherApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);
    private final GeolocationService geolocationService;
    private final RealtimeWeatherService realtimeWeatherService;
    private final ModelMapper modelMapper;


    //It will give Realtime weather info based on client IP address
    @GetMapping
    public ResponseEntity<?> getRealtimeWeahterByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIPAddress(request);
        System.out.println("IP address is " + ipAddress);

        try {
            Location locationFromIP = geolocationService.getLocation(ipAddress);
            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);

            RealtimeWeatherDTO realtimeWeatherDTO = modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
            return ResponseEntity.ok(realtimeWeatherDTO);
        } catch (GeolocationException e){
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e){
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode) {
        try {
            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
            return ResponseEntity.ok(entity2DTO(realtimeWeather));
        }catch (LocationNotFoundException e){
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode,
                                                                 @RequestBody @Valid RealtimeWeather realtimeWeatherInRequest){
        realtimeWeatherInRequest.setLocationCode(locationCode);
        try {
            RealtimeWeather updatedRealtimeWeather = realtimeWeatherService.update(locationCode,realtimeWeatherInRequest);

            return ResponseEntity.ok(entity2DTO(updatedRealtimeWeather));
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Map RealtimeWeather to RealtimeWeatherDTO and returns
    private RealtimeWeatherDTO entity2DTO(RealtimeWeather realtimeWeather){
        return modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
    }

}
