package com.skyapi.weatherforecast.common;




import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "locations")
@RequiredArgsConstructor
public class Location {

    @Id
    @Column(length = 12, nullable = false, unique = true)
    @NotNull(message = "Location code can not be null")
    @Length(min = 3, max = 12, message = "Location code must have 3-12 characters")
    private String code;

    @Column(length = 128, nullable = false)
    @JsonProperty("city_name")
    @NotNull(message = "City name can not be null")
    @Length(min = 3, max = 123, message = "City name must have 3-64 characters")
    private String cityName;

    @Column(length = 128)
    @JsonProperty("region_name")
    @Length(min = 3, max = 128, message = "Region name must have 3-128 characters")
    private String regionName;

    @Column(length = 64, nullable = false)
    @JsonProperty("country_name")
    @NotNull(message = "Country name can not be null")
    @Length(min = 2, max = 64, message = "City name must have 3-64 characters")
    private String countryName;

    @Column(length = 2, nullable = false)
    @JsonProperty("country_code")
    @NotNull(message = "Country code can not be null")
    @Length(min = 2, max = 2, message = "Country code must have 2 characters")
    private String countryCode;

    private boolean enabled;

    @JsonIgnore
    private boolean trashed;

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private RealtimeWeather realtimeWeather;

    @OneToMany(mappedBy = "id.location", cascade = CascadeType.ALL)
    private List<HourlyWeather> listHourlyWeather = new ArrayList<>();

    public Location(String cityName, String regionName, String countryName, String countryCode) {
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return cityName + ", " + (regionName != null ? regionName + ", " : "") + countryName;
    }

    public Location code(String code) {
        setCode(code);
        return this;
    }

}
