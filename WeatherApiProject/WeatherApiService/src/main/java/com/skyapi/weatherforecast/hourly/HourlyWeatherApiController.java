package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/hourly")
@AllArgsConstructor
public class HourlyWeatherApiController {

    private final HourlyWeatherService hourlyWeatherService;
    private final GeolocationService geolocationService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request){
        String IPAddress = CommonUtility.getIPAddress(request);

        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
            Location locationFromIP = geolocationService.getLocation(IPAddress);
            List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocation(locationFromIP, currentHour);

            if(hourlyForecast.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntity2DTO(hourlyForecast));

        } catch (NumberFormatException | GeolocationException e) {
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException ex){
            return ResponseEntity.notFound().build();
        }
    }

    //Response Entity
    private HourlyWeatherListDTO listEntity2DTO(List<HourlyWeather> hourlyForecast){
        Location location = hourlyForecast.get(0).getId().getLocation();
        HourlyWeatherListDTO listDTO = new HourlyWeatherListDTO();
        listDTO.setLocation(location.toString());

        hourlyForecast.forEach(
                hourlyWeather -> {
                    HourlyWeatherDTO dto = modelMapper.map(hourlyWeather, HourlyWeatherDTO.class);
                    listDTO.addHourlyWeatherDTO(dto);
                }
        );
        return listDTO;
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable("locationCode") String locationCode, HttpServletRequest request){

        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
            List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocationCode(locationCode, currentHour);
            if(hourlyForecast.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntity2DTO(hourlyForecast));

        }catch (NumberFormatException e){
            return ResponseEntity.badRequest().build();
        }catch(LocationNotFoundException ex){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateHourlyForecast(
            @PathVariable("locationCode") String locationCode,
            @RequestBody List<HourlyWeatherDTO> listDto) throws BadRequestException {

        if(listDto.isEmpty()){
            throw new BadRequestException("Hourly forecast data cannot be empty");
        }

        return ResponseEntity.accepted().build();

    }



}
