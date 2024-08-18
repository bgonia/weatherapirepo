package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import com.skyapi.weatherforecast.common.Location;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTests {
	
	public static final String END_POINT_PATH = "/v1/locations";
	
	@Autowired MockMvc mockMvc;
	@Autowired ObjectMapper mapper;
	@MockBean
	LocationService service;
	
	@Test
	public void testAddShouldReturn400BadRequest() throws Exception {
		Location location = new Location();
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
			
	}
	
	@Test
	public void testAddShouldReturn201Created() throws Exception{
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United states of America");
		location.setEnabled(true);
		
		Mockito.when(service.add(location)).thenReturn(location);
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code", is("NYC_USA")))
			.andExpect(jsonPath("$.city_name", is("New York City")))
			.andExpect(header().string("Location", "/v1/locations/NYC_USA"))
			.andDo(print());
		
	}


	@Test
	public void testValidateRequestBodyLocationCodeLength() throws Exception {
		Location location = new Location();
		location.setCode("");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United states of America");
		location.setEnabled(true);

		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.errors[0]", is("Location code must have 3-12 characters")))
				.andDo(print());
	}

	@Test
	public void testValidateRequestBodyAllFieldsInvalid() throws Exception {
		Location location = new Location();

		String bodyContent = mapper.writeValueAsString(location);

		MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString();
		assertThat(responseBody).contains("Location code can not be null");
		assertThat(responseBody).contains("City name can not be null");
		assertThat(responseBody).contains("Region name must have 3-128 characters");
		assertThat(responseBody).contains("Country code can not be null");
		assertThat(responseBody).contains("Country name can not be null");


	}

	@Test
	public void testValidateRequestBodyLocationCode() throws Exception {
		Location location = new Location();
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United states of America");
		location.setEnabled(true);

		String bodyContent = mapper.writeValueAsString(location);

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.errors[0]", is("Location code can not be null")))
				.andDo(print());

	}


	
	@Test
	public void testListShouldReturn204NoContent() throws Exception {
		
		Mockito.when(service.list()).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get(END_POINT_PATH))
				.andExpect(status().isNoContent())
				.andDo(print());
		
	}
	
	@Test
	public void testListShouldReturn200OK() throws Exception {
		Location location1 = new Location();
		location1.setCode("NYC_USA");
		location1.setCityName("New York City");
		location1.setRegionName("New York");
		location1.setCountryCode("US");
		location1.setCountryName("United states of America");
		location1.setEnabled(true);
		
		Location location2 = new Location();
		location2.setCode("LACA_USA");
		location2.setCityName("Los Angeles");
		location2.setRegionName("California");
		location2.setCountryCode("US");
		location2.setCountryName("United states of America");
		location2.setEnabled(true);
		
		
		Mockito.when(service.list()).thenReturn(List.of(location1, location2));
		
		mockMvc.perform(get(END_POINT_PATH))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$[0].code", is("NYC_USA")))
		.andExpect(jsonPath("$[0].city_name", is("New York City")))
		.andExpect(jsonPath("$[1].code", is("LACA_USA")))
		.andExpect(jsonPath("$[1].city_name", is("Los Angeles")))
		.andDo(print());
		
	}
	
	
	@Test
	public void testGetShouldReturn405MethodNotAllowed() throws Exception {
		String requestURI = END_POINT_PATH + "/ABCDEF";
		
		mockMvc.perform(post(requestURI))
			.andExpect(status().isMethodNotAllowed())
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturn404NotFound() throws Exception {
		String requestURI = END_POINT_PATH + "/ABCDEF";
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturn200OK() throws Exception {
		String code = "LACA_USA";
		String requestURI = END_POINT_PATH + "/" + code;
		
		Location location = new Location();
		location.setCode("LACA_USA");
		location.setCityName("Los Angeles");
		location.setRegionName("California");
		location.setCountryCode("US");
		location.setCountryName("United states of America");
		location.setEnabled(true);
		
		Mockito.when(service.get(code)).thenReturn(location);
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code", is(code)))
			.andExpect(jsonPath("$.city_name", is("Los Angeles")))
			.andDo(print());
			
	}
	
	@Test
	public void testShouldReturn404NotFound() throws Exception {
		
		Location location = new Location();
		location.setCode("ABCDEF");
		location.setCityName("Los Angeles");
		location.setRegionName("California");
		location.setCountryCode("US");
		location.setCountryName("United states of America");
		location.setEnabled(true);

		Mockito.when(service.update(location)).thenThrow(new LocationNotFoundException("No location found"));
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	
	@Test
	public void testShouldReturn400BadRequest() throws Exception {
		
		Location location = new Location();
		location.setCityName("Los Angeles");
		location.setRegionName("California");
		location.setCountryCode("US");
		location.setCountryName("United states of America");
		location.setEnabled(true);

		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	
	@Test
	public void testShouldReturn200OK() throws Exception {
		
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United states of America");
		location.setEnabled(true);

		Mockito.when(service.update(location)).thenReturn(location);
		
		String bodyContent = mapper.writeValueAsString(location);
		
		mockMvc.perform(put(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON).content(bodyContent))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code", is("NYC_USA")))
			.andExpect(jsonPath("$.city_name", is("New York City")))
			.andDo(print());
	}

	@Test
	public void testDeleteShouldReturn404NotFound() throws Exception{
		String code = "LACA_USA";
		String requestURI = END_POINT_PATH + "/" + code;

		Mockito.doThrow(LocationNotFoundException.class).when(service).delete(code);

		mockMvc.perform(delete(requestURI))
				.andExpect(status().isNotFound())
				.andDo(print());
	}

	@Test
	public void testDeleteShouldReturn204NoContent() throws Exception{
		String code = "LACA_USA";
		String requestURI = END_POINT_PATH + "/" + code;

		Mockito.doNothing().when(service).delete(code);

		mockMvc.perform(delete(requestURI))
				.andExpect(status().isNoContent())
				.andDo(print());
	}




}