package com.skyapi.weatherforecast.location;

import org.springframework.web.bind.annotation.*;


import com.skyapi.weatherforecast.common.Location;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

	private LocationService locationService;
	
	public LocationApiController(LocationService locationService) {
		super();
		this.locationService = locationService;
	}
	
	@PostMapping
	public ResponseEntity<Location> addLocation(@RequestBody @Valid Location location){
		Location addLocation = locationService.add(location);
		URI uri = URI.create("/v1/locations/" + addLocation.getCode());
		return ResponseEntity.created(uri).body(addLocation);
	}
	
	@GetMapping
	public ResponseEntity<?> listLocations(){
		List<Location> locations = locationService.list();
		if(locations.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(locations);	
	}
	
	@GetMapping("/{code}")
	public ResponseEntity<?> listLocations(@PathVariable("code") String code){
		Location location = locationService.get(code);
		if(location == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(location);	
	}
	
	@PutMapping
	public ResponseEntity<?> updateLocation(@RequestBody @Valid Location location){
		try {
			Location updatedLocation = locationService.update(location);
			return ResponseEntity.ok(updatedLocation);
		} catch (LocationNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{code}")
	public ResponseEntity<?> deleteLocation(@PathVariable("code") String code){
        try {
            locationService.delete(code);
			return ResponseEntity.noContent().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
	}
	
	
	
	
}
