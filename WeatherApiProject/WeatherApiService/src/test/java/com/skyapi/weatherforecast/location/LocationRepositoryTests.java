package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;


import java.util.Date;
import java.util.List;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class LocationRepositoryTests {
	
	@Autowired
	private LocationRepository repository;
    @Autowired
    private LocationRepository locationRepository;

	@Test
	public void testAddSuccess() {
		Location location = new Location();
		location.setCode("MBMH_IN");
		location.setCityName("Mumbai");
		location.setRegionName("Maharashtra");
		location.setCountryCode("IN");
		location.setCountryName("INDIA");
		location.setEnabled(true);
		
		Location savedLocation =  repository.save(location);
		assertThat(savedLocation).isNotNull();
		assertThat(savedLocation.getCode()).isEqualTo("MBMH_IN");
	}
	
	
	@Test
	public void testListSuccess() {
		
		List<Location> locations = repository.findUntrashed();
		
		assertThat(locations).isNotEmpty();
		locations.forEach(System.out::println);
		
	}
	
	@Test
	public void testGetNotFound() {
		String code = "ABCD";
		
		Location location = repository.findByCode(code);
		
		assertThat(location).isNull();
		
	}
	
	@Test
	public void testGetFound() {
		String code = "DELHI_IN";
		
		Location location = repository.findByCode(code);
		
		assertThat(location).isNotNull();
		assertThat(location.getCode()).isEqualTo(code);		
	}

	@Test
	public void testTrashSuccess() {
		String code = "NYC_USA";

		repository.trashByCode(code);
		Location location = repository.findByCode(code);
		assertThat(location).isNull();

	}

	@Test
	public void testAddRealtimeWeatherData(){
		String code = "NYC_USA";
		Location location = repository.findByCode(code);
		RealtimeWeather realtimeWeather = location.getRealtimeWeather();

		if(realtimeWeather == null){
			realtimeWeather = new RealtimeWeather();;
			realtimeWeather.setLocation(location);
			location.setRealtimeWeather(realtimeWeather);
		}

		realtimeWeather.setTemperature(-1);
		realtimeWeather.setHumidity(30);
		realtimeWeather.setPrecipitation(40);
		realtimeWeather.setStatus("Snowy");
		realtimeWeather.setWindSpeed(15);
		realtimeWeather.setLastUpdated(new Date());

		Location updatedLocation = repository.save(location);

		assertThat(updatedLocation.getRealtimeWeather().getLocationCode()).isEqualTo(code);


	}


	@Test
	public void testAddHourlyWeatherData(){
		Location location = locationRepository.findById("DELHI_IN").get();

		List<HourlyWeather> listHourlyWeather = location.getListHourlyWeather();

        new HourlyWeather();
        HourlyWeather forecast1 = HourlyWeather.of()
				.id(new HourlyWeatherId(10, location))
				.temperature(15)
				.precipitation(40)
				.status("Sunny")
				.build();

		HourlyWeather forecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(11)
				.temperature(16)
				.precipitation(50)
				.status("Cloudy");

		listHourlyWeather.add(forecast1);
		listHourlyWeather.add(forecast2);

		Location updatedLocation = repository.save(location);

		assertThat(updatedLocation.getListHourlyWeather()).isNotEmpty();

	}

	@Test
	public void testFindByCountryCodeAndCityNameNotFound(){
		String countryCode = "NYC_USA";
		String cityName = "New York City";

		Location location = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
		assertThat(location).isNull();
	}

	@Test
	public void testFindByCountryCodeAndCityNameFound(){
		String countryCode = "IN";
		String cityName = "Delhi";

		Location location = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
		assertThat(location.getCountryCode()).isEqualTo(countryCode);
		assertThat(location.getCityName()).isEqualTo(cityName);
	}






}
