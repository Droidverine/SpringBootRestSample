package com.consumer.weatherapi.WeatherRest.Repository;

import com.consumer.weatherapi.WeatherRest.Model.WeatherMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherMetricRepository extends JpaRepository<WeatherMetric, Long> {
    List<WeatherMetric> findBySensorIdInAndTimestampBetween(List<String> sensorIds, LocalDateTime start, LocalDateTime end);
    List<WeatherMetric> findByTimestampBetween(LocalDateTime start, LocalDateTime end);


}
