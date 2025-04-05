package com.consumer.weatherapi.WeatherRest.Repository;

import com.consumer.weatherapi.WeatherRest.Model.WeatherMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherMetricRepository extends JpaRepository<WeatherMetric, Long> {

    //query for in memory avg,min,max,sum calculation.
    List<WeatherMetric> findBySensorIdInAndTimestampBetween(List<String> sensorIds, LocalDateTime start, LocalDateTime end);
    List<WeatherMetric> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    //Custom SQL query to get results than whole data. does avg,min,sum,max on database query level
    @Query(value = """
                SELECT 
                    sensor_id,
                    AVG(temperature) AS avg_temperature,
                    MIN(temperature) AS min_temperature,
                    MAX(temperature) AS max_temperature,
                    SUM(temperature) AS sum_temperature,
                    AVG(humidity) AS avg_humidity,
                    MIN(humidity) AS min_humidity,
                    MAX(humidity) AS max_humidity,
                    SUM(humidity) AS sum_humidity
                FROM weather_metric
                WHERE timestamp BETWEEN :start AND :end
                AND (:sensorIds IS NULL OR sensor_id IN (:sensorIds))
                GROUP BY sensor_id
            """, nativeQuery = true)
    List<Object[]> aggregateMetrics(
            @Param("sensorIds") List<String> sensorIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


}
