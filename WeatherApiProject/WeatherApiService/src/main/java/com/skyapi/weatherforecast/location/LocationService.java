package com.skyapi.weatherforecast.location;

import java.util.List;

import com.skyapi.weatherforecast.common.Location;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class LocationService {
	
	private LocationRepository repository;
	
	public LocationService(LocationRepository repository) {
		this.repository = repository;
	}
	
	public Location add(Location location) {
		return repository.save(location);
	}
	
	public List<Location> list(){
		return repository.findUntrashed();
	}
	
	public Location get(String code){
		return repository.findByCode(code);
	}
	
	public Location update(Location locationInRequest) throws LocationNotFoundException {
		String code = locationInRequest.getCode();
		Location locationInDB = repository.findByCode(code);
		if(locationInDB == null) {
			throw new LocationNotFoundException("No location found with the given code: "+ code);
		}
		
		locationInDB.setCityName(locationInRequest.getCityName());
		locationInDB.setRegionName(locationInRequest.getRegionName());
		locationInDB.setCountryCode(locationInRequest.getCountryCode());
		locationInDB.setCountryName(locationInRequest.getCountryName());
		locationInDB.setEnabled(locationInRequest.isEnabled());
		
		return repository.save(locationInDB);
	}

	public void delete(String code) throws LocationNotFoundException {
		Location location = repository.findByCode(code);
		if(location == null){
			throw new LocationNotFoundException("No location found with the given code: "+ code);
		}
		repository.trashByCode(code);
	}

	
}
