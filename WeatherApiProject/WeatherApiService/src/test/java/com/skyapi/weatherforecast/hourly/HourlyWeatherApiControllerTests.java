package com.skyapi.weatherforecast.hourly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HourlyWeatherApiController.class)
public class HourlyWeatherApiControllerTests {

    private static final String END_POINT_PATH = "/v1/hourly";
    private static final String X_CURRENT_HOUR = "X-Current-Hour";
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private HourlyWeatherService hourlyWeatherService;
    @MockBean private GeolocationService geolocationService;

    @Test
    public void testGetByIPShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn400BadRequestBecauseGeoLocationException() throws Exception {

        when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "9"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn204NoContent() throws Exception {
        int currentHour = 9;
        Location location = new Location().code("DELHI_IN");

        Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        Mockito.when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }


    @Test
    public void testGetByIPShouldReturn200OK() throws Exception {
        int currentHour = 9;

        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United states of America");

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(15)
                .precipitation(60)
                .status("Sunny");

        Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andDo(print());
    }


    //Test cases for getHourlyWeatherByLocationCode

    @Test
    public void testGetByCodeShouldReturn400BadRequest() throws Exception {
            String locationCode = "DELHI_IN";
            String requestURI = END_POINT_PATH + "/" + locationCode;;

            mockMvc.perform(get(requestURI))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn404NotFound() throws Exception {
        String locationCode = "DELHI_IN";
        int currentHour = 9;
        String requestURI = END_POINT_PATH + "/" + locationCode;
        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn204NoContent() throws Exception {
        String locationCode = "DELHI_IN";
        int currentHour = 9;
        String requestURI = END_POINT_PATH + "/" + locationCode;
        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn200OK() throws Exception {
        String locationCode = "DELHI_IN";
        int currentHour = 9;
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United states of America");

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(11)
                .temperature(15)
                .precipitation(60)
                .status("Sunny");


        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
        String requestURI = END_POINT_PATH +  "/NYC_USA";

        List<HourlyWeatherDTO> listDTO = Collections.emptyList();

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Hourly forecast data cannot be empty")))
                .andDo(print());

    }









}
